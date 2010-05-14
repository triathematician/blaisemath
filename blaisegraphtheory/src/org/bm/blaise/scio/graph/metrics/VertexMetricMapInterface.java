/*
 * VertexMetricInterface.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.List;
import org.bm.blaise.scio.graph.*;

/**
 * <p>
 *      This is a template for a vertex metric on a graph that can return a double
 *      value for each vertex in the graph. This class provides a method that can be used to make the
 *      computations more efficient.
 * </p>
 * @param <N> the type of number
 * @author Elisha Peterson
 */
public interface VertexMetricMapInterface<N> extends VertexMetricInterface<N> {

    /**
     * Computes map associating each vertex to a value metric
     * 
     * @param graph the graph
     * @param vertex the vertex
     *
     * @return values of the metric
     */
    public List<N> getValues(GraphInterface graph);
}
