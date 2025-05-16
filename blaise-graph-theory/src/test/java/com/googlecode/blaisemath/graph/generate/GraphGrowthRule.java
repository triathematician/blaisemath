package com.googlecode.blaisemath.graph.generate;

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

import com.google.common.annotations.Beta;
import com.google.common.graph.Graph;

import java.util.Set;

/**
 * A "growth rule" that operates on a subset of nodes in a graph and produces a new set of nodes.
 *
 * @author Elisha Peterson
 */
@SuppressWarnings("UnstableApiUsage")
@Beta
interface GraphGrowthRule {

    /**
     * Name of rule for display.
     * @return name
     */
    String getName();

    /**
     * "Grows" a subset of nodes on a graph according to this rule. May also shrink or modify the set of nodes in
     * some other way.
     * @param <N> node type
     * @param graph the entire graph
     * @param seed the seed input set, a subset of nodes of the graph
     * @return larger subset of the graph
     */
    <N> Set<N> grow(Graph<N> graph, Set<N> seed);

}
