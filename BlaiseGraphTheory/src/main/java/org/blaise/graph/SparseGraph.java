/*
 * SparseGraph.java
 * Created May 21, 2010
 */

package org.blaise.graph;

import com.google.common.collect.Multimap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.blaise.util.Edge;

/**
 * <p>
 *  General graph structure that maintains cached information to speed graph computations.
 *  Maintains a set of all edges in the graph, a map associating each vertex with its adjacent edges,
 *  and information about connected components. The graph cannot be changed once it is created.
 * </p>
 *
 * @param <V> the type of the nodes
 *
 * @author Elisha Peterson
 */
public class SparseGraph<V> extends GraphSupport<V> {

    /** The adjacencies in components of the graph */
//    protected final Table<V, V, LinkedHashSet<Edge<V>>> adjacencies = HashBasedTable.create()
    protected final Map<V,Map<V,Set<Edge<V>>>> adjacencies = new LinkedHashMap<V, Map<V,Set<Edge<V>>>>();
    /** Edges in graph (replicated for speed) */
    protected final Set<Edge<V>> edges = new LinkedHashSet<Edge<V>>();
    /** Information about the graph's components (replicated for speed) */
    protected final GraphComponents<V> components;

    /**
     * Construct graph with specific nodes and edges
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     */
    public SparseGraph(boolean directed, V[] nodes, Iterable<V[]> edges) {
        this(directed, Arrays.asList(nodes), edges);
    }

    /**
     * Construct graph with specific nodes and edges
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     */
    public SparseGraph(boolean directed, Collection<V> nodes, Iterable<V[]> edges) {
        super(nodes, directed);
        for (V[] e : edges) {
            addEdge(e[0], e[1]);
        }
        this.components = new GraphComponents(this, GraphUtils.componentsEdgeMap(adjacencies));
    }

    /**
     * Construct graph with a sparse adjacency representation.
     * @param directed if graph is directed
     * @param adjacencies map with adjacency info
     */
    public SparseGraph(boolean directed, Multimap<V,V> adjacencies) {
        super(GraphUtils.nodes(adjacencies), directed);
        for (V x : adjacencies.keySet()) {
            for (V y : adjacencies.get(x)) {
                addEdge(x, y);
            }
        }
        this.components = new GraphComponents(this, GraphUtils.components(adjacencies));
    }

    protected synchronized void addEdge(V x, V y) {
        Map<V, Set<Edge<V>>> getx = adjacencies.get(x);
        Map<V, Set<Edge<V>>> gety = adjacencies.get(y);

        // make sure it doesn't already exist
        if (directed && getx != null && getx.containsKey(y)) {
            Edge[] getxy = getx.get(y).toArray(new Edge[0]);
            if (getxy.length > 0 && getxy[0].getNode1() == x) {
                return;
            }
            if (getxy.length > 1 && getxy[1].getNode1() == x) {
                return;
            }
        } else if (!directed && ( (getx != null && getx.containsKey(y)) || (gety != null && gety.containsKey(x)) ) ) {
            return;
        }

        // once checks have been made, create the edge and add it
        Edge<V> edge = new Edge<V>(x,y);
        if (getx == null) {
            adjacencies.put(x, getx = new HashMap<V,Set<Edge<V>>>());
        }
        if (getx.get(y) == null) {
            getx.put(y, new HashSet<Edge<V>>());
        }
        getx.get(y).add(edge);
        if (gety == null) {
            adjacencies.put(y, gety = new HashMap<V,Set<Edge<V>>>());
        }
        if (gety.get(x) == null) {
            gety.put(x, new HashSet<Edge<V>>());
        }
        gety.get(x).add(edge);
        edges.add(edge);
    }

    @Override
    public boolean adjacent(V x, V y) {
        Map<V, Set<Edge<V>>> map = adjacencies.get(x);
        if (map == null) {
            return false;
        }
        Set<Edge<V>> set = map.get(y);
        return set != null && !set.isEmpty();
    }

    /**
     * Remove edge between two vertices, if it exists
     * @param v1 first vertex
     * @param v2 second vertex
     * @return true if edge was found and removed
     */
    protected synchronized boolean removeEdge(V v1, V v2) {
        Map<V, Set<Edge<V>>> map = adjacencies.get(v1);
        Set<Edge<V>> remove = new HashSet<Edge<V>>();
        boolean found = false;
        if (map != null) {
            Set<Edge<V>> set = map.get(v2);
            if (set != null && !set.isEmpty()) {
                remove.addAll(set);
                map.remove(v2);
                found = true;
            } else if (set != null) {
                map.remove(v2);
            }
        }
        map = adjacencies.get(v2);
        if (map != null) {
            Set<Edge<V>> set = map.get(v1);
            if (set != null && !set.isEmpty()) {
                remove.addAll(set);
                map.remove(v1);
                found = true;
            } else if (set != null) {
                map.remove(v1);
            }
        }
        edges.removeAll(remove);
        return found;
    }

    public GraphComponents<V> getComponentInfo() {
        return components;
    }

    @Override
    public String toString() {
        return String.format("SparseGraph[%s,%d nodes,%d edges]", directed?"directed":"undirected", nodeCount(), edgeCount());
    }

    public Set<Edge<V>> edges() {
        return edges;
    }

    public Collection<? extends Edge<V>> edgesAdjacentTo(V x) {
        Map<V, Set<Edge<V>>> getx = adjacencies.get(x);
        if (getx == null) {
            return Collections.EMPTY_SET;
        }
        Set<Edge<V>> res = new HashSet<Edge<V>>();
        for (Set<Edge<V>> se : getx.values()) {
            res.addAll(se);
        }
        return res;
    }


}
