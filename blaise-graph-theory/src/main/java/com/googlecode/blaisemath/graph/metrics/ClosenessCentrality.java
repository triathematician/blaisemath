package com.googlecode.blaisemath.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2020 Elisha Peterson
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
import com.google.common.collect.Maps;
import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphMetrics;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.util.Instrument;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implements closeness centrality (Sabidussi 1966), the inverse sum of
 * distances from one node to other nodes. The same calculation can be used to
 * compute the "eccentricity" of the node, the max distance from this node to
 * any other node, termed graph centrality by Hage/Harary 1995. Instances
 * of both metrics are provided.
 *
 * @author Elisha Peterson
 */
public class ClosenessCentrality extends AbstractGraphNodeMetric<Double> {
    
    public ClosenessCentrality() {
        super("Closeness centrality");
    }

    @Override
    public <N> Double apply(Graph<N> graph, N node) {
        int n = graph.nodes().size();
        Map<N, Integer> lengths = new HashMap<>();
        GraphUtils.breadthFirstSearch(graph, node, HashMultiset.create(), lengths, new ArrayDeque<>(), HashMultimap.create());
        double cptSize = lengths.size();
        double sum = lengths.values().stream().mapToDouble(v -> v).sum();
        return cptSize / n * (n - 1.0) / sum;
    }

    @Override
    public <N> Map<N, Double> apply(Graph<N> graph) {
        int id = Instrument.start("ClosenessCentrality.allValues", graph.nodes().size()+" nodes", graph.edges().size()+" edges");
        Map<N, Double> res = GraphMetrics.applyToComponents(graph, new ApplyConnected());
        Instrument.end(id);
        return res;
    }

    /** Delegate metric that computes values for a connected portion of a graph */
    private static class ApplyConnected extends AbstractGraphNodeMetric<Double> {

        ApplyConnected() {
            super("");
        }

        @Override
        public <N> Double apply(Graph<N> graph, N node) {
            // not used
            return null;
        }

        @Override
        public <N> Map<N,Double> apply(Graph<N> graph) {
            Map<N, Double> res = Maps.newHashMap();
            Set<N> nodes = graph.nodes();
            int n = nodes.size();
            double max = n - 1.0;

            for (N start : nodes) {
                Map<N, Integer> lengths = new HashMap<>();
                GraphUtils.breadthFirstSearch(graph, start, HashMultiset.create(), lengths, new ArrayDeque<>(), HashMultimap.create());
                double sum = lengths.values().stream().mapToDouble(v -> v).sum();
                res.put(start, max / sum);
            }
            return res;
        }
    }
}
