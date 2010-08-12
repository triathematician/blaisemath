/*
 * GraphMetrics.java
 * Created May 12, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import org.bm.blaise.scio.graph.Graphs;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.Subgraph;
import org.bm.blaise.scio.graph.ValuedGraph;

/**
 * <p>
 *      Contains several implementations of <code>VertexMetricInterface</code> that can
 *      be used to getValueMap metrics for a vertex in a given graph.
 * </p>
 * @author Elisha Peterson
 */
public class GraphMetrics {



    //
    // METHODS FOR COMPILING INFORMATION ABOUT METRICS IN A GRAPH (ALL NODES)
    //

    /**
     * Returns computeDistribution of the values of a particular metric
     * @param graph the graph
     * @param metric metric used to generate values
     */
    public static <N> Map<N,Integer> computeDistribution(Graph graph, NodeMetric<N> metric) {
        return distribution(metric.allValues(graph));
    }

    /**
     * Computes the computeDistribution associated with a given map, i.e. the number of entries corresponding
     * to each particular value. If the values are of a <code>Comparable</code> type, the map is sorted
     * according to that order.
     * @param values the values to consolidate
     * @return a mapping from the values to the count of those values
     */
    public static <N> Map<N, Integer> distribution(Collection<N> values) {
        if (values.size() == 0) return Collections.emptyMap();
        boolean comparable = false;
        for (N en : values) { comparable = en instanceof Comparable; break; }
        Map<N, Integer> result = comparable ? new TreeMap<N, Integer>() : new HashMap<N, Integer>();

        for (N en : values)
            result.put(en, result.containsKey(en) ? result.get(en) + 1 : 1);

        return result;
    }
    
    //
    // NODE METRICS
    //

