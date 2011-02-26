/*
 * CooperationSubsetMetric.java
 * Created Jul 14, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;

/**
 * Measures difference in team performance with and without a node/subset of nodes.
 * Result contains several values: first is selfish contribution, second is altruistic;
 *  third is entire team value, fourth is partial asssessment value, fifth is partial team value
 * @author Elisha Peterson
 */
public class CooperationSubsetMetric<N extends Number> implements SubsetMetric<double[]> {

    SubsetMetric2<N> baseM;

    /** Construct with base subset metric. */
    public CooperationSubsetMetric(SubsetMetric<N> baseM) {
        this.baseM = new SubsetMetric2(baseM);
    }
    /** Construct with base subset metric. */
    public CooperationSubsetMetric(SubsetMetric2<N> baseM) {
        this.baseM = baseM;
    }

    @Override public String toString() {
        return "CooperationMetric[" + baseM.baseM + "]";
    }

    public <V> double[] getValue(Graph<V> graph, Collection<V> nodes) {
        int n = graph.order(), m = nodes.size();
        List<V> all = graph.nodes();
        ArrayList<V> complement = new ArrayList<V>(all);
        complement.removeAll(nodes);
        double outcomeAll = n == 0 ? 0.0 : baseM.getValue(graph, all, all).doubleValue();
        double outcomeAll2 = m == n ? 0.0 : baseM.getValue(graph, all, complement).doubleValue();
        double outcomePart = m == n ? 0.0 : baseM.getValue(graph, complement, complement).doubleValue();
        return new double[] { 
            outcomeAll - outcomeAll2,   // selfish contribution
            outcomeAll2 - outcomePart,  // altruistic contribution
            outcomeAll,                 // value of the outcome for the whole team
            outcomeAll2,                // value of the outcome for the whole team, perceived by complement
            outcomePart };              // value of the outcome for the complement
    }

}
