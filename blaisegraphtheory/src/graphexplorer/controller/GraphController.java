/*
 * GraphController.java
 * Created Jul 28, 2010
 */

package graphexplorer.controller;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.layout.IterativeGraphLayout;
import org.bm.blaise.scio.graph.layout.StaticGraphLayout;
import org.bm.blaise.scio.graph.metrics.NodeMetric;

/**
 * <p>
 *   This controller is used to handle views into a graph object, with a single
 *   graph viewable at a time. The controller is set up with a persistent graph
 *   (the <code>baseGraph</code> property in the super-class)
 *   that represents the "base graph" represented by the controller.
 *   A filter sits on top of this graph, providing a "partial view" of the object,
 *   (the <code>baseGraph</code> property).
 * </p>
 * <p>
 *   The controller has sub-controllers used to handle the visible <i>layout</i> of the
 *   graph and the <i>metrics</i> computed for the active graph. In general these
 *   will interact with the <code>baseGraph</code>, although the layout controller
 *   also maintains location information for non-visible nodes.
 * </p>
 *
 * @author Elisha Peterson
 */
public class GraphController extends AbstractGraphController
        implements PropertyChangeListener {
    
    //
    // PROPERTY CHANGE NAMES
    //

    /** File name */
    public static final String $NAME = "name";
    /** The viewable graph displayed */
    public static final String $VIEWGRAPH = "viewable";

    //
    // PROPERTIES
    //
    
    /** File Name of graph */
    private String name = null;

    /** Handles the filter */
    private FilterController fc = null;
    /** Handles the layout */
    private LayoutController lc = null;
    /** Handles the metric */
    private StatController sc = null;
    /** Handles node decorations */
    private DecorController dc = null;

    //
    // CONSTRUCTORS & FACTORY METHODS
    //

    /** Constructs controller without an object */
    protected GraphController() {
        fc = new FilterController(this);
        sc = new StatController(this);
        lc = new LayoutController(this);
        dc = new DecorController(this, sc);
    }

    /** Construct instance of controller for a regular graph */
    public GraphController(Graph graph) {
        this();
        setBaseGraph(graph);
    }

    /** Construct instance of controller for a regular graph w/ specified name */
    public GraphController(Graph graph, String name) {
        this();
        setName(name);
        setBaseGraph(graph);
    }

    //
    // INITIALIZERS & UTILITIES
    //

    @Override
    public String toString() {
        return "GraphController";
    }

    //
    // PROPERTY PATTERNS
    //

    /** @return name of the graph being controlled (corresponds to file name) */
    public String getName() { 
        return name;
    }
    
    /**
     * Sets name of graph being controlled.
     * @param name the graph's name (typically a file name)
     */
    public void setName(String name) {
        if (this.name == null ? name != null : !this.name.equals(name)) {
            String oldName = this.name;
            this.name = name;
            pcs.firePropertyChange($NAME, oldName, name);
        }
    }

    //
    // GRAPH DELEGATES
    //

    /** @return true if filter is enabled */
    public boolean isFilterEnabled() { return fc.isEnabled(); }
    /** Sets filter to be enabled or disabled */
    public void setFilterEnabled(boolean val) { fc.setEnabled(val); }
    /** @return filter threshold */
    public double getFilterThreshold() { return fc.getFilterThreshold(); }
    /** Set filter threshold */
    public void setFilterThreshold(double threshold) { fc.setFilterThreshold(threshold); }
    /** @return filtered graph, which is currently visible */
    public Graph getViewGraph() { return fc.getFilteredGraph(); }
    /** @return viewable nodes */
    public List getViewNodes() { return fc.getFilteredGraph().nodes(); }

    /** @return active node metric */
    public NodeMetric getMetric() { return sc.getMetric(); }
    /** Sets underlying metric */
    public void setMetric(NodeMetric m) { sc.setMetric(m); }
    /** Returns metric values */
    public List getMetricValues() { return sc.getValues(); }

    /** @return map with positions of all nodes */
    public Map<Object, Point2D.Double> getNodePositions() { return lc.getPositions(); }
    /** @return position of specified node */
    public Point2D.Double positionOf(Object node) { return lc.positionOf(node); }
    /** Sets positions of nodes in current graph */
    public void setNodePositions(Map<Object, Point2D.Double> pos) { lc.setPositions(pos); }

    /** Apply specified static layout algorithm (graph will keep animating) */
    public void applyLayout(StaticGraphLayout layout, double... parameters) { lc.applyLayout(layout, parameters); }
    /** Steps current iterative graph layout */
    public void stepLayout() { lc.stepLayout(); }
    /** @return active layout algorithm */
    public IterativeGraphLayout getLayoutAlgorithm() { return lc.getLayoutAlgorithm(); }
    /** Sets active layout algorithm */
    public void setLayoutAlgorithm(IterativeGraphLayout layout) { lc.setLayoutAlgorithm(layout); }
    /** @return true if layout animation is on */
    public boolean isLayoutAnimating() { return lc.isAnimating(); }
    /** Turns on/off layout animation */
    public void setLayoutAnimating(boolean value) { lc.setAnimating(value); }

    /** @return set of nodes that have highlights */
    public Set getHighlightNodes() { return dc.getHighlightNodes(); }
    /** Sets nodes that have highlights */
    public void setHighlightNodes(Set subset) { dc.setHighlightNodes(subset); }
    /** Sets visible label at specified node */
    public void setNodeLabel(Object node, String label) { dc.setNodeLabel(node, label); }



    //
    // EVENT HANDLING
    //

    /**
     * Sets up a "follows" relationship for the specified controller.
     * Whenever this graph's "view" changes, the specified controller
     * will update its base graph to this view.
     */
    void addViewGraphFollower(final AbstractGraphController controller) {
        addPropertyChangeListener($VIEWGRAPH, new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                controller.setBaseGraph(getViewGraph());
            }
        });
    }

    /**
     * Sets up a "follows" relationship for the specified controller.
     * Whenever this graph's "base graph" changes, the specified controller
     * will update its base graph to that base graph.
     */
    void addBaseGraphFollower(final AbstractGraphController controller) {
        addPropertyChangeListener($BASE, new PropertyChangeListener(){
            public void propertyChange(PropertyChangeEvent evt) {
                controller.setBaseGraph(getBaseGraph());
            }
        });
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (!$BASE.equals(evt.getPropertyName()))
            pcs.firePropertyChange(evt);
    }

}
