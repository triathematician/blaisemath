/*
 * GraphNodeMetric.java
 * Created on Oct 26, 2009
 */

package org.blaise.graph.metrics;

import org.blaise.graph.Graph;

/**
 * Returns a value for a single node in a graph.
 * @param <N> the type of value returned (usually a number)
 * 
 * @author Elisha Peterson
 */
public interface GraphNodeMetric<N> {

    /**
     * Computes the value of the metric for the given graph and node.
     * @param graph the graph
     * @param node a node in the graph
     * @return value of the metric
     * @throws IllegalArgumentException if the value cannot be computed for
     *      specified graph (e.g. graph is null, or graph is directed, but the
     *      metric only applies to undirected graphs)
     */
    public <V> N value(Graph<V> graph, V node);

}
