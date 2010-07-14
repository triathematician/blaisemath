/*
 * GraphFactory.java
 * Created Jul 14, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Provides methods for creating and returning graphs of certain types.
 *
 * @author Elisha Peterson
 */
public class GraphFactory {

    /**
     * Creates an instance of either a matrix-based graph or a sparse-based graph, depending
     * upon the number of edges and vertices
     * @param directed whether graph is directed
     * @param vertices collection of vertices
     * @param edges collection of edges as vertex-pairings
     * @return graph with specified edges
     */
    public static <V> Graph<V> getGraph(boolean directed, Collection<V> vertices, Collection<V[]> edges) {
        int n = vertices.size();
        int nEdges = edges.size();
        return (n <= 50 || (n<=100 && nEdges > 1000) )
                ? MatrixGraph.getInstance(directed, vertices, edges)
                : SparseGraph.getInstance(directed, vertices, edges);
    }

    /**
     * Returns graph with n vertices and no edges
     * @param n number of vertices
     * @param directed whether graph is directed
     * @return empty graph with specified number of vertices
     */
    public static Graph<Integer> getEmptyGraph(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getGraph(directed, intList(n), (Collection) Collections.emptyList());
    }

    /**
     * Returns graph with all possible edges.
     * @param n number of vertices
     * @param directed whether graph is directed
     * @return complete graph; if directed, result includes loops
     */
    public static Graph<Integer> getCompleteGraph(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        List<Integer[]> edges;
        if (!directed) {
            edges = new ArrayList<Integer[]>();
            for(int i = 0; i < n; i++)
                for (int j = i+1; j < n; j++)
            edges.add(new Integer[]{i, j});
        } else {
            edges = new AbstractList<Integer[]>() {
                @Override public Integer[] get(int index) { return new Integer[]{ index/n, index%n }; }
                @Override public int size() { return n*n; }
            };
        }
        return getGraph(directed, intList(n), edges);
    }

    /**
     * Returns graph with all edges connected in a loop.
     * @param n number of vertices
     * @param directed whether graph is directed
     * @return cycle graph
     */
    public static Graph<Integer> getCycleGraph(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getGraph(directed, intList(n),
            new AbstractList<Integer[]>() {
                @Override public Integer[] get(int index) { return new Integer[] { index, (index+1)%n }; }
                @Override public int size() { return n; }
            }
        );
    }

    /**
     * Returns graph with all vertices connected to a central hub.
     * @param n number of vertices
     * @return star graph (undirected)
     */
    public static Graph<Integer> getStarGraph(final int n) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getGraph(false, intList(n),
            new AbstractList<Integer[]>() {
                @Override public Integer[] get(int index) { return new Integer[] { 0, index+1 }; }
                @Override public int size() { return n==0 ? 0 : n-1; }
            }
        );
    }

    /**
     * Returns graph with all vertices connected to a central hub, and all other vertices
     * connected in a cyclic fashion.
     * @param n number of vertices
     * @return star graph (undirected)
     */
    public static Graph<Integer> getWheelGraph(final int n) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getGraph(false, intList(n),
            new AbstractList<Integer[]>() {
                @Override public Integer[] get(int index) {
                    return index < n-1
                            ? new Integer[] { 0, index+1 }
                         : index == 2*n-2
                             ? new Integer[] { n-1, 1}
                             : new Integer[] { index-n+1, index-n+2 };

                }
                @Override public int size() { return 2*n-1; }
            }
        );
    }

    //
    // UTILITY METHODS
    //

    /** Returns abstract list of integers 0,...,n-1 */
    static List<Integer> intList(final int n) {
        return new AbstractList<Integer>() {
            @Override public Integer get(int index) { return index; }
            @Override public int size() { return n; }
        };
    }

} // CLASS GraphFactory
