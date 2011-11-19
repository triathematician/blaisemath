/*
 * GraphManager.java
 * Created Jan 29, 2011
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.layout.IterativeGraphLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;
import org.bm.util.Delegator;
import org.bm.util.PointManager;

/**
 * Manages a graph and its node locations. Also manages a single iterative layout
 * that can change the positions of the nodes. Broadcasts changes when they occur.
 * Provides six property changes:
 * <ul>
 * <li>{@code graphData}: the underlying graph has changed, including nodes and edges</li>
 * <li>{@code edgeData}: the nodes haven't changed, but the set of edges has</li>
 * <li>{@code layoutAlgorithm}: the layout algorithm has changed</li>
 * <li>{@code layoutAnimating}: the timer controlling layout iteration has started or stopped</li>
 * <li>{@code positionData}: the positions of nodes has changed</li>
 * </ul>
 * External changes to this class are heavily managed. The following methods may be accessed to change thigns:
 * <ul>
 * <li><code>updateGraph()</code>: notifies the manager that the graph (nodes, edges) has changed</li>
 * <li><code>updateEdges()</code>: notifies the manager that the edges have changed, but the nodes have not</li>
 * <li><code>requestPositionMap(), requestPositionArray()</code>: notifies that an external position request has been made, which will be forwarded to either the layout or the position object</li>
 * <li><b>Layout</b>: <code>applyLayout(), setLayoutAlgorithm(), setLayoutAnimating(), iterateLayout(), startLayoutTask(), stopLayoutTask()</code>
 * </ul>
 * It is assumed that separate threads may be making changes to the graph and iterating the layout, so this
 * class acts defensively to ensure synchronization. So the class only provides direct access to the <code>Graph</code> object,
 * the current positions (as a <code>Map</code>), and the current layout. To retrieve representations of the
 * graph's nodes, edges, and positions simultaneously, one can use <code>getGraphData()</code> to return the three
 * simultaneously.
 * 
 * @author elisha
 */
public final class GraphManager {

    /** The underlying graph */
    private Graph graph;
    /** Stores the current list of graph nodes */
    private transient List nodes;
    /** Manages locations of nodes in the graph */
    private final PointManager<Object, Point2D.Double> locator = new PointManager<Object, Point2D.Double>(
            // provide inital position to objects (origin)
            new Delegator<Object, Point2D.Double>(){
                public Point2D.Double of(Object src) { return new Point2D.Double(); }
            });

    
    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /** Constructs manager for the specified graph. */
    public GraphManager(Graph graph) {
        setGraph(graph);
    }
    // </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /** 
     * Return the graph
     * @return the adapter's graph 
     */
    public Graph getGraph() { 
        return graph; 
    }
    /** Changes the graph */
    public synchronized void setGraph(Graph g) {
        if (g == null)
            throw new IllegalArgumentException("Graph cannot be null.");
        if (this.graph == null) {
            this.graph = g;
            nodes = graph.nodes();
            locator.setObjects(nodes);
            locator.setLocationMap(INITIAL_LAYOUT.layout(g, LAYOUT_PARAMETERS));
            if (iLayout != null)
                iLayout.requestPositions(locator.getLocationMap(), true);
        } else {
            this.graph = g;
            nodes = graph.nodes();
            locator.setObjects(nodes);
            if (iLayout != null)
                iLayout.requestPositions(locator.getLocationMap(), true);
        }
        fireLocationsChanged();
    }
    
    // </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc="Methods that Update the Graph/Node Positions Externally">
    //
    // UPDATE METHODS
    //
    /** Call this method to notify that the set of vertices AND edges has changed. */
    public synchronized void updateGraph() {
        nodes = graph.nodes();
        locator.setObjects(nodes);
        if (iLayout != null)
            iLayout.requestPositions(locator.getLocationMap(), true);
        fireLocationsChanged();
    }
    
    /**
     * Returns location map of the graph
     */
    public Map<Object, Point2D.Double> getLocationMap() {
        return locator.getLocationMap();
    }

    /** Update the locations of the specified nodes with the specified values */
    public void requestPositionMap(Map<Object, Point2D.Double> nodePositions) {
        if (nodePositions != null)
            if (iLayout != null)
                iLayout.requestPositions(nodePositions, false);
            else {
                locator.setLocationMap(nodePositions);
                fireLocationsChanged();
            }
    }
    /**
     * Update the locations of the specified nodes with the specified values
     * @param nodes list of nodes corresponding to positions
     * @param pos array of positions
     */
    public void requestPositionArray(List nodes, Point2D.Double[] pos) {
        if (pos != null)
            if (iLayout != null) {
                Map<Object, Point2D.Double> pMap = new HashMap<Object, Point2D.Double>();
                for (int i = 0; i < Math.min(nodes.size(), pos.length); i++)
                    pMap.put(nodes.get(i), pos[i]);
                iLayout.requestPositions(pMap, false);
            } else
                locator.setLocationArray(pos);
    }
    // </editor-fold>

    
    // <editor-fold defaultstate="collapsed" desc="Layout Code">
    //
    // LAYOUT METHODS
    //

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

