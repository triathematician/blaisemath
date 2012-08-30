/*
 * GraphManager.java
 * Created Jan 29, 2011
 */

package org.blaise.graph.view;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.blaise.graph.GAInstrument;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphBuilders;
import org.blaise.graph.layout.IterativeGraphLayout;
import org.blaise.graph.layout.StaticGraphLayout;
import org.blaise.util.PointManager;

/**
 * <p>
 *   Links up a {@link IterativeGraphLayout} with a {@link PointManager} to maintain
 *   consistent positions of nodes in a {@link Graph}.
 *   Also provides timers/threads for executing iterative layouts, and notifies
 *   listeners when the positions change.
 * </p>
 *
 * @param <C> type of node in graph
 * @author elisha
 */
public final class GraphManager<C> implements PropertyChangeListener {

    //<editor-fold defaultstate="collapsed" desc="CONSTANTS">
    /** Default time between layout iterations. */
    private static final int DEFAULT_DELAY = 10;
    /** Default # iterations per layout step */
    private static final int DEFAULT_ITER = 2;

    /** The initial layout scheme */
    private static final StaticGraphLayout INITIAL_LAYOUT = StaticGraphLayout.CIRCLE;
    /** The layout scheme for adding vertices */
    private static final StaticGraphLayout ADDING_LAYOUT = StaticGraphLayout.ORIGIN;
    /** The initial layout parameters */
    private static final double[] LAYOUT_PARAMETERS = new double[] { 3 };
    //</editor-fold>

    /** Graph */
    private Graph<C> graph;
    /** Used for iterative graph layouts */
    private transient IterativeGraphLayout iLayout;
    /** Maintains locations of nodes in the graph (in local coordinates) */
    private final PointManager<C, Point2D.Double> locations = new PointManager<C, Point2D.Double>();

    /** Timer that performs iterative layout */
    private transient java.util.Timer layoutTimer;
    /** Timer task */
    private transient java.util.TimerTask layoutTask;


    /** Initializes with an empty graph */
    public GraphManager() {
        this(GraphBuilders.EMPTY_GRAPH);
    }

