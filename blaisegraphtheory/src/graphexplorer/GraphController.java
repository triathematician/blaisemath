/*
 * GraphController.java
 * Created Jul 28, 2010
 */

package graphexplorer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.ValuedGraph;
import org.bm.blaise.scio.graph.layout.IterativeGraphLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;
import org.bm.blaise.scio.graph.metrics.NodeMetric;
import stormtimer.BetterTimer;

/**
 * <p>
 * Central class that controls the state of a graph explorer app with a "current active"
 * graph, an active metric, an animating layout, and a highlighted selection of nodes.
 * </p>
 * <p>
 * The values of the metric are not stored by default, but they are cached in case the
 * user requests the same values more than once. The computation of metric values is done
 * in a background thread. If a request is sent while the thread is running, the values
 * returns as null. Otherwise, a property change is sent to notify listeners when the
 * computed values are available.
 * </p>
 *
 * @author Elisha Peterson
 */
public class GraphController {

    /** Handles property change events */
    PropertyChangeSupport pcs;

    /** Name of graph */
    private String name = null;

    /** Stores the graph (current graph for longitudinal case) */
    private Graph graph = null;
    /** Stores the set of nodes & their locations (all nodes for longitudinal graphs) */
    private Map<Object,Point2D.Double> nodes = null;
    /** Stores the "subset" of the graph that is currently of interest (may be null or empty) */
    private Set subset = null;
    /** Stores the active metric (may be null) */
    private NodeMetric metric = null;
    /** Stores values of the metric */
    private List values = null;
    /** Stores the animating layout (may be null) */
    private IterativeGraphLayout layout = null;

    /** Flag indicating whether current graph is longitudinal */
    private boolean longitudinal = false;
    /** Longitudinal graph (should be null if not in a longitudinal state) */
    private LongitudinalGraph lGraph = null;
    /** Time of slice in a longitudinal graph (should be null if not in a longitudinal state) */
    private Double time = null;
    /** Stores current set of nodes in longitudinal case */
    private List activeNodes = null;

    //
    // CONSTRUCTOR & FACTORY METHODS
    //

    /** Constructs controller without an object */
    private GraphController() {
        pcs = new PropertyChangeSupport(this);
    }

    @Override public String toString() { return "GraphController"; }

    /** @return instance of controller for a regular graph */
    public static GraphController getInstance(Graph graph) {
        GraphController result = new GraphController();
        result.setPrimaryGraph(graph);
        return result;
    }

    /** @return instance of controller for a regular graph w/ specified name */
    public static GraphController getInstance(Graph graph, String name) {
        GraphController result = new GraphController();
        result.setPrimaryGraph(graph);
        result.setName(name);
        return result;
    }

    /** @return instance of controller for a longitudinal graph */
    public static GraphController getInstance(LongitudinalGraph graph) {
        GraphController result = new GraphController();
        result.setPrimaryGraph(graph);
        return result;
    }

    /** @return instance of controller for a longitudinal graph w/ specified name */
    public static GraphController getInstance(LongitudinalGraph graph, String name) {
        GraphController result = new GraphController();
        result.setPrimaryGraph(graph);
        result.setName(name);
        return result;
    }

    //
    // VALIDITY
    //

    /** @return true if controller is in a valid state */
    public boolean valid() {
        if (graph == null)
            return false;
        if (longitudinal && lGraph == null)
            return false;
        return true;
    }

    //
    // STATUS/OUTPUT
    //

    /** Updates the application reportStatus bar. */
    public void reportStatus(String string) { pcs.firePropertyChange("status", null, string); }
    /** Updates the application reportOutput. */
    public void reportOutput(String string) { pcs.firePropertyChange("output", null, string); }

    //
    // GETTERS
    //

    /** @return name of the graph being controlled (corresponds to file name) */
    public String getName() { return name; }

    /** @return primary object */
    public Object getPrimaryGraph() { return longitudinal ? lGraph : graph; }

    /** @return all nodes */
    public Set getPrimaryNodes() { return nodes.keySet(); }

    /** @return all nodes with positions */
    public Map<Object, Point2D.Double> getPositions() { return nodes; }

    /** @return active graph */
    public Graph getActiveGraph() { return graph; }

    /** @return highlighted subset of nodes (non-null, but maybe empty) */
    public Set getNodeSubset() { return subset; }

    /** @return active metric (maybe null) */
    public NodeMetric getMetric() { return metric; }

    boolean running = false;

