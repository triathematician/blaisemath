/*
 * GlobalMetric.java
 * Created Aug 6, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import org.bm.blaise.scio.graph.Graph;

/**
 * This interface provides a single method for returning a global
 * value associated with a graph.
 *
 * @param <N> the type of value returned
 * @author Elisha Peterson
 */
public interface GlobalMetric<N> {

    /**
     * @return name
     */
    public String getName();

    /**
     * @returnn description of the metric
     */
    public String getDescription();

    /**
     * Checks to see if graph is supported by this algorithm.
     * @param directed true if graph is directed, else false
     * @return true if metric supports graph of given specs
     */
    public boolean supportsGraph(boolean directed);

    /**
     * Computes the value of the metric for the given graph
     * @param graph the graph
     * @return value of the metric
     */
    public <V> N value(Graph<V> graph);
}
