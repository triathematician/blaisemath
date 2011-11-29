/*
 * MatrixGraph.java
 * Created May 21, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides an implementation of a graph that is backed by an adjacency matrix.
 * This is useful for graphs where the number of edges is on the order of n<sup>2</sup>,
 * where n is the number of nodes in the graph.
 *
 * @param <V> the type of the nodes
 * @see SparseGraph
 *
 * @author Elisha Peterson
 */
public class MatrixGraph<V> extends GraphSupport<V> {

    //
    // FACTORY METHODS
    //

    /**
     * Factory method to return an immutable instance of a matrix graph. Adds all nodes and edges. If an edge
     * is specified by a node not in the set of nodes, that node is included in the resulting graph's nodes.
     * @param directed whether graph is directed
     * @param nodes the nodes in the graph
     * @param edges the edges in the graph, as node pairs; each must have a 0 element and a 1 element
     */
    public static <V> MatrixGraph<V> getInstance(boolean directed, Iterable<V> nodes, Iterable<V[]> edges) {
        ArrayList<V> allNodes = new ArrayList<V>();
        for (V v : nodes) allNodes.add(v);
        for (V[] e : edges) {
            if (!allNodes.contains(e[0])) allNodes.add(e[0]);
            if (!allNodes.contains(e[1])) allNodes.add(e[1]);
        }
        MatrixGraph<V> g = new MatrixGraph<V>(allNodes, directed);
        int i1, i2;
        for (V[] e : edges) {
            i1 = allNodes.indexOf(e[0]);
            i2 = allNodes.indexOf(e[1]);
            g.mx[i1][i2] = 1;
            if (!directed) g.mx[i2][i1] = 1;
        }
        return g;
    }

    //
    // IMPLEMENTATION
    //

    /** Adjacency matrix. */
    protected final int[][] mx;
    /** Components */
    protected final Collection<Set<V>> components;

    private MatrixGraph(Iterable<V> nodes, boolean directed) {
        super(listOf(nodes), directed);
        int n = this.nodes.size();
        mx = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                mx[i][j] = 0;
        components = GraphUtils.components(this, this.nodes);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < mx.length; i++) {
            for (int j = 0; j < mx.length; j++)
                result.append(mx[i][j]).append(" ");
            result.append("\n");
        }
        return result.toString();
    }

    public Collection<Set<V>> components() {
        return components;
    }

    @Override
    public boolean adjacent(V x, V y) {
        return adjacentByIndex(nodes.indexOf(x), nodes.indexOf(y));
    }

    private boolean adjacentByIndex(int i1, int i2) {
        return i1 != -1 && i2 != -1 && mx[i1][i2] != 0;
    }

    @Override
    public int degree(V x) {
        if (!nodes.contains(x)) return 0;
        int ix = nodes.indexOf(x);
        int sum = 0;
        for (int i = 0; i < mx.length; i++)
            if (mx[ix][i] != 0)
                sum += (i==ix && !directed) ? 2 : 1;
        return sum;

    }

    public Set<V> neighbors(V x) {
        if (!nodes.contains(x)) return Collections.emptySet();
        int ix = nodes.indexOf(x);
        HashSet<V> result = new HashSet<V>();
        for (int i = 0; i < mx.length; i++)
            if (mx[ix][i] != 0) result.add(nodes.get(i));
        return result;

    }

    public int edgeCount() {
        int sum = 0;
        int n = mx.length;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (mx[i][j] != 0)
                    sum += (i==j && !directed) ? 2 : 1;
        return directed ? sum : sum/2;
    }


    //
    // STATIC UTILITIES
    //

    private static <V> List<V> listOf(Iterable<V> it) {
        if (it instanceof List)
            return (List) it;
        List<V> result = new ArrayList<V>();
        for (V v: it)
            result.add(v);
        return result;
    }
}