    /** @return active metric values (the last ones that have been computed) */
    public List getMetricValues() {
        if (running)
            return null;
        else if (values == null && metric != null && graph != null) {
            running = true;
//            System.out.println("getMetricValues computing");
//            System.out.println(".. metric: " + metric);
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(new Runnable(){
                public void run() {
                    pcs.firePropertyChange("status", null, "Computing metric " + metric + "...");
                    long t0 = System.currentTimeMillis();
                    values = (metric == null || graph == null) ? null
                            : metric.allValues(graph);
                    running = false;
                    long t = System.currentTimeMillis() - t0;
                    pcs.firePropertyChange("status", null, metric + " computation completed (" + t + "ms).");
                    pcs.firePropertyChange("values", null, values);
                }
            });
            executor.shutdown();
        } else
            return values;
        return null;
    }

    /** @return active layout (maybe null) */
    public IterativeGraphLayout getIterativeLayout() { return layout; }

    /** @return true if controller is in a longitudinal state */
    public boolean isLongitudinal() {
        assert valid();
        return longitudinal;
    }

    /** @return longitudinal graph */
    public LongitudinalGraph getLongitudinalGraph() { return lGraph; }

    /** @return time for l-graph */
    public double getTime() { return time; }

    /** @return nodes for current longitudinal graph slice */
    public List getActiveNodes() { return activeNodes; }

    //
    // SETTERS
    //

