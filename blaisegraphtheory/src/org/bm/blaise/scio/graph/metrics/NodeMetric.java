/*
 * VertexMetricInterface.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.List;
import org.bm.blaise.scio.graph.*;

/**
 * This interface provides a methods for returning a value associated with
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
     * Returns the maximum value of this metric achievable for a single node.
     * (Also called the standardization constant.)
     * Dividing by this should result in all values between 0 and 1.
     * @param order order of the graph
     * @return max value achievable by a single node
     */
    public <V> double nodeMax(boolean directed, int order);

    /**
     * <p>Returns the theoretical maximum value of the centralization score for
     * a directed or undirected <b>connected</b> graph of the specified order.</p>
     * <p>The centralization score is the sum over all nodes n_i of (C_A(n*)-C_A(n_i)),
     * where C_A is the metric value, and C_A(n*) is the maximum value over all the n_i.
     * Thus, this measures the total deviation from the maximum value.</p>
     * <p>This method should return the <i>theoretical maximum value</i> of the
     * centralization score, i.e. the largest possible such score for any <b>connected</b> graph of
     * the given order.</p>
     * <p>This value may not always be known, in which case it suffices to return
     * <code>Double.NaN</code>.</p>
     * @param order order of the graph
     * @return maximum value possible to compute via this metric, or <code>Double.NaN</code>
     *      if the value is not known.
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
