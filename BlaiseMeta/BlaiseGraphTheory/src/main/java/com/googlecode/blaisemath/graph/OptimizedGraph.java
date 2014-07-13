/**
 * OptimizedGraph.java
 * Created Aug 18, 2012
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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.googlecode.blaisemath.util.Edge;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *   A storage-optimized graph component that caches degrees and divides nodes into
 *   <i>isolates</i>, <i>leaf nodes</i>, <i>connector nodes</i> (degree 2), and
 *   <i>core nodes</i> (degree > 2).
 *   This maximizes speed for algorithms that make large numbers of calls to
 *   graph API methods.
 * </p>
 * @author elisha
 */
public class OptimizedGraph<V> extends SparseGraph<V> {

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
        super(directed, nodes, edges);
        initCachedElements();
    }

    private void initCachedElements() {
        for (V v : nodes) {
            int deg = super.degree(v);
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
            neighbors.putAll(v, super.neighbors(v));
        }
        for (V v : nodes) {
            for (V y : neighbors.get(v)) {
                Integer get = degrees.get(y);
                if (get == null) {
                    throw new IllegalStateException("Node " + y + " (neighbor of " + v + ") was not found in provided node set");
                }
                if (degrees.get(y) == 1) {
                    adjLeaves.get(v).add(y);
                }
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /**
     * Nodes with deg 0
     * @return nodes
     */
    public Set<V> getIsolates() {
        return isolates;
    }

    /**
     * Nodes with deg 1
     * @return nodes
     */
    public Set<V> getLeafNodes() {
        return leafNodes;
    }

    /**
     * Nodes with deg 2
     * @return nodes
     */
    public Set<V> getConnectorNodes() {
        return connectorNodes;
    }

    /**
     * Nodes with deg >= 3
     * @return nodes
     */
    public Set<V> getCoreNodes() {
        return coreNodes;
    }

    /**
     * Collection of neighbors
     * @return neighbors
     */
    public Multimap<V, V> getNeighbors() {
        return neighbors;
    }

    //</editor-fold>

    /**
     * Return leaf nodes adjacent to specified node
     * @param v node to check
     * @return leaf ndoes
     */
    public Set<V> getAdjacentLeafNodes(V v) {
        return adjLeaves.get(v);
    }


    //
    // OVERRIDES
    //

    @Override
    public boolean adjacent(V x, V y) {
        return neighbors.get(x).contains(y);
    }

    @Override
    public Set<V> neighbors(V x) {
        return neighbors.get(x);
    }

    @Override
    public int degree(V x) {
        return degrees.get(x);
    }



}
