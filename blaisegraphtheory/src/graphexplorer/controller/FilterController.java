/*
 * FilterController.java
 * Created Nov 18, 2010
 */

package graphexplorer.controller;

import org.bm.blaise.scio.graph.FilteredWeightedGraph;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.WeightedGraph;

/**
 * Provides a filtered view of a graph. The filter may be "turned on and off".
 * 
 * @author elisha
 */
public class FilterController extends AbstractGraphController {

    //
    // PROPERTY CHANGE NAMES
    //

    /** Property change when filtered result graph changes */
    public static final String $FILTERED_GRAPH = "filtered graph";
    /** Threshold for underlying filter */
    public static final String $FILTER_THRESHOLD = "filter threshold";

    //
    // PROPERTIES
    //
    
    /** Whether the filter is currently enabled */
    boolean enabled = false;
    /** The filtered result graph */
    Graph filtered;

    /** Construct with no base object. */
    public FilterController(GraphController gc) {
        gc.addBaseGraphFollower(this);
        addPropertyChangeListener(gc);
        setBaseGraph(gc.getBaseGraph());
        updateGraph();
    }

    //
    // FILTERING FUNCTIONALITY
    //

    @Override
    protected void updateGraph() {
        Graph oldFiltered = filtered;
        if (!(baseGraph instanceof WeightedGraph))
            enabled = false;
        if (enabled) {
            WeightedGraph weightedBase = (WeightedGraph) baseGraph;
            if (filtered == null || !(filtered instanceof FilteredWeightedGraph))
                filtered = FilteredWeightedGraph.getInstance(weightedBase);
            else {
                FilteredWeightedGraph filterGraph = (FilteredWeightedGraph) filtered;
                if (filterGraph.getBaseGraph() != baseGraph)
                    filtered = FilteredWeightedGraph.getInstance(weightedBase);
            }
        } else
            filtered = baseGraph;
        pcs.firePropertyChange($FILTERED_GRAPH, oldFiltered, filtered);
    }

    //
    // PROPERTY PATTERNS
    //

    /** Return the filtered graph (or the base graph if the filter is "off") */
    public Graph getFilteredGraph() {
        return filtered;
    }

    /** @return filter status */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets filter status
     */
    public void setEnabled(boolean enabled) {
        if (!(baseGraph instanceof WeightedGraph))
            enabled = false;
        if (this.enabled != enabled) {
            this.enabled = enabled;
            updateGraph();
        }
    }

    /** @return filter threshold */
    public Double getFilterThreshold() {
        return isEnabled() ? ((FilteredWeightedGraph)filtered).getThreshold()
                : null;
    }

    /**
     * Sets filter threshold
     */
    public void setFilterThreshold(double value) {
        if (!(baseGraph instanceof WeightedGraph))
            return;
        setEnabled(true);
        ((FilteredWeightedGraph)baseGraph).setThreshold(value);
        pcs.firePropertyChange($FILTER_THRESHOLD, null, value);
    }

}
