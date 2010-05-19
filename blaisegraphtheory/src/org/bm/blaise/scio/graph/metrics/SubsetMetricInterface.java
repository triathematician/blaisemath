/*
 * VertexMetricInterface.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.Collection;
import org.bm.blaise.scio.graph.*;

/**
 * <p>
 *      This is a template for a metric on a graph defined on subsets of vertices
 *      that returns numeric values.
 * </p>
 * @param <N> the type of number
 * @author Elisha Peterson
 */
public interface SubsetMetricInterface<N> {

    /**
     * Computes the value of the metric for the given graph and collection of vertices.
     * 
     * @param graph the graph
     * @param vertices the collection of vertices
     *
     * @return value of the metric
     */
    public N getValue(GraphInterface graph, Collection<Integer> vertices);
}
