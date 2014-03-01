/*
 * ClosenessCentrality.java
 * Created Jul 23, 2010
 */
package org.blaise.graph.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.blaise.graph.GAInstrument;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphUtils;

/**
 * <p> Implements closeness centrality (Sabidussi 1966), the inverse sum of
 * distances from one node to other nodes. The same calculation can be used to
 * compute the "eccentricity" of the node, the max distance from this node to
 * any other node, termed <i>graph centrality</i> by Hage/Harary 1995. Instances
 * of both metrics are provided. </p>
 *
 * @author elisha
 */
public class GraphCentrality implements GraphNodeMetric<Double> {

    private final boolean useSum = false;

    public <V> Double value(Graph<V> graph, V node) {
        int n = graph.nodeCount();
        HashMap<V, Integer> lengths = new HashMap<V, Integer>();
        GraphUtils.breadthFirstSearch(graph, node, new HashMap<V, Integer>(), lengths, new Stack<V>(), new HashMap<V, Set<V>>());
        double cptSize = lengths.size();
        if (useSum) {
            double sum = 0.0;
            for (Integer i : lengths.values()) {
                sum += i;
            }
            return cptSize / n * (n - 1.0) / sum;
        } else {
            double max = 0.0;
            for (Integer i : lengths.values()) {
                max = Math.max(max, i);
            }
            return cptSize / n * 1.0 / max;
        }
    }

    public <V> Map<V,Double> allValues(Graph<V> graph) {
        int id = GAInstrument.start("GraphCentrality.allValues", graph.nodeCount()+" nodes", graph.edgeCount()+" edges");
        
        if (graph.nodeCount() == 0) {
            return Collections.emptyMap();
        } else if (graph.nodeCount() == 1) {
            return Collections.singletonMap((V) graph.nodes().toArray()[0], 0.0);
        }

        int n = graph.nodeCount();
        Set<Graph<V>> components = GraphUtils.getComponentGraphs(graph);
        HashMap<V, Double> values = new HashMap<V, Double>();
        for (Graph<V> cg : components) {
            if (cg.nodeCount() == 1) {
                values.put(cg.nodes().iterator().next(), 0.0);
            } else {
                computeAllValuesConnected(cg, values);
            }
        }
        for (Graph<V> cg : components) {
            double multiplier = cg.nodeCount() / (double) n;
            for (V v : cg.nodes()) {
                values.put(v, multiplier * values.get(v));
            }
        }
        
        GAInstrument.end(id);
        return values;
    }

    /**
     * Computes values for a connected portion of a graph
     */
    private <V> void computeAllValuesConnected(Graph<V> graph, Map<V, Double> values) {
        Set<V> nodes = graph.nodes();
        int n = nodes.size();
        double max = (n - 1.0);

        for (V start : nodes) {
            HashMap<V, Integer> lengths = new HashMap<V, Integer>();
            GraphUtils.breadthFirstSearch(graph, start, new HashMap<V, Integer>(), lengths, new Stack<V>(), new HashMap<V, Set<V>>());
            if (useSum) {
                double sum1 = 0.0;
                for (Integer j : lengths.values()) {
                    sum1 += j;
                }
                values.put(start, max / sum1);
            } else {
                double max1 = 0.0;
                for (Integer j : lengths.values()) {
                    max1 = Math.max(max1, j);
                }
                values.put(start, 1.0 / max1);
            }
        }
    }
}
