package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import com.google.common.collect.*;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * A storage-optimized graph component that caches degrees and divides nodes into isolates, leaf nodes, connector nodes
 * (degree 2), and core nodes (degree more than 2). This maximizes speed for algorithms that make large numbers of calls to
 * graph API methods.
 *
 * @param <N> graph node type
 * 
 * @author Elisha Peterson
 */
public final class OptimizedGraph<N> implements Graph<N> {

    /** Base graph */
    private final Graph<N> base;
    
    /** Degree cache */
    private final Map<N, Integer> degrees = Maps.newHashMap();
    /** Isolate nodes (deg = 0) */
    private final Set<N> isolates = Sets.newHashSet();
    /** Leaf nodes (deg = 1) */
    private final Set<N> leafNodes = Sets.newHashSet();
    /** Connector nodes (deg = 2) */
    private final Set<N> connectorNodes = Sets.newHashSet();
    /** Non-leaf nodes (deg >= 3) */
    private final Set<N> coreNodes = Sets.newHashSet();
    /** General objects adjacent to each node */
    private final SetMultimap<N, N> neighbors = HashMultimap.create();
    /**
     * Leaf objects adjacent to each node. Values consist of objects that
     * have degree 1 ONLY.
     */
    private final SetMultimap<N, N> adjLeaves = HashMultimap.create();

    /**
     * Construct optimized graph version of the given graph.
     * @param graph graph to optimize
     */
    public OptimizedGraph(Graph<N> graph) {
        this.base = graph;
        initCachedElements();
    }

    /**
     * Construct optimized graph with specific nodes and edges.
     * @param directed whether graph is directed
     * @param nodes nodes in the graph
     * @param edges edges in the graph, as ordered node pairs; each must have a 0 element and a 1 element
     */
    public OptimizedGraph(boolean directed, Collection<N> nodes, Iterable<EndpointPair<N>> edges) {
        base = GraphUtils.createFromEdges(directed, nodes, edges);
        initCachedElements();
    }

    //region INITIALIZATION

    /** Initializes set of pre-computed elements. */
    private void initCachedElements() {
        for (N n : base.nodes()) {
            int deg = base.degree(n);
            degrees.put(n, deg);
            switch (deg) {
                case 0:
                    isolates.add(n);
                    break;
                case 1:
                    leafNodes.add(n);
                    break;
                case 2:
                    connectorNodes.add(n);
                    break;
                default:
                    coreNodes.add(n);
                    break;
            }
            neighbors.putAll(n, base.adjacentNodes(n));
        }
        for (N n : base.nodes()) {
            for (N y : neighbors.get(n)) {
                Integer get = degrees.get(y);
                checkState(get != null, "Node " + y + " (neighbor of " + n + ") was not found in provided node set");
                if (degrees.get(y) == 1) {
                    adjLeaves.get(n).add(y);
                }
            }
        }
    }

    //endregion

    //region PROPERTIES

    /**
     * Nodes with deg 0
     * @return nodes
     */
    public Set<N> isolates() {
        return Collections.unmodifiableSet(isolates);
    }

    /**
     * Nodes with deg 1
     * @return nodes
     */
    public Set<N> leafNodes() {
        return Collections.unmodifiableSet(leafNodes);
    }

    /**
     * Nodes with deg 2
     * @return nodes
     */
    public Set<N> connectorNodes() {
        return Collections.unmodifiableSet(connectorNodes);
    }

    /**
     * Nodes with deg &gt;= 3
     * @return nodes
     */
    public Set<N> coreNodes() {
        return Collections.unmodifiableSet(coreNodes);
    }

    /**
     * Get copy of neighbors.
     * @return neighbors
     */
    public Multimap<N, N> neighborMap() {
        return Multimaps.unmodifiableSetMultimap(neighbors);
    }

    //endregion

    //region OVERRIDES

    @Override
    public Set<N> nodes() {
        return base.nodes();
    }

    @Override
    public Set<EndpointPair<N>> edges() {
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
    public ElementOrder<N> nodeOrder() {
        return base.nodeOrder();
    }

    @Override
    public Set<N> adjacentNodes(N node) {
        return neighbors.get(node);
    }

    @Override
    public int degree(N x) {
        return degrees.get(x);
    }

    @Override
    public int inDegree(N node) {
        return 0;
    }

    @Override
    public int outDegree(N node) {
        return 0;
    }

    @Override
    public Set<N> predecessors(N node) {
        return base.predecessors(node);
    }

    @Override
    public Set<N> successors(N node) {
        return base.successors(node);
    }

    @Override
    public Set<EndpointPair<N>> incidentEdges(N node) {
        return base.incidentEdges(node);
    }

    @Override
    public boolean hasEdgeConnecting(N x, N y) {
        return neighbors.containsEntry(x, y);
    }

    @Override
    public boolean hasEdgeConnecting(EndpointPair<N> pair) {
        return hasEdgeConnecting(pair.nodeU(), pair.nodeV());
    }

    //endregion

    //region ADDITIONAL QUERIES

    /**
     * Extract the core graph, consisting of only nodes with degree at least 2.
     * @return graph with isolates and leaves pruned
     */
    public Graph<N> core() {
        return Graphs.inducedSubgraph(base, Iterables.concat(coreNodes, connectorNodes));
    }

    /**
     * Return the node adjacent to a leaf
     * @param leaf leaf to check
     * @return adjacent node
     * @throws IllegalArgumentException if node is not a leaf
     */
    public N neighborOfLeaf(N leaf) {
        checkArgument(leafNodes.contains(leaf));
        N res = Iterables.getFirst(neighbors.get(leaf), null);
        checkState(res != null);
        return res;
    }

    /**
     * Return leaf nodes adjacent to specified node
     * @param n node to check
     * @return leaf nodes
     */
    public Set<N> leavesAdjacentTo(N n) {
        return adjLeaves.get(n);
    }

    //endregion

}
