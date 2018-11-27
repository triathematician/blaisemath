package com.googlecode.blaisemath.graph.mod.metrics;

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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Queues;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.util.Instrument;
import com.googlecode.blaisemath.graph.GraphUtils;
import java.util.Deque;

/**
 * Provides a metric describing the betweenness centrality of a vertex in a
 * CONNECTED graph. Returns infinity if the graph is not connected. May take a
 * long time for large graphs. Computationally, the centrality measures
 * the probability that a given node lies on a randomly chosen geodesic.
 *
 * @author Elisha Peterson
 */
public class BetweenCentrality extends AbstractGraphNodeMetric<Double> {
    
    public BetweenCentrality() {
        super("Betweenness centrality");
    }

    @Override
    public <V> Double apply(Graph<V> graph, V node) {
        return apply(graph).get(node);
    }

    @Override
    public <V> Map<V,Double> apply(Graph<V> graph) {
        int id = Instrument.start("BetweenCentrality.allValues", graph.nodes().size()+" nodes", graph.edges().size()+" edges");
        Map<V, Double> between = new HashMap<>();
        graph.nodes().forEach(n -> between.put(n, 0.0));
        graph.nodes().forEach(n -> applyBrandes(graph, n, between, graph.isDirected() ? 1.0 : 0.5));
        Instrument.end(id);
        return between;
    }

    /**
     * Breadth-first search algorithm for an unweighted graph to generate betweenness scores, with specified starting
     * vertex. From <i>Brandes</i>, "A Faster Algorithm for Betweenness Centrality".
     *
     * @param graph the graph
     * @param start the start vertex
     * @param result data structure storing existing betweenness centrality values
     * @param multiplier applied to all elements of resulting map
     * @return data structure encoding the result
     */
    static <V> Map<V, Double> applyBrandes(Graph<V> graph, V start, Map<V, Double> result, double multiplier) {
        Set<V> nodes = graph.nodes();
        if (!nodes.contains(start)) {
            return new HashMap<>();
        }

        // number of shortest paths to each vertex
        Multiset<V> numShortest = HashMultiset.create();
        // length of shortest paths to each vertex
        Map<V, Integer> lengths = new HashMap<>();
        // tracks elements in non-increasing order for later use
        Deque<V> deque = Queues.newArrayDeque();
        // tracks vertex predecessors in resulting tree
        Multimap<V,V> predecessors = HashMultimap.create();

        GraphUtils.breadthFirstSearch(graph, start, numShortest, lengths, deque, predecessors);

        // compute betweenness
        Map<V, Double> dependencies = new HashMap<>();
        for (V v : nodes) {
            dependencies.put(v, 0.0);
        }
        while (!deque.isEmpty()) {
            V w = deque.pollLast();
            for (V v : predecessors.get(w)) {
                dependencies.put(v, dependencies.get(v) + (double) numShortest.count(v) / numShortest.count(w) * (1 + dependencies.get(w)));
            }
            if (w != start) {
                result.put(w, result.get(w) + multiplier * dependencies.get(w));
            }
        }

        return result;

    }
    
}
