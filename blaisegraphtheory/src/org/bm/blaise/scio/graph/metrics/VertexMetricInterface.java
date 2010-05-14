/*
 * VertexMetricInterface.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics;

import org.bm.blaise.scio.graph.*;
import java.util.Map;

/**
 * <p>
 *      This is a template for a vertex metric on a graph that can return a double
 *      value for each vertex in the graph.
 * </p>
 * @param <N> the type of number
 * @author Elisha Peterson
 */
public interface VertexMetricInterface<N> {

    /**
     * Computes the value of the metric for the given graph and vertex.
     * 
     * @param graph the graph
     * @param vertex the vertex
     *
     * @return value of the metric
     */
    public N getValue(GraphInterface graph, int vertex);
}
