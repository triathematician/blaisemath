/*
 * GraphLayoutManager.java
 * Created Jan 29, 2011
 */

package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.googlecode.blaisemath.annotation.InvokedFromThread;
import com.googlecode.blaisemath.graph.modules.layout.PositionalAddingLayout;
import com.googlecode.blaisemath.graph.modules.layout.SpringLayout;
import com.googlecode.blaisemath.graph.modules.suppliers.GraphSuppliers;
import com.googlecode.blaisemath.util.coordinate.CoordinateChangeEvent;
import com.googlecode.blaisemath.util.coordinate.CoordinateListener;
import com.googlecode.blaisemath.util.coordinate.CoordinateManager;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

/**
 * <p>
 *   Manages graph layout within a background thread, in situations where the graph
 *   or node locations might be simultaneously modified from other threads.
 *   Executes a graph layout algorithm in a background thread. Uses an
 *   {@link IterativeGraphLayout} algorithm, whose results are supplied to the
 *   {@link CoordinateManager}. This class is not thread-safe, so all of its
 *   methods should be accessed from a single thread. However, coordinate locations can
 *   be accessed or updated in the {@code CoordinateManager} from any thread, since it is
 *   thread-safe.
 * </p>
 *
 * @param <N> type of node in graph
 * @author elisha
 */
@NotThreadSafe
public final class GraphLayoutManager<N> {
    
    private static final int NODE_CACHE_SIZE = 20000;

    /** Graph property */
    public static final String GRAPH_PROP = "graph";
    /** Layout property */
    public static final String LAYOUT_PROP = "layoutAlgorithm";
    /** Whether layout is active */
    public static final String LAYOUT_ACTIVE_PROP = "layoutTaskActive";
    
    /** Default time between layout iterations. */
    private static final int DEFAULT_DELAY = 10;
    /** Default # iterations per layout step */
    private static final int DEFAULT_ITER = 2;
    
    /** The initial layout scheme */
    private final StaticGraphLayout initialLayout = StaticGraphLayout.CIRCLE;
    /** The initial layout parameters */
    private final Double initialLayoutParameters = 50.0;
    /** The layout scheme for adding vertices */
    private final StaticGraphLayout addingLayout = new PositionalAddingLayout();
    /** The initial layout parameters */
    private final Double addingLayoutParameters = 100.0;
    
    /** The cooling parameter at step 0, defined by the iterative layout */
    private double coolingParameter0 = 1.0;
    /** Cooling curve. Determines the cooling parameter at each step, as a product of initial cooling parameter. */
    private final Function<Integer,Double> coolingCurve;
    
    /** Graph */
    private Graph<N> graph;
    /** Maintains locations of nodes in the graph */
    private final CoordinateManager<N, Point2D.Double> coordManager = CoordinateManager.create(NODE_CACHE_SIZE);
    /** Contains the algorithm for iterating graph layouts. */
    private IterativeGraphLayout iLayout;

    /** Represents the currently active layout task */
    private IterateLayoutService layoutService = null;
    
