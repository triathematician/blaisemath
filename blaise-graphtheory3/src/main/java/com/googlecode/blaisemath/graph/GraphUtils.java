package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.*;
import com.google.common.collect.Table.Cell;
import com.google.common.graph.*;
import com.googlecode.blaisemath.linear.Matrices;
import com.googlecode.blaisemath.util.Instrument;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

/**
 * Contains several utility methods for creating and analyzing graphs.
 *
 * @see Graphs
 *
 * @author Elisha Peterson
 */
public class GraphUtils {

    /** Used to sort graphs in descending order by size */
    public static final Comparator<Graph> GRAPH_SIZE_DESCENDING = (o1, o2) -> {
        int size1 = o1.nodes().size();
        int size2 = o2.nodes().size();
        int edges1 = o1.edges().size();
        int edges2 = o2.edges().size();
        return size1 == size2 && edges1 == edges2 ? o2.nodes().toString().compareTo(o1.nodes().toString())
                : size1 == size2 ? edges2 - edges1
                : size2 - size1;
    };

    // utility class
    private GraphUtils() {
    }

    //region CREATORS
    
    /**
     * Create an empty graph for vertices of appropriate type.
     * @param <V> vertex type
     * @param directed whether result should be a directed graph
     * @return new empty graph
     */
    public static <V> Graph<V> emptyGraph(boolean directed) {
        return ImmutableGraph.copyOf(directed ? GraphBuilder.directed().build() : GraphBuilder.undirected().build());
    }

    /**
     * Create a graph using a given list of nodes and edges.
     * @param <V> vertex type
     * @param directed whether result should be a directed graph
     * @param nodes nodes
     * @param edges edges
     * @return new graph
     */
    public static <V> Graph<V> createFromEdges(boolean directed, Iterable<V> nodes, Iterable<EndpointPair<V>> edges) {
        MutableGraph<V> res = directed ? GraphBuilder.directed().allowsSelfLoops(true).build() : GraphBuilder.undirected().allowsSelfLoops(true).build();
        nodes.forEach(res::addNode);
        edges.forEach(e -> res.putEdge(e.nodeU(), e.nodeV()));
        return res;
    }

    /**
     * Create a graph using a given list of nodes and edges
     * @param <V> vertex type
     * @param directed whether result should be a directed graph
     * @param nodes nodes
     * @param edges edges
     * @return new empty graph
     */
    public static <V> Graph<V> createFromArrayEdges(boolean directed, Iterable<V> nodes, Iterable<V[]> edges) {
        MutableGraph<V> res = directed ? GraphBuilder.directed().allowsSelfLoops(true).build() : GraphBuilder.undirected().allowsSelfLoops(true).build();
        nodes.forEach(res::addNode);
        edges.forEach(e -> res.putEdge(e[0], e[1]));
        return res;
    }

    /**
     * Creates an undirected copy of the specified graph.
     * @param <V> graph node type
     * @param graph a graph
     * @return undirected copy with the same collection of edges
     */
    public static <V> Graph<V> copyUndirected(Graph<V> graph) {
        return createFromEdges(false, graph.nodes(), graph.edges());
    }

    //endregion

    //region PRINTING

    /**
     * Returns string representation of specified graph
     * @param graph the graph to print
     * @return string representation of graph
     */
    public static String printGraph(Graph<?> graph) {
        return printGraph(graph, true, true);
    }

    /**
     * Returns string representation of specified graph
     * @param <V> graph node type
     * @param graph the graph to print
     * @param printNodes whether to print nodes or not
     * @param printEdges whether to print edges or not
     * @return string representation of graph
     */
    public static <V> String printGraph(Graph<V> graph, boolean printNodes, boolean printEdges) {
        if (!printEdges && !printNodes) {
            return "GRAPH";
        }
        
        Set<V> nodes = graph.nodes();
        boolean sortable = nodes.stream().allMatch(n -> n instanceof Comparable);
        if (sortable) {
            nodes = new TreeSet<V>(nodes);
        }
        StringBuilder result = new StringBuilder();
        if (printNodes) {
            result.append("NODES: ").append(nodes).append("  ");
        }
        
        if (printEdges) {
            result.append("EDGES:");
            for (V v : nodes) {
                result.append(" ").append(v).append(": ")
                        .append(sortable ? new TreeSet(graph.successors(v)) : graph.successors(v));
            }
        }
        return result.toString().trim();
    }

