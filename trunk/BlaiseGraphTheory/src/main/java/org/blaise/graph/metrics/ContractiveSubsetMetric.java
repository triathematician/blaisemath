/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.metrics;

import java.util.Set;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphUtils;

/**
 * Provides a derived {@link GraphSubsetMetric} by contracting all the
 * nodes in a subset to a single node, and using a {@link GraphNodeMetric}
 * on that node.
 *
 * @author Elisha Peterson
 */
public class ContractiveSubsetMetric<N> implements GraphSubsetMetric<N> {

    GraphNodeMetric<N> baseMetric;

    /**
     * Constructs with provided base metric.
     * @param baseMetric the metric to use for computations of contracted node
     */
    public ContractiveSubsetMetric(GraphNodeMetric<N> baseMetric) { 
        this.baseMetric = baseMetric; 
    }

    public <V> N getValue(Graph<V> graph, Set<V> nodes) {
        V starNode = null;
        for (V v : nodes) { starNode = v; break; }
        Graph<V> contracted = GraphUtils.contractedGraph(graph, nodes, starNode);
        return baseMetric.value(contracted, starNode);
    }

}
