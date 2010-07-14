/*
 * CooperationSubsetMetric.java
 * Created Jul 14, 2010
 */

package org.bm.blaise.scio.graph.metrics.subset;

import java.util.Collection;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;

/**
 * Measures difference in team performance with and without a node/subset of nodes.
 * @author Elisha Peterson
 */
public class CooperationSubsetMetric<N extends Number> implements SubsetMetric<double[]> {

    SubsetMetric2<N> baseM;

    /** Construct with base subset metric. */
    public CooperationSubsetMetric(SubsetMetric<N> baseM) {
        this.baseM = new SubsetMetric2(baseM);
    }

    public <V> double[] getValue(Graph<V> graph, Collection<V> nodes) {
        List<V> all = graph.nodes();
        double outcomeAll = baseM.getValue(graph, all, all).doubleValue();
        double outcomeAll2 = baseM.getValue(graph, all, nodes).doubleValue();
        double outcomePart = baseM.getValue(graph, nodes, nodes).doubleValue();
        return new double[] { outcomeAll - outcomeAll2, outcomeAll2 - outcomePart };
    }

}
