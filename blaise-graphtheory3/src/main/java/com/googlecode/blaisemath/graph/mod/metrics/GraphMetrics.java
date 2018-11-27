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

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

/**
 * Utility class for working with {@link GraphMetric}, {@link GraphNodeMetric},
 * and {@link GraphSubsetMetric} instances.
 *
 * @author elisha
 */
public class GraphMetrics {
    
    // utility class
    private GraphMetrics() {
    }

    /**
     * Compute the distribution of the values of a particular metric, for all nodes in the graph.
     * @param <N> node type
     * @param <T> metric result type
     * @param graph the graph
     * @param metric metric used to generate values
     * @return distribution of values
     */
    public static <N,T> Multiset<T> distribution(Graph<N> graph, GraphNodeMetric<T> metric) {
        return HashMultiset.create(Iterables.transform(graph.nodes(), n -> metric.apply(graph, n)));
    }

    /**
     * Compute a metric that applies only to connected graphs, by weighting values in proportion to the size of the component
     * they are in.
     * @param <N> graph node type
     * @param graph graph
     * @param connectedGraphMetric a function that computes values for connected graphs
     * @return result
     */
    public static <N> Map<N, Double> applyToComponents(Graph<N> graph, GraphNodeMetric<Double> connectedGraphMetric) {
        int n = graph.nodes().size();
        if (n == 0) {
            return emptyMap();
        } else if (n == 1) {
            return singletonMap((N) graph.nodes().toArray()[0], 0.0);
        }

        Set<Graph<N>> components = GraphUtils.componentGraphs(graph);
        Map<N, Double> values = new HashMap<>();
        for (Graph<N> c : components) {
            if (c.nodes().size() == 1) {
                values.put(c.nodes().iterator().next(), 0.0);
            } else {
                values.putAll(connectedGraphMetric.apply(c));
            }
        }
        for (Graph<N> c : components) {
            double multiplier = c.nodes().size() / (double) n;
            for (N v : c.nodes()) {
                values.put(v, multiplier * values.get(v));
            }
        }
        return values;
    }

}
