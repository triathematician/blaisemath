/*
 * GlobalMetrics.java
 * Created May 12, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.HashMap;
import org.bm.blaise.scio.graph.GraphUtils;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.Subgraph;

/**
 * <p>
 *      Contains several implementations of <code>GlobalMetric</code> that can
 *      be used to compute metrics for a given graph.
 * </p>
 * @author Elisha Peterson
 */
public final class GlobalMetrics { private GlobalMetrics() {}

    /** Global metric describing the order of the graph */
    public static GlobalMetric<Integer> ORDER = new GlobalMetric<Integer>() {
        public String getName() { return "Order"; }
        public String getDescription() { return "Number of vertices in the graph."; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Integer value(Graph<V> graph) { return graph.order(); }
    };

    /** Global metric describing the # edges in a graph */
    public static GlobalMetric<Integer> EDGE_NUMBER = new GlobalMetric<Integer>() {
        public String getName() { return "Edge Number"; }
        public String getDescription() { return "Number of edges in the graph."; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Integer value(Graph<V> graph) { return graph.edgeNumber(); }
    };

    /** Global metric describing the density of the graph (# edges divided by # possible) */
    public static GlobalMetric<Double> DENSITY = new GlobalMetric<Double>() {
        public String getName() { return "Link Density"; }
        public String getDescription() { return "Number of edges in the graph divided by the total number possible."; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Double value(Graph<V> graph) {
            int n = graph.order();
            return graph.isDirected()
                    ? graph.edgeNumber() / (n*(n-1))
                    : graph.edgeNumber() / (n*(n-1)/2.0);
        }
    };

    /** Global metric describing the average degree of the graph */
    public static GlobalMetric<Double> DEGREE_AVERAGE = new GlobalMetric<Double>() {
        public String getName() { return "Average Degree"; }
        public String getDescription() { return "Average degree of vertices in the graph. Uses average indegree or outdegree for a directed graph."; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Double value(Graph<V> graph) { 
            return graph.isDirected()
                    ? graph.edgeNumber() / (double) graph.order()
                    : 2.0 * graph.edgeNumber() / (double) graph.order();
        }
    };

    /** 
     * Global metric describes the diameter of the graph, or the largest diameter
     * of one of its subcomponents.
     */
    public static GlobalMetric<Integer> DIAMETER = new GlobalMetric<Integer>() {
        public String getName() { return "Diameter"; }
        public String getDescription() { return "Diameter of the graph (longest path between two vertices)."; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Integer value(Graph<V> graph) {
            if (graph.order() == 0)
                return 0;
            int maxLength = 0;
            HashMap<V,Integer> lengths = new HashMap<V,Integer>();
            for (V node : graph.nodes()) {
                GraphUtils.breadthFirstSearch(graph, node,
                        new HashMap<V,Integer>(), lengths,
                        new Stack<V>(), new HashMap<V,Set<V>>());
                for (Integer i : lengths.values())
                    if (i > maxLength)
                        maxLength = i;
            }
            return maxLength;
        }
    };

    /**
     * Global metric describes the radius of the graph, or the largest diameter
     * of one of its subcomponents.
     */
    public static GlobalMetric<Integer> RADIUS = new GlobalMetric<Integer>() {
        public String getName() { return "Radius"; }
        public String getDescription() { return "Radius of the graph (minimum number r such that all vertices are within r links of a particular vertex)."; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Integer value(Graph<V> graph) {
            if (graph.order() == 0)
                return 0;
            int minMaxLength = Integer.MAX_VALUE;
            HashMap<V,Integer> lengths = new HashMap<V,Integer>();
            for (V node : graph.nodes()) {
                int maxLength = 0;
                GraphUtils.breadthFirstSearch(graph, node,
                        new HashMap<V,Integer>(), lengths,
                        new Stack<V>(), new HashMap<V,Set<V>>());
                for (Integer i : lengths.values())
                    if (i > maxLength)
                        maxLength = i;
                if (maxLength > 0)
                    minMaxLength = Math.min(maxLength, minMaxLength);
            }
            return minMaxLength;
        }
    };

    /** 
     * Global metric describing the clustering coefficient of the graph;
     * in the directed case, measures "transitivity", i.e. when a->b,b->c implies a->c
     */
    public static GlobalMetric<Double> CLUSTERING_A = new GlobalMetric<Double>() {
        public String getName() { return "Clustering A"; }
        public String getDescription() { return "Computes the clustering coefficient:"
                + " Out of all triples of vertices with at least two edges, how many have three edges?"; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Double value(Graph<V> graph) {
            int[] tri = triples(graph);
            int triangles = tri[0], triples = tri[1];
            if (!graph.isDirected()) {
                triangles /= 3;
                triples -= 2*triangles;
            }
            return triangles / (double) triples;
        }
    };

    /**
     * Global metric describing the clustering coefficient of the graph;
     * in the directed case, measures "transitivity", i.e. when a->b,b->c implies a->c
     */
    public static GlobalMetric<Double> CLUSTERING_B = new GlobalMetric<Double>() {
        public String getName() { return "Clustering B"; }
        public String getDescription() { return "Computes the clustering coefficient:"
                + " Out of all length-3 paths, how many are enclosed by a triangle?"; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Double value(Graph<V> graph) {
            int[] tri = triples(graph);
            int triangles = tri[0], triples = tri[1];
            if (!graph.isDirected())
                triangles /= 3;
            return triangles / (double) triples;
        }
    };

    /** 
     * Computes triple characteristics of a graph
     * @param <V> coordinate type of graph
     * @param graph the graph
     * @return int[] array where first entry is number of triangles and second is number of path triples
     *      (i.e., when three nodes are connected together)
     */
    public static <V> int[] triples(Graph<V> graph) {
        int triangles = 0;
        int triples = 0;
        for (V node : graph.nodes()) {
            Set<V> g1 = graph.neighbors(node);
            int dist1 = g1.size();
            int aDist1 = new Subgraph(graph, g1).edgeNumber();
            List<V> g2 = GraphUtils.neighborhood(graph, node, 2);
            int dist2 = g2.size()-1 - g1.size();

            if (graph.isDirected()) {
                // in the directed case, potential triples are connected nodes at distance 1 and nodes at distance 2
                // ... each node at distance 2 contributes a triple, but no triangle
                triples += aDist1 + dist2;
                triangles += aDist1;
            } else {
                // in undirected case, each pair of nodes @ distance 1 contributes to a triple
                // ... each edge in this neighborhood indicates a triangle
                // corrections for later: each triangle is counted 3 times
                triples += dist1*(dist1-1)/2;
                triangles += aDist1;
            }
        }
        return new int[] { triangles, triples };
    }

}
