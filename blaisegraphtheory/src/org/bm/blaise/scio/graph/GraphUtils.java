/**
 * GraphUtils.java
 * Created on Oct 14, 2009
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *   <code>GraphUtils</code> isAdjacent a collection of utility algorithms for computations
 *   in graph theory.
 * </p>
 *
 * @author Elisha Peterson
 */
public class GraphUtils {

    /**
     * Constructs degree map of specified graph.
     * @return map associating vertices to integers representing their degree.
     */
    public static <V> Map<V, Integer> getDegreeMap(GraphInterface<V> graph) {
        Map<V, Integer> result = new HashMap<V, Integer>();
        for (V v : graph) {
            result.put(v, 0);
        }
        for (EdgeInterface<V> e : graph.getEdges()) {
            result.put(e.getSource(), result.get(e.getSource()) + 1);
            result.put(e.getSink(), result.get(e.getSink()) + 1);
        }
        return result;
    }

    /**
     * Constructs and returns degree distribution.
     *
     * @return double array in which the ith entry is the number of vertices with degree i;
     *  the size is the maximum degree of any vertex in the graph
     */
    public static <V> double[] getDegreeDistribution(GraphInterface<V> graph) {
        Map<V, Integer> degreeMap = getDegreeMap(graph);
        int maxDegree = 0;
        for (V v : degreeMap.keySet()) {
            maxDegree = Math.max(maxDegree, degreeMap.get(v));
        }
        double[] result = new double[maxDegree+1];
        for (V v : degreeMap.keySet()) {
            result[degreeMap.get(v)]++;
        }
        return result;
    }

    /**
     * Computes degree of specified vertex in a graph.
     */
    public static <V> int getDegree(GraphInterface<V> graph, V vertex) {
        int degree = 0;
        for(EdgeInterface<V> e : graph.getEdges())
            if (e.isAdjacent(vertex))
                degree++;
        return degree;
    }

    /**
     * Computes basic neighborhood of a vertex.
     */
    public static <V> Set<V> getNeighborhood(GraphInterface<V> graph, V vertex) {
        Set<V> result = new HashSet<V>();
        for(EdgeInterface<V> e : graph.getEdges())
            if (e.getSource().equals(vertex))
                result.add(e.getSink());
            else if (e.getSink().equals(vertex))
                result.add(e.getSource());
        return result;
    }

    /**
     * Counts number of neighbor connections at specified vertex in a graph.
     */
    public static <V> int getCliqueCount(GraphInterface<V> graph, V vertex) {
        Set<V> nbhd = getNeighborhood(graph, vertex);
        int count = 0;
        for(EdgeInterface<V> e : graph.getEdges()) {
            if (e.isAdjacent(vertex))
                continue;
            if (nbhd.contains(e.getSource()) && nbhd.contains(e.getSink()))
                count++;
        }
        return count;
    }

    /**
     * Returns counts of neighbor connections.
     */
    public static <V> Map<V, Integer> getCliqueCountMap(GraphInterface<V> graph) {
        Map<V, Integer> result = new HashMap<V, Integer>();
        for (V v : graph) {
            result.put(v, getCliqueCount(graph, v));
        }
        return result;
    }

    /**
     * Computes distance between two vertices in a graph.
     */
    public static <V> int getDistance(GraphInterface<V> graph, V v1, V v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        if ( ! graph.containsVertex(v1) || ! graph.containsVertex(v2) ) {
            return -1;
        }
        Set<V> left = new HashSet<V>();
        for (V v : graph) {
            left.add(v);
        }
        left.remove(v1);
        Set<V> cur = new HashSet<V>();

        HashMap<Integer, Set<V>> distMap = new HashMap<Integer, Set<V>>();
        cur.add(v1);
        distMap.put(0, cur);

        int curDist = 1;
        while(left.size() > 0 && cur.size() > 0) {
            cur = new HashSet<V>();
            for (EdgeInterface<V> e : graph.getEdges()) {
                Set<V> lastSet = distMap.get(curDist - 1);
                if (lastSet.contains(e.getSource()) && left.contains(e.getSink())) {
                    cur.add(e.getSink());
                } else if (lastSet.contains(e.getSink()) && left.contains(e.getSource())) {
                    cur.add(e.getSource());
                }
            }
            distMap.put(curDist, cur);
            left.removeAll(cur);
            curDist++;
        }
        return -1;
    }


    /**
     * Computes neighborhood around vertex of specified radius.
     */
    public static <V> Set<V> getNeighborhood(GraphInterface<V> graph, V v1, int radius) {
        Set<V> result = new HashSet<V>();
        if ( ! graph.containsVertex(v1) ) {
            return result;
        }

        Set<V> left = new HashSet<V>();
        for (V v : graph) {
            left.add(v);
        }
        left.remove(v1);
        Set<V> cur = new HashSet<V>();

        HashMap<Integer, Set<V>> distMap = new HashMap<Integer, Set<V>>();
        cur.add(v1);
        result.add(v1);
        distMap.put(0, cur);

        int curDist = 1;
        while(curDist <= radius && left.size() > 0 && cur.size() > 0) {
            cur = new HashSet<V>();
            for (EdgeInterface<V> e : graph.getEdges()) {
                Set<V> lastSet = distMap.get(curDist - 1);
                if (lastSet.contains(e.getSource()) && left.contains(e.getSink())) {
                    cur.add(e.getSink());
                    result.add(e.getSink());
                } else if (lastSet.contains(e.getSink()) && left.contains(e.getSource())) {
                    cur.add(e.getSource());
                    result.add(e.getSource());
                }
            }
            distMap.put(curDist, cur);
            left.removeAll(cur);
            curDist++;
        }

        return result;
    }

    /**
     * Computes component of specified vertex.
     * @param <V>
     * @param graph
     * @param vertex
     * @return
     */
    public static <V> Set<V> getComponent(GraphInterface<V> graph, V vertex) {
        return getNeighborhood(graph, vertex, Integer.MAX_VALUE);
    }

    /**
     * Computes all (connected) components of the graph.
     */
    public static <V> Set<Set<V>> getComponents(GraphInterface<V> graph) {
        Set<Set<V>> result = new HashSet<Set<V>>();
        List<V> remaining = new ArrayList<V>();
        remaining.addAll(graph.getVertices());
        while (! remaining.isEmpty()) {
            Set<V> cpt = getComponent(graph, remaining.get(0));
            result.add(cpt);
            remaining.removeAll(cpt);
        }
        return result;
    }

}
