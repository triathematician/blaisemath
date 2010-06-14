/*
 * VertexMetricInterface.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics;

import org.bm.blaise.scio.graph.*;

/**
 * This interface provides a single method for returning a value associated with
 * a node in a graph.
 *
 * @param <N> the type of value returned
 * @author Elisha Peterson
 */
public interface NodeMetric<N> {

    /**
     * Computes the value of the metric for the given graph and node.
     * @param graph the graph
     * @param node a node in the graph
     * @return value of the metric
     */
    public <V> N getValue(Graph<V> graph, V node);
}
