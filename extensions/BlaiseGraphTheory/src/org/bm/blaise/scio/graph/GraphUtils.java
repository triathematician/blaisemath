/**
 * GraphUtils.java
 * Created on Oct 14, 2009
 */

package org.bm.blaise.scio.graph;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import org.bm.blaise.scio.matrix.Matrices;

/**
 * Contains several utility methods for creating and analyzing graphs.
 *
 * @author Elisha Peterson
 */
public class GraphUtils {

    // Ensure non-instantiability
    private GraphUtils() {}

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
        ArrayList<V[]> edges = new ArrayList<V[]>(graph.edgeCount());
        for (V v1 : vertices)
            for (V v2 : graph.neighbors(v1))
                if (graph.adjacent(v1, v2)) {
                    V[] arr = (V[]) Array.newInstance(v1.getClass(), 2);
                    arr[0] = v1;
                    arr[1] = v2;
                    edges.add(arr);
                }
        return GraphFactory.getGraph(directed, vertices, edges);
    }

    /** Creates an undirected copy of the specified graph. */
    public static <V> Graph<V> undirectedCopy(Graph<V> g) {
        ArrayList<V> vertices = new ArrayList<V>(g.nodes());
        ArrayList<V[]> edges = new ArrayList<V[]>(g.edgeCount());
        for (V v1 : vertices)
            for (V v2 : g.neighbors(v1)) {
                V[] arr = (V[]) Array.newInstance(v1.getClass(), 2);
                arr[0] = v1;
                arr[1] = v2;
                edges.add(arr);
            }
        return GraphFactory.getGraph(false, vertices, edges);
    }

    /** 
     * Creates an undirected view of the specified graph. Neighbor sets are
     * computed upon request by iterating through all the nodes, which may cause
     * some performance problems.
     * 
     * @param g a directed graph
     * @return an undirected view of the graph
     */
    public static <V> Graph<V> undirectedWrapper(Graph<V> g) {
        return new AbstractWrapperGraph<V>(g) {
            @Override
            public boolean isDirected() { return false; }
            @Override
            public boolean adjacent(V x, V y) { return parent.adjacent(x,y) || parent.adjacent(y,x); }
            @Override
            public int degree(V x) { return neighbors(x).size(); }
            @Override
            public Set<V> neighbors(V x) {
                if (!parent.isDirected())
                    return parent.neighbors(x);
                Set<V> result = new HashSet<V>();
                for (V v : nodes())
                    if (adjacent(x, v))
                        result.add(v);
                return result;
            }
            @Override
            public int edgeCount() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
    }

    //
    // EDGE METHODS
    //

    /**
     * Finds all the edges in a graph and returns as an array of index-pairs.
     * Indices are the same as in the order of nodes in the graph's nodes method.
     * This method runs in n^2 time, where n is the number of nodes.
     * @param graph the input graph
     * @param <V> vertex type in graph
     */
    public static <V> int[][] getEdges(Graph<V> graph) {
        long t0 = System.currentTimeMillis();
        List<int[]> result = new ArrayList<int[]>();
        List<V> l = graph.nodes();
        if (graph instanceof SparseGraph) {
            SparseGraph<V> sg = (SparseGraph<V>) graph;
            for (int i = 0; i < l.size(); i++) {
                V node1 = l.get(i);
                for (V node2 : sg.neighbors(node1))
                    result.add(new int[]{i, l.indexOf(node2)});
            }
        } else {
            for (int i = 0; i < l.size(); i++) {
                for (int j = (graph.isDirected() ? 0 : i+1); j < l.size(); j++) {
                    if (graph.adjacent(l.get(i), l.get(j)))
                        result.add(new int[]{i, j});
                }
            }
        }
        int[][] res = result.toArray(new int[][]{});
        long t1 = System.currentTimeMillis();
        if (t1-t0>20)
            System.err.println("GraphUtils.getEdges took " + (t1-t0) + "ms");
        return res;
    }
    
    /**
     * Converts a graph into a dynamic mapping
     * @param <V> vertex type in graph
     * @return graph of node adjacencies
     */
    public static <V> Map<V,Set<V>> getEdgeMap(final Graph<V> graph) {
        return new AbstractMap<V, Set<V>>() {
            @Override
            public Set<Entry<V, Set<V>>> entrySet() {
                return new AbstractSet<Entry<V, Set<V>>>() {
                    @Override
                    public Iterator<Entry<V, Set<V>>> iterator() {
                        final Iterator<V> it1 = graph.nodes().iterator();
                        return new Iterator<Entry<V, Set<V>>>() {
                            public boolean hasNext() { return it1.hasNext(); }
                            public Entry<V, Set<V>> next() { 
                                final V key = it1.next();
                                return new Entry<V, Set<V>>(){
                                    public V getKey() { return key; }
                                    public Set<V> getValue() { return graph.neighbors(key); }
                                    public Set<V> setValue(Set<V> value) { return null; }
                                }; }
                            public void remove() {}
                        };
                    }
                    @Override
                    public int size() { return graph.nodes().size(); }
                };
            }            
        };
    }

    //
    // ADJACENCY MATRIX METHODS
    //

    /**
     * Computes adjacency matrix of a graph
     * @param <V> type of vertex in the graph
     * @param graph the input graph
     * @return matrix of integers describing adjacencies... contains 0's and 1's...
     *      it is symmetric when the graph is undirectedCopy, otherwise it may not be symmetric
     */
    public static <V> boolean[][] adjacencyMatrix(Graph<V> graph) {
        List<V> nodes = graph.nodes();
        int n = nodes.size();
        boolean[][] result = new boolean[n][n];
        for (int i1 = 0; i1 < n; i1++)
            for (int i2 = 0; i2 < n; i2++)
                result[i1][i2] = graph.adjacent(nodes.get(i1), nodes.get(i2));
        return result;
    }

    /**
     * Computes the adjacency matrix and several of its powers.
     * @param <V> type of vertex in the graph
     * @param graph the input graph
     * @param maxPower maximum power of the adjacency matrix to include in result
     * @return matrix of integers describing adjacencies... contains 0's and 1's...
     *      it is symmetric when the graph is undirectedCopy, otherwise it may not be symmetric
     */
    public static <V> int[][][] adjacencyMatrixPowers(Graph<V> graph, int maxPower) {
        boolean[][] adj0 = adjacencyMatrix(graph);
        int[][] adj1 = new int[adj0.length][adj0.length];
        for (int i = 0; i < adj1.length; i++)
            for (int j = 0; j < adj1.length; j++)
                adj1[i][j] = adj0[i][j] ? 1 : 0;
        int[][][] result = new int[maxPower][adj1.length][adj1[0].length];
        result[0] = adj1;
        int cur = 2;
        while (cur <= maxPower) {
            result[cur - 1] = Matrices.matrixProduct(result[cur - 2], adj1);
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
    public static <V> ValuedGraph<V,Integer> geodesicTree(Graph<V> graph, V vertex) {
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
    public static <V> ValuedGraph<V,Integer> geodesicTree(Graph<V> graph, V vertex, int max) {
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
        
        Graph<V> baseGraph = GraphFactory.getGraph(graph.isDirected(), vertices, edges);
        ValuedGraphWrapper<V,Integer> result = new ValuedGraphWrapper<V,Integer>(baseGraph);
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
     * Generates list of nodes from an adjacency map
     * @param adj an adjacency map
     * @return list of nodes
     */
    public static <V> List<V> nodes(Map<V, Set<V>> adj) {
        Set<V> result = new HashSet<V>();
        for (V node : adj.keySet()) {
            result.add(node);
            result.addAll(adj.get(node));
        }
        return new ArrayList<V>(result);
    }
    
    
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
     * Generates connected components from an adjacency map.
     * @param adj an adjacency map
     * @return set of components, as a set of sets
     */
    public static <V> Set<Set<V>> components(Map<V, Set<V>> adj) {
        HashSet<Set<V>> result = new HashSet<Set<V>>();
        for (V node : adj.keySet()) {
            Set<V> nbhd = adj.get(node);
            boolean found = false;
            for (Set<V> component : result)
                if (component.contains(node) || !Collections.disjoint(component, nbhd)) {
                    component.add(node);
                    component.addAll(nbhd);
                    found = true;
                    break;
                }
            if (!found) {
                HashSet<V> component = new HashSet<V>();
                component.add(node);
                component.addAll(nbhd);
                result.add(component);
            }
        }
        return result;
    }
    
    /**
     * Generates connected components as a list of subgraphs.
     * @param graph the graph of interest
     * @return list of connected component subgraphs
     */
    public static <V> List<Graph<V>> getComponentGraphs(Graph<V> graph) {
        GraphComponents<V> gc = new GraphComponents<V>(graph, components(graph));
        return gc.getComponentGraphs();
        
    }
    
    /**
     * Generates adjacency map from a subgraph.
     * @param graph the graph
     * @param nodes subset of nodes
     * @return adjacency map restricted to the given subset
     */
    public static <V> Map<V, Set<V>> adjacencies(Graph<V> graph, Collection<V> nodes) {
        Map<V, Set<V>> result = new HashMap<V, Set<V>>();
        for (V v : nodes) {
            HashSet<V> nbhd = new HashSet<V>(graph.neighbors(v));
            nbhd.retainAll(nodes);
            result.put(v, nbhd);
        }
        return result;
    }
    
    /**
     * Generates connected components from a graph.
     * @param graph the graph
     * @return set of connected components
     */
    public static <V> Set<Set<V>> components(Graph<V> graph) {
        return components(adjacencies(graph, graph.nodes()));
    }
    
    /**
     * Generates connected components from a subset of vertices in a graph.
     * @param graph the graph
     * @param nodes subset of nodes
     * @return set of connected components
     */
    public static <V> Set<Set<V>> components(Graph<V> graph, Collection<V> nodes) {
        return components(adjacencies(graph, nodes));
    }

    

    /**
     * Performs breadth-first search algorithm to enumerate the nodes in a graph,
     * starting from the specified start node.
     * @param graph the graph under consideration
     * @param start the starting node.
     * @param numShortest a map that will be filled with info on the # of shortest paths
     * @param lengths a map that will be filled with info on the lengths of shortest paths
     * @param stack a stack that will be filled with elements in non-increasing order of distance 
     * @param pred a map that will be filled with adjacency information for the shortest paths
     */
    public static <V> void breadthFirstSearch(Graph<V> graph, V start,
            Map<V, Integer> numShortest, Map<V, Integer> lengths,
            Stack<V> stack, Map<V,Set<V>> pred) {

        List<V> nodes = graph.nodes();
        for (V v : nodes) numShortest.put(v, 0);
        numShortest.put(start, 1);
        for (V v : nodes) lengths.put(v, -1);
        lengths.put(start, 0);
        for (V v : nodes) pred.put(v, new HashSet<V>());

        // breadth-first search algorithm
        LinkedList<V> queue = new LinkedList<V>(); // tracks elements for search algorithm

        queue.add(start);
        while (!queue.isEmpty()) {
            V v = queue.remove();
            stack.addElement(v);
            for (V w : graph.neighbors(v)) {
                // if w is found for the first time in the tree, add it to the queue, and adjust the length
                if (lengths.get(w) == -1) {
                    queue.add(w);
                    lengths.put(w, lengths.get(v) + 1);
                }
                // adjust the number of shortest paths to w if shortest path goes through v
                if (lengths.get(w) == lengths.get(v) + 1) {
                    numShortest.put(w, numShortest.get(w) + numShortest.get(v));
                    pred.get(w).add(v);
                }
            }
        }
//        System.out.println("      lengths: " + lengths);
//        System.out.println("      #paths:  " + numShortest);
//        System.out.println("  stack: " + stack);
//        System.out.println("  preds: " + pred);

    } // METHOD breadthFirstSearch


    //
    // CONTRACTED ELEMENTS
    //
    
    /** Contracts list of nodes, replacing all the "contract" nodes with "replace". */
    public static <V> List<V> contractedNodeList(List<V> nodes, Collection<V> contract, V replace) {
        ArrayList<V> result = new ArrayList<V>(nodes);
        result.removeAll(contract);
        result.add(0, replace);
        return result;
        
    }

    /** Contracts list of components, combining all components with vertices in subset. */
    public static <V> Set<Set<V>> contractedComponents(Collection<Set<V>> components, Collection<V> subset, V vertex) {
        HashSet<Set<V>> result = new HashSet<Set<V>>();
        Set<V> contracted = new HashSet<V>();
        contracted.add(vertex);
        result.add(contracted);
        for (Set<V> c : components)
            if (Collections.disjoint(c, subset))
                result.add(new HashSet<V>(c));
            else
                contracted.addAll(c);
        return result;
    }
}
