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
 * A collection of static utility algorithms for graph theoretic computations.
 *
 * @author Elisha Peterson
 */
public final class GraphUtils {

    private GraphUtils() {}

    /**
     * Constructs and returns degree distribution.
     *
     * @return double array in which the ith entry is the number of vertices with degree i;
     *  the size is the maximum degree of any vertex in the graph
     */
    public static <V> double[] degreeDistribution(Graph<V> graph) {
        Map<Integer, Integer> degreeMap = GraphMetrics.computeDistribution(graph, GraphMetrics.DEGREE);
        int maxDegree = 0;
        for (Integer deg : degreeMap.keySet())
            maxDegree = Math.max(maxDegree, deg);
        double[] result = new double[maxDegree+1];
        Arrays.fill(result, 0);
        for (Integer deg : degreeMap.keySet())
            result[deg] = degreeMap.get(deg);
        return result;
    }

    /**
     * Computes distance between two vertices in a graph.
     * @param graph the graph
     * @param v1 first vertex
     * @param v2 second vertex
     * @return distance in number of steps, or Integer.MAX_VALUE if not in the same component
     */
    public static <V> int distance(int i1, int i2, Graph<V> graph) {
        int size = graph.size();
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
    public static ArrayList<Set<Integer>> allDistances(int vertex, Graph graph, int maxRadius) {
        ArrayList<Set<Integer>> result = new ArrayList<Set<Integer>>();
        if (vertex < 0 || vertex >= graph.size())
            return result;

        // describes remaining vertices in graph to check
        HashSet<Integer> left = new HashSet<Integer>();
        for (int i = 0; i < graph.size(); i++)
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
    public static <V> Set<Integer> neighborhood(int vertex, Graph<V> graph, int radius) {
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
    public static Set<Integer> component(int vertex, Graph graph) {
        return neighborhood(vertex, graph, Integer.MAX_VALUE);
    }

    /**
     * Computes all (connected) components of the graph.
     */
    public static <V> Set<Set<Integer>> components(Graph<V> graph) {
        Set<Set<Integer>> result = new HashSet<Set<Integer>>();
        ArrayList<Integer> left = new ArrayList<Integer>();
        for (int i = 0; i < graph.size(); i++)
            left.add(i);
        while (! left.isEmpty()) {
            Set<Integer> cpt = component(left.get(0), graph);
            result.add(cpt);
            left.removeAll(cpt);
        }
        return result;
    }


    /**
     * Computes adjacency matrix of a graph
     * @param <V> type of vertex in the graph
     * @param graph the input graph
     * @return matrix of integers describing adjacencies... contains 0's and 1's...
     *      it is symmetric when the graph is undirected, otherwise it may not be symmetric
     */
    public <V> int[][] adjacency(Graph2<V> graph) {
        boolean directed = graph.isDirected();
        ArrayList vertices = new ArrayList<V>();
        vertices.addAll(graph.getVertices());
        int[][] result = new int[vertices.size()][vertices.size()];
        for (Edge e : graph.getEdges()) {
            result[e.source][e.sink] = 1;
            if (!directed)
                result[e.sink][e.source] = 1;
        }
        return result;
    }

    /**
     * Computes several powers of the adjacency matrix
     * @param <V> type of vertex in the graph
     * @param graph the input graph
     * @param maxPower maximum power of the adjacency matrix to include in result
     * @return matrix of integers describing adjacencies... contains 0's and 1's...
     *      it is symmetric when the graph is undirected, otherwise it may not be symmetric
     */
    public <V> int[][][] adjacencyPowers(Graph2<V> graph, int maxPower) {
        int[][] adj1 = adjacency(graph);
        int[][][] result = new int[maxPower][adj1.length][adj1[0].length];
        result[0] = adj1;
        int cur = 2;
        while (cur <= maxPower) {
            result[cur - 1] = matrixProduct(result[cur - 2], adj1);
            cur++;
        }
        return result;
    }

    /**
     * Computes product of two matrices of integers
     * First entry is row, second is column.
     * Requires # columns in m1 equal to number of rows in m2
     */
    private int[][] matrixProduct(int[][] m1, int[][] m2) {
        int rows1 = m1.length, cols1 = m1[0].length;
        int rows2 = m2.length, cols2 = m2[0].length;
        if (cols1 != rows2)
            throw new IllegalArgumentException("matrixProduct: incompatible matrix sizes");
        int[][] result = new int[rows1][cols2];
        for (int i = 0; i < rows1; i++)
            for (int j = 0; j < rows2; j++) {
                int sum = 0;
                for (int k = 0; k < rows1; k++)
                    sum += m1[i][k]*m2[k][j];
                result[i][j] = sum;
            }
        return result;
    }

}
