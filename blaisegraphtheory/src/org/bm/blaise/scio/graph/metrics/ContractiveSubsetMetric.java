/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.Collection;
import org.bm.blaise.scio.graph.ContractedGraph;
import org.bm.blaise.scio.graph.Graph;

/**
 * Provides a <code>SubsetMetric</code> computed by contracting all the
 * nodes in a subset to a single node, and using a particular <code>NodeMetric</code>
 * on that node.
 *
 * @author Elisha Peterson
 */
public class ContractiveSubsetMetric<N> implements SubsetMetric<N> {

    NodeMetric<N> baseMetric;

    /**
     * Constructs with provided base metric.
     * @param baseMetric the metric to use for computations of contracted node
     */
    public ContractiveSubsetMetric(NodeMetric<N> baseMetric) { this.baseMetric = baseMetric; }

    public <V> N getValue(Graph<V> graph, Collection<V> nodes) {
        V starNode = null;
        for (V v : nodes) { starNode = v; break; }
        Graph<V> contracted = new ContractedGraph<V>(graph, nodes, starNode);
//        System.out.println("Contractive value: " + contracted);
        return baseMetric.value(contracted, starNode);
    }

}
