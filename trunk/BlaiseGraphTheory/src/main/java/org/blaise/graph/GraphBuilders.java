/**
 * GraphBuilders.java
 * Created Aug 18, 2012
 */

package org.blaise.graph;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>
 *  Provides a number of simplistic graph builders.
 * </p>
 * @author elisha
 */
public class GraphBuilders {
    
    /** A completely empty graph */
    public static final Graph EMPTY_GRAPH = new SparseGraph(false, Collections.EMPTY_SET, Collections.EMPTY_SET);
    
    /** Constructs graph with n vertices */
    public static class EmptyGraphBuilder extends GraphBuilder.Support<Integer> {
        public EmptyGraphBuilder() {}
        public EmptyGraphBuilder(boolean directed, int nodes) { super(directed, nodes); }
        public Graph<Integer> createGraph() {
            if (nodes < 0) {
                throw new IllegalArgumentException("Numbers must be nonnegative! n=" + nodes);
            }
            return new SparseGraph(directed, intSet(nodes), (Collection) Collections.emptyList());
        }
    }

    /** Constructs complete graph with n vertices */
    public static class CompleteGraphBuilder extends GraphBuilder.Support<Integer> {
        public CompleteGraphBuilder() {}
        public CompleteGraphBuilder(boolean directed, int nodes) { super(directed, nodes); }
        public Graph<Integer> createGraph() {
            if (nodes < 0) {
                throw new IllegalArgumentException("Numbers must be nonnegative! n=" + nodes);
            }
            List<Integer[]> edges = new ArrayList<Integer[]>();
            for (int i = 0; i < nodes; i++) {
                for (int j = i + 1; j < nodes; j++) {
                    edges.add(new Integer[]{i, j});
                    if (directed) {
                        edges.add(new Integer[]{j, i});
                    }
                }
            }
            return new SparseGraph(directed, intSet(nodes), edges);
        }
    }

    /** Constructs cycle graph with n vertices */
    public static class CycleGraphBuilder extends GraphBuilder.Support<Integer> {
        public CycleGraphBuilder() {}
        public CycleGraphBuilder(boolean directed, int nodes) { super(directed, nodes); }
        public Graph<Integer> createGraph() {
            if (nodes < 0) {
                throw new IllegalArgumentException("Numbers must be nonnegative! n=" + nodes);
            }
            return new SparseGraph(directed, intSet(nodes),
                    new AbstractList<Integer[]>() {
                        @Override
                        public Integer[] get(int index) {
                            return new Integer[]{index, (index + 1) % nodes};
                        }

                        @Override
                        public int size() {
                            return nodes;
                        }
                    });
        }
    }

    /** Constructs star graph with n vertices. 
     * All vertices are connected to a central hub. */
    public static class StarGraphBuilder extends GraphBuilder.Support<Integer> {
        public StarGraphBuilder() {}
        public StarGraphBuilder(boolean directed, int nodes) { super(directed, nodes); }
        public Graph<Integer> createGraph() {
            if (nodes < 0) {
                throw new IllegalArgumentException("Numbers must be nonnegative! n=" + nodes);
            }
            return new SparseGraph(directed, intSet(nodes),
                    new AbstractList<Integer[]>() {
                        @Override
                        public Integer[] get(int index) {
                            return new Integer[]{0, index + 1};
                        }

                        @Override
                        public int size() {
                            return nodes == 0 ? 0 : nodes - 1;
                        }
                    });
        }
    }

    /** 
     * Constructs wheel graph with n vertices. 
     * All vertices are connected to a central hub, and all non-central
     * vertices connected in a cyclic fashion. 
     */
    public static class WheelGraphBuilder extends GraphBuilder.Support<Integer> {
        public WheelGraphBuilder() {}
        public WheelGraphBuilder(boolean directed, int nodes) { super(directed, nodes); }
        public Graph<Integer> createGraph() {
            if (nodes < 0) {
                throw new IllegalArgumentException("Numbers must be nonnegative! n=" + nodes);
            }
            ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
            for (int i = 1; i < nodes; i++) {
                edges.add(new Integer[]{0, i});
            }
            for (int i = 1; i < nodes - 1; i++) {
                edges.add(new Integer[]{i, i + 1});
                if (directed) {
                    edges.add(new Integer[]{i + 1, i});
                }
            }
            edges.add(new Integer[]{nodes - 1, 1});
            if (directed) {
                edges.add(new Integer[]{1, nodes - 1});
            }
            return new SparseGraph(directed, intSet(nodes), edges);
        }
    }

    //
    // UTILITY METHODS
    //
    
    /**
     * Returns abstract list of integers 0,...,n-1
     */
    public static Set<Integer> intSet(final int n) {
        Set<Integer> res = new HashSet<Integer>();
        for (int i = 0; i < n; i++) {
            res.add(i);
        }
        return res;
    }

}
