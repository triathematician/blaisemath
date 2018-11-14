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
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.errorprone.annotations.Immutable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * A storage-optimized graph component that caches degrees and divides nodes into isolates, leaf nodes, connector nodes
 * (degree 2), and core nodes (degree > 2). This maximizes speed for algorithms that make large numbers of calls to
 * graph API methods.
 *
 * @param <V> graph node type
 * 
 * @author elisha
 */
@Immutable
public final class OptimizedGraph<V> implements Graph<V> {

    /** Base graph */
    private final Graph<V> base;
    
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


    /**
     * Construct graph with specific nodes and edges
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     */
    public OptimizedGraph(boolean directed, Collection<V> nodes, Iterable<EndpointPair<V>> edges) {
        base = GraphUtils.createFromEdges(directed, nodes, edges);
        initCachedElements();
    }

    //region PROPERTIES

    /**
     * Nodes with deg 0
     * @return nodes
     */
    public Set<V> isolates() {
        return Collections.unmodifiableSet(isolates);
    }

    /**
     * Nodes with deg 1
     * @return nodes
     */
    public Set<V> leafNodes() {
        return Collections.unmodifiableSet(leafNodes);
    }

    /**
     * Nodes with deg 2
     * @return nodes
     */
    public Set<V> connectorNodes() {
        return Collections.unmodifiableSet(connectorNodes);
    }

    /**
     * Nodes with deg &gt;= 3
     * @return nodes
     */
    public Set<V> coreNodes() {
        return Collections.unmodifiableSet(coreNodes);
    }

    /**
     * Get copy of neighbors.
     * @return neighbors
     */
    public Multimap<V, V> neighborMap() {
        return Multimaps.unmodifiableSetMultimap(neighbors);
    }

    //endregion

    //region OVERRIDES

    @Override
    public Set<V> nodes() {
        return base.nodes();
    }

    @Override
    public Set<EndpointPair<V>> edges() {
        return base.edges();
    }

    @Override
    public boolean isDirected() {
        return base.isDirected();
    }

    @Override
    public boolean allowsSelfLoops() {
        return base.allowsSelfLoops();
    }

    @Override
    public ElementOrder<V> nodeOrder() {
        return base.nodeOrder();
    }

    @Override
    public Set<V> adjacentNodes(V node) {
        return neighbors.get(node);
    }

    @Override
    public int degree(V x) {
        return degrees.get(x);
    }

    @Override
    public int inDegree(V node) {
        return 0;
    }

    @Override
    public int outDegree(V node) {
        return 0;
    }

    @Override
    public Set<V> predecessors(V node) {
        return base.predecessors(node);
    }

    @Override
    public Set<V> successors(V node) {
        return base.successors(node);
    }

    @Override
    public Set<EndpointPair<V>> incidentEdges(V node) {
        return base.incidentEdges(node);
    }

    @Override
    public boolean hasEdgeConnecting(V x, V y) {
        return neighbors.containsEntry(x, y);
    }

    //endregion

    /**
     * Return the node adjacent to a leaf
     * @param leaf leaf to check
     * @return adjacent node
     * @throws IllegalArgumentException if node is not a leaf
     */
    public V neighborOfLeaf(V leaf) {
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
    public Set<V> leavesAdjacentTo(V v) {
        return adjLeaves.get(v);
    }

    private void initCachedElements() {
        for (V v : base.nodes()) {
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
            neighbors.putAll(v, base.adjacentNodes(v));
        }
        for (V v : base.nodes()) {
            for (V y : neighbors.get(v)) {
                Integer get = degrees.get(y);
                checkState(get != null, "Node " + y + " (neighbor of " + v + ") was not found in provided node set");
                if (degrees.get(y) == 1) {
                    adjLeaves.get(v).add(y);
                }
            }
        }
    }

}
