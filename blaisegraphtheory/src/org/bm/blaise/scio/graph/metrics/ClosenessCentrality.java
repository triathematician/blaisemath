/*
 * ClosenessCentrality.java
 * Created Jul 23, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.ArrayList;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.Graphs;
import org.bm.blaise.scio.graph.ValuedGraph;

/**
 * <p>
 *   Implements closeness centrality (Sabidussi 1966), the inverse sum of distances from
 *   one node to other nodes. The same calculation can be used to compute the "eccentricity"
 *   of the node, the max distance from this node to any other node, termed <i>graph centrality</i>
 *   by Hage/Harary 1995. Instances of both metrics are provided.
 * </p>
 * @author elisha
 */
public class ClosenessCentrality implements NodeMetric<Double> {

    /** Determines whether to use sum of distances or max of distances */
    boolean useSum = true;

    /**
     * Construct with specified parameters.
     * @param useSum if true, returns inverse of sum of distances to other vertices; otherwise returns inverse of max of distances
     */
    public ClosenessCentrality(boolean useSum) {
        this.useSum = useSum;
    }

    @Override public String toString() { return "Closeness Centrality"; }

    /** @return instance of closeness centrality metric (standardized) */
    public static NodeMetric<Double> getInstance() { return new ClosenessCentrality(true); }
    /** @return instance of max-closeness centrality metric, sometimes called <i>graph centrality</i> (standardized) */
    public static NodeMetric<Double> getMaxInstance() { return new ClosenessCentrality(false); }

    public boolean supportsGraph(boolean directed) { return true; }
    public <V> double nodeMax(boolean directed, int order) { return useSum ? 1/(order-1.0) : 1.0; }
    public <V> double centralMax(boolean directed, int order) { throw new UnsupportedOperationException("Not supported yet."); }

    public <V> Double value(Graph<V> graph, V node) {
        int n = graph.order();
        ValuedGraph<V, Integer> vg = Graphs.geodesicTree(graph, node);
        double result = 0;
        int nulls = 0;
        Integer value;
        for (V v : graph.nodes()) {
            if (v == node) continue;
            value = vg.getValue(v);
            if (value == null)
                nulls++;
            else if (useSum)
                result += vg.getValue(v);
            else
                result = Math.max(result, vg.getValue(v));
        }
        if (result == 0) return 0.0;
        result = 1/result;
        if (nulls > 0)
            result *= (n-1.0-nulls)/(n-1.0); // adjust for null values (in other components)
        return result;
    }

    public <V> List<Double> allValues(Graph<V> graph) {
        List<Double> result = new ArrayList<Double>(graph.order());
        for (V v : graph.nodes())
            result.add(value(graph, v));
        return result;
    }

}
