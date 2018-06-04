/*
 * ClosenessCentrality.java
 * Created Jul 23, 2010
 */
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

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.googlecode.blaisemath.util.GAInstrument;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphMetrics;
import com.googlecode.blaisemath.graph.GraphUtils;
import java.util.ArrayDeque;

/**
 * <p> Implements closeness centrality (Sabidussi 1966), the inverse sum of
 * distances from one node to other nodes. The same calculation can be used to
 * compute the "eccentricity" of the node, the max distance from this node to
 * any other node, termed <i>graph centrality</i> by Hage/Harary 1995. Instances
 * of both metrics are provided. </p>
 *
 * @author elisha
 */
public class ClosenessCentrality extends AbstractGraphNodeMetric<Double> {
    
    public ClosenessCentrality() {
        super("Closeness centrality");
    }

    @Override
    public <V> Double apply(Graph<V> graph, V node) {
        int n = graph.nodeCount();
        Map<V, Integer> lengths = new HashMap<V, Integer>();
        GraphUtils.breadthFirstSearch(graph, node, HashMultiset.<V>create(), lengths, 
                new ArrayDeque<V>(), HashMultimap.<V,V>create());
        double cptSize = lengths.size();
        double sum = 0.0;
        for (Integer i : lengths.values()) {
            sum += i;
        }
        return cptSize / n * (n - 1.0) / sum;
    }

    @Override
    public <V> Map<V,Double> apply(Graph<V> graph) {
        int id = GAInstrument.start("ClosenessCentrality.allValues", graph.nodeCount()+" nodes", graph.edgeCount()+" edges");
        Map<V,Double> res = GraphMetrics.applyToComponents(graph, new ApplyConnected<V>());
        GAInstrument.end(id);
        return res;
    }

    /** Computes values for a connected portion of a graph */
    private static class ApplyConnected<V> implements Function<Graph<V>, Map<V, Double>> {
        @Override
        public Map<V,Double> apply(Graph<V> graph) {
            Map<V, Double> res = Maps.newHashMap();
            Set<V> nodes = graph.nodes();
            int n = nodes.size();
            double max = n - 1.0;

            for (V start : nodes) {
                Map<V, Integer> lengths = new HashMap<V, Integer>();
                GraphUtils.breadthFirstSearch(graph, start, HashMultiset.<V>create(), 
                        lengths, new ArrayDeque<V>(), HashMultimap.<V,V>create());
                double sum1 = 0.0;
                for (Integer j : lengths.values()) {
                    sum1 += j;
                }
                res.put(start, max / sum1);
            }
            return res;
        }
    }
}
