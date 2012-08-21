/*
 * GraphMetric.java
 * Created Aug 6, 2010
 */

package org.blaise.graph.metrics;

import org.blaise.graph.Graph;


/**
 * Returns a global value associated with a graph.
 * @param <N> the type of value returned
 * 
 * @author Elisha Peterson
 */
public interface GraphMetric<N> {

    /**
     * Computes the value of the metric for the given graph.
     * @param graph the graph
     * @return value of the metric
     * @throws IllegalArgumentException if the value cannot be computed for
     *      specified graph (e.g. graph is null, or graph is directed, but the
     *      metric only applies to undirected graphs)
     */
    public <V> N value(Graph<V> graph);
    
}
