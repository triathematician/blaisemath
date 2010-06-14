/**
 * GraphUtils.java
 * Created on Oct 14, 2009
 */

package org.bm.blaise.scio.graph;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

/**
 * Contains several utility methods for creating and analyzing graphs.
 *
 * @author Elisha Peterson
 */
public class Graphs {

    // Ensure non-instantiability
    private Graphs() {}

    //
    // GENERIC PRINT METHODS
    //

    /**
     * Returns string representation of specified graph
     * @param graph the graph to print
     * @return string representation of graph
     */
    public static <V> String printGraph(Graph<V> graph) {
        return printGraph(graph, true, true);
    }

    /**
     * Returns string representation of specified graph
     * @param graph the graph to print
     * @param printNodes whether to print nodes or not
     * @param printEdges whether to print edges or not
     * @return string representation of graph
     */
    public static <V> String printGraph(Graph<V> graph, boolean printNodes, boolean printEdges) {
        if (!printEdges && !printNodes)
            return "GRAPH";
        else if (printNodes && !printEdges)
            return "NODES: " + graph.nodes();

        StringBuilder result = printNodes 
                ? new StringBuilder("NODES: ").append(graph.nodes()).append("  EDGES:")
                : new StringBuilder("EDGES:");
        for (V v : graph.nodes())
            result.append(" ").append(v).append(": ").append(graph.neighbors(v));
        return result.toString();
    }

    //
    // CREATION METHODS (SPECIFIC TYPES)
    //

    /** Returns abstract list of integers 0,...,n-1 */
    private static List<Integer> intList(final int n) {
        return new AbstractList<Integer>() {
            @Override public Integer get(int index) { return index; }
            @Override public int size() { return n; }
        };
    }

    /**
     * Creates an instance of either a matrix-based graph or a sparse-based graph, depending
     * upon the number of edges and vertices
     * @param directed whether graph is directed
     * @param vertices collection of vertices
     * @param edges collection of edges as vertex-pairings
     * @return graph with specified edges
     */
    public static <V> Graph<V> getInstance(boolean directed, Collection<V> vertices, Collection<V[]> edges) {
        int n = vertices.size();
        int nEdges = edges.size();
        return (n <= 50 || (n<=100 && nEdges > 1000) )
                ? MatrixGraph.getInstance(directed, vertices, edges)
                : SparseGraph.getInstance(directed, vertices, edges);
    }

    /**
     * Returns graph with n vertices and no edges
     * @param n number of vertices
     * @param directed whether graph is directed
     * @return empty graph with specified number of vertices
     */
    public static Graph<Integer> getEmptyGraphInstance(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getInstance(directed, intList(n), (Collection) Collections.emptyList());
    }

    /**
     * Returns graph with all possible edges.
     * @param n number of vertices
     * @param directed whether graph is directed
     * @return complete graph; if directed, result includes loops
     */
    public static Graph<Integer> getCompleteGraphInstance(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        List<Integer[]> edges;
        if (!directed) {
            edges = new ArrayList<Integer[]>();
            for(int i = 0; i < n; i++)
                for (int j = i+1; j < n; j++)
            edges.add(new Integer[]{i, j});
        } else {
            edges = new AbstractList<Integer[]>() {
                @Override public Integer[] get(int index) { return new Integer[]{ index/n, index%n }; }
                @Override public int size() { return n*n; }
            };
        }
        return getInstance(directed, intList(n), edges);
    }

    /**
     * Returns graph with all edges connected in a loop.
     * @param n number of vertices
     * @param directed whether graph is directed
     * @return cycle graph
     */
    public static Graph<Integer> getCycleGraphInstance(final int n, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getInstance(directed, intList(n),
            new AbstractList<Integer[]>() {
                @Override public Integer[] get(int index) { return new Integer[] { index, (index+1)%n }; }
                @Override public int size() { return n; }
            }
        );
    }

