/*
 * MatrixGraph.java
 * Created May 21, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Contains an implementation of a graph that is backed by an adjacency matrix.
 *
 * @param <V> the object type for nodes in the graph
 *
 * @author Elisha Peterson
 */
public class MatrixGraph<V> implements Graph<V> {

    /** Whether graph is directed or not. */
    final boolean directed;
    /** The nodes in the graph */
    private ArrayList<V> nodes;
    /** Adjacency matrix. */
    int[][] mx;

    private MatrixGraph(boolean directed, Iterable<V> nodes) {
        this.directed = directed;
        nodes = new ArrayList<V>();
        for (V v : nodes) this.nodes.add(v);
        int n = this.nodes.size();
        mx = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                mx[i][j] = 0;
    }
    
    /**
     * Factory method to return an immutable instance of a sparse graph. Adds all nodes and edges. If an edge
     * is specified by a node not in the set of nodes, that node is included in the resulting graph's nodes.
     * @param directed whether graph is directed
     * @param nodes the nodes in the graph
     * @param edges the edges in the graph, as node pairs; each must have a 0 element and a 1 element
     */
    public static <V> MatrixGraph<V> createMatrixGraph(boolean directed, Iterable<V> nodes, Iterable<V[]> edges) {
        ArrayList<V> allNodes = new ArrayList<V>();
        for (V v : nodes) allNodes.add(v);
        for (V[] e : edges) {
            if (!allNodes.contains(e[0])) allNodes.add(e[0]);
            if (!allNodes.contains(e[1])) allNodes.add(e[1]);
        }
        MatrixGraph<V> g = new MatrixGraph<V>(directed, allNodes);
        for (V[] e : edges)
            g.mx[allNodes.indexOf(e[0])][allNodes.indexOf(e[1])] = 1;
        return g;
    }

    public int order() { return nodes.size(); }
    public Collection<V> nodes() { return Collections.unmodifiableList(nodes); }
    public boolean contains(V x) { return nodes.contains(x); }

    public boolean adjacent(V x, V y) { return adjacent(nodes.indexOf(x), nodes.indexOf(y)); }
    private boolean adjacent(int i1, int i2) { return i1 != -1 && i2 != -1 && mx[i1][i2] != 0; }
    public int degree(V x) {
        if (!nodes.contains(x)) return 0;
        int ix = nodes.indexOf(x);
        int sum = 0;
        for (int i = 0; i < mx.length; i++)
            if (mx[ix][i] != 0)
                sum++;
        return sum;

    }
    public List<V> neighbors(V x) {
        if (!nodes.contains(x)) return Collections.emptyList();
        int ix = nodes.indexOf(x);
        ArrayList<V> result = new ArrayList<V>();
        for (int i = 0; i < mx.length; i++)
            if (mx[ix][i] != 0) result.add(nodes.get(i));
        return result;
        
    }
    public int size() {
        int sum = 0;
        int n = mx.length;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (mx[i][j] != 0)
                    sum++;
        return directed ? sum : sum/2;
    }
}
