/*
 * DecorController.java
 * Created Nov 18, 2010
 */

package graphexplorer.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bm.blaise.scio.graph.ValuedGraph;

/**
 * <p>
 * Responsible for managing "decorations" of a graph, e.g. node sizes and/or colors
 * based on metric computations, or preset values.
 * </p>
 *
 * @author elisha
 */
public class DecorController extends AbstractGraphController
        implements PropertyChangeListener {

    //
    // PROPERTY CHANGE NAMES
    //

    /** Changes to the "highlighted" subset of nodes. */
    public static final String $HIGHLIGHT = "highlight";
    /** General decor changes. */
    public static final String $DECOR = "decor";

    //
    // PROPERTIES
    //

    /** Stores the "subset" of the graph that is currently of interest (may be null or empty) */
    private Set subset = null;

    //
    // CONSTRUCTORS
    //

    /** Set up with listening for specified controllers. */
    public DecorController(GraphController gc, StatController mc) {
        gc.addViewGraphFollower(this);
        mc.addPropertyChangeListener(StatController.$VALUES, this);
        addPropertyChangeListener(gc);
        setBaseGraph(gc.getBaseGraph());
    }

    //
    // EVENT HANDLING
    //

    public void propertyChange(PropertyChangeEvent evt) {

    }


    //
    // PROPERTY PATTERNS
    //

    /** @return highlighted subset of nodes (non-null, but maybe empty) */
    public Set getHighlightNodes() {
        return subset == null ? Collections.emptySet() : subset;
    }
    /**
     * Sets the current selection of nodes for the subset. If any elements of the
     * subset <i>are not</i> in the larger set of nodes, an exception is thrown.
     * @param subset subset of nodes to select; if null, the subset is set to the empty subset
     * @throws IllegalArgumentException if the subset is not contained in the set
     *   of nodes of the primary graph object (or all its nodes if the graph is longitudinal)
     */
    public void setHighlightNodes(Set subset) {
        if (this.subset != subset) {
            Set oldSubset = this.subset;
            if (subset == null) {
                this.subset = new HashSet();
                pcs.firePropertyChange($HIGHLIGHT, oldSubset, this.subset);
            } else {
                if (baseGraph.nodes().containsAll(subset)) {
                    this.subset = subset;
                    pcs.firePropertyChange($HIGHLIGHT, oldSubset, this.subset);
                } else {
                    reportStatus("Invalid node subset: " + subset);
                    pcs.firePropertyChange($HIGHLIGHT, oldSubset, this.subset);
                }
            }
        }
    }

    /**
     * Sets the label corresponding to the specified node in the graph.
     * If the graph is already a "valued graph", this updates the value.
     * If not, returns an error.
     *
     * @param node the node to label
     * @param label the label of the node
     */
    public void setNodeLabel(Object node, String label) {
        if (baseGraph instanceof ValuedGraph) {
            ValuedGraph vg = (ValuedGraph) baseGraph;
            Object oldValue = vg.getValue(node);
            vg.setValue(node, label);
            pcs.firePropertyChange($DECOR, oldValue, label);
        } else {
            reportStatus("ERROR: unable to change node label: graph is not a valued graph!");
        }
    }

}
