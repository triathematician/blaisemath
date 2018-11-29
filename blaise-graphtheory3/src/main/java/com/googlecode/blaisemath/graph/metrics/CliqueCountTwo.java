package com.googlecode.blaisemath.graph.metrics;

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

import java.util.Set;

import com.google.common.graph.Graph;
import com.google.common.graph.Graphs;
import com.googlecode.blaisemath.graph.GraphUtils;

/**
 * Computes the clique count of a particular vertex,
 * i.e. the number of connections between edges in the neighborhood
 * of the vertex, not counting the edges adjacent to the vertex itself.
 * Current computation time is linear in the # of edges in the graph (vertex case),
 * and quadratic in the map case (linear in edges * linear in vertices).
 *
 * @author Elisha Peterson
 */
public class CliqueCountTwo extends AbstractGraphNodeMetric<Integer> {

    public CliqueCountTwo() {
        super("Clique count (radius 2)");
    }
    
    @Override
    public <V> Integer apply(Graph<V> graph, V vertex) {
        Set<V> neighborhood = GraphUtils.neighborhood(graph, vertex, 2);
        return Graphs.inducedSubgraph(graph, neighborhood).edges().size() - neighborhood.size() + 1;
    }
    
}
