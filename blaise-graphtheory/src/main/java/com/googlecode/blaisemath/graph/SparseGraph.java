/*
 * SparseGraph.java
 * Created May 21, 2010
 */

package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import com.googlecode.blaisemath.util.Edge;
import com.googlecode.blaisemath.util.Edge.UndirectedEdge;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.concurrent.Immutable;

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
@Immutable
public final class SparseGraph<V> extends GraphSupport<V> {

    /** Edges in graph (replicated for speed) */
    private final Set<Edge<V>> edges = new LinkedHashSet<Edge<V>>();
    
    /**
     * Edges in graph, by vertex. Each vertex contains all edges adjacent to the
     * vertex, regardless of direction.
     */
    private final SetMultimap<V,Edge<V>> edgeIndex = HashMultimap.create();   
    /**
     * The adjacencies in components of the graph. If directed, rows are sources
     * and columns are destinations. Each pair of vertices may have multiple edges.
     * If undirected, the set of edges is the same for both directions.
     */
    private final Table<V, V, Set<Edge<V>>> edgeTable = HashBasedTable.create();    
    /** Information about the graph's components (replicated for speed) */
    private GraphComponents<V> components;

    /**
     * Helper constructor for factory methods
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     */
    private SparseGraph(boolean directed, Iterable<V> nodes) {
        super(directed, nodes);
    }
    
    /**
     * Construct graph with specific nodes and edges.
     * @param <V> graph node type
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     * @return created graph
     */
    public static <V> SparseGraph<V> createFromEdges(boolean directed, Iterable<V> nodes, Iterable<Edge<V>> edges) {
        SparseGraph<V> res = new SparseGraph<V>(directed, Lists.newArrayList(nodes));
        for (Edge<V> e : edges) {
            res.addEdge(e.getNode1(), e.getNode2());
        }
        res.components = new GraphComponents(res, GraphUtils.components(res.edgeTable));
        return res;
    }
    
    //
    // FACTORY METHODS
    //

    /**
     * Construct graph with specific nodes and edges
     * @param <V> graph node type
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
     * @param <V> graph node type
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
     * @param <V> graph node type
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
    
    //<editor-fold defaultstate="collapsed" desc="edge construction helpers">

    /** Invoke from initializer only */
    private void addEdge(V x, V y) {
        Edge<V> edge = directed ? addDirectedEdge(x, y) : addUndirectedEdge(x, y);
        edges.add(edge);
        edgeIndex.put(x, edge);
        edgeIndex.put(y, edge);
    }
    
    /** Invoke from initializer only */
    private Edge<V> addDirectedEdge(V x, V y) {
        if (!edgeTable.contains(x, y)) {
            edgeTable.put(x, y, new HashSet<Edge<V>>());
        }
        Edge<V> edge = new Edge<V>(x, y);
        edgeTable.get(x, y).add(edge);
        return edge;
    }
    
    /** Invoke from initializer only */
    private Edge<V> addUndirectedEdge(V x, V y) {
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
    
    // </editor-fold>

    @Override
    public String toString() {
        return String.format("SparseGraph[%s,%d nodes,%d edges]", 
                directed?"directed":"undirected", nodeCount(), edgeCount());
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
    

    public GraphComponents<V> getComponentInfo() {
        return components;
    }

    @Override
    public Set<Edge<V>> edges() {
        return Collections.unmodifiableSet(edges);
    }

    @Override
    public Collection<Edge<V>> edgesAdjacentTo(V x) {
        return Collections.unmodifiableSet(edgeIndex.get(x));
    }

}
