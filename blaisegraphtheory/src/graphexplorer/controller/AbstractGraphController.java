/*
 * AbstractGraphController.java
 * Created Nov 18, 2010
 */

package graphexplorer.controller;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import org.bm.blaise.scio.graph.Graph;

/**
 * <p>
 *   Provides generic functionality for controllers operating with a single "base" graph.
 *   This base graph may be of any time, and may or may not be the graph that is
 *   "presented" as the "active" graph.
 * </p>
 * 
 * @author elisha
 */
abstract public class AbstractGraphController {

    //
    // PROPERTY CHANGE NAMES
    //

    /** Status update */
    public static final String $STATUS = "status";
    /** Output stream update */
    public static final String $OUTPUT = "output";
    /** Global constant denoting that an active graph has changed. */
    public static final String $BASE = "base graph";

    //
    // PROPERTIES
    //

    /** The base graph for the controller */
    protected Graph baseGraph = null;

    /** Handles property change events */
    PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    //
    // CONSTRUCTOR
    //

    /** Construct instance (without a base graph) */
    protected AbstractGraphController() {}

    /** Construct instance (with a base graph) */
    protected AbstractGraphController(Graph base) { baseGraph = base; }

    //
    // PROPERTY PATTERNS
    //

    /** @return active graph */
    public Graph getBaseGraph() {
        return baseGraph;
    }

    /** Sets active graph. */
    public final void setBaseGraph(Graph graph) {
        if (this.baseGraph != graph) {
            Graph oldGraph = baseGraph;
            baseGraph = graph;
            updateGraph();
            fireBaseGraphChanged(oldGraph, graph);
        }
    }

    /** 
     * Provides a hook for updating the graph IMMEDIATELY after the baseGraph
     * property has changed in the <code>setBaseGraph</code>method.
     */
    protected void updateGraph() {}


    //
    // STATUS/OUTPUT
    //

    /** Updates the application reportStatus bar. */
    public void reportStatus(String string) { pcs.firePropertyChange($STATUS, null, string); }
    /** Updates the application reportOutput. */
    public void reportOutput(String string) { pcs.firePropertyChange($OUTPUT, null, string); }

    //
    // EVENT HANDLING
    //

    /**
     * Notifies listeners that a graph has changed;
     * also provides hook for subclasses to perform additional actions when the
     * graph changes.
     */
    protected void fireBaseGraphChanged(Graph oldGraph, Graph newGraph) {
        pcs.firePropertyChange($BASE, oldGraph, newGraph);
    }

    public synchronized void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) { pcs.removePropertyChangeListener(propertyName, listener); }
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener) { pcs.removePropertyChangeListener(listener); }
    public synchronized void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) { pcs.addPropertyChangeListener(propertyName, listener); }
    public synchronized void addPropertyChangeListener(PropertyChangeListener listener) { pcs.addPropertyChangeListener(listener); }

}
