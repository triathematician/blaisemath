/*
 * GlobalMetrics.java
 * Created May 12, 2010
 */
package com.googlecode.blaisemath.graph.modules.metrics;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.googlecode.blaisemath.graph.GraphMetric;
import java.util.HashMap;
import java.util.Set;
import java.util.Stack;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;

/**
 * <p> Contains several implementations of
 * <code>GraphMetric</code> that can be used to compute metrics for a given
 * graph. </p>
 *
 * @author Elisha Peterson
 */
public final class GlobalMetrics {

    private GlobalMetrics() {
    }
    
    /**
     * Number of components in the graph
     */
    public static final GraphMetric<Integer> COMPONENTS = new GraphMetric<Integer>() {
        public String getName() {
            return "Components";
        }

        public String getDescription() {
            return "Number of connected components in the graph.";
        }

        public boolean supportsGraph(boolean directed) {
            return true;
        }

        public Integer apply(Graph graph) {
            return GraphUtils.components(graph).size();
        }
    };
    
    /**
     * Global metric describing the order of the graph
     */
    public static final GraphMetric<Integer> ORDER = new GraphMetric<Integer>() {
        public String getName() {
            return "Order";
        }

        public String getDescription() {
            return "Number of vertices in the graph.";
        }

        public boolean supportsGraph(boolean directed) {
            return true;
        }

        public Integer apply(Graph graph) {
            return graph.nodeCount();
        }
    };
    
    /**
     * Global metric describing the # edges in a graph
     */
    public static final GraphMetric<Integer> EDGE_NUMBER = new GraphMetric<Integer>() {
        public String getName() {
            return "Edge Number";
        }

        public String getDescription() {
            return "Number of edges in the graph.";
        }

        public boolean supportsGraph(boolean directed) {
            return true;
        }

        public Integer apply(Graph graph) {
            return graph.edgeCount();
        }
    };
    
    /**
     * Global metric describing the density of the graph (# edges divided by #
     * possible)
     */
    public static final GraphMetric<Double> DENSITY = new GraphMetric<Double>() {
        public String getName() {
            return "Link Density";
        }

        public String getDescription() {
            return "Number of edges in the graph divided by the total number possible.";
        }

        public boolean supportsGraph(boolean directed) {
            return true;
        }

        public Double apply(Graph graph) {
            int n = graph.nodeCount();
            return graph.isDirected()
                    ? graph.edgeCount() / (n * (n - 1))
                    : graph.edgeCount() / (n * (n - 1) / 2.0);
        }
    };
    
    /**
     * Global metric describing the average degree of the graph
     */
    public static final GraphMetric<Double> DEGREE_AVERAGE = new GraphMetric<Double>() {
        public String getName() {
            return "Average Degree";
        }

        public String getDescription() {
            return "Average degree of vertices in the graph. Uses average indegree or outdegree for a directed graph.";
        }

        public boolean supportsGraph(boolean directed) {
            return true;
        }

        public Double apply(Graph graph) {
            return graph.isDirected()
                    ? graph.edgeCount() / (double) graph.nodeCount()
                    : 2.0 * graph.edgeCount() / (double) graph.nodeCount();
        }
    };
    
    /**
     * Global metric describes the diameter of the graph, or the largest
     * diameter of one of its subcomponents.
     */
    public static final GraphMetric<Integer> DIAMETER = new GraphMetric<Integer>() {
        public String getName() {
            return "Diameter";
        }

        public String getDescription() {
            return "Diameter of the graph (longest path between two vertices).";
        }

        public boolean supportsGraph(boolean directed) {
            return true;
        }

        public Integer apply(Graph graph) {
            return applyTyped(graph);
        }
        
        private <V> Integer applyTyped(Graph<V> graph) {
            if (graph.nodeCount() == 0) {
                return 0;
            }
            int maxLength = 0;
            HashMap<V, Integer> lengths = new HashMap<V, Integer>();
            for (V node : graph.nodes()) {
                GraphUtils.breadthFirstSearch(graph, node,
                        new HashMap<V, Integer>(), lengths,
                        new Stack<V>(), new HashMap<V, Set<V>>());
                for (Integer i : lengths.values()) {
                    if (i > maxLength) {
                        maxLength = i;
                    }
                }
            }
            return maxLength;
        }
    };
    