    /** Timer that performs iterative layout */
    private transient java.util.Timer layoutTimer;
    /** Timer task */
    private transient java.util.TimerTask layoutTask;
    /** Used for iterative graph layouts */
    private transient IterativeGraphLayout iLayout;

    /**
     * Applies a particular layout algorithm and updates node positions.
     * @param layout the layout algorithm
     * @param parameters parameters for the algorithm
     */
    public void applyLayout(StaticGraphLayout layout, double... parameters) {
        Map<Object,Point2D.Double> pos = layout.layout(graph, parameters);
        locator.setLocationMap(pos);
        if (iLayout != null)
            iLayout.requestPositions(pos, false);
        fireLocationsChanged();
    }

    /** @return current iterative layout algorithm */
    public IterativeGraphLayout getLayoutAlgorithm() { 
        return iLayout;
    }

    /**
     * Sets up with an iterative graph layout. Cancels any ongoing layout timer.
     * Does not start a new layout.
     */
    public void setLayoutAlgorithm(IterativeGraphLayout layout) {
        if (layout != iLayout) {
            IterativeGraphLayout old = iLayout;
            stopLayoutTask();
            iLayout = layout;
            iLayout.requestPositions(locator.getLocationMap(), true);
            fireAlgorithmChanged(old, layout);
        }
    }

    /** @return true if an iterative layout is active */
    public boolean isLayoutAnimating() {
        return layoutTask != null;
    }

    /** Sets layout to animate */
    public void setLayoutAnimating(boolean value) {
        boolean old = layoutTask != null;
        if (value != old) {
            if (value)
                startLayoutTask(DEFAULT_DELAY, DEFAULT_ITER);
            else
                stopLayoutTask();
            fireAnimatingChanged(old, value);
        }
    }

    private static int _i = 0;
    private static int _p = 0;

    /** Iterates layout, if an iterative layout has been provided. */
    public synchronized void iterateLayout() {
//        System.out.println("Starting layout");
        if (iLayout != null) {
            long t0 = System.currentTimeMillis();
            try {
                iLayout.iterate(graph);
            } catch (ConcurrentModificationException ex) {
                System.err.println("Failed Layout Iteration: ConcurrentModificationException");
            }
            long t1 = System.currentTimeMillis();
            locator.setLocationMap(iLayout.getPositions());
            fireLocationsChanged();
            long t2 = System.currentTimeMillis();
            if ((t1-t0) > 100)
                System.err.println("Long Iterative Layout Step " + (++_i) + ": " + (t1-t0) + "ms");
            if ((t2-t1) > 100)
                System.err.println("Long Position Update " + (++_p) + ": " + (t2-t1) + "ms");
        }
//        System.out.println("... layout complete");
    }

    /**
     * Activates the layout timer, if an iterative layout has been provided.
     * @param delay delay in ms between layout calls
     */
    public void startLayoutTask(int delay, final int iter) {
        if (iLayout == null) return;
        if (layoutTimer == null)
            layoutTimer = new java.util.Timer();
        stopLayoutTask();
        layoutTask = new TimerTask() {
            @Override public void run() {
                for(int i=0;i<iter;i++)
                    iterateLayout();
            }
        };
        layoutTimer.schedule(layoutTask, delay, delay);
    }

    /** Stops the layout timer */
    public void stopLayoutTask() {
        if (layoutTask != null) {
            layoutTask.cancel();
            layoutTask = null;
        }
    }// </editor-fold>   

    
    // <editor-fold defaultstate="collapsed" desc="Event Handling">
    //
    // EVENT HANDLING
    //

    /** The entire graph has changed: nodes, positions, edges */
    private synchronized void fireLocationsChanged() { 
        Object[] loc = locator.getLocationArray();
        Point2D.Double[] pLoc = new Point2D.Double[loc.length];
        System.arraycopy(loc, 0, pLoc, 0, loc.length);
        pcs.firePropertyChange("locationArray", null, pLoc);
    }
    /** The layout algorithm has changed */
    private synchronized void fireAlgorithmChanged(IterativeGraphLayout old, IterativeGraphLayout nue) { 
        pcs.firePropertyChange("layoutAlgorithm", old, nue);
    }
    /** The layout algorithm has started/stopped its timer */
    private synchronized void fireAnimatingChanged(boolean old, boolean nue) { 
        pcs.firePropertyChange("layoutAnimating", old, nue);
    }

    /** Handles property change events */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) { pcs.removePropertyChangeListener(propertyName, listener); }
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) { pcs.removePropertyChangeListener(listener); }
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) { pcs.addPropertyChangeListener(propertyName, listener); }
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) { pcs.addPropertyChangeListener(listener); }

    // </editor-fold>
}
