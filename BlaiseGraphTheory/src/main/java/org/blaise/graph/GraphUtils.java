/**
 * GraphUtils.java Created on Oct 14, 2009
 */
package org.blaise.graph;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import org.blaise.math.linear.Matrices;
import org.blaise.util.Edge;

/**
 * Contains several utility methods for creating and analyzing graphs.
 *
 * @author Elisha Peterson
 */
public class GraphUtils {
    
    /** Used to sort graphs in descending order by size */
    public static final Comparator<Graph> GRAPH_SIZE_DESCENDING = new Comparator<Graph>() {
        @Override
        public int compare(Graph o1, Graph o2) {
            return -(o1.nodeCount() == o2.nodeCount() && o1.edgeCount() == o2.edgeCount() ? o1.nodes().toString().compareTo(o2.nodes().toString())
                    : o1.nodeCount() == o2.nodeCount() ? o1.edgeCount() - o2.edgeCount()
                    : o1.nodeCount() - o2.nodeCount());
        }
    };

    // utility class
    private GraphUtils() {
    }

    //<editor-fold defaultstate="collapsed" desc="GENERIC PRINT METHODS">
    //
    // GENERIC PRINT METHODS
    //
    /**
     * Returns string representation of specified graph
     *
     * @param graph the graph to print
     * @return string representation of graph
     */
    public static <V> String printGraph(Graph<V> graph) {
        return printGraph(graph, true, true);
    }

