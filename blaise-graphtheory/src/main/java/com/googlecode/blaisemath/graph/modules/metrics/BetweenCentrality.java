/*
 * BetweenCentrality.java
 * Created Jul 3, 2010
 */
package com.googlecode.blaisemath.graph.modules.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.googlecode.blaisemath.graph.GraphNodeMetric;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import com.googlecode.blaisemath.graph.GAInstrument;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

/**
 * <p> Provides a metric describing the betweenness centrality of a vertex in a
 * CONNECTED graph. Returns infinity if the graph is not connected. May take a
 * long time for large graphs. </p> <p> Computationally, the centrality measures
 * the probability that a given node lies on a randomly chosen geodesic. </p>
 *
 * @author Elisha Peterson
 */
public class BetweenCentrality implements GraphNodeMetric<Double> {

    public <V> Double apply(Graph<V> graph, V node) {
        return allValues(graph).get(node);
    }

    public <V> Map<V,Double> allValues(Graph<V> graph) {
        int id = GAInstrument.start("BetweenCentrality.allValues", graph.nodeCount()+" nodes", graph.edgeCount()+" edges");
        HashMap<V, Double> between = new HashMap<V, Double>();
        for (V v : graph.nodes()) {
            between.put(v, 0.0);
        }
        for (V start : graph.nodes()) {
            brandes(graph, start, between, graph.isDirected() ? 1.0 : 0.5);
        }
        GAInstrument.end(id);
        return between;
    }

    /**
     * Breadth-first search algorithm for an unweighted graph to generate
     * betweenness scores, with specified starting vertex. From <i>Brandes</i>,
     * "A Faster Algorithm for Betweenness Centrality"
     *
     * @param graph the graph
     * @param start the start vertex
     * @param between data structure storing existing betweenness centrality values
     * @param multiplier applied to all elements of resulting map
     * @return data structure encoding the result
     */
    static <V> HashMap<V, Double> brandes(Graph<V> graph, V start, HashMap<V, Double> between, double multiplier) {
        Set<V> nodes = graph.nodes();
        if (!nodes.contains(start)) {
            return new HashMap<V, Double>();
        }

        // number of shortest paths to each vertex
        HashMap<V, Integer> numShortest = new HashMap<V, Integer>(); 
        // length of shortest paths to each vertex
        HashMap<V, Integer> lengths = new HashMap<V, Integer>(); 
        // tracks elements in non-increasing order for later use
        Stack<V> stack = new Stack<V>(); 
        // tracks vertex predecessors in resulting tree
        HashMap<V, Set<V>> pred = new HashMap<V, Set<V>>(); 

        GraphUtils.breadthFirstSearch(graph, start, numShortest, lengths, stack, pred);

        // compute betweenness
        HashMap<V, Double> dependencies = new HashMap<V, Double>();
        for (V v : nodes) {
            dependencies.put(v, 0.0);
        }
        while (!stack.isEmpty()) {
            V w = stack.pop();
            for (V v : pred.get(w)) {
                dependencies.put(v, dependencies.get(v)
                        + (double) numShortest.get(v) / numShortest.get(w) * (1 + dependencies.get(w)));
            }
            if (w != start) {
                between.put(w, between.get(w)+multiplier*dependencies.get(w));
            }
        }

        return between;

    }
    
}