    /**
     * Computes the degree of a vertex in a graph,
     * i.e. the number of adjacent edges.
     * Current computational time is linear in the # of edges in the graph.
     */
    public static NodeMetric<Integer> DEGREE = new NodeMetric<Integer>() {
        @Override public String toString() { return "Degree"; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> double nodeMax(boolean directed, int order) { return order-1; }
        public <V> double centralMax(boolean directed, int order) {
            // theoretical max occurs for a star graph
            return directed
                    ? (order-1)*(order-1)
                    : (order-1)*(order-2);
        }
        public <V> Integer value(Graph<V> graph, V vertex) { return graph.degree(vertex); }
        public <V> List<Integer> allValues(Graph<V> graph) {
            List<Integer> result = new ArrayList<Integer>(graph.order());
            for (V v : graph.nodes()) result.add(graph.degree(v));
            return result;
        }
    };

    /**
     * Computes the second-order degree of a vertex in a graph, i.e. how many vertices are within two hops.
     */
    public static NodeMetric<Integer> DEGREE2 = new NodeMetric<Integer>() {
        @Override public String toString() { return "2nd-Order Degree"; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> double nodeMax(boolean directed, int order) { return order-1; }
        public <V> double centralMax(boolean directed, int order) { throw new UnsupportedOperationException("Not yet implemented..."); }
        public <V> Integer value(Graph<V> graph, V vertex) {
            return Graphs.neighborhood(graph, vertex, 2).size() - 1;
        }
        public <V> List<Integer> allValues(Graph<V> graph) {
            List<Integer> result = new ArrayList<Integer>(graph.order());
            for (V v : graph.nodes()) result.add(value(graph, v));
            return result;
        }
    };


    /**
     * Computes the clique count of a particular vertex,
     * i.e. the number of connections between edges in the neighborhood
     * of the vertex, not counting the edges adjacent to the vertex itself.
     * Current computation time is linear in the # of edges in the graph (vertex case),
     * and quadratic in the map case (linear in edges * linear in vertices).
     */
    public static NodeMetric<Integer> CLIQUE_COUNT = new NodeMetric<Integer>() {
        @Override public String toString() { return "Clique Count"; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> double nodeMax(boolean directed, int order) { return (order-1)*(order-2)*(directed ? 1.0 : 0.5); }
        public <V> double centralMax(boolean directed, int order) { throw new UnsupportedOperationException("Not yet implemented..."); }
        public <V> Integer value(Graph<V> graph, V vertex) {
            List<V> nbhd = graph.neighbors(vertex);
            return new Subgraph(graph, nbhd).edgeNumber();
        }
        public <V> double standardization(Graph<V> graph) { return 1.0/((graph.order()-1)*(graph.order()-2)*(graph.isDirected()?2:1)); }
        public <V> List<Integer> allValues(Graph<V> graph) {
            List<Integer> result = new ArrayList<Integer>(graph.order());
            for (V v : graph.nodes()) result.add(value(graph, v));
            return result;
        }
    };


    /**
     * Computes the 2nd order clique count of a particular vertex,
     * i.e. the number of connections between edges in the 2nd order neighborhood
     * of the vertex, subtracting the number of vertices in that neighborhood (the 2nd order degree).
     * Current computation time is linear in the # of edges in the graph (vertex case),
     * and quadratic in the map case (linear in edges * linear in vertices).
     */
    public static NodeMetric<Integer> CLIQUE_COUNT2 = new NodeMetric<Integer>() {
        @Override public String toString() { return "2nd-Order Clique Count"; }
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> double nodeMax(boolean directed, int order) { return (order-1)*(order-2)*(directed ? 1.0 : 0.5); }
        public <V> double centralMax(boolean directed, int order) { throw new UnsupportedOperationException("Not yet implemented..."); }
        public <V> Integer value(Graph<V> graph, V vertex) {
            List<V> nbhd = Graphs.neighborhood(graph, vertex, 2);
            return new Subgraph(graph, nbhd).edgeNumber() - nbhd.size() + 1;
        }
        public <V> List<Integer> allValues(Graph<V> graph) {
            List<Integer> result = new ArrayList<Integer>(graph.order());
            for (V v : graph.nodes()) result.add(value(graph, v));
            return result;
        }
    };

    //
    // GLOBAL METRICS
    //

    /** Global metric describing the order of the graph */
    public static GlobalMetric<Integer> ORDER = new GlobalMetric<Integer>() {
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Integer value(Graph<V> graph) { return graph.order(); }
    };

    /** Global metric describing the # edges in a graph */
    public static GlobalMetric<Integer> EDGE_NUMBER = new GlobalMetric<Integer>() {
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Integer value(Graph<V> graph) { return graph.edgeNumber(); }
    };

    /** Global metric describing the average degree of the graph */
    public static GlobalMetric<Double> DEGREE_AVERAGE = new GlobalMetric<Double>() {
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Double value(Graph<V> graph) { 
            return graph.isDirected()
                    ? graph.edgeNumber() / (double) graph.order()
                    : 2.0 * graph.edgeNumber() / (double) graph.order();
        }
    };

    /** 
     * Global metric describing the clustering coefficient of the graph;
     * in the directed case, measures "transitivity", i.e. when a->b,b->c implies a->c
     */
    public static GlobalMetric<Double> CLUSTERING = new GlobalMetric<Double>() {
        public boolean supportsGraph(boolean directed) { return true; }
        public <V> Double value(Graph<V> graph) {
            int triangles = 0;
            int triples = 0;
            for (V node : graph.nodes()) {
                List<V> g1 = graph.neighbors(node);
                int dist1 = g1.size();
                int aDist1 = new Subgraph(graph, g1).edgeNumber();
                List<V> g2 = Graphs.neighborhood(graph, node, 2);
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

            if (!graph.isDirected()) {
                triangles /= 3;
                triples -= 2*triangles;
            }

            // although each triple & triangle has been counted 3 times too many, it's okay because the return value is a ratio
            return triangles / (double) triples;
        }
    };

    //
    // UTILITY METHOD SAVED HERE
    //

    /** Enumerates all subsets of the integer subset [0,1,2,...,n-1] as an abstract list. */
    private static List<List<Integer>> enumerateSubsets(final int n) {
        return new AbstractList<List<Integer>>() {
            @Override
            public List<Integer> get(int index) {
                ArrayList<Integer> summand = new ArrayList<Integer>();
                for (int bit = 0; bit < n; bit++)
                    if ((index >> bit) % 2 == 1) summand.add(bit);
                return summand;
            }
            @Override
            public int size() { return (int) Math.pow(2, n); }
        };
    }
}
