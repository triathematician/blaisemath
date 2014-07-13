/*
 * SparseGraph.java
 * Created May 21, 2010
 */

package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.Edge.UndirectedEdge;
import java.util.HashSet;
import java.util.Map.Entry;

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

    /** Edges in graph (replicated for speed) */
    protected final Set<Edge<V>> edges = new LinkedHashSet<Edge<V>>();
    
    /**
     * Edges in graph, by vertex. Each vertex contains all edges adjacent to the
     * vertex, regardless of direction.
     */
    protected final SetMultimap<V,Edge<V>> edgeIndex = HashMultimap.create();   
    /**
     * The adjacencies in components of the graph. If directed, rows are sources
     * and columns are destinations. Each pair of vertices may have multiple edges.
     * If undirected, the set of edges is the same for both directions.
     */
    protected final Table<V, V, Set<Edge<V>>> edgeTable = HashBasedTable.create();    
    /** Information about the graph's components (replicated for speed) */
    protected GraphComponents<V> components;

    /**
     * Helper constructor for factory methods
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     */
    private SparseGraph(boolean directed, Iterable<V> nodes) {
        super(directed, nodes);
    }
    
    /**
     * Construct graph with specific nodes and edges
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     */
    public SparseGraph(boolean directed, Iterable<V> nodes, Iterable<Edge<V>> edges) {
        this(directed, nodes);
        for (Edge<V> e : edges) {
            addEdge(e.getNode1(), e.getNode2());
        }
        this.components = new GraphComponents(this, GraphUtils.components(edgeTable));
    }
    
    //
    // FACTORY METHODS
    //

    /**
     * Construct graph with specific nodes and edges
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     * @return graph
     */
    public static <V> SparseGraph<V> createFromArrayEdges(boolean directed, V[] nodes, Iterable<V[]> edges) {
        SparseGraph<V> res = new SparseGraph<V>(directed, Arrays.asList(nodes));
        for (V[] e : edges) {
            res.addEdge(e[0], e[1]);
        }
        res.components = new GraphComponents(res, GraphUtils.components(res.edgeTable));
        return res;
    }

    /**
     * Construct graph with specific nodes and edges
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     * @return graph
     */
    public static <V> SparseGraph<V> createFromArrayEdges(boolean directed, Iterable<V> nodes, Iterable<V[]> edges) {
        SparseGraph<V> res = new SparseGraph<V>(directed, nodes);
        for (V[] e : edges) {
            res.addEdge(e[0], e[1]);
        }
        res.components = new GraphComponents(res, GraphUtils.components(res.edgeTable));
        return res;
    }

    /**
     * Construct graph with a sparse adjacency representation.
     * @param directed if graph is directed
     * @param adjacencies map with adjacency info
     * @return graph
     */
    public static <V> SparseGraph<V> createFromAdjacencies(boolean directed, Multimap<V,V> adjacencies) {
        SparseGraph<V> res = new SparseGraph<V>(directed, GraphUtils.nodes(adjacencies));
        for (Entry<V,V> en : adjacencies.entries()) {
            res.addEdge(en.getKey(), en.getValue());
        }
        res.components = new GraphComponents(res, GraphUtils.components(res.edgeTable));
        return res;
    }
    
    //<editor-fold defaultstate="collapsed" desc="add/remove Edge helpers">

    protected final synchronized void addEdge(V x, V y) {
        Edge<V> edge = directed ? addDirectedEdge(x, y) : addUndirectedEdge(x, y);
        edges.add(edge);
        edgeIndex.put(x, edge);
        edgeIndex.put(y, edge);
    }
    
    protected Edge<V> addDirectedEdge(V x, V y) {
        if (!edgeTable.contains(x, y)) {
            edgeTable.put(x, y, new HashSet<Edge<V>>());
        }
        Edge<V> edge = new Edge<V>(x, y);
        edgeTable.get(x, y).add(edge);
        return edge;
    }
    
    protected Edge<V> addUndirectedEdge(V x, V y) {
        if (!edgeTable.contains(x, y)) {
            edgeTable.put(x, y, new HashSet<Edge<V>>());
        }
        if (!edgeTable.contains(y, x)) {
            edgeTable.put(y, x, new HashSet<Edge<V>>());
        }
        UndirectedEdge<V> edge = new UndirectedEdge<V>(x, y);
        edgeTable.get(x, y).add(edge);
        edgeTable.get(y, x).add(edge);
        return edge;
    }

    @Override
    public boolean adjacent(V x, V y) {
        if (edgeTable.contains(x, y) && !edgeTable.get(x, y).isEmpty()) {
            return true;
        }
        if (directed && edgeTable.contains(y, x) && !edgeTable.get(y, x).isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Remove edge between two vertices, if it exists
     * @param v1 first vertex
     * @param v2 second vertex
     * @return true if edge was found and removed
     */
    protected synchronized boolean removeEdge(V v1, V v2) {
        Edge<V> edge = directed ? new Edge<V>(v1, v2) : new UndirectedEdge<V>(v1, v2);
        if (edgeTable.contains(v1, v2)) {
            edgeTable.get(v1, v2).remove(edge);
        }
        if (!directed && edgeTable.contains(v2, v1)) {
            edgeTable.get(v2, v1).remove(edge);
        }
        edgeIndex.remove(v1, edge);
        edgeIndex.remove(v2, edge);
        return edges.remove(edge);
    }
    
    //</editor-fold>

    @Override
    public String toString() {
        return String.format("SparseGraph[%s,%d nodes,%d edges]", 
                directed?"directed":"undirected", nodeCount(), edgeCount());
    }
    

    public GraphComponents<V> getComponentInfo() {
        return components;
    }

    public Set<Edge<V>> edges() {
        return edges;
    }

    public Collection<? extends Edge<V>> edgesAdjacentTo(V x) {
        return edgeIndex.get(x);
    }


}
