package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import com.google.common.graph.Graph;

import java.util.Set;

/**
 * Returns a value associated with a subset of nodes in a graph.
 * @param <T> the type of value returned
 * 
 * @author Elisha Peterson
 */
public interface GraphSubsetMetric<T> {

    /**
     * Computes the value of the metric for the given graph and nodes.
     * @param <N> graph node type
     * @param graph the graph
     * @param nodes a collection of nodes in the graph
     * @return value of the metric
     * @throws IllegalArgumentException if the value cannot be computed for specified graph (e.g. graph is null, or
     *      graph is directed, but the metric only applies to undirected graphs)
     */
    <N> T getValue(Graph<N> graph, Set<N> nodes);
}
