package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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
 * Global metric describes the radius of the graph, or the largest diameter of
 * one of its subcomponents.
 *
 * @author Elisha Peterson
 */
public class GraphRadius extends AbstractGraphMetric<Integer> {

    public GraphRadius() {
        super("Graph radius", "Radius of the graph (minimum number r such that all nodes are within r edges of a particular node).", true);
    }

    @Override
    public Integer apply(Graph graph) {
        if (graph.nodes().isEmpty()) {
            return 0;
        }
        int minMaxLength = Integer.MAX_VALUE;
        Map<Object, Integer> lengths = new HashMap<>();
        for (Object node : graph.nodes()) {
            int maxLength = 0;
            GraphUtils.breadthFirstSearch(graph, node, HashMultiset.create(), lengths, new ArrayDeque<>(), HashMultimap.create());
            maxLength = Math.max(maxLength, Ordering.natural().max(lengths.values()));
            if (maxLength > 0) {
                minMaxLength = Math.min(maxLength, minMaxLength);
            }
        }
        return minMaxLength;
    }
}
