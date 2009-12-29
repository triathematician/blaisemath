/*
 * GraphMetricInterface.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics;

import org.bm.blaise.scio.graph.*;
import java.util.Map;

/**
 * <p>Defines a vertex metric on a graph.</p>
 * @author ae3263
 */
public interface GraphMetricInterface<V> {

    /**
     * Computes the value of the metric for the given graph and vertex.
     * 
     * @param graph the graph
     * @param vertex the vertex
     *
     * @return value of the metric
     */
    public double getValue(GraphInterface<V> graph, V vertex);

    /**
     * Returns mapping of vertices to metric value for all vertices in the graph.
     *
     * @param graph the graph
     */
    public Map<V, Double> compute(GraphInterface<V> graph);
}