    /**
     * Global metric describes the radius of the graph, or the largest diameter
     * of one of its subcomponents.
     */
    public static final GraphMetric<Integer> RADIUS = new GraphMetric<Integer>() {
        public String getName() {
            return "Radius";
        }

        public String getDescription() {
            return "Radius of the graph (minimum number r such that all vertices are within r links of a particular vertex).";
        }

        public boolean supportsGraph(boolean directed) {
            return true;
        }

        public Integer apply(Graph graph) {
            return applyTyped(graph);
        }
        
        private <V> Integer applyTyped(Graph<V> graph) {
            if (graph.nodeCount() == 0) {
                return 0;
            }
            int minMaxLength = Integer.MAX_VALUE;
            HashMap<V, Integer> lengths = new HashMap<V, Integer>();
            for (V node : graph.nodes()) {
                int maxLength = 0;
                GraphUtils.breadthFirstSearch(graph, node,
                        new HashMap<V, Integer>(), lengths,
                        new Stack<V>(), new HashMap<V, Set<V>>());
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
    };
    
    /**
     * Global metric describing the clustering coefficient of the graph; in the
     * directed case, measures "transitivity", i.e. when a->b,b->c implies a->c
     */
    public static final GraphMetric<Double> CLUSTERING_A = new GraphMetric<Double>() {
        public String getName() {
            return "Clustering A";
        }

        public String getDescription() {
            return "Computes the clustering coefficient:"
                    + " Out of all triples of vertices with at least two edges, how many have three edges?";
        }

        public boolean supportsGraph(boolean directed) {
            return true;
        }

        public Double apply(Graph graph) {
            int[] tri = triples(graph);
            int triangles = tri[0], triples = tri[1];
            if (!graph.isDirected()) {
                triangles /= 3;
                triples -= 2 * triangles;
            }
            return triangles / (double) triples;
        }
    };
    
    /**
     * Global metric describing the clustering coefficient of the graph; in the
     * directed case, measures "transitivity", i.e. when a->b,b->c implies a->c
     */
    public static final GraphMetric<Double> CLUSTERING_B = new GraphMetric<Double>() {
        public String getName() {
            return "Clustering B";
        }

        public String getDescription() {
            return "Computes the clustering coefficient:"
                    + " Out of all length-3 paths, how many are enclosed by a triangle?";
        }

        public boolean supportsGraph(boolean directed) {
            return true;
        }

        public Double apply(Graph graph) {
            int[] tri = triples(graph);
            int triangles = tri[0], triples = tri[1];
            if (!graph.isDirected()) {
                triangles /= 3;
            }
            return triangles / (double) triples;
        }
    };

    /**
     * Computes triple characteristics of a graph
     *
     * @param <V> coordinate type of graph
     * @param graph the graph
     * @return int[] array where first entry is number of triangles and second
     * is number of path triples (i.e., when three nodes are connected together)
     */
    public static <V> int[] triples(Graph<V> graph) {
        int triangles = 0;
        int triples = 0;
        for (V node : graph.nodes()) {
            Set<V> g1 = graph.neighbors(node);
            int dist1 = g1.size();
            int aDist1 = GraphUtils.copySubgraph(graph, g1).edgeCount();
            Set<V> g2 = GraphUtils.neighborhood(graph, node, 2);
            int dist2 = g2.size() - 1 - g1.size();

            if (graph.isDirected()) {
                // in the directed case, potential triples are connected nodes at distance 1 and nodes at distance 2
                // ... each node at distance 2 contributes a triple, but no triangle
                triples += aDist1 + dist2;
                triangles += aDist1;
            } else {
                // in copyUndirected case, each pair of nodes @ distance 1 contributes to a triple
                // ... each edge in this neighborhood indicates a triangle
                // corrections for later: each triangle is counted 3 times
                triples += dist1 * (dist1 - 1) / 2;
                triangles += aDist1;
            }
        }
        return new int[]{triangles, triples};
    }
}
