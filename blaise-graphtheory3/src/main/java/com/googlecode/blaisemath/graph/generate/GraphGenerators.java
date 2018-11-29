package com.googlecode.blaisemath.graph.generate;

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

import java.util.AbstractList;
import java.util.List;

public class GraphGenerators {

    /** Utility class */
    private GraphGenerators() {}

    /**
     * Generate graph with given set of edges.
     * @param parameters the parameters
     * @param edges edges
     * @return created graph
     */
    protected static Graph<Integer> createGraphWithEdges(DefaultGeneratorParameters parameters, Iterable<Integer[]> edges) {
        return GraphUtils.createFromArrayEdges(parameters.isDirected(), intList(0, parameters.getNodeCount()), edges);
    }

    /**
     * Returns abstract list of integers min, ..., max-1
     * @param min list min
     * @param max list max
     * @return list
     */
    protected static List<Integer> intList(final int min, final int max) {
        return new AbstractList<Integer>() {
            @Override
            public Integer get(int index) {
                return min + index;
            }
            @Override
            public int size() {
                return max - min;
            }
        };
    }

}