    /**
     * Constructs manager for the specified graph.
     * @param graph the graph
     */
    public GraphManager(Graph<C> graph) {
        setGraph(graph);
        locations.addPropertyChangeListener(this);
    }

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        // pass changes from point manager to active layout
        if (evt.getSource() == locations) {
            iLayout.requestPositions(locations.getLocationMap(), true);
        }
    }
    

    // <editor-fold defaultstate="collapsed" desc="PROPERTIES & UPDATE METHODS">
    //
    // PROPERTIES & UPDATE METHODS
    //

    /**
     * Return the graph
     * @return the adapter's graph
     */
    public Graph<C> getGraph() {
        return graph;
    }

    /**
     * Changes the graph. Uses the default initial position layout to position
     * nodes if the current graph was null, otherwise uses the adding layout for
     * any nodes that do not have current positions.
     * 
     * @param g the graph
     */
    public synchronized void setGraph(Graph<C> g) {
        if (this.graph != g) {
            Graph old = this.graph;
            if (g == null) {
                setLayoutAnimating(false);
                locations.cache(locations.getObjects());
            } else if (this.graph != g) {
                this.graph = g;
                Set<C> oldNodes = new HashSet<C>(old.nodes());
                oldNodes.removeAll(g.nodes());
                locations.cache(oldNodes);
                Map<C,Point2D.Double> newLoc = old == null ? INITIAL_LAYOUT.layout(g, LAYOUT_PARAMETERS) : ADDING_LAYOUT.layout(g, LAYOUT_PARAMETERS);
                for (C c : locations.getObjects()) {
                    newLoc.remove(c);
                }
                locations.add(newLoc);
            }
            pcs.firePropertyChange("graph", old, g);
        }
    }
    
    /**
     * Call to indicate that the structure of the graph has changed.
     */
    public synchronized void graphUpdated() {
        Set<C> oldNodes = new HashSet<C>(locations.getObjects());
        oldNodes.removeAll(graph.nodes());
        locations.cache(oldNodes);
        Map<C,Point2D.Double> newLoc = ADDING_LAYOUT.layout(graph, LAYOUT_PARAMETERS);
        for (C c : locations.getObjects()) {
            newLoc.remove(c);
        }
        locations.add(newLoc);
    }

    /**
     * Object used to map locations of points.
     * @return point manager
     */
    public PointManager<C, Point2D.Double> getPointManager() {
        return locations;
    }
    
    /**
     * Returns the locations of objects in the graph.
     * @return locations, as a copy of the map provided in the point manager
     */
    public Map<C, Point2D.Double> getLocations() {
        return locations.getLocationMap();
    }

    /**
     * Update the locations of the specified nodes with the specified values.
     * If an iterative layout is currently active, locations are updated at the
     * layout. Otherwise, locations are updated by the point manager.
     * 
     * @param nodePositions new locations for objects
     */
    public synchronized void requestLocations(Map<C, Point2D.Double> nodePositions) {
        if (nodePositions != null) {
            if (iLayout != null) {
                iLayout.requestPositions(nodePositions, false);
            } else {
                locations.update(nodePositions);
            }
        }
    }
    
    /**
     * Update positions using specified layout algorithm.
     * @param layout static layout algorithm
     */
    public synchronized void applyLayout(StaticGraphLayout layout, double... parameters){
        requestLocations(layout.layout(graph, parameters));
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Layout Code">
    //
    // LAYOUT METHODS
    //

    /** 
     * Get layout algorithm
     * @return current iterative layout algorithm 
     */
    public synchronized IterativeGraphLayout getLayoutAlgorithm() {
        return iLayout;
    }

    /**
     * Sets up with an iterative graph layout. Cancels any ongoing layout timer.
     * Does not start a new layout.
     * @param layout the layout algorithm
     */
    public synchronized void setLayoutAlgorithm(IterativeGraphLayout layout) {
        if (layout != iLayout) {
            stopLayoutTask();
            IterativeGraphLayout old = iLayout;
            iLayout = layout;
            iLayout.requestPositions(locations.getLocationMap(), true);
            pcs.firePropertyChange("layoutAlgorithm", old, layout);
        }
    }

    /**
     * Whether layout is animating
     * @return true if an iterative layout is active
     */
    public boolean isLayoutAnimating() {
        return layoutTask != null;
    }

    /**
     * Sets layout to animate
     * @param value true to animate, false to stop animating
     */
    public synchronized void setLayoutAnimating(boolean value) {
        boolean old = layoutTask != null;
        if (value != old) {
            if (value) {
                startLayoutTask(DEFAULT_DELAY, DEFAULT_ITER);
            } else {
                stopLayoutTask();
            }
            pcs.firePropertyChange("layoutAnimating", !value, value);
        }
    }

    /**
     * Activates the layout timer, if an iterative layout has been provided.
     * @param delay delay in ms between layout calls
     * @param iter number of iterations per update
     */
    public void startLayoutTask(int delay, final int iter) {
        if (iLayout == null) {
            return;
        }
        if (layoutTimer == null) {
            layoutTimer = new java.util.Timer();
        }
        stopLayoutTask();
        layoutTask = new TimerTask() {
            @Override public void run() {
                for(int i=0;i<iter;i++) {
                    iterateLayout();
                }
            }
        };
        layoutTimer.schedule(layoutTask, delay, delay);
    }

    /** 
     * Stops the layout timer 
     */
    public void stopLayoutTask() {
        if (layoutTask != null) {
            layoutTask.cancel();
            layoutTask = null;
        }
    }

    /** 
     * Iterates layout, if an iterative layout has been provided. 
     */
    public synchronized void iterateLayout() {
        if (iLayout != null) {
            int id = GAInstrument.start("GraphManager.iterateLayout");
            try {
                Map<C, Point2D.Double> locs = iLayout.iterate(graph);
                GAInstrument.middle(id, "iLayout.iterate completed");
                locations.update(locs);
            } catch (ConcurrentModificationException ex) {
                Logger.getLogger(GraphManager.class.getName()).log(Level.WARNING, "Failed Layout Iteration: ConcurrentModificationException");
            }
            GAInstrument.end(id);
        }
    }
    
    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Event Handling">
    //
    // EVENT HANDLING
    //

    /** Handles property change events */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) { pcs.removePropertyChangeListener(propertyName, listener); }
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) { pcs.removePropertyChangeListener(listener); }
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) { pcs.addPropertyChangeListener(propertyName, listener); }
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) { pcs.addPropertyChangeListener(listener); }

    // </editor-fold>


}
