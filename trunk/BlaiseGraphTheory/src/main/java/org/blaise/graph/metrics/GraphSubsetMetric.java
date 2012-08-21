/*
 * GraphSubsetMetric.java
 * Created on Oct 26, 2009
 */

package org.blaise.graph.metrics;

import java.util.Set;
import org.blaise.graph.Graph;

/**
 * Returns a value associated with a subset of nodes in a graph.
 * @param <N> the type of value returned
 * 
 * @author Elisha Peterson
 */
public interface GraphSubsetMetric<N> {

    /**
     * Computes the value of the metric for the given graph and nodes.
     * @param graph the graph
     * @param nodes a collection of nodes in the graph
     * @return value of the metric
     * @throws IllegalArgumentException if the value cannot be computed for
     *      specified graph (e.g. graph is null, or graph is directed, but the
     *      metric only applies to undirected graphs)
     */
    public <V> N getValue(Graph<V> graph, Set<V> nodes);
}
