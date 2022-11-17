package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2022 Elisha Peterson
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
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Ordering;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/**
 * Global metric describes the diameter of the graph, or the largest diameter of
 * one of its subcomponents.
 *
 * @author Elisha Peterson
 */
public class GraphDiameter extends AbstractGraphMetric<Integer> {

    public GraphDiameter() {
        super("Graph diameter", "Diameter of the graph (longest path between two nodes).", true);
    }

    @Override
    public Integer apply(Graph graph) {
        return applyTyped(graph);
    }
        
    private static <N> Integer applyTyped(Graph<N> graph) {
        if (graph.nodes().size() == 0) {
            return 0;
        }
        int maxLength = 0;
        Map<N, Integer> lengths = new HashMap<>();
        for (N node : graph.nodes()) {
            GraphUtils.breadthFirstSearch(graph, node, HashMultiset.create(), lengths, new ArrayDeque<>(), HashMultimap.create());
            maxLength = Math.max(maxLength, Ordering.natural().max(lengths.values()));
        }
        return maxLength;
    }
}
