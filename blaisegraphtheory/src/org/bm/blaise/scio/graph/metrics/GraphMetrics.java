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
    // SPECIFIC METRICS PROVIDED FOR CONVENIENCE
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
        public <V> double centralMax(boolean directed, int order) { return directed ? (order-1)*(order-1) : (order-1)*(order-2); } // for a star graph
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
    // UTILITY METHOD SAVED HERE
    //

    /** Enumerates all subsets of the integer subset [0,1,2,...,n-1] as an abstract list. */
    static List<List<Integer>> enumerateSubsets(final int n) {
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
