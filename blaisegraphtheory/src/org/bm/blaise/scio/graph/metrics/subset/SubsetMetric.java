/*
 * SubsetMetric.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics.subset;

import java.util.Collection;
import org.bm.blaise.scio.graph.*;

/**
 * This interface provides a single method for returning a value associated with
 * a subset of nodes in a graph.
 *
 * @param <N> the type of value returned
 * @author Elisha Peterson
 */
public interface SubsetMetric<N> {

    /**
     * Computes the value of the metric for the given graph and nodes.
     * @param graph the graph
     * @param nodes a collection of nodes in the graph
     * @return value of the metric
     */
    public <V> N getValue(Graph<V> graph, Collection<V> nodes);
}