    //endregion

    //region SUBGRAPHS
    
    /**
     * Extract the core graph from a parent graph, consisting of only nodes
     * with degree at least 2.
     * @param <V> graph node type
     * @param parent parent graph
     * @return graph with isolates and leaves pruned
     */
    public static <V> Graph<V> core(Graph<V> parent) {
        Set<V> cNodes = Sets.newLinkedHashSet();
        if (parent instanceof OptimizedGraph) {
            OptimizedGraph<V> og = (OptimizedGraph<V>) parent;
            cNodes.addAll(og.coreNodes());
            cNodes.addAll(og.connectorNodes());
        } else {
            for (V v : parent.nodes()) {
                if (parent.degree(v) >= 2) {
                    cNodes.add(v);
                }
            }
        }
        return Graphs.inducedSubgraph(parent, cNodes);
    }
    
    //endregion
    
    //region ADJACENCY MATRIX METHODS

    /**
     * Computes adjacency matrix of a graph
     * @param <V> graph node type
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
                result[i1][i2] = graph.isDirected() ? graph.successors(order.get(i1)).contains(order.get(i2))
                        : graph.hasEdgeConnecting(order.get(i1), order.get(i2));
            }
        }
        return result;
    }

    /**
     * Computes the adjacency matrix and several of its powers.
     * @param <V> graph node type
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

    //endregion

    //region DEGREE

    /**
     * Computes and returns degree distribution.
     * @param <V> graph node type
     * @param graph the graph
     * @return map associating degree #s with counts, sorted by degree
     */
    public static <V> Multiset<Integer> degreeDistribution(Graph<V> graph) {
        return HashMultiset.create(Iterables.transform(graph.nodes(), graph::degree));
    }

    //endregion

    //region GEODESIC & SPANNING TREE METHODS

