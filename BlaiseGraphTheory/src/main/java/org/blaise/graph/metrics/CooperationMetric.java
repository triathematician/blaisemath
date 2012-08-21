/*
 * CooperationMetric.java
 * Created Jul 14, 2010
 */

package org.blaise.graph.metrics;

import java.util.HashSet;
import java.util.Set;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphUtils;

/**
 * Measures difference in team performance with and without a node/subset of nodes.
 * Result contains several values: first is selfish contribution, second is altruistic;
 *  third is entire team value, fourth is partial assessment value, fifth is partial team value
 * 
 * @author Elisha Peterson
 */
public class CooperationMetric<N extends Number> implements GraphSubsetMetric<double[]> {

    GraphSubsetMetric<N> baseM;

    /** Construct with base subset metric. */
    public CooperationMetric(GraphSubsetMetric<N> baseM) {
        this.baseM = baseM;
    }

    @Override public String toString() {
        return "CooperationMetric[" + baseM + "]";
    }

    public <V> double[] getValue(Graph<V> graph, Set<V> nodes) {
        int n = graph.nodeCount(), m = nodes.size();
        Set<V> all = graph.nodes();
        Set<V> complement = new HashSet<V>(all);
        complement.removeAll(nodes);
        double outcomeAll = n == 0 ? 0.0 : baseM.getValue(graph, all).doubleValue();
        double outcomeAll2 = m == n ? 0.0 : baseM.getValue(graph, complement).doubleValue();
        double outcomePart = m == n ? 0.0 : baseM.getValue(GraphUtils.subgraph(graph, complement), complement).doubleValue();
        return new double[] { 
            outcomeAll - outcomeAll2,   // selfish contribution
            outcomeAll2 - outcomePart,  // altruistic contribution
            outcomeAll,                 // value of the outcome for the whole team
            outcomeAll2,                // value of the outcome for the whole team, perceived by complement
            outcomePart };              // value of the outcome for the complement
    }

}