    /** 
     * Returns graph with all vertices connected to a central hub.
     * @param n number of vertices
     * @return star graph (undirected)
     */
    public static Graph<Integer> getStarGraphInstance(final int n) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getInstance(false, intList(n),
            new AbstractList<Integer[]>() {
                @Override public Integer[] get(int index) { return new Integer[] { 0, index+1 }; }
                @Override public int size() { return n==0 ? 0 : n-1; }
            }
        );
    }

    /**
     * Returns graph with all vertices connected to a central hub, and all other vertices
     * connected in a cyclic fashion.
     * @param n number of vertices
     * @return star graph (undirected)
     */
    public static Graph<Integer> getWheelGraphInstance(final int n) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be nonnegative! n="+n);
        return getInstance(false, intList(n),
            new AbstractList<Integer[]>() {
                @Override public Integer[] get(int index) {
                    return index < n-1
                            ? new Integer[] { 0, index+1 }
                         : index == 2*n-2
                             ? new Integer[] { n-1, 1}
                             : new Integer[] { index-n+1, index-n+2 };

                }
                @Override public int size() { return 2*n-1; }
            }
        );
    }
    

    //
    // CREATION METHODS (RANDOM)
    //

    /** Ensures probability is valid. */
    private static void checkProbability(float p) {
        if (p < 0 || p > 1) throw new IllegalArgumentException("Probalities must be between 0 and 1: (" + p + " was used.");
    }

    /**
     * Returns graph with random number of connections between vertices
     * @param n number of vertices
     * @param p probability of each edge
     * @param directed whether resulting graph is directed
     * @return directed or undirected graph with randomly chosen edges
     */
    public static Graph<Integer> getRandomInstance(int n, float p, boolean directed) {
        if (n < 0) throw new IllegalArgumentException("Numbers must be positive! n="+n);
        checkProbability(p);
        ArrayList<Integer[]> edges = new ArrayList<Integer[]>();
        for (int i = 0; i < n; i++)
            for (int j = directed ? 0 : i+1; j < n; j++)
                if (Math.random() < p)
                    edges.add(new Integer[]{i, j});
        return getInstance(directed, intList(n), edges);
    }

    /** Used to sort pairs of integers when order of the two matters. */
    private static final Comparator<Integer[]> PAIR_COMPARE = new Comparator<Integer[]>() {
        public int compare(Integer[] o1, Integer[] o2) {
            if (o1.length != 2 || o2.length != 2)
                throw new IllegalStateException("This object only compares integer pairs.");
            return o1[0]==o2[0] ? o1[1]-o2[1] : o1[0]-o2[0];
        }
    };

    /** Used to sort pairs of integers when order of the two does not matter. */
    private static final Comparator<Integer[]> PAIR_COMPARE_UNDIRECTED = new Comparator<Integer[]>() {
        public int compare(Integer[] o1, Integer[] o2) {
            if (o1.length != 2 || o2.length != 2)
                throw new IllegalStateException("This object only compares integer pairs.");
            int min1 = Math.min(o1[0], o1[1]);
            int min2 = Math.min(o2[0], o2[1]);
            return min1==min2 ? Math.max(o1[0],o1[1])-Math.max(o2[0],o2[1]) : min1-min2;
        }
    };

    /**
     * Returns a graph with specified number of vertices and edges
     * @param n number of vertices
     * @param nEdges number of edges
     * @param directed whether resulting graph is directed
     * @return directed or undirected graph with randomly chosen edges
     */
    public static Graph<Integer> getRandomInstance(int n, int nEdges, boolean directed) {
        // TODO - check for appropriate number of edges
        if (n < 0 || nEdges < 0) throw new IllegalArgumentException("Numbers must be positive! (n,e)=("+n+","+nEdges+")");
        TreeSet<Integer[]> edges = new TreeSet<Integer[]>(directed ? PAIR_COMPARE : PAIR_COMPARE_UNDIRECTED);
        Integer[] potential;
        for (int i = 0; i < nEdges; i++) {
            do {
                potential = new Integer[] { (int)(n * Math.random()), (int)(n * Math.random()) };
            } while ((directed && potential[0]==potential[1]) || edges.contains(potential));
            edges.add(potential);
        }
        return getInstance(directed, intList(n), edges);
    }

    //
    // DUPLICATION METHODS
    //

    /**
     * Creates a copy of the specified graph by iterating through all possible adjacencies.
     * Also optimizes the underlying representation by choosing either a sparse graph or a matrix
     * graph representation. If the input graph has extra properties, such as node labels,
     * they are not included in the copy.
     * @param graph a graph
     * @return an instance of the graph with the same vertices and edges, but a new copy of it
     */
    public static <V> Graph<V> copyGraph(Graph<V> graph) {
        boolean directed = graph.isDirected();
        ArrayList<V> vertices = new ArrayList<V>(graph.nodes());
        ArrayList<V[]> edges = new ArrayList<V[]>(graph.edgeNumber());
        for (V v1 : vertices)
            for (V v2 : vertices)
                if (graph.adjacent(v1, v2)) {
                    V[] arr = (V[]) Array.newInstance(v1.getClass(), 2);
                    arr[0] = v1;
                    arr[1] = v2;
                    edges.add(arr);
                }
        return getInstance(directed, vertices, edges);
    }

    //
    // ADJACENCY MATRIX METHODS
    //

    /**
     * Computes product of two matrices of integers
     * First entry is row, second is column.
     * Requires # columns in m1 equal to number of rows in m2
     */
    private static int[][] matrixProduct(int[][] m1, int[][] m2) {
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

    /**
     * Computes adjacency matrix of a graph
     * @param <V> type of vertex in the graph
     * @param graph the input graph
     * @return matrix of integers describing adjacencies... contains 0's and 1's...
     *      it is symmetric when the graph is undirected, otherwise it may not be symmetric
     */
    public static <V> int[][] adjacencyMatrix(Graph<V> graph) {
        boolean directed = graph.isDirected();
        List<V> nodes = graph.nodes();
        int n = nodes.size();
        int[][] result = new int[n][n];
        for (int i1 = 0; i1 < n; i1++)
            for (int i2 = 0; i2 < n; i2++)
                if (graph.adjacent(nodes.get(i1), nodes.get(i2))) {
                    result[i1][i2] = 1;
                    if (!directed)
                        result[i2][i1] = 1;
                }
        return result;
    }

    /**
     * Computes the adjacency matrix and several of its powers.
     * @param <V> type of vertex in the graph
     * @param graph the input graph
     * @param maxPower maximum power of the adjacency matrix to include in result
     * @return matrix of integers describing adjacencies... contains 0's and 1's...
     *      it is symmetric when the graph is undirected, otherwise it may not be symmetric
     */
    public static <V> int[][][] adjacencyMatrixPowers(Graph<V> graph, int maxPower) {
        int[][] adj1 = adjacencyMatrix(graph);
        int[][][] result = new int[maxPower][adj1.length][adj1[0].length];
        result[0] = adj1;
        int cur = 2;
        while (cur <= maxPower) {
            result[cur - 1] = matrixProduct(result[cur - 2], adj1);
            cur++;
        }
        return result;
    }

    //
    // DEGREE METHODS
    //

    /**
     * Computes and returns degree distribution.
     * @return int array in which the ith entry is the number of vertices with degree i;
     *  the size is the maximum degree of any vertex in the graph
     */
    public static <V> int[] degreeDistribution(Graph<V> graph) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        int degree = -1;
        for (V v : graph.nodes()) {
            degree = graph.degree(v);
            while (result.size() < degree+1)
                result.add(0);
            result.set(degree, result.get(degree)+1);
        }
        int[] result2 = new int[result.size()];
        for (int i = 0; i < result2.length; i++)
            result2[i] = result.get(i);
        return result2;
    }

    //
    // GEODESIC & SPANNING TREE METHODS
    //

    /**
     * Computes and creates a tree describing geodesic distances from a specified vertex.
     * Choice of geodesic when multiple are possible is unspecified.
     * The graph only contains the vertices that are in the same component as the starting
     * vertex (forward component if directed).
     * @param graph the starting graph
     * @param vertex the starting vertex
     * @return graph with objects associated to each vertex that describe the distance
     *    from the main vertex.
     */
    public static <V> NodeValueGraph<V,Integer> geodesicTree(Graph<V> graph, V vertex) {
        return geodesicTree(graph, vertex, Integer.MAX_VALUE);
    }

    /**
     * Computes and creates a tree describing geodesic distances from a specified vertex,
     * up through a distance specified by the max parameter.
     * Choice of geodesic when multiple are possible is unspecified.
     * The graph only contains the vertices that are in the same component as the starting
     * vertex (forward component if directed).
     * @param graph the starting graph
     * @param vertex the starting vertex
     * @param max the maximum distance to proceed from the starting vertex
     * @return graph with objects associated to each vertex that describe the distance
     *    from the main vertex.
     */
    public static <V> NodeValueGraph<V,Integer> geodesicTree(Graph<V> graph, V vertex, int max) {
        // vertices left to add
        ArrayList<V> remaining = new ArrayList<V>(graph.nodes());
        // vertices added already, by distance
        ArrayList<HashSet<V>> added = new ArrayList<HashSet<V>>();
        // edges in tree
        ArrayList<V[]> edges = new ArrayList<V[]>();
        // stores size of remaining vertices
        int sRemaining = -1;

        remaining.remove(vertex);
        added.add(new HashSet<V>(Arrays.asList(vertex)));
        while (sRemaining != remaining.size() && added.size() < (max == Integer.MAX_VALUE ? max : max+1) ) {
            sRemaining = remaining.size();
            added.add(new HashSet<V>());
            for (V v1 : added.get(added.size()-2)) {
                HashSet<V> toRemove = new HashSet<V>();
                for (V v2 : remaining)
                    if (graph.adjacent(v1, v2)) {
                        toRemove.add(v2);
                        added.get(added.size()-1).add(v2);
                        V[] arr = (V[]) Array.newInstance(v1.getClass(), 2);
                        arr[0] = v1; arr[1] = v2;
                        edges.add(arr);
                    }
                remaining.removeAll(toRemove);
            }
        }
        
        ArrayList<V> vertices = new ArrayList<V>();
        for (HashSet<V> hs : added) vertices.addAll(hs);
        
        Graph<V> baseGraph = getInstance(graph.isDirected(), vertices, edges);
        NodeValueGraphWrapper<V,Integer> result = new NodeValueGraphWrapper<V,Integer>(baseGraph);
        for (int i = 0; i < added.size(); i++)
            for (V v : added.get(i))
                result.setValue(v, i);
        return result;
    }

    /**
     * Finds geodesic distance between two vertices in a graph
     * @param graph the graph
     * @param start first vertex
     * @param end second vertex
     * @return the geodesic distance between the vertices, or 0 if they are the same vertex, or -1 if they are not connected
     */
    public static <V> int geodesicDistance(Graph<V> graph, V start, V end) {
        if (start.equals(end))
            return 0;
        if (!(graph.contains(start) && graph.contains(end)))
            return -1;

        // vertices left to add
        ArrayList<V> remaining = new ArrayList<V>(graph.nodes());
        // vertices added already, by distance
        ArrayList<HashSet<V>> added = new ArrayList<HashSet<V>>();
        // stores size of remaining vertices
        int sRemaining;

        remaining.remove(start);
        added.add(new HashSet<V>(Arrays.asList(start)));
        do {
            sRemaining = remaining.size();
            added.add(new HashSet<V>());
            for (V v1 : added.get(added.size()-2)) {
                HashSet<V> toRemove = new HashSet<V>();
                for (V v2 : remaining)
                    if (graph.adjacent(v1, v2)) {
                        if (v2.equals(end))
                            return added.size()-1;
                        toRemove.add(v2);
                        added.get(added.size()-1).add(v2);
                    }
                remaining.removeAll(toRemove);
            }
        } while (sRemaining != remaining.size());
        
        return -1;
    }

    //
    // NEIGHBORHOOD & COMPONENT METHODS
    //

    /**
     * Computes neighborhood about provided vertex up to a given radius,
     * as a set of vertices. The result <b>always includes</b> the vertex itself.
     * @param graph the graph
     * @param vertex the starting vertex
     * @param radius the maximum distance to consider
     * @return a list containing the vertices within the neighborhood
     */
    public static <V> List<V> neighborhood(Graph<V> graph, V vertex, int radius) {
        return geodesicTree(graph, vertex, radius).nodes();
    }

    /**
     * Computes component of specified vertex in the graph
     * (only the forward component if the graph is directed).
     * @param graph the graph under consideration
     * @param vertex the starting vertex
     * @return list of other vertices in the specified vertex's component
     */
    public static <V> List<V> component(Graph<V> graph, V vertex) {
        return geodesicTree(graph, vertex).nodes();
    }

    /**
     * Computes all (connected) components of an undirected graph.
     * @param graph the graph to examine; must be undirected
     * @return list of lists of the vertices in various components
     * @throw IllegalArgumentException if provided graph is directed
     */
    public static <V> List<List<V>> components(Graph<V> graph) {
        if (graph.isDirected())
            throw new IllegalArgumentException("Currently unable to compute components for a directed graph.");
        List<List<V>> result = new ArrayList<List<V>>();
        ArrayList<V> left = new ArrayList<V>();
        left.addAll(graph.nodes());
        while (! left.isEmpty()) {
            List<V> cpt = component(graph, left.get(0));
            result.add(cpt);
            left.removeAll(cpt);
        }
        return result;
    }

    /**
     * Computes all (connected) components of an undirected graph, returning a list of new
     * graphs describing the components.
     * @param graph the graph to examine; must be undirected
     * @return list of lists of the vertices in various components
     * @throw IllegalArgumentException if provided graph is directed
     */
    public static <V> List<Graph<V>> componentGraphs(Graph<V> graph) {
        if (graph.isDirected())
            throw new IllegalArgumentException("Currently unable to compute components for a directed graph.");
        List<Graph<V>> result = new ArrayList<Graph<V>>();
        ArrayList<V> left = new ArrayList<V>();
        left.addAll(graph.nodes());
        while (! left.isEmpty()) {
            List<V> cpt = component(graph, left.get(0));
            result.add(copyGraph(new Subgraph(graph, cpt)));
            left.removeAll(cpt);
        }
        return result;
    }


}