    /** Handles property change events */
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);


    /** Initializes with an empty graph */
    public GraphLayoutManager() {
        this(GraphSuppliers.EMPTY_GRAPH, new SpringLayout());
    }

    /**
     * Constructs manager for the specified graph.
     * @param graph the graph
     * @param layout the layout algorithm used by the manager
     */
    public GraphLayoutManager(Graph<N> graph, @Nullable IterativeGraphLayout layout) {
        this.iLayout = layout;
        this.coolingCurve = new Function<Integer,Double>(){
            @Override
            public Double apply(Integer x) {
                return .1 + .9/Math.log10(x+10);
            }
        };
        coordManager.addCoordinateListener(new CoordinateListener<N,Point2D.Double>(){
            @InvokedFromThread("unknown")
            @Override
            public void coordinatesChanged(CoordinateChangeEvent<N, Point2D.Double> evt) {
                // get the current iterative layout, ensuring it doesn't change between null check and use
                IterativeGraphLayout layout = iLayout;
                if (layout != null) {
                    layout.requestPositions(coordManager.getActiveLocationCopy(), true);
                }
            }
        });
        setGraph(graph);
    }


    // <editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /**
     * Object used to map locations of points.
     * @return point manager
     */
    public CoordinateManager<N, Point2D.Double> getCoordinateManager() {
        return coordManager;
    }

    /**
     * Returns copy of the locations of objects in the graph.
     * @return locations, as a copy of the map provided in the point manager
     */
    public Map<N, Point2D.Double> getNodeLocationCopy() {
        return coordManager.getActiveLocationCopy();
    }

    /**
     * Return the graph
     * @return the layout manager's graph
     */
    public Graph<N> getGraph() {
        return graph;
    }
    
    /**
     * Changes the graph. Uses the default initial position layout to position
     * nodes if the current graph was null, otherwise uses the adding layout for
     * any nodes that do not have current positions.
     *
     * @param g the graph
     */
    public void setGraph(Graph<N> g) {
        Graph old = this.graph;
        if (g == null) {
            setLayoutTaskActive(false);
        } else if (old != g) {
            boolean active = isLayoutTaskActive();
            setLayoutTaskActive(false);
            this.graph = g;
            initializeNodeLocations(old, g);
            setLayoutTaskActive(active);
            pcs.firePropertyChange(GRAPH_PROP, old, g);
        }
    }

    /**
     * When the graph is changes, call this method to set up initial positions
     * for nodes in the graph. Will attempt to use cached nodes if possible.
     * Otherwise, it may execute the "initial layout" algorithm or the "adding
     * layout" algorithm.
     * 
     * @todo may take some time to execute if the graph is large, consider improving
     *   this class's design by running the initial layout in a background thread;
     *   also, locking on the CM may be problematic if the layout takes a long time
     */
    private void initializeNodeLocations(Graph<N> old, Graph<N> g) {
        synchronized (coordManager) {
            Set<N> oldNodes = Sets.difference(coordManager.getActive(), g.nodes());
            coordManager.deactivate(oldNodes);
            // defer to existing locations if possible
            if (coordManager.locatesAll(g.nodes())) {
                coordManager.reactivate(g.nodes());
            } else {
                // lays out new graph entirely
                Map<N,Point2D.Double> newLoc;
                if (old == null) {
                    newLoc = initialLayout.layout(g, Collections.EMPTY_MAP, Collections.EMPTY_SET, initialLayoutParameters);
                } else {
                    Map<N, Point2D.Double> curLocs = coordManager.getActiveLocationCopy();
                    newLoc = addingLayout.layout(g, curLocs, Collections.EMPTY_SET, addingLayoutParameters);
                }
                // remove objects that are already in coordinate manager
                newLoc.keySet().removeAll(coordManager.getActive());
                newLoc.keySet().removeAll(coordManager.getInactive());
                coordManager.reactivate(g.nodes());
                coordManager.putAll(newLoc);
            }

            // log size mismatches to help with debugging
            int sz = coordManager.getActive().size();
            boolean check = sz == g.nodeCount();
            if (!check) {
                Logger.getLogger(GraphLayoutManager.class.getName()).log(Level.WARNING, 
                        "Object sizes don''t match: {0} locations, but {1} nodes!", 
                        new Object[]{sz, g.nodeCount()});
            }
        }
    }

    /**
     * Get layout algorithm
     * @return current iterative layout algorithm
     */
    public IterativeGraphLayout getLayoutAlgorithm() {
        return iLayout;
    }

    /**
     * Sets up with an iterative graph layout. Cancels any ongoing layout, and does
     * not start a new one.
     * @param layout the layout algorithm
     */
    public void setLayoutAlgorithm(IterativeGraphLayout layout) {
        if (layout != iLayout) {
            setLayoutTaskActive(false);
            IterativeGraphLayout old = iLayout;
            iLayout = layout;
            coolingParameter0 = iLayout.getCoolingParameter();
            iLayout.requestPositions(coordManager.getActiveLocationCopy(), true);
            pcs.firePropertyChange(LAYOUT_PROP, old, layout);
        }
    }

    /**
     * Return whether layout task is currently active.
     * @return true if an iterative layout is active
     */
    public boolean isLayoutTaskActive() {
        return layoutService != null && layoutService.isRunning();
    }

    /**
     * Use to change the status of the layout task, either starting or stopping it.
     * @param value true to animate, false to stop animating
     */
    public void setLayoutTaskActive(boolean value) {
        boolean old = isLayoutTaskActive();
        if (value != old) {
            if (value) {
                startLayoutTask(DEFAULT_DELAY, DEFAULT_ITER);
            } else {
                stopLayoutTaskNow();
            }
            pcs.firePropertyChange(LAYOUT_ACTIVE_PROP, !value, value);
        }
    }
    
    // </editor-fold>
    

    //<editor-fold defaultstate="collapsed" desc="MUTATORS">
    // 
    // MUTATORS
    //
    
    /**
     * Update the locations of the specified nodes with the specified values.
     * If an iterative layout is currently active, locations are updated at the
     * layout. Otherwise, locations are updated by the point manager. Nodes that are
     * in the graph but whose positions are not in the provided map will not be moved.
     *
     * @param nodePositions new locations for objects
     */
    public void requestLocations(Map<N, Point2D.Double> nodePositions) {
        checkNotNull(nodePositions);
        if (isLayoutTaskActive()) {
            iLayout.requestPositions(nodePositions, false);
        } else {
            coordManager.putAll(nodePositions);
        }
    }

    /**
     * Update positions of current using specified layout algorithm. This method will
     * replace the coordinates of objects in the graph.
     * @param layout static layout algorithm
     * @param ic initial conditionsn fo rstatic layout algorithm
     * @param fixed what nodes should be fixed in applying the layout
     * @param parameters layout parameters
     * @param <P> parameters type
     */
    public <P> void applyLayout(StaticGraphLayout<P> layout, Map<N,Point2D.Double> ic,
            Set<N> fixed, P parameters){
        requestLocations(layout.layout(graph, ic, fixed, parameters));
    }

    /**
     * Manually iterate layout, if an iterative layout has been provided.
     */
    public void iterateLayout() {
        if (iLayout != null && !isLayoutTaskActive()) {
            iLayout.iterate(graph);
            coordManager.setCoordinateMap(iLayout.getPositionsCopy());
        }
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Layout Task">
    //
    // LAYOUT TASK
    //

    /**
     * Activates the layout timer, if an iterative layout has been provided.
     * @param delay delay in ms between layout calls
     * @param iter number of iterations per updateCoordinates
     */
    private void startLayoutTask(int delay, final int iter) {
        if (iLayout != null) {
            stopLayoutTaskNow();
            iLayout.setCoolingParameter(coolingParameter0);
            layoutService = new IterateLayoutService(delay, iter);
            layoutService.startAsync();
        }
    }

    /**
     * Stops the layout timer
     */
    private void stopLayoutTaskNow() {
        if (layoutService != null) {
            layoutService.stopAsync();
            try {
                layoutService.awaitTerminated(100, TimeUnit.MILLISECONDS);
            } catch (TimeoutException ex) {
                Logger.getLogger(GraphLayoutManager.class.getName()).log(Level.WARNING,
                        "Layout service was not terminated", ex);
            }
        }
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Event Handling">
    //
    // EVENT HANDLING
    //

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) { 
        pcs.addPropertyChangeListener(listener); 
    }

    // </editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    
    private class IterateLayoutService extends AbstractScheduledService {
        /** Delay between iterations */
        private final int delayMillis;
        /** # of iterations per loop */
        private final int iter;
        /** Total # of iterations */
        private int iterTot = 0;
        
        IterateLayoutService(int delayMillis, int iter) {
            this.delayMillis = delayMillis;
            this.iter = iter;
            
            addListener(new Listener() {
                @Override
                public void failed(Service.State from, Throwable failure) {
                    Logger.getLogger(IterateLayoutService.class.getName()).log(Level.SEVERE,
                        "Layout service failed", failure);
                }
            }, MoreExecutors.sameThreadExecutor());
        }
        
        @Override
        protected void runOneIteration() throws Exception {
            try {
                for (int i = 0; i < iter; i++) {
                    iLayout.iterate(graph);
                    if (Thread.interrupted()) {
                        throw new InterruptedException("Layout canceled");
                    }
                }
                coordManager.setCoordinateMap(iLayout.getPositionsCopy());
                iterTot += iter;
                int proxyIter = Math.max(0, iterTot-100);
                iLayout.setCoolingParameter(coolingParameter0*coolingCurve.apply(proxyIter));
                if (Thread.interrupted()) {
                    throw new InterruptedException("Layout canceled");
                }
            } catch (InterruptedException x) {
                Logger.getLogger(IterateLayoutService.class.getName()).log(Level.FINE,
                        "Background layout task interrupted", x);
                // restore interrupt after bypassing update
                Thread.currentThread().interrupt();
            }
        }

        @Override
        protected Scheduler scheduler() {
            return Scheduler.newFixedDelaySchedule(0, delayMillis, TimeUnit.MILLISECONDS);
        }
    }
    
    //</editor-fold>
    

}
