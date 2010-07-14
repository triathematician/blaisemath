/*
 * VertexMetricInterface.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.List;
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

    // default implementation using getAllValues:
    //
    // return getAllValues(graph).get(graph.nodes().indexOf(node));

    /**
     * Computes the value of the metric for <i>all</i> nodes in the given graph.
     * @param graph the graph
     * @return values of the metric, ordered as the list of nodes in the graph
     */
    public <V> List<N> getAllValues(Graph<V> graph);

    // default implementation using getValue:
    //
    // List<N> result = new ArrayList<N>(graph.order());
    // for (V v : graph.nodes())
    //   result.add(getValue(graph, v));
    // return result;
}