    /**
     * Computes and creates a tree describing geodesic distances from a
     * specified vertex. Choice of geodesic when multiple are possible is
     * unspecified.
     * @param <V> graph node type
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
     * @param <V> graph node type
     * @param graph the starting graph
     * @param vertex the starting vertex
     * @param max the maximum distance to proceed from the starting vertex
     * @return graph with objects associated to each vertex that describe the
     * distance from the main vertex.
     */
    public static <V> Map<V, Integer> geodesicTree(Graph<V> graph, V vertex, int max) {
        // vertices left to add
        Set<V> remaining = Sets.newHashSet(graph.nodes());
        // vertices added already, by distance
        List<Set<V>> added = Lists.newArrayList();
        // stores size of remaining vertices
        int sRemaining = -1;
        
        int cmax = max == Integer.MAX_VALUE ? max-1 : max;

        remaining.remove(vertex);
        added.add(new HashSet<V>(Arrays.asList(vertex)));
        while (sRemaining != remaining.size() && added.size() < cmax+1) {
            sRemaining = remaining.size();
            added.add(new HashSet<V>());
            for (V v1 : added.get(added.size() - 2)) {
                Set<V> toRemove = Sets.newHashSet();
                for (V v2 : remaining) {
                    if (graph.hasEdgeConnecting(v1, v2)) {
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
     * @param <V> graph node type
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
        if (!(graph.nodes().contains(start) && graph.nodes().contains(end))) {
            return -1;
        }

        List<V> verticesToAdd = Lists.newArrayList(graph.nodes());
        List<Set<V>> verticesAdded = Lists.newArrayList();
        int verticesToAddCount;

        verticesToAdd.remove(start);
        verticesAdded.add(Sets.newHashSet(start));
        do {
            verticesToAddCount = verticesToAdd.size();
            verticesAdded.add(new HashSet<V>());
            for (V v1 : verticesAdded.get(verticesAdded.size() - 2)) {
                Set<V> toRemove = new HashSet<V>();
                for (V v2 : verticesToAdd) {
                    if (graph.hasEdgeConnecting(v1, v2)) {
                        if (v2.equals(end)) {
                            return verticesAdded.size() - 1;
                        }
                        toRemove.add(v2);
                        verticesAdded.get(verticesAdded.size() - 1).add(v2);
                    }
                }
                verticesToAdd.removeAll(toRemove);
            }
        } while (verticesToAddCount != verticesToAdd.size());

        return -1;
    }

    //endregion
    
    //region NEIGHBORHOOD & COMPONENT METHODS

    /**
     * Generates ordered set of nodes from an adjacency map
     * @param <V> graph node type
     * @param adj an adjacency map
     * @return list of nodes
     */
    public static <V> Set<V> nodes(Multimap<V,V> adj) {
        Set<V> result = Sets.newLinkedHashSet();
        result.addAll(adj.keySet());
        result.addAll(adj.values());
        return result;
    }

    /**
     * Computes neighborhood about provided vertex up to a given radius, as a
     * set of vertices. The result <b>always includes</b> the vertex itself.
     * @param <V> graph node type
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
     * @param <V> graph node type
     * @param dirAdj an adjacency map (may be directed)
     * @return set of components, as a set of sets
     */
    public static <V> Collection<Set<V>> components(Multimap<V,V> dirAdj) {
        // ensure symmetry
        Multimap<V,V> adj = HashMultimap.create();
        for (Entry<V,V> en : dirAdj.entries()) {
            adj.put(en.getKey(), en.getValue());
            adj.put(en.getValue(), en.getKey());
        }
        
        List<Set<V>> res = Lists.newArrayList();
        Set<V> toAdd = Sets.newHashSet(adj.keySet());
        while (!toAdd.isEmpty()) {
            V next = toAdd.iterator().next();
            Set<V> vComp = component(adj, next);
            res.add(vComp);
            toAdd.removeAll(vComp);
        }
        return res;
    }
    
    private static <V> Set<V> component(Multimap<V,V> adj, V v0) {
        Set<V> toSearch = Sets.newHashSet(v0);
        Set<V> res = Sets.newHashSet();
        while (!toSearch.isEmpty()) {
            Set<V> next = Sets.newHashSet();
            for (V v : toSearch) {
                next.addAll(adj.get(v));
            }
            res.addAll(toSearch);
            next.removeAll(res);
            toSearch = next;
        }
        return res;
    }

    /**
     * Generates connected components from an adjacency map.
     * @param <V> graph node type
     * @param adj an adjacency map
     * @return set of components, as a set of sets
     */
    public static <V> Collection<Set<V>> components(Table<V,V,?> adj) {
        Multimap<V,V> multimap = LinkedHashMultimap.create();
        for (Cell<V,V,?> cell : adj.cellSet()) {
            multimap.put(cell.getRowKey(), cell.getColumnKey());
        }
        return components(multimap);
    }

    /**
     * Generates connected components from a graph.
     * @param <V> graph node type
     * @param graph the graph
     * @return set of connected components
     */
    public static <V> Collection<Set<V>> components(Graph<V> graph) {
        return components(adjacencies(graph, graph.nodes()));
    }

    /**
     * Generates connected components from a subset of vertices in a graph.
     * @param <V> graph node type
     * @param graph the graph
     * @param nodes subset of nodes
     * @return set of connected components
     */
    public static <V> Collection<Set<V>> components(Graph<V> graph, Set<V> nodes) {
        return components(adjacencies(graph, nodes));
    }

    /**
     * Generates connected components as a list of subgraphs.
     * @param <V> graph node type
     * @param graph the graph of interest
     * @return set of connected component subgraphs
     */
    public static <V> Set<Graph<V>> componentGraphs(Graph<V> graph) {
        int id = Instrument.start("componentGraphs", "" + graph.nodes().size());
        Set<Graph<V>> result = new GraphComponents<>(graph, components(graph)).getComponentGraphs();
        Instrument.end(id);
        return result;

    }

    /**
     * Generates adjacency map from a subgraph.
     * @param <V> graph node type
     * @param graph the graph
     * @param nodes subset of nodes
     * @return adjacency map restricted to the given subset
     */
    public static <V> Multimap<V,V> adjacencies(Graph<V> graph, Set<V> nodes) {
        Multimap<V,V> res = LinkedHashMultimap.create();
        for (V v : nodes) {
            res.putAll(v, Sets.intersection(graph.adjacentNodes(v), nodes));
        }
        return res;
    }

    /**
     * Performs breadth-first search algorithm to enumerate the nodes in a
     *  graph, starting from the specified start node.
     * @param <V> graph node type
     * @param graph the graph under consideration
     * @param start the starting node.
     * @param numShortest a map that will be filled with info on the # of
     *  shortest paths
     * @param lengths a map that will be filled with info on the lengths of
     *  shortest paths
     * @param deque a stack (LIFO) that will be filled with elements in non-increasing
     *  order of distance
     * @param pred a map that will be filled with adjacency information for the
     *  shortest paths
     */
    public static <V> void breadthFirstSearch(Graph<V> graph, V start,
            Multiset<V> numShortest, Map<V, Integer> lengths,
            Deque<V> deque, Multimap<V,V> pred) {

        Set<V> nodes = graph.nodes();
        numShortest.add(start);
        for (V v : nodes) {
            lengths.put(v, -1);
        }
        lengths.put(start, 0);

        // breadth-first search algorithm
        Deque<V> queue = Queues.newArrayDeque();
        queue.add(start);
        while (!queue.isEmpty()) {
            V v = queue.remove();
            deque.add(v);
            for (V w : graph.adjacentNodes(v)) {
                // if w is found for the first time in the tree, add it to the queue, and adjust the length
                if (lengths.get(w) == -1) {
                    queue.add(w);
                    lengths.put(w, lengths.get(v) + 1);
                }
                // adjust the number of shortest paths to w if shortest path goes through v
                if (lengths.get(w) == lengths.get(v) + 1) {
                    numShortest.add(w, numShortest.count(v));
                    pred.get(w).add(v);
                }
            }
        }
    }

    //endregion
    
    //region CONTRACTED ELEMENTS

    /**
     * Creates a contracted graph from a parent graph, where all of a specified
     * subset of nodes are contracted to a single node
     * @param <V> graph node type
     * @param graph parent graph
     * @param contract nodes to contract
     * @param replace node to replace all contracted nodes
     * @return graph where the specified nodes have been contracted
     */
    public static <V> Graph<V> contractedGraph(Graph<V> graph, Collection<V> contract, V replace) {
        List<EndpointPair<V>> edges = Lists.newArrayList();
        for (EndpointPair<V> e : graph.edges()) {
            V node1 = contract.contains(e.nodeU()) ? replace : e.nodeU();
            V node2 = contract.contains(e.nodeV()) ? replace : e.nodeV();
            edges.add(e.isOrdered() ? EndpointPair.ordered(node1, node2) : EndpointPair.unordered(node1, node2));
        }
        return createFromEdges(graph.isDirected(), contractedNodeSet(graph.nodes(), contract, replace), edges);
    }
    
    /**
     * Contracts list of nodes, replacing all the "contract" nodes with
     * "replace".
     * @param <V> graph node type
     * @param nodes collection of nodes
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
     * @param <V> graph node type
     * @param components list of components to contract
     * @param subset subset to contract
     * @param vertex what to replace contracted subset with
     * @return contracted components
     */
    public static <V> Set<Set<V>> contractedComponents(Collection<Set<V>> components, Collection<V> subset, V vertex) {
        Set<Set<V>> result = Sets.newHashSet();
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

    //endregion
    
}
