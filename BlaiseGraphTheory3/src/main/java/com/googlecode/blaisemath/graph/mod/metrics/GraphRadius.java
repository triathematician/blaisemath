package com.googlecode.blaisemath.graph.mod.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Global metric describes the radius of the graph, or the largest diameter of
 * one of its subcomponents.
 *
 * @author elisha
 */
public class GraphRadius extends GraphMetricSupport<Integer> {

    public GraphRadius() {
        super("Graph radius", "Radius of the graph (minimum number r such that all vertices are within r links of a particular vertex).", true);
    }

    @Override
    public Integer apply(Graph graph) {
        return applyTyped(graph);
    }

    private static <V> Integer applyTyped(Graph<V> graph) {
        if (graph.nodeCount() == 0) {
            return 0;
        }
        int minMaxLength = Integer.MAX_VALUE;
        Map<V, Integer> lengths = new HashMap<V, Integer>();
        for (V node : graph.nodes()) {
            int maxLength = 0;
            GraphUtils.breadthFirstSearch(graph, node,
                    HashMultiset.<V>create(), lengths,
                    new Stack<V>(), HashMultimap.<V,V>create());
            for (Integer i : lengths.values()) {
                if (i > maxLength) {
                    maxLength = i;
                }
            }
            if (maxLength > 0) {
                minMaxLength = Math.min(maxLength, minMaxLength);
            }
        }
        return minMaxLength;
    }
}
