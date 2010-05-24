/*
 * SparseGraph.java
 * Created May 21, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Provides an implementation of a <i>sparse graph</i> backed by lists of
 * adjacencies of vertices.
 *
 * @param <V> the object type of the nodes
 *
 * @author Elisha Peterson
 */
public class SparseGraph<V> implements Graph<V> {

    /** Whether graph is directed or not. */
    final boolean directed;
    /** The nodes in the graph */
    private ArrayList<V> nodes;
    /** The adjacencies in the graph */
    private HashMap<V, ArrayList<V>> adjacencies;

    private SparseGraph(boolean directed) { this.directed = directed; nodes = new ArrayList<V>(); adjacencies = new HashMap<V, ArrayList<V>>(); }

    /**
     * Factory method to return an immutable instance of a sparse graph. Adds all nodes and edges. If an edge
     * is specified by a node not in the set of nodes, that node is included in the resulting graph's nodes.
     * @param directed whether graph is directed
     * @param nodes the nodes in the graph
     * @param edges the edges in the graph, as node pairs; each must have a 0 element and a 1 element
     */
    public static <V> SparseGraph<V> createSparseGraph(boolean directed, Iterable<V> nodes, Iterable<V[]> edges) {
        SparseGraph<V> g = new SparseGraph<V>(directed);
        for (V v : nodes)
            g.nodes.add(v);
        for (V[] e : edges) {
            if (!g.nodes.contains(e[0])) g.nodes.add(e[0]);
            if (!g.nodes.contains(e[1])) g.nodes.add(e[1]);
            if (!g.adjacencies.containsKey(e[0]))
                g.adjacencies.put(e[0], new ArrayList<V>());
            g.adjacencies.get(e[0]).add(e[1]);
            if (!directed) {
                if (!g.adjacencies.containsKey(e[1]))
                    g.adjacencies.put(e[1], new ArrayList<V>());
                g.adjacencies.get(e[1]).add(e[0]);
            }
        }
        return g;
    }

    public int order() { return nodes.size(); }
    public Collection<V> nodes() { return Collections.unmodifiableList(nodes); }
    public boolean contains(V x) { return nodes.contains(x); }
    
    public boolean adjacent(V x, V y) { return adjacencies.containsKey(x) && adjacencies.get(x).contains(y); }
    public int degree(V x) { return adjacencies.containsKey(x) ? adjacencies.get(x).size() : 0; }
    public List<V> neighbors(V x) { return adjacencies.containsKey(x) ? Collections.unmodifiableList(adjacencies.get(x)) : (List<V>) Collections.emptyList(); }
    public int size() {
        int total = 0;
        for (Entry<V, ArrayList<V>> en : adjacencies.entrySet())
            total += en.getValue().size();
        return directed ? total : total / 2;
    }

}
