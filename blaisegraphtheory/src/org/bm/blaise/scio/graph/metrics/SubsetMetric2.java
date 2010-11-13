/*
 * SubsetMetric2.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.Collection;
import org.bm.blaise.scio.graph.*;

/**
 * This class provides a single method for returning a value associated with
 * two subsets of nodes in the graph.
 *
 * @param <N> the type of value returned
 * @author Elisha Peterson
 */
public class SubsetMetric2<N> {

    SubsetMetric<N> baseM;

    /** Construct with base subset metric. */
    public SubsetMetric2(SubsetMetric<N> baseM) {
        this.baseM = baseM;
    }

    /**
     * Computes the value of the metric for the given graph and nodes.
     * @param graph the graph
     * @param participants the subset of nodes "participating"
     * @param subset the subset of nodes (a sub-collection of participants) that
     *   assesses the value of the outcome
     * @return value of the metric
     */
    public <V> N getValue(Graph<V> graph, Collection<V> participants, Collection<V> subset) {
        return (N) baseM.getValue(new Subgraph(graph, participants), subset);
    }
}
