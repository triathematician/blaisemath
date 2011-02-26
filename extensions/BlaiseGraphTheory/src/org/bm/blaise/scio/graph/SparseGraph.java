/*
 * SparseGraph.java
 * Created May 21, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Provides an implementation of a graph that is backed by a map associating
 * each vertex to its adjacent vertices. This is useful for graphs with a large
 * number of vertices and a relatively small number of edges.
 *
 * If the graph is undirected, it is assumed that adjacencies are stored pointing
 * in both directions along each edge.
 *
 * The graph's nodes and connections are immutable; they cannot change after construction.
 *
 * @param <V> the type of the nodes
 *
 * @author Elisha Peterson
 */
public class SparseGraph<V> implements Graph<V> {

    /** Whether graph is directed or not. */
    private final boolean directed;
    /** The adjacencies in the graph */
    private final Map<V, Set<V>> adjacencies;

    /** Do not permit instantiation */
    private SparseGraph(boolean directed, Map<V, Set<V>> adjacencies) {
        this.directed = directed;
        this.adjacencies = adjacencies;
    }

    @Override
    public String toString() {
        return adjacencies.toString();
    }

    /**
     * Factory method to return an immutable instance of a sparse graph. Adds all nodes and edges. If an edge
     * is specified by a node not in the set of nodes, that node is included in the resulting graph's nodes.
     * @param directed whether graph is directed
     * @param nodes the nodes in the graph
     * @param edges the edges in the graph, as node pairs; each must have a 0 element and a 1 element
     */
    public static <V> SparseGraph<V> getInstance(boolean directed, Iterable<V> nodes, Iterable<V[]> edges) {
        Map<V,Set<V>> adj = new TreeMap<V,Set<V>>();
        for (V v : nodes)
            adj.put(v, new TreeSet<V>());
        for (V[] e : edges) {
            if (!adj.containsKey(e[0]))
                adj.put(e[0], new TreeSet<V>());
            if (!adj.containsKey(e[1]))
                adj.put(e[1], new TreeSet<V>());
            adj.get(e[0]).add(e[1]);
            if (!directed)
                adj.get(e[1]).add(e[0]);
        }
        return new SparseGraph<V>(directed, adj);
    }

    public int order() {
        return adjacencies.size();
    }

    public List<V> nodes() {
        return new ArrayList<V>(adjacencies.keySet());
    }

    public boolean contains(V x) {
        return adjacencies.containsKey(x);
    }

    public boolean isDirected() { 
        return directed;

    }
    public boolean adjacent(V x, V y) {
        return adjacencies.containsKey(x) && adjacencies.get(x).contains(y);
    }

    public int degree(V x) { 
        if (!adjacencies.containsKey(x)) return 0;
        Set<V> nbhd = adjacencies.get(x);
        return (!directed && nbhd.contains(x)) ? nbhd.size()+1 : nbhd.size();
    }

    public Set<V> neighbors(V x) {
        Set<V> adj = adjacencies.get(x);
        if (adj == null)
            return Collections.emptySet();
        else
            return adj;
    }

    public int edgeNumber() {
        int total = 0;
        for (Entry<V, Set<V>> en : adjacencies.entrySet()) {
            total += en.getValue().size()
                    + (!directed && en.getValue().contains(en.getKey()) ? 1 : 0);
        }
        return directed ? total : total / 2;
    }

}
