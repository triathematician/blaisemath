/*
 * SparseGraph.java
 * Created May 21, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * <p>
 *   Stores graph structure as a map associating each vertex to its neighboring vertices.
 *   Stores each component in a separate map for speed. If the graph is undirected,
 *   the adjacency map stores both directions. This implementation does not allow
 *   the graph to change once it has been constructed.
 * </p>
 *
 * @param <V> the type of the nodes
 *
 * @author Elisha Peterson
 */
public class SparseGraph<V> extends GraphSupport<V> {
    
    //
    // FACTORY METHODS
    //

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
    
    //
    // IMPLEMENTATION
    //

    /** The adjacencies in components of the graph */
    private final Map<V, Set<V>> adjacencies;
    /** Information about the graph's components */
    private final GraphComponents<V> components;

    /** Do not permit instantiation */
    private SparseGraph(boolean directed, Map<V, Set<V>> adjacencies) {
        super(GraphUtils.nodes(adjacencies), directed);
        this.adjacencies = adjacencies;
        this.components = new GraphComponents(this, GraphUtils.components(adjacencies));
    }

    @Override
    public String toString() {
        return adjacencies.toString();
    }
    
    @Override
    public boolean adjacent(V x, V y) {
        return adjacencies.containsKey(x) && adjacencies.get(x).contains(y);
    }

    @Override
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

    public int edgeCount() {
        int total = 0;
        for (Entry<V, Set<V>> en : adjacencies.entrySet()) {
            total += en.getValue().size()
                    + (!directed && en.getValue().contains(en.getKey()) ? 1 : 0);
        }
        return directed ? total : total / 2;
    }

}
