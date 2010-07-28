/*
 * VertexMetricInterface.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.List;
import org.bm.blaise.scio.graph.*;

/**
 * This interface provides a single method for returning a value associated with
 * a node in a graph. These metrics should ignore any weighting on graphs.
 *
 * @param <N> the type of value returned
 * @author Elisha Peterson
 */
public interface NodeMetric<N> {

    /**
     * Checks to see if graph is supported by this algorithm.
     * @param directed true if graph is directed, else false
     * @return true if metric supports graph of given specs
     */
    public boolean supportsGraph(boolean directed);

    /**
     * Returns the standardization constant for this metric for given graph,
     * i.e. the maximum value achievable for a single node
     * Multiplying by this should result in all values between 0 and 1.
     * @param order order of the graph
     * @return max value achievable by a single node
     */
    public <V> double nodeMax(boolean directed, int order);

    /**
     * Returns the theoretical max value of the centralization score for
     * an entire graph computed using this metric. This score is the sum of
     * (max-value), where max is the maximum value for the whole graph.
     * @param order order of the graph
     * @return maximum value possible to compute via this metric
     * @throws UnsupportedOperationException if the max value is unknown
     */
    public <V> double centralMax(boolean directed, int order);

    /**
     * Computes the value of the metric for the given graph and node.
     * @param graph the graph
     * @param node a node in the graph
     * @return value of the metric
     */
    public <V> N value(Graph<V> graph, V node);

    /**
     * Computes the value of the metric for <i>all</i> nodes in the given graph.
     * @param graph the graph
     * @return values of the metric, ordered as the list of nodes in the graph
     */
    public <V> List<N> allValues(Graph<V> graph);

}
