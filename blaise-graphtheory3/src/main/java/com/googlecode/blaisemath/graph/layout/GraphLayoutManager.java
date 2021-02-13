package com.googlecode.blaisemath.graph.layout;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.Sets;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.coordinate.CoordinateManager;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.IterativeGraphLayout;
import com.googlecode.blaisemath.graph.StaticGraphLayout;
import com.googlecode.blaisemath.graph.layout.CircleLayout.CircleLayoutParameters;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/**
 * Manages graph layout within a background thread, in situations where the graph or node locations might be
 * simultaneously modified from other threads. Executes a graph layout algorithm in a background thread. Uses an
 * {@link IterativeGraphLayout} algorithm, whose results are supplied to the {@link CoordinateManager}.
 * <p>
 * This class is not thread-safe, so all of its methods should be accessed from a single thread. However, coordinate
 * locations can be accessed or updated in the {@code CoordinateManager} from any thread.
 *
 * @param <N> type of node in graph
 * @author Elisha Peterson
 */
public final class GraphLayoutManager<N> {
    
    private static final Logger LOG = Logger.getLogger(GraphLayoutManager.class.getName());
    
    //region CONSTANTS
    
    static final int NODE_CACHE_SIZE = 20000;

    /** Graph property */
    public static final String P_GRAPH = "graph";
    /** Layout property */
    public static final String P_LAYOUT = "layoutAlgorithm";
    /** Whether layout is active */
    public static final String P_LAYOUT_ACTIVE = "layoutTaskActive";
    
    //endregion
    
    /** Graph */
    private Graph<N> graph;
    /** Locates nodes in the graph */
    private final CoordinateManager<N, Point2D.Double> coordinateManager = CoordinateManager.create(NODE_CACHE_SIZE);
    
    /** The initial layout scheme */
    private final StaticGraphLayout<CircleLayoutParameters> initialLayout = CircleLayout.getInstance();
    /** The initial layout parameters */
    private final CircleLayoutParameters initialLayoutParameters = new CircleLayoutParameters(50);
    /** The layout scheme for adding nodes */
    private final StaticGraphLayout<CircleLayoutParameters> addingLayout = new PositionalAddingLayout();
    /** The initial layout parameters */
    private final CircleLayoutParameters addingLayoutParameters = new CircleLayoutParameters(100);

    /** Manager for iterative graph layout algorithm */
    private final IterativeGraphLayoutManager iterativeLayoutManager = new IterativeGraphLayoutManager();
    /** Service that manages iterative graph layout on background thread. */
    private IterativeGraphLayoutService iterativeLayoutService;
    
    /** Handles property change events */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    //region CONSTRUCTOR and FACTORY
    
    /** Initializes with an empty graph */
    public GraphLayoutManager() {
        iterativeLayoutManager.setCoordinateManager(coordinateManager);
        setGraph(GraphUtils.emptyGraph(false));
    }
    
    /** 
     * Initializes with a given graph.
     * @param <N> graph node type
     * @param graph graph for layout
     * @return manager instance
     */
    public static <N> GraphLayoutManager<N> create(Graph<N> graph) {
        GraphLayoutManager<N> res = new GraphLayoutManager<>();
        res.setGraph(graph);
        return res;
    }
    
    //endregion

    //region PROPERTIES

    /**
     * Object providing node locations.
     * @return point manager
     */
    public CoordinateManager<N, Point2D.Double> getCoordinateManager() {
        return coordinateManager;
    }

    /**
     * Return copy of the locations of objects in the graph.
     * @return locations, as a copy of the map provided in the point manager
     */
    public Map<N, Point2D.Double> getNodeLocationCopy() {
        return coordinateManager.getActiveLocationCopy();
    }

    /**
     * Return the graph.
     * @return the layout manager's graph
     */
    public Graph<N> getGraph() {
        return graph;
    }
    
    /**
     * Change the graph. Uses the default initial position layout to position nodes if the current graph was null,
     * otherwise uses the adding layout for any nodes that do not have current positions.
     *
     * @param g the graph
     */
    public void setGraph(Graph<N> g) {
        requireNonNull(g);
        Graph<N> old = this.graph;
        if (old != g) {
            boolean active = isLayoutTaskActive();
            setLayoutTaskActive(false);
            this.graph = g;
            iterativeLayoutManager.setGraph(g);
            initializeNodeLocations(old, g);
            setLayoutTaskActive(active);
            pcs.firePropertyChange(P_GRAPH, old, g);
        }
    }

    /**
     * Get layout algorithm.
     * @return current iterative layout algorithm
     */
    public @Nullable IterativeGraphLayout getLayoutAlgorithm() {
        return iterativeLayoutManager.getLayout();
    }

    /**
     * Set a new iterative graph layout. Cancels any ongoing layout, and does not start a new one.
     * @param layout the layout algorithm
     */
    public void setLayoutAlgorithm(@Nullable IterativeGraphLayout layout) {
        Object old = iterativeLayoutManager.getLayout();
        if (layout != old) {
            setLayoutTaskActive(false);
            iterativeLayoutService = new IterativeGraphLayoutService(iterativeLayoutManager);
            iterativeLayoutManager.setLayout(layout);
            pcs.firePropertyChange(P_LAYOUT, old, layout);
        }
    }
    
