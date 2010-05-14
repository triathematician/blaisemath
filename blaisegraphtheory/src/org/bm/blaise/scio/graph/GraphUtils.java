/**
 * GraphUtils.java
 * Created on Oct 14, 2009
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bm.blaise.scio.graph.metrics.GraphMetrics;

/**
 * <p>
 *   <code>GraphUtils</code> is a collection of utility algorithms for computations
 *   in graph theory.
 * </p>
 *
 * @author Elisha Peterson
 */
public class GraphUtils {

    /**
     * Constructs and returns degree distribution.
     *
     * @return double array in which the ith entry is the number of vertices with degree i;
     *  the size is the maximum degree of any vertex in the graph
     */
    public static <V> double[] degreeDistribution(GraphInterface<V> graph) {
        Map<Integer, Integer> degreeMap = GraphMetrics.computeDistribution(graph, GraphMetrics.DEGREE);
        int maxDegree = 0;
        for (Integer deg : degreeMap.keySet())
            maxDegree = Math.max(maxDegree, deg);
        double[] result = new double[maxDegree+1];
        Arrays.fill(result, 0);
        for (Integer deg : degreeMap.values())
            result[deg]++;
        return result;
    }

    /**
     * Computes basic neighborhood of a vertex.
     */
    public static <V> List<Integer> neighborhood(int vertex, GraphInterface<V> graph) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(Edge e : graph.getEdges())
            if (e.source == vertex)
                result.add(e.sink);
            else if (e.sink == vertex)
                result.add(e.source);
        return result;
    }

    /**
     * Computes distance between two vertices in a graph.
     * @param graph the graph
     * @param v1 first vertex
     * @param v2 second vertex
     * @return distance in number of steps, or Integer.MAX_VALUE if not in the same component
     */
    public static <V> int distance(int i1, int i2, GraphInterface<V> graph) {
        int size = graph.getSize();
        if (i1 == i2)
            return 0;
        if (i1 < 0 || i2 < 0 || i1 >= size || i2 >= size)
            return -1;

        // Remaining vertices in graph left to check
        Set<Integer> left = new HashSet<Integer>();
        for (int i = 0; i < size; i++)
            left.add(i);
        left.remove(i1);

        // Current collection of vertices to check
        Set<Integer> cur = new HashSet<Integer>();

        // Maps distances to vertices
        HashMap<Integer, Set<Integer>> distMap = new HashMap<Integer, Set<Integer>>();
        cur.add(i1);
        distMap.put(0, cur);

        int curDist = 1;
        while(left.size() > 0 && cur.size() > 0) {
            cur = new HashSet<Integer>();
            for (Edge e : graph.getEdges()) {
                Set<Integer> lastSet = distMap.get(curDist - 1);
                if (lastSet.contains(e.getSource()) && left.contains(e.getSink())) {
                    if (e.sink == i2)
                        return curDist;
                    else
                        cur.add(e.getSink());
                } else if (lastSet.contains(e.getSink()) && left.contains(e.getSource())) {
                    if (e.source == i2)
                        return curDist;
                    else
                        cur.add(e.getSource());
                }
            }
            distMap.put(curDist, cur);
            left.removeAll(cur);
            curDist++;
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Computes and returns a list of sets describing the distance from given vertex to every other vertex in the graph.
     * Vertices not in the same component are said to be at a distance of Integer.MAX_VALUE
     * @param graph the graph
     * @param vertex the starting vertex
     * @param maxRadius the maximum distance to consider
     * @return a list, whose i'th entry is the set of vertex at distance i from given vertex;
     *      if specified vertex is not in the graph, returns an empty list
     */
    public static ArrayList<Set<Integer>> allDistances(int vertex, GraphInterface graph, int maxRadius) {
        ArrayList<Set<Integer>> result = new ArrayList<Set<Integer>>();
        if (vertex < 0 || vertex >= graph.getSize())
            return result;

        // describes remaining vertices in graph to check
        HashSet<Integer> left = new HashSet<Integer>();
        for (int i = 0; i < graph.getSize(); i++)
            left.add(i);
        

        // describes vertices at current distance
        int curDistance = 0;
        HashSet<Integer> cur = new HashSet<Integer>();
        HashSet<Integer> last;
        cur.add(vertex); left.remove(vertex);
        result.add(cur);

        curDistance++;
        while (curDistance <= maxRadius && cur.size() > 0) {
            last = cur;
            cur = new HashSet<Integer>();
            for (Object o : graph.getEdges()) {
                Edge e = (Edge) o;
                if (last.contains(e.getSource()) && left.contains(e.getSink())) {
                    cur.add(e.sink); left.remove(e.sink);
                } else if (last.contains(e.getSink()) && left.contains(e.getSource())) {
                    cur.add(e.source); left.remove(e.source);
                }
            }
            result.add(cur);
            curDistance++;
        }

        return result;
    }


    /**
     * Computes neighborhood about provided vertex up to a given radius.
     * @param graph the graph
     * @param vertex the starting vertex
     * @param maxDistance the maximum distance to consider
     * @return a set containing the vertices within the neighborhood
     */
    public static <V> Set<Integer> neighborhood(int vertex, GraphInterface<V> graph, int radius) {
        ArrayList<Set<Integer>> all = allDistances(vertex, graph, radius);
        HashSet<Integer> result = new HashSet<Integer>();
        for (Set<Integer> set : all)
            result.addAll(set);
        return result;
    }

    /**
     * Computes component of specified vertex.
     * @param graph
     * @param vertex
     * @return
     */
    public static Set<Integer> component(int vertex, GraphInterface graph) {
        return neighborhood(vertex, graph, Integer.MAX_VALUE);
    }

    /**
     * Computes all (connected) components of the graph.
     */
    public static <V> Set<Set<Integer>> components(GraphInterface<V> graph) {
        Set<Set<Integer>> result = new HashSet<Set<Integer>>();
        ArrayList<Integer> left = new ArrayList<Integer>();
        for (int i = 0; i < graph.getSize(); i++)
            left.add(i);
        while (! left.isEmpty()) {
            Set<Integer> cpt = component(left.get(0), graph);
            result.add(cpt);
            left.removeAll(cpt);
        }
        return result;
    }

}
