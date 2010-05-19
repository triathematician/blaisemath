/*
 * GraphMetrics.java
 * Created May 12, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.bm.blaise.scio.graph.GraphUtils;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.bm.blaise.scio.graph.Edge;
import org.bm.blaise.scio.graph.GraphInterface;

/**
 * <p>
 *      Contains several implementations of <code>VertexMetricInterface</code> that can
 *      be used to getValueMap metrics for a vertex in a given graph.
 * </p>
 * @author Elisha Peterson
 */
public class GraphMetrics {

    //
    // STATIC METHODS
    //

    /**
     * Returns mapping of vertices to metric value for all vertices in the graph.
     * Keys in the resulting path are iterated in the same order as in the graph.
     * @param graph the graph
     * @param metric metric used to generate the values
     */
    public static <N> List<N> computeValues(GraphInterface graph, VertexMetricInterface<N> metric) {
        ArrayList<N> result = new ArrayList<N>();
        for (int i = 0; i < graph.size(); i++)
            result.add(metric.getValue(graph, i));
        return result;
    }

    /**
     * Returns computeDistribution of the values of a particular metric
     * @param graph the graph
     * @param metric metric used to generate values
     */
    public static <N> Map<N, Integer> computeDistribution(GraphInterface graph, VertexMetricInterface<N> metric) {
        return distribution(computeValues(graph, metric));
    }

    /**
     * Computes the computeDistribution associated with a given map, i.e. the number of entries corresponding
     * to each particular value. If the values are of a <code>Comparable</code> type, the map is sorted
     * according to that order.
     * @param map a map w/ entries mapping some kind of object to some kind of value
     * @return a mapping from the values to the count of those values
     */
    public static <N> Map<N, Integer> distribution(List<N> list) {
        boolean comparable = false;
        for (N en : list) {
            comparable = en instanceof Comparable;
            break;
        }
        Map<N, Integer> result = comparable ? new TreeMap<N, Integer>() : new HashMap<N, Integer>();
        for (N en : list) {
            if (result.containsKey(en))
                result.put(en, result.get(en) + 1);
            else
                result.put(en, 1);
        }
        return result;
    }

    //
    // PARTICULAR METRICS
    //

    /** 
     * Computes the degree of a vertex in a graph,
     * i.e. the number of adjacent edges.
     * Current computational time is linear in the # of edges in the graph.
     */
    public static VertexMetricMapInterface<Integer> DEGREE = new VertexMetricMapInterface<Integer>() {
        public Integer getValue(GraphInterface graph, int vertex) {
            int degree = 0;
            for (Object e : graph.getEdges())
                if (((Edge)e).adjacentTo(vertex))
                    degree++;
            return degree;
        }
        public List<Integer> getValues(GraphInterface graph) {
            Integer[] result = new Integer[graph.size()];
            Arrays.fill(result, 0);
            for (Object o : graph.getEdges()) {
                Edge e = (Edge) o;
                result[e.getSource()]++;
                result[e.getSink()]++;
            }
            return Arrays.asList(result);
        }
    };

    /**
     * Computes the second-order degree of a vertex in a graph, i.e. how many vertices are within two hops.
     */
    public static VertexMetricInterface<Integer> DEGREE2 = new VertexMetricInterface<Integer>() {
        public Integer getValue(GraphInterface graph, int vertex) {
            return GraphUtils.neighborhood(vertex, graph, 2).size() - 1;
        }
    };


    /**
     * Computes the clique count of a particular vertex,
     * i.e. the number of connections between edges in the neighborhood
     * of the vertex, not counting the edges adjacent to the vertex itself.
     * Current computation time is linear in the # of edges in the graph (vertex case),
     * and quadratic in the map case (linear in edges * linear in vertices).
     */
    public static VertexMetricInterface<Integer> CLIQUE_COUNT = new VertexMetricInterface<Integer>() {
        public Integer getValue(GraphInterface graph, int vertex) {
            int count = 0;
            List<Integer> nbhd = GraphUtils.neighborhood(vertex, graph);
            for (Object o : graph.getEdges()) {
                Edge e = (Edge) o;
                if (e.adjacentTo(vertex))
                    continue;
                if (nbhd.contains(e.getSource()) && nbhd.contains(e.getSink()))
                    count++;
            }
            return count;
        }
    };
}