    /**
     * Sets name of graph being controlled.
     * @param name the graph's name (typically a file name)
     */
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        pcs.firePropertyChange("name", oldName, name);
    }

    /** 
     * Sets the controller to the specified graph. Resets all properties of the controller
     * accordingly.
     * @param graph a graph
     */
    public void setPrimaryGraph(Graph graph) {
        if (graph == null)
            throw new NullPointerException("setGraph called with null argument");
        if (this.graph != graph) {
            this.graph = graph;
            this.nodes = new TreeMap<Object, Point2D.Double>();
            for (Object o : graph.nodes())
                this.nodes.put(o, new Point2D.Double());
            this.subset = new HashSet();
            this.metric = null;
            this.values = null;
            this.layout = null;
            this.longitudinal = false;
            this.lGraph = null;
            this.time = null;
            this.activeNodes = graph.nodes();
            pcs.firePropertyChange("primary", null, graph);
        }
        assert valid();
    }

    /**
     * Sets the controller to the specified longitudinal graph. Resets all properties
     * of the controller accordingly.
     * @param graph a longitudinal graph
     */
    public void setPrimaryGraph(LongitudinalGraph graph) {
        if (graph == null)
            throw new NullPointerException("setGraph called with null argument");
        if (this.lGraph != graph) {
            this.graph = null;
            this.nodes = new TreeMap<Object, Point2D.Double>();
            for (Object o : graph.getAllNodes())
                this.nodes.put(o, new Point2D.Double());
            this.subset = new HashSet();
            this.metric = null;
            this.values = null;
            this.layout = null;
            this.longitudinal = true;
            this.lGraph = graph;
            pcs.firePropertyChange("primary", null, graph);
            setTime(lGraph.getMinimumTime());
        }
        assert valid();
    }

    /**
     * Sets the current selection of nodes for the subset. If any elements of the
     * subset <i>are not</i> in the larger set of nodes, an exception is thrown.
     * @param subset subset of nodes to select; if null, the subset is set to the empty subset
     * @throws IllegalArgumentException if the subset is not contained in the set
     *   of nodes of the primary graph object (or all its nodes if the graph is longitudinal)
     */
    public void setNodeSubset(Set subset) {
        if (!valid())
            throw new IllegalStateException("Cannot call this method while in an invalid state!");
        if (this.subset != subset) {
            Set oldSubset = this.subset;
            if (subset == null) {
                this.subset = new HashSet();
                pcs.firePropertyChange("subset", oldSubset, this.subset);
            } else {
                if (nodes.keySet().containsAll(subset)) {
                    this.subset = subset;
                    pcs.firePropertyChange("subset", oldSubset, this.subset);
                } else {
                    reportStatus("Invalid node subset: " + subset);
                    pcs.firePropertyChange("subset", oldSubset, this.subset);
                }
            }
        }
        assert valid();
    }

    /**
     * Sets the currently active metric
     * @param metric the new metric
     */
    public void setMetric(NodeMetric metric) {
        if (!valid())
            throw new IllegalStateException("Cannot call this method while in an invalid state!");
        if (this.metric != metric) {
            Object oldMetric = this.metric;
            this.metric = metric;
            this.values = null;
            pcs.firePropertyChange("metric", oldMetric, metric);
        }
        assert valid();
    }

    /**
     * Sets the time slice of the longitudinal graph. If the longitudinal graph
     * does not contain a slice at the specified time, returns a graph at the
     * closest available time; the time stored is then the exact time of the slice.
     * @param time the new time
     */
    public void setTime(double time) {
        assert valid();
        if (!isLongitudinal())
            throw new IllegalStateException("Should not call setTime when not in longitudinal state!");
        
        if (this.time == null || this.time != time || this.graph == null || this.activeNodes == null) {
            this.graph = lGraph.slice(time, false);
            this.values = null;
            this.activeNodes = graph.nodes();
            Double oldTime = this.time;
            this.time = time; // TODO - this should be altered in case the returned graph is not at the exact time
            pcs.firePropertyChange("time", oldTime, time);
        }
        assert valid();
    }

    //
    // LAYOUT METHODS
    //

    /** Timer handling animating layouts */
    BetterTimer layoutTimer;
    /** Whether animation is currently running */
    boolean animating = false;

    /**
     * Sets the locations of some or all nodes in the graph.
     * @param positions a map specifying positions of nodes
     */
    public void setPositions(Map<Object, Point2D.Double> positions) {
        if (!nodes.keySet().containsAll(positions.keySet()))
            throw new IllegalArgumentException("Position map contains invalid keys!");
        if (animating)
            layout.requestPositions(positions);
        else {
            nodes.putAll(positions);
            pcs.firePropertyChange("positions", null, nodes);
        }
    }

    /** Applies specified layout to the active graph */
    void applyLayout(StaticGraphLayout layout, double... parameters) {
        setPositions(layout.layout(graph, parameters));
    }

    /**
     * Sets the currently active layout, but does not start it up
     * @param layout the new layout
     */
    public void setIterativeLayout(IterativeGraphLayout layout) {
        if (!valid())
            throw new IllegalStateException("Cannot call this method while in an invalid state!");
        if (this.layout != layout) {
            if (layoutTimer != null) layoutTimer.stop();
            Object oldLayout = this.layout;
            this.layout = layout;
            if (layout != null)
                layout.reset(currentPositions());
            pcs.firePropertyChange("layout", oldLayout, layout);
        }
    }

    /**
     * @return true if layout is currently animating
     */
    boolean isAnimating() {
        return animating;
    }

    /**
     * Activates layout animation, or resets it.
     */
    public void animateLayout() {
        if (layout == null)
            return;
        boolean oldValue = animating;
        if (layoutTimer != null) {
            layoutTimer.cancel();
            layoutTimer = null;
        }
        layoutTimer = new BetterTimer(1);
        layoutTimer.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                layout.iterate(graph);
                nodes.putAll(layout.getPositions());
                pcs.firePropertyChange("positions", null, nodes);
            }
        });
        layoutTimer.start();
        animating = true;
        pcs.firePropertyChange("animating", oldValue, animating);
    }

    /**
     * Steps layout animation (if not running).
     */
    public void stepLayout() {
        if (!animating && layout != null) {
            layout.iterate(graph);
            nodes.putAll(layout.getPositions());
            pcs.firePropertyChange("positions", null, nodes);
        }
    }

    /**
     * Stops layout animation (if running)
     */
    public void stopLayout() {
        layoutTimer.cancel();
        boolean oldValue = animating;
        animating = false;
        if (oldValue != animating)
            pcs.firePropertyChange("animating", oldValue, animating);
    }

    //
    // LABELING UPDATES
    //

    /**
     * Sets the label corresponding to the specified node in the graph.
     * If the graph is already a "valued graph", this updates the value.
     * If not, returns an error.
     *
     * @param node the node to label
     * @param label the label of the node
     */
    public void setActiveGraphLabel(Object node, String label) {
        if (graph instanceof ValuedGraph) {
            ValuedGraph vg = (ValuedGraph) graph;
            Object oldValue = vg.getValue(node);
            vg.setValue(node, label);
            pcs.firePropertyChange("node value", oldValue, label);
        } else {
            reportStatus("ERROR: unable to change node label: graph is not a valued graph!");
        }
    }

    //
    // private utilities
    //

    /** @return list of current node locations, in same order as that stored in the graph */
    private Map<Object,Point2D.Double> currentPositions() {
        LinkedHashMap<Object,Point2D.Double> result = new LinkedHashMap<Object,Point2D.Double>();
        for (Object o : activeNodes)
            result.put(o, nodes.get(o));
        return result;
    }

    //
    // PropertyChangeSupport methods
    //

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

}
