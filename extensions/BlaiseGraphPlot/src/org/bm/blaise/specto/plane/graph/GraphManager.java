/*
 * GraphManager.java
 * Created Jan 29, 2011
 */

package org.bm.blaise.specto.plane.graph;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphUtils;
import org.bm.blaise.scio.graph.layout.IterativeGraphLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;

/**
 * Manages a graph and its node locations. Also manages a single iterative layout
 * that can change the positions of the nodes. Broadcasts changes when they occur.
 * Provides six property changes:
 * <ul>
 * <li><code>$GRAPH</code>: the underlying graph has changed, including nodes and edges</li>
 * <li><code>$EDGES</code>: the nodes haven't changed, but the set of edges has</li>
 * <li><code>$LAYOUT_ALGORITHM</code>: the layout algorithm has changed</li>
 * <li><code>$ANIMATING</code>: the timer controlling layout iteration has started or stopped</li>
 * <li><code>$POSITIONS</code>: the positions of nodes has changed</li>
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

    // <editor-fold defaultstate="collapsed" desc="Property Change Constants">
    /** Changes to the underlying graph (set of nodes and edges) */
    public static final String $GRAPH = "graph";
    /** Changes to the edge set only */
    public static final String $EDGES = "edges";
    /** Changes to layout algorithm */
    public static final String $LAYOUT_ALGORITHM = "layout algorithm";
    /** Changes to animating status */
    public static final String $ANIMATING = "animating";
    /** Changes to node positions */
    public static final String $POSITIONS = "positions";
    // </editor-fold>

    // <editor-fold desc="Properties">
    /** The underlying graph */
    private Graph graph;
    /** Stores the current list of graph nodes */
    private transient List nodes;
    /** Stores the edges as an array of int[] pairs */
    private transient int[][] edges;
    /** Manages locations of nodes in the graph */
    private final NodeLocationManager locator = new NodeLocationManager();;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /** Constructs manager for the specified graph. */
    public GraphManager(Graph graph) {
        setGraph(graph);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Property Patterns">

    /** @return the adapter's graph */
    public synchronized Graph getGraph() { return graph; }
    /** Changes the graph */
    public synchronized void setGraph(Graph g) {
        if (g == null)
            throw new IllegalArgumentException("Graph cannot be null.");
        if (this.graph == null) {
            this.graph = g;
            nodes = graph.nodes();
            edges = GraphUtils.getEdges(graph);
            locator.setActiveNodes(nodes);
            locator.setNodePositions(INITIAL_LAYOUT.layout(g, LAYOUT_PARAMETERS));
            if (iLayout != null)
                iLayout.requestPositions(locator.cur, true);
        } else {
            this.graph = g;
            nodes = graph.nodes();
            edges = GraphUtils.getEdges(graph);
            locator.setActiveNodes(nodes);
            if (iLayout != null)
                iLayout.requestPositions(locator.cur, true);
        }
        fireGraphChanged();
    }

    /** @return current graph data, including nodes, locations, and edges */
    public synchronized Object[] getGraphData() {
        if (edges == null) edges = GraphUtils.getEdges(graph);
        return new Object[] { locator.nodes, locator.getLocationArray(), edges };
    }

    /** @return the current mapping of nodes to locations */
    public Map<Object, Point2D.Double> getNodePositions() { return Collections.unmodifiableMap(locator.cur); }
    /** @return the location of a single node */
    public Point2D.Double getNodePosition(Object node) { return locator.cur.get(node); }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods that Update the Graph/Node Positions Externally">
    //
    // UPDATE METHODS
    //
    /** Call this method to notify that the set of vertices AND edges has changed. */
    public synchronized void updateGraph() {
        nodes = graph.nodes();
        edges = GraphUtils.getEdges(graph);
        locator.setActiveNodes(nodes);
        if (iLayout != null)
            iLayout.requestPositions(locator.cur, true);
        fireGraphChanged();
    }

    /** Call this method to notify that the set of edges has changed. */
    public synchronized void updateEdges() {
        edges = GraphUtils.getEdges(graph);
        fireEdgesChanged();
    }

    /** Update the locations of the specified nodes with the specified values */
    public void requestPositionMap(Map<Object, Point2D.Double> nodePositions) {
        if (nodePositions != null)
            if (iLayout != null)
                iLayout.requestPositions(nodePositions, false);
            else {
                locator.setNodePositions(nodePositions);
                firePositionChanged();
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
                locator.setNodePositions(pos);
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
        locator.setNodePositions(pos);
        if (iLayout != null)
            iLayout.requestPositions(pos, false);
        firePositionChanged();
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
            iLayout.requestPositions(locator.cur, true);
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
            locator.setNodePositions(iLayout.getPositions());
            firePositionChanged();
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

    // <editor-fold defaultstate="collapsed" desc="Inner Classes">
    //
    // INNER CLASSES
    //

    /** Manages locations of vertices in the graph. */
    private static class NodeLocationManager {
        
        /** List of active nodes */
        final List nodes = Collections.synchronizedList(new ArrayList());
        /** Current locations */
        final Map<Object, Point2D.Double> cur = Collections.synchronizedMap(new HashMap<Object, Point2D.Double>());
        /** Cached locations */
        final Map<Object, Point2D.Double> cache = Collections.synchronizedMap(new HashMap<Object, Point2D.Double>());
        
        /** Array of current point locations; order/size is that of nodes object, objects that of cur. */
        private transient Point2D.Double[] arr;

        /** Return location array for specified nodes */
        private synchronized Point2D.Double[] getLocationArray() { return arr; }

        /** 
         * Sets the set of "active" nodes. Anything in cur not here will be returned to the cache.
         * @param nodes list of node objects to be active
         */
        private synchronized void setActiveNodes(List nodes) {
            Set cs = cur.keySet();
            if (cs.containsAll(nodes) && nodes.containsAll(cs))
                return;

            // store objects that we need to cache
            Set toCache = new HashSet();
            for (Object o : cs)
                if (!nodes.contains(o))
                    toCache.add(o);
            for (Object o : toCache)
                cache.put(o, cur.remove(o));
            resizeCache();

            // find new objects and add them to cs
            // use cached locations if possible; otherwise, add near the origin
            int nz = 0; double r, th;
            for (Object o : nodes)
                if (!cs.contains(o)) {
                    nz++;
                    r = .001*Math.log10(nz);
                    th = .01*nz;
                    cur.put(o, cache.containsKey(o) ? cache.get(o)
                            : new Point2D.Double(r*Math.cos(th), r*Math.sin(th)));
                }

            // update node list
            this.nodes.clear();
            this.nodes.addAll(nodes);
            updateArray(true);
        }

        /** 
         * Updates positions of specified nodes (adds all to current position map).
         * The key set here does not necessarily correspond to the "active" nodes,
         * but will update
         */
        private synchronized void setNodePositions(Map<Object, Point2D.Double> pos) {
            cur.putAll(pos);
            updateArray(false);
        }

        private synchronized void setNodePositions(Point2D.Double[] pos) {
            for (int i = 0; i < Math.min(pos.length, nodes.size()); i++)
                cur.put(nodes.get(i), pos[i]);
            updateArray(false);
        }

        /** Updates the array of locations */
        private synchronized void updateArray(boolean nodeUpdate) {
            if (nodeUpdate)
                arr = new Point2D.Double[nodes.size()];
            for (int i = 0; i < arr.length; i++)
                arr[i] = cur.get(nodes.get(i));
        }

        /** Maximum cache size */
        private static final int MAX_CACHE = 5000;
        /** Ensure the cache doesn't get too big */
        private void resizeCache() {
            int nRemove = cache.size() - MAX_CACHE;
            if (nRemove > 0) {
                List toRemove = new ArrayList();
                for (Object o : cache.keySet()) {
                    toRemove.add(o);
                    if (toRemove.size() >= nRemove)
                        break;
                }
                System.err.println("Removing " + nRemove + " elements from node location cache: " + toRemove);
                for (Object o : toRemove)
                    cache.remove(o);
                System.err.println("... new size: " + cache.size());
            }
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Handling">
    //
    // EVENT HANDLING
    //

    /** The entire graph has changed: nodes, positions, edges */
    private synchronized void fireGraphChanged() { 
        pcs.firePropertyChange($GRAPH, null, new Object[]{nodes, locator.getLocationArray(), edges});
    }
    /** The node positions have changed */
    private synchronized void firePositionChanged() {
        pcs.firePropertyChange($POSITIONS, null, new Object[]{nodes, locator.getLocationArray()});
    }
    /** The edges have changed */
    private synchronized void fireEdgesChanged() { 
        pcs.firePropertyChange($EDGES, null, new Object[]{nodes, edges});
    }
    /** The layout algorithm has changed */
    private synchronized void fireAlgorithmChanged(IterativeGraphLayout old, IterativeGraphLayout nue) { 
        pcs.firePropertyChange($LAYOUT_ALGORITHM, old, nue);
    }
    /** The layout algorithm has started/stopped its timer */
    private synchronized void fireAnimatingChanged(boolean old, boolean nue) { 
        pcs.firePropertyChange($ANIMATING, old, nue);
    }

    /** Handles property change events */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) { pcs.removePropertyChangeListener(propertyName, listener); }
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) { pcs.removePropertyChangeListener(listener); }
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) { pcs.addPropertyChangeListener(propertyName, listener); }
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) { pcs.addPropertyChangeListener(listener); }
    // </editor-fold>
}
