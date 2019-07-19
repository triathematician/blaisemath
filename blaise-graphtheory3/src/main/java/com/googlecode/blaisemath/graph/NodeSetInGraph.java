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

import com.google.common.collect.Sets;
import com.google.common.graph.Graph;
import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;

import static java.util.Objects.requireNonNull;

/**
 * Encapsulate graph with a collection of nodes in the graph.
 *
 * @param <N> node type
 * 
 * @author Elisha Peterson
 */
public class NodeSetInGraph<N> {

    private final Set<N> nodes;
    private final Graph<N> graph;

    /**
     * Construct with just nodes. The graph will be null.
     * @param nodes nodes
     */
    public NodeSetInGraph(N... nodes) {
        requireNonNull(nodes);
        this.graph = null;
        this.nodes = Sets.newLinkedHashSet(Arrays.asList(nodes));
    }

    /**
     * Construct with a set of nodes and optional graph.
     * @param nodes nodes
     * @param graph graph
     */
    public NodeSetInGraph(Set<N> nodes, @Nullable Graph<N> graph) {
        requireNonNull(nodes);
        this.graph = graph;
        // allow for nodes not in graph, but remove them
        this.nodes = Sets.newLinkedHashSet(nodes);
        if (graph != null) {
            this.nodes.retainAll(graph.nodes());
        }
    }

    public Set<N> getNodes() {
        return nodes;
    }

    public @Nullable Graph<N> getGraph() {
        return graph;
    }
}