    /**
     * Returns string representation of specified graph
     *
     * @param graph the graph to print
     * @param printNodes whether to print nodes or not
     * @param printEdges whether to print edges or not
     * @return string representation of graph
     */
    public static <V> String printGraph(Graph<V> graph, boolean printNodes, boolean printEdges) {
        if (!printEdges && !printNodes) {
            return "GRAPH";
        } else if (printNodes && !printEdges) {
            Set<V> nodes = graph.nodes();
            if (nodes.size() > 0 && nodes.iterator().next() instanceof Comparable) {
                nodes = new TreeSet<V>(nodes);
            }
            return "NODES: " + nodes;
        }

        Set<V> nodes = graph.nodes();
        boolean sortable = nodes.size() > 0 && nodes.iterator().next() instanceof Comparable;
        if (sortable) {
            nodes = new TreeSet<V>(nodes);
        }
        StringBuilder result = new StringBuilder();
        if (printNodes) {
            result.append("NODES: ").append(nodes).append("  ");
        }
        result.append("EDGES:");
        for (V v : nodes) {
            result.append(" ").append(v).append(": ").append(sortable ? new TreeSet(graph.outNeighbors(v)) : graph.outNeighbors(v));
        }
        return result.toString();
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="COPY/DUPLICATION">
    //
    // DUPLICATION METHODS
    //
    
    /**
     * Creates a copy of the specified graph by iterating through all possible
     * adjacencies. Also optimizes the underlying representation by choosing
     * either a sparse graph or a matrix graph representation. If the input
     * graph has extra properties, such as node labels, they are not included in
     * the copy.
     * @param graph a graph
     * @return an instance of the graph with the same vertices and edges, but a new copy of it
     */
    public static <V> Graph<V> copy(Graph<V> graph) {
        List<V[]> edges = Lists.newArrayList();
        for (Edge<V> e : graph.edges()) {
            V[] arr = (V[]) Array.newInstance(e.getNode1().getClass(), 2);
            arr[0] = e.getNode1();
            arr[1] = e.getNode2();
            edges.add(arr);
        }
        return new SparseGraph(graph.isDirected(), graph.nodes(), edges);
    }

    /**
     * Creates an undirected copy of the specified graph.
     * @param graph a graph
     * @return undirected copy with the same collection of edges
     */
    public static <V> Graph<V> copyUndirected(Graph<V> graph) {
        List<V[]> edges = Lists.newArrayList();
        for (Edge<V> e : graph.edges()) {
            V[] arr = (V[]) Array.newInstance(e.getNode1().getClass(), 2);
            arr[0] = e.getNode1();
            arr[1] = e.getNode2();
            edges.add(arr);
        }
        return new SparseGraph(false, graph.nodes(), edges);
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="SUBGRAPHS">
    //
    // SUBGRAPHS
    //
    
    /**
     * Create a subgraph of a parent graph.
     * @param <V> node type
     * @param parent parent graph
     * @param nodes nodes of parent to keep
     * @return new graph
     */
    public static <V> Graph<V> subgraph(Graph<V> parent, Set<V> nodes) {
        List<V[]> edges = Lists.newArrayList();
        for (Edge<V> e : parent.edges()) {
            if (nodes.contains(e.getNode1()) && nodes.contains(e.getNode2())) {
                V[] arr = (V[]) Array.newInstance(e.getNode1().getClass(), 2);
                arr[0] = e.getNode1();
                arr[1] = e.getNode2();
                edges.add(arr);
            }
        }
        return new SparseGraph(parent.isDirected(), nodes, edges);
    }
    
    /**
     * Extract the core graph from a parent graph, consisting of only nodes
     * with degree at least 2.
     * @param parent parent graph
     * @return graph with isolates and leaves pruned
     */
    public static <V> Graph<V> core(Graph<V> parent) {
        boolean directed = parent.isDirected();
        Set<V> cNodes = Sets.newLinkedHashSet();
        if (parent instanceof OptimizedGraph) {
            OptimizedGraph<V> og = (OptimizedGraph<V>) parent;
            cNodes.addAll(og.getCoreNodes());
            cNodes.addAll(og.getConnectorNodes());
        } else {
            for (V v : parent.nodes()) {
                if (parent.degree(v) >= 2) {
                    cNodes.add(v);
                }
            }
        }
        List<V[]> edges = Lists.newArrayList();
        for (Edge<V> e : parent.edges()) {
            V[] arr = (V[]) Array.newInstance(e.getNode1().getClass(), 2);
            arr[0] = e.getNode1();
            arr[1] = e.getNode2();
            edges.add(arr);
        }
        return new SparseGraph(directed, cNodes, edges);
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="ADJACENCY MATRIX">
    //
    // ADJACENCY MATRIX METHODS
    //
    /**
     * Computes adjacency matrix of a graph
     *
     * @param <V> type of vertex in the graph
     * @param graph the input graph
     * @param order if empty, will be filled with order of nodes; if non-empty, will be used to order nodes in graph
     * @return matrix of integers describing adjacencies... contains 0's and
     * 1's... it is symmetric when the graph is copyUndirected, otherwise it may
     * not be symmetric
     */
    public static <V> boolean[][] adjacencyMatrix(Graph<V> graph, List<V> order) {
        if (order == null) {
            throw new IllegalArgumentException();
        }
        if (order.isEmpty()) {
            order.addAll(graph.nodes());
        }
        int n = order.size();
        boolean[][] result = new boolean[n][n];
        for (int i1 = 0; i1 < n; i1++) {
            for (int i2 = 0; i2 < n; i2++) {
                result[i1][i2] = graph.isDirected() ? graph.outNeighbors(order.get(i1)).contains(order.get(i2))
                        : graph.adjacent(order.get(i1), order.get(i2));
            }
        }
        return result;
    }

    /**
     * Computes the adjacency matrix and several of its powers.
     *
     * @param <V> type of vertex in the graph
     * @param graph the input graph
     * @param order if empty, will be filled with order of nodes; if non-empty, will be used to order nodes in graph
     * @param maxPower maximum power of the adjacency matrix to include in
     * result
     * @return matrix of integers describing adjacencies... contains 0's and
     * 1's... it is symmetric when the graph is copyUndirected, otherwise it may
     * not be symmetric
     */
    public static <V> int[][][] adjacencyMatrixPowers(Graph<V> graph, List<V> order, int maxPower) {
        boolean[][] adj0 = adjacencyMatrix(graph, order);
        int[][] adj1 = new int[adj0.length][adj0.length];
        for (int i = 0; i < adj1.length; i++) {
            for (int j = 0; j < adj1.length; j++) {
                adj1[i][j] = adj0[i][j] ? 1 : 0;
            }
        }
        int[][][] result = new int[maxPower][adj1.length][adj1[0].length];
        result[0] = adj1;
        int cur = 2;
        while (cur <= maxPower) {
            result[cur - 1] = Matrices.matrixProduct(result[cur - 2], adj1);
            cur++;
        }
        return result;
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="DEGREE">
    //
    // DEGREE METHODS
    //

    /**
     * Computes and returns degree distribution.
     * @param graph the graph
     * @return map associating degree #s with counts, sorted by degree
     */
    public static <V> Multiset<Integer> degreeDistribution(Graph<V> graph) {
        Multiset<Integer> res = HashMultiset.create();
        for (V v : graph.nodes()) {
            res.add(graph.degree(v));
        }
        return res;
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="GEODESIC & SPANNING TREE METHODS">
    //
    // GEODESIC & SPANNING TREE METHODS
    //
    /**
     * Computes and creates a tree describing geodesic distances from a
     * specified vertex. Choice of geodesic when multiple are possible is
     * unspecified.
     *
     * @param graph the starting graph
     * @param vertex the starting vertex
     * @return map with vertex distance lengths
     */
    public static <V> Map<V, Integer> geodesicTree(Graph<V> graph, V vertex) {
        return geodesicTree(graph, vertex, Integer.MAX_VALUE);
    }

    /**
     * Computes and creates a tree describing geodesic distances from a
     * specified vertex, up through a distance specified by the max parameter.
     * Choice of geodesic when multiple are possible is unspecified. The graph
     * only contains the vertices that are in the same component as the starting
     * vertex (forward component if directed).
     *
     * @param graph the starting graph
     * @param vertex the starting vertex
     * @param max the maximum distance to proceed from the starting vertex
     * @return graph with objects associated to each vertex that describe the
     * distance from the main vertex.
     */
    public static <V> Map<V, Integer> geodesicTree(Graph<V> graph, V vertex, int max) {
        // vertices left to add
        HashSet<V> remaining = Sets.newHashSet(graph.nodes());
        // vertices added already, by distance
        ArrayList<Set<V>> added = Lists.newArrayList();
        // stores size of remaining vertices
        int sRemaining = -1;

        remaining.remove(vertex);
        added.add(new HashSet<V>(Arrays.asList(vertex)));
        while (sRemaining != remaining.size() && added.size() < (max == Integer.MAX_VALUE ? max : max + 1)) {
            sRemaining = remaining.size();
            added.add(new HashSet<V>());
            for (V v1 : added.get(added.size() - 2)) {
                HashSet<V> toRemove = Sets.newHashSet();
                for (V v2 : remaining) {
                    if (graph.adjacent(v1, v2)) {
                        toRemove.add(v2);
                        added.get(added.size() - 1).add(v2);
                        V[] arr = (V[]) Array.newInstance(v1.getClass(), 2);
                        arr[0] = v1;
                        arr[1] = v2;
                    }
                }
                remaining.removeAll(toRemove);
            }
        }

        Map<V, Integer> result = new HashMap<V, Integer>();
        for (int i = 0; i < added.size(); i++) {
            for (V v : added.get(i)) {
                result.put(v, i);
            }
        }
        return result;
    }

    /**
     * Finds geodesic distance between two vertices in a graph
     *
     * @param graph the graph
     * @param start first vertex
     * @param end second vertex
     * @return the geodesic distance between the vertices, or 0 if they are the
     * same vertex, or -1 if they are not connected
     */
    public static <V> int geodesicDistance(Graph<V> graph, V start, V end) {
        if (start.equals(end)) {
            return 0;
        }
        if (!(graph.contains(start) && graph.contains(end))) {
            return -1;
        }

        // vertices left to add
        ArrayList<V> remaining = Lists.newArrayList(graph.nodes());
        // vertices added already, by distance
        ArrayList<HashSet<V>> added = Lists.newArrayList();
        // stores size of remaining vertices
        int sRemaining;

        remaining.remove(start);
        added.add(Sets.newHashSet(start));
        do {
            sRemaining = remaining.size();
            added.add(new HashSet<V>());
            for (V v1 : added.get(added.size() - 2)) {
                HashSet<V> toRemove = new HashSet<V>();
                for (V v2 : remaining) {
                    if (graph.adjacent(v1, v2)) {
                        if (v2.equals(end)) {
                            return added.size() - 1;
                        }
                        toRemove.add(v2);
                        added.get(added.size() - 1).add(v2);
                    }
                }
                remaining.removeAll(toRemove);
            }
        } while (sRemaining != remaining.size());

        return -1;
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="NEIGHBORHOOD & COMPONENT">
    //
    // NEIGHBORHOOD & COMPONENT METHODS
    //
    
    /**
     * Generates list of nodes from an adjacency map
     * @param adj an adjacency map
     * @return list of nodes
     */
    public static <V> List<V> nodes(Multimap<V,V> adj) {
        Set<V> result = Sets.newLinkedHashSet();
        result.addAll(adj.keySet());
        result.addAll(adj.values());
        return new ArrayList<V>(result);
    }

    /**
     * Computes neighborhood about provided vertex up to a given radius, as a
     * set of vertices. The result <b>always includes</b> the vertex itself.
     * @param graph the graph
     * @param vertex the starting vertex
     * @param radius the maximum distance to consider
     * @return a list containing the vertices within the neighborhood
     */
    public static <V> Set<V> neighborhood(Graph<V> graph, V vertex, int radius) {
        return geodesicTree(graph, vertex, radius).keySet();
    }

    /**
     * Generates connected components from an adjacency map.
     * @param adj an adjacency map
     * @return set of components, as a set of sets
     */
    public static <V> Collection<Set<V>> components(Multimap<V,V> adj) {
        List<Set<V>> result = Lists.newArrayList();
        for (V node : adj.keySet()) {
            Collection<V> nbhd = adj.get(node);
            Set<Set<V>> joins = Sets.newHashSet();

            // look for components with common elements
            for (Set<V> component : result) {
                if (component.contains(node) || !Collections.disjoint(component, nbhd)) {
                    joins.add(component);
                }
            }

            if (joins.size() == 1) {
                // if just one common element found, add stuff to it
                joins.iterator().next().addAll(nbhd);
                joins.iterator().next().add(node);
            } else {
                // otherwise create a new set from all the joined elements, and remove the old ones
                HashSet<V> nue = Sets.newHashSet(nbhd);
                nue.add(node);
                for (Set<V> j : joins) {
                    nue.addAll(j);
                    if (!result.remove(j)) {
                        throw new IllegalStateException("Failed to remove component "+j);
                    }
                }
                result.add(nue);
            }
        }
        return result;
    }

    /**
     * Generates connected components from an adjacency map.
     * @param adj an adjacency map
     * @return set of components, as a set of sets
     */
    public static <V,W> Collection<Set<V>> componentsEdgeMap(Map<V,Map<V,W>> adj) {
        List<Set<V>> result = Lists.newArrayList();
        for (V node : adj.keySet()) {
            Set<V> nbhd = adj.get(node).keySet();
            Set<Set<V>> joins = Sets.newHashSet();

            // look for components with common elements
            for (Set<V> component : result) {
                if (component.contains(node) || !Collections.disjoint(component, nbhd)) {
                    joins.add(component);
                }
            }

            if (joins.size() == 1) {
                // if just one common element found, add stuff to it
                joins.iterator().next().addAll(nbhd);
                joins.iterator().next().add(node);
            } else {
                // otherwise create a new set from all the joined elements, and remove the old ones
                HashSet<V> nue = Sets.newHashSet(nbhd);
                nue.add(node);
                for (Set<V> j : joins) {
                    nue.addAll(j);
                    if (!result.remove(j)) {
                        throw new IllegalStateException("Failed to remove component "+j);
                    }
                }
                result.add(nue);
            }
        }
        return result;
    }

    /**
     * Generates connected components as a list of subgraphs.
     *
     * @param graph the graph of interest
     * @return set of connected component subgraphs
     */
    public static <V> Set<Graph<V>> getComponentGraphs(Graph<V> graph) {
        int id = GAInstrument.start("componentGraphs", "" + graph.nodeCount());
        Set<Graph<V>> result = graph instanceof SparseGraph ? ((SparseGraph) graph).getComponentInfo().getComponentGraphs()
                : new GraphComponents<V>(graph, components(graph)).getComponentGraphs();
        GAInstrument.end(id);
        return result;

    }

    /**
     * Generates adjacency map from a subgraph.
     *
     * @param graph the graph
     * @param nodes subset of nodes
     * @return adjacency map restricted to the given subset
     */
    public static <V> Multimap<V,V> adjacencies(Graph<V> graph, Collection<V> nodes) {
        Multimap<V,V> res = LinkedHashMultimap.create();
        for (V v : nodes) {
            HashSet<V> nbhd = Sets.newHashSet(graph.neighbors(v));
            nbhd.retainAll(nodes);
            res.putAll(v, nbhd);
        }
        return res;
    }

    /**
     * Generates connected components from a graph.
     *
     * @param graph the graph
     * @return set of connected components
     */
    public static <V> Collection<Set<V>> components(Graph<V> graph) {
        if (graph instanceof SparseGraph) {
            return ((SparseGraph) graph).getComponentInfo().getComponents();
        } else {
            return components(adjacencies(graph, graph.nodes()));
        }
    }

    /**
     * Generates connected components from a subset of vertices in a graph.
     *
     * @param graph the graph
     * @param nodes subset of nodes
     * @return set of connected components
     */
    public static <V> Collection<Set<V>> components(Graph<V> graph, Collection<V> nodes) {
        return components(adjacencies(graph, nodes));
    }

    /**
     * Performs breadth-first search algorithm to enumerate the nodes in a
     * graph, starting from the specified start node.
     *
     * @param graph the graph under consideration
     * @param start the starting node.
     * @param numShortest a map that will be filled with info on the # of
     * shortest paths
     * @param lengths a map that will be filled with info on the lengths of
     * shortest paths
     * @param stack a stack that will be filled with elements in non-increasing
     * order of distance
     * @param pred a map that will be filled with adjacency information for the
     * shortest paths
     */
    public static <V> void breadthFirstSearch(Graph<V> graph, V start,
            Map<V, Integer> numShortest, Map<V, Integer> lengths,
            Stack<V> stack, Map<V, Set<V>> pred) {

        Set<V> nodes = graph.nodes();
        for (V v : nodes) {
            numShortest.put(v, 0);
        }
        numShortest.put(start, 1);
        for (V v : nodes) {
            lengths.put(v, -1);
        }
        lengths.put(start, 0);
        for (V v : nodes) {
            pred.put(v, new HashSet<V>());
        }

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
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="CONTRACTING ELEMENTS">
    //
    // CONTRACTED ELEMENTS
    //
    
    /**
     * Creates a contracted graph from a parent graph, where all of a specified
     * subset of nodes are contracted to a single node
     * @param graph parent graph
     * @param contract nodes to contract
     * @param replace node to replace all contracted nodes
     * @return graph where the specified nodes have been contracted
     */
    public static <V> Graph<V> contractedGraph(Graph<V> graph, Collection<V> contract, V replace) {
        ArrayList<V[]> edges = Lists.newArrayList();
        for (Edge<V> e : graph.edges()) {
            V[] arr = (V[]) Array.newInstance(e.getNode1().getClass(), 2);
            arr[0] = contract.contains(e.getNode1()) ? replace : e.getNode1();
            arr[1] = contract.contains(e.getNode2()) ? replace : e.getNode2();
            edges.add(arr);
        }
        return new SparseGraph(graph.isDirected(), contractedNodeSet(graph.nodes(), contract, replace), edges);
    }
    
    /**
     * Contracts list of nodes, replacing all the "contract" nodes with
     * "replace".
     * @param contract nodes to contract
     * @param replace node to replace all contracted nodes
     * @return node set
     */
    public static <V> Set<V> contractedNodeSet(Collection<V> nodes, Collection<V> contract, V replace) {
        Set<V> result = Sets.newHashSet(nodes);
        result.removeAll(contract);
        result.add(replace);
        return result;

    }

    /**
     * Contracts list of components, combining all components with vertices in subset.
     */
    public static <V> Set<Set<V>> contractedComponents(Collection<Set<V>> components, Collection<V> subset, V vertex) {
        HashSet<Set<V>> result =Sets.newHashSet();
        Set<V> contracted = Sets.newHashSet();
        contracted.add(vertex);
        result.add(contracted);
        for (Set<V> c : components) {
            if (Collections.disjoint(c, subset)) {
                result.add(Sets.newHashSet(c));
            } else {
                contracted.addAll(c);
            }
        }
        return result;
    }
    
    //</editor-fold>
    
}
