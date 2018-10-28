/**
 * OptimizedGraph.java
 * Created Aug 18, 2012
 */

package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.util.Edge;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.Immutable;

/**
 * <p>
 *   A storage-optimized graph component that caches degrees and divides nodes into
 *   <i>isolates</i>, <i>leaf nodes</i>, <i>connector nodes</i> (degree 2), and
 *   <i>core nodes</i> (degree &gt; 2).
 *   This maximizes speed for algorithms that make large numbers of calls to
 *   graph API methods.
 * </p>
 * @param <V> graph node type
 * 
 * @author elisha
 */
@Immutable
public final class OptimizedGraph<V> implements Graph<V> {

    /** Base graph */
    private final SparseGraph<V> base;
    
    /** Degree cache */
    private final Map<V, Integer> degrees = Maps.newHashMap();
    /** Isolate nodes (deg = 0) */
    private final Set<V> isolates = Sets.newHashSet();
    /** Leaf nodes (deg = 1) */
    private final Set<V> leafNodes = Sets.newHashSet();
    /** Connector nodes (deg = 2) */
    private final Set<V> connectorNodes = Sets.newHashSet();
    /** Non-leaf nodes (deg >= 3) */
    private final Set<V> coreNodes = Sets.newHashSet();
    /** General objects adjacent to each node */
    private final SetMultimap<V, V> neighbors = HashMultimap.create();
    /**
     * Leaf objects adjacent to each node. Values consist of objects that
     * have degree 1 ONLY.
     */
    private final SetMultimap<V, V> adjLeaves = HashMultimap.create();


    //
    // CONSTRUCTORS
    //

    /**
     * Construct graph with specific nodes and edges
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     */
    public OptimizedGraph(boolean directed, Collection<V> nodes, Iterable<Edge<V>> edges) {
        base = SparseGraph.createFromEdges(directed, nodes, edges);
        initCachedElements();
    }

    private void initCachedElements() {
        for (V v : base.nodes) {
            int deg = base.degree(v);
            degrees.put(v, deg);
            switch (deg) {
                case 0: 
                    isolates.add(v);
                    break;
                case 1:
                    leafNodes.add(v); 
                    break;
                case 2: 
                    connectorNodes.add(v); 
                    break;
                default: 
                    coreNodes.add(v);
                    break;
            }
            neighbors.putAll(v, base.neighbors(v));
        }
        for (V v : base.nodes) {
            for (V y : neighbors.get(v)) {
                Integer get = degrees.get(y);
                checkState(get != null, "Node " + y + " (neighbor of " + v + ") was not found in provided node set");
                if (degrees.get(y) == 1) {
                    adjLeaves.get(v).add(y);
                }
            }
        }
    }

    //region PROPERTIES
    //
    // PROPERTIES
    //

    /**
     * Nodes with deg 0
     * @return nodes
     */
    public Set<V> getIsolates() {
        return Collections.unmodifiableSet(isolates);
    }

    /**
     * Nodes with deg 1
     * @return nodes
     */
    public Set<V> getLeafNodes() {
        return Collections.unmodifiableSet(leafNodes);
    }

    /**
     * Nodes with deg 2
     * @return nodes
     */
    public Set<V> getConnectorNodes() {
        return Collections.unmodifiableSet(connectorNodes);
    }

    /**
     * Nodes with deg &gt;= 3
     * @return nodes
     */
    public Set<V> getCoreNodes() {
        return Collections.unmodifiableSet(coreNodes);
    }

    /**
     * Get copy of neighbors.
     * @return neighbors
     */
    public Multimap<V, V> getNeighbors() {
        return Multimaps.unmodifiableSetMultimap(neighbors);
    }

    //endregion

    /**
     * Return the node adjacent to a leaf
     * @param leaf leaf to check
     * @return adjacent node
     * @throws IllegalArgumentException if node is not a leaf
     */
    public V getNeighborOfLeaf(V leaf) {
        checkArgument(leafNodes.contains(leaf));
        V res = Iterables.getFirst(neighbors.get(leaf), null);
        checkState(res != null);
        return res;
    }
    
    /**
     * Return leaf nodes adjacent to specified node
     * @param v node to check
     * @return leaf nodes
     */
    public Set<V> getLeavesAdjacentTo(V v) {
        return adjLeaves.get(v);
    }


    //
    // OVERRIDES
    //

    @Override
    public boolean adjacent(V x, V y) {
        return neighbors.containsEntry(x, y);
    }

    @Override
    public Set<V> neighbors(V x) {
        return neighbors.get(x);
    }

    @Override
    public int degree(V x) {
        return degrees.get(x);
    }

    //<editor-fold defaultstate="collapsed" desc="DELEGATES">
    //
    // DELEGATES
    //

    @Override
    public boolean isDirected() {
        return base.isDirected();
    }

    @Override
    public int nodeCount() {
        return base.nodeCount();
    }

    @Override
    public Set<V> nodes() {
        return base.nodes();
    }

    @Override
    public boolean contains(V x) {
        return base.contains(x);
    }

    @Override
    public int edgeCount() {
        return base.edgeCount();
    }

    @Override
    public Set<Edge<V>> edges() {
        return base.edges();
    }

    @Override
    public Iterable<Edge<V>> edgesAdjacentTo(V x) {
        return base.edgesAdjacentTo(x);
    }

    @Override
    public int outDegree(V x) {
        return base.outDegree(x);
    }

    @Override
    public Set<V> outNeighbors(V x) {
        return base.outNeighbors(x);
    }

    @Override
    public int inDegree(V x) {
        return base.inDegree(x);
    }

    @Override
    public Set<V> inNeighbors(V x) {
        return base.inNeighbors(x);
    }
    
    // </editor-fold>



}
