/*
 * SubsetMetric2.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph.metrics.subset;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        return (N) baseM.getValue(Subgraph.getInstance(graph, participants), subset);
    }

    //
    // UTILITY METHOD SAVED HERE
    //

    /** Enumerates all subsets of the integer subset [0,1,2,...,n-1] as an abstract list. */
    private static List<List<Integer>> enumerateSubsets(final int n) {
        return new AbstractList<List<Integer>>() {
            @Override
            public List<Integer> get(int index) {
                ArrayList<Integer> summand = new ArrayList<Integer>();
                for (int bit = 0; bit < n; bit++)
                    if ((index >> bit) % 2 == 1) summand.add(bit);
                return summand;
            }
            @Override
            public int size() { return (int) Math.pow(2, n); }
        };
    }
}