    /**
     * Get parameters associated with the current layout.
     * @return parameters
     */
    public Object getLayoutParameters() {
        return iterativeLayoutManager.getParameters();
    }

    /**
     * Set parameters for the current layout.
     * @param params new parameters
     */
    public void setLayoutParameters(Object params) {
        iterativeLayoutManager.setParameters(params);
    }

    /**
     * Return whether layout task is currently active.
     * @return true if an iterative layout is active
     */
    public boolean isLayoutTaskActive() {
        return iterativeLayoutService != null && 
                iterativeLayoutService.isLayoutActive();
    }

    /**
     * Change the status of the layout task, either starting or stopping it.
     * @param on true to animate, false to stop animating
     */
    public void setLayoutTaskActive(boolean on) {
        boolean old = isLayoutTaskActive();
        if (on == old) {
            return;
        } else if (on) {
            stopLayoutTaskNow();
            iterativeLayoutService = new IterativeGraphLayoutService(iterativeLayoutManager);
            iterativeLayoutService.startAsync();
        } else {
            stopLayoutTaskNow();
        }
        pcs.firePropertyChange(P_LAYOUT_ACTIVE, !on, on);
    }
    
    //endregion

    //region LAYOUT OPERATIONS

    /**
     * When the graph is changes, call this method to set up initial positions for nodes in the graph. Will attempt to
     * use cached nodes if possible. Otherwise, it may execute the "initial layout" algorithm or the "adding layout"
     * algorithm.
     *
     * TODO - may take some time to execute if the graph is large, consider improving this class's design by running the
     *   initial layout in a background thread; also, locking on the CM may be problematic if the layout takes a long time
     */
    private void initializeNodeLocations(Graph<N> old, Graph<N> g) {
        synchronized (coordinateManager) {
            Set<N> oldNodes = Sets.difference(coordinateManager.getActive(), g.nodes());
            coordinateManager.deactivate(oldNodes);
            // defer to existing locations if possible
            if (coordinateManager.locatesAll(g.nodes())) {
                coordinateManager.reactivate(g.nodes());
            } else {
                // lays out new graph entirely
                Map<N, Point2D.Double> newLoc;
                if (old == null) {
                    newLoc = initialLayout.layout(g, null, initialLayoutParameters);
                } else {
                    Map<N, Point2D.Double> curLoc = coordinateManager.getActiveLocationCopy();
                    newLoc = addingLayout.layout(g, curLoc, addingLayoutParameters);
                }
                // remove objects that are already in coordinate manager
                newLoc.keySet().removeAll(coordinateManager.getActive());
                newLoc.keySet().removeAll(coordinateManager.getInactive());
                coordinateManager.reactivate(g.nodes());
                coordinateManager.putAll(newLoc);
            }

            // log size mismatches to help with debugging
            int sz = coordinateManager.getActive().size();
            boolean check = sz == g.nodes().size();
            if (!check) {
                LOG.log(Level.WARNING, "Object sizes don''t match: {0} locations, but {1} nodes!",
                        new Object[]{sz, g.nodes().size()});
            }
        }
    }

    /**
     * Update the locations of the specified nodes with the specified values. If an iterative layout is currently active,
     * locations are updated at the layout. Otherwise, locations are updated by the point manager. Nodes that are in the
     * graph but whose positions are not in the provided map will not be moved.
     *
     * @param locations new locations for objects
     */
    public void requestLocations(Map<N, Point2D.Double> locations) {
        requireNonNull(locations);
        if (isLayoutTaskActive()) {
            iterativeLayoutManager.requestPositions(locations, false);
        } else {
            coordinateManager.putAll(locations);
        }
    }

    /**
     * Update positions of current using specified layout algorithm. This method will replace the coordinates of objects
     * in the graph.
     *
     * @param layout static layout algorithm
     * @param ic initial conditions for static layout algorithm
     * @param parameters layout parameters
     * @param <P> parameters type
     */
    public <P> void applyLayout(StaticGraphLayout<P> layout, Map<N,Point2D.Double> ic, P parameters){
        requestLocations(layout.layout(graph, ic, parameters));
    }

    /**
     * Manually iterate layout, if an iterative layout has been provided.
     */
    public void iterateLayout() {
        iterativeLayoutService.runOneIteration();
    }

    /**
     * Stop the layout timer.
     */
    private void stopLayoutTaskNow() {
        if (iterativeLayoutService != null) {
            iterativeLayoutService.stopAsync();
            iterativeLayoutManager.reset();
            try {
                iterativeLayoutService.awaitTerminated(100, TimeUnit.MILLISECONDS);
            } catch (TimeoutException ex) {
                LOG.log(Level.WARNING, "Layout service was not terminated", ex);
            }
        }
    }

    //endregion

    //region EVENTS

    public final void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public final void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener); 
    }

    //endregion

}
