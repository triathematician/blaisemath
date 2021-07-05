package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2021 Elisha Peterson
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
import com.google.common.graph.Graphs;

/**
 * Computes the clique count of a particular node,
 * i.e. the number of connections between edges in the neighborhood
 * of the node, not counting the edges adjacent to the node itself.
 * Current computation time is linear in the # of edges in the graph (node case),
 * and quadratic in the map case (linear in edges * linear in nodes).
 *
 * @author Elisha Peterson
 */
@SuppressWarnings("UnstableApiUsage")
public class CliqueCount extends AbstractGraphNodeMetric<Integer> {

    public CliqueCount() {
        super("Clique count");
    }
    
    @Override
    public <N> Integer apply(Graph<N> graph, N node) {
        return Graphs.inducedSubgraph(graph, graph.adjacentNodes(node)).edges().size();
    }
    
}
