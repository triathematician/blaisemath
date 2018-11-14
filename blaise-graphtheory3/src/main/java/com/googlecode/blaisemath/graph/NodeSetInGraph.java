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

import com.google.common.collect.Sets;
import com.google.common.graph.Graph;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Data structure describing a collection of nodes in a graph. This is a useful way to pass
 * a reference to both the collection of nodes and the graph.
 * @param <E> node type
 * 
 * @author petereb1
 */
public class NodeSetInGraph<E> {

    private final Set<E> nodes;
    private final Graph<E> graph;

    public NodeSetInGraph(Set<E> nodes, @Nullable Graph<E> graph) {
        checkNotNull(nodes);
        this.graph = graph;
        // allow for nodes not in graph, but remove them
        this.nodes = Sets.newLinkedHashSet(nodes);
        if (graph != null) {
            this.nodes.retainAll(graph.nodes());
        }
    }
    
    /**
     * Factory method for creating a node set.
     * @param <E> node type
     * @param nodes nodes
     * @return node set data structure
     */
    public static <E> NodeSetInGraph<E> create(E... nodes) {
        return new NodeSetInGraph<>(Sets.newHashSet(nodes), null);
    }

    public Set<E> getNodes() {
        return nodes;
    }

    @Nullable 
    public Graph<E> getGraph() {
        return graph;
    }
}
