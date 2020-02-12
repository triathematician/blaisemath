package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2020 Elisha Peterson
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
import com.google.common.graph.*;
import com.googlecode.blaisemath.graph.internal.Matrices;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

/**
 * Utility methods for creating and analyzing graphs.
 *
 * @author Elisha Peterson
 */
@SuppressWarnings("UnstableApiUsage")
public class GraphUtils {

    //region COMPARATORS

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

    //endregion

    // utility class
    private GraphUtils() {
    }

    //region CREATORS
    
    /**
     * Create an empty graph for nodes of appropriate type.
     * @param <N> node type
     * @param directed whether result should be a directed graph
     * @return new empty graph
     */
    public static <N> Graph<N> emptyGraph(boolean directed) {
        return ImmutableGraph.copyOf(directed ? GraphBuilder.directed().build() : GraphBuilder.undirected().build());
    }

    /**
     * Create a graph using a given list of nodes and edges.
     * @param <N> node type
     * @param directed whether result should be a directed graph
     * @param nodes nodes
     * @param edges edges
     * @return new graph
     */
    public static <N> Graph<N> createFromEdges(boolean directed, Iterable<N> nodes, Iterable<EndpointPair<N>> edges) {
        MutableGraph<N> res = directed ? GraphBuilder.directed().allowsSelfLoops(true).build()
                : GraphBuilder.undirected().allowsSelfLoops(true).build();
        nodes.forEach(res::addNode);
        edges.forEach(e -> res.putEdge(e.nodeU(), e.nodeV()));
        return res;
    }

    /**
     * Create a graph using a given list of nodes and edges
     * @param <N> node type
     * @param directed whether result should be a directed graph
     * @param nodes nodes
     * @param edges edges
     * @return new empty graph
     */
    public static <N> Graph<N> createFromArrayEdges(boolean directed, Iterable<N> nodes, Iterable<N[]> edges) {
        MutableGraph<N> res = directed ? GraphBuilder.directed().allowsSelfLoops(true).build()
                : GraphBuilder.undirected().allowsSelfLoops(true).build();
        nodes.forEach(res::addNode);
        edges.forEach(e -> res.putEdge(e[0], e[1]));
        return res;
    }

    /**
     * Creates an undirected copy of the specified graph.
     * @param <N> graph node type
     * @param graph a graph
     * @return undirected copy with the same collection of edges
     */
    public static <N> Graph<N> copyUndirected(Graph<N> graph) {
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
     * @param <N> graph node type
     * @param graph the graph to print
     * @param printNodes whether to print nodes or not
     * @param printEdges whether to print edges or not
     * @return string representation of graph
     */
    public static <N> String printGraph(Graph<N> graph, boolean printNodes, boolean printEdges) {
        if (!printEdges && !printNodes) {
            return "GRAPH";
        }
        
        Set<N> nodes = graph.nodes();
        boolean sortable = nodes.stream().allMatch(n -> n instanceof Comparable);
        if (sortable) {
            nodes = new TreeSet<>(nodes);
        }
        StringBuilder result = new StringBuilder();
        if (printNodes) {
            result.append("NODES: ").append(nodes).append("  ");
        }
        
        if (printEdges) {
            result.append("EDGES:");
            for (N n : nodes) {
                result.append(" ").append(n).append(": ")
                        .append(sortable ? new TreeSet(graph.successors(n)) : graph.successors(n));
            }
        }
        return result.toString().trim();
    }

    //endregion
    
    //region ADJACENCY MATRIX METHODS

    /**
     * Compute adjacency matrix of a graph.
     * @param <N> graph node type
     * @param graph the input graph
     * @param order if empty, will be filled with order of nodes; if non-empty, will be used to order nodes in graph
     * @return matrix of integers describing adjacencies... contains 0's and 1's;  it is symmetric when the graph is
     * undirected, otherwise it may not be symmetric
     */
    public static <N> boolean[][] adjacencyMatrix(Graph<N> graph, List<N> order) {
        requireNonNull(order);
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
     * @param <N> graph node type
     * @param graph the input graph
     * @param order if empty, will be filled with order of nodes; if non-empty, will be used to order nodes in graph
     * @param maxPower maximum power of the adjacency matrix to include in result
     * @return matrix of integers describing adjacencies... contains 0's and 1's... it is symmetric when the graph is
     *         undirected, otherwise it may not be symmetric
     */
    public static <N> int[][][] adjacencyMatrixPowers(Graph<N> graph, List<N> order, int maxPower) {
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
     * @param <N> graph node type
     * @param graph the graph
     * @return map associating degree #s with counts, sorted by degree
     */
    public static <N> Multiset<Integer> degreeDistribution(Graph<N> graph) {
        return GraphMetrics.distribution(graph, (g, n) -> graph.degree(n));
    }

    //endregion

    //region GEODESIC & SPANNING TREE METHODS

    /**
     * Computes and creates a tree describing geodesic distances from a specified node, traversing the graph in the direction
     * of the edges. When there are multiple paths with the same minimum length, the resulting path is unspecified.
     * @param <N> graph node type
     * @param graph the starting graph
     * @param node the starting node
     * @return map with node distance lengths
     */
    public static <N> Map<N, Integer> geodesicTree(Graph<N> graph, N node) {
        return geodesicTree(graph, node, Integer.MAX_VALUE);
    }

    /**
     * Computes and creates a tree describing geodesic distances from a specified node, up through a distance
     * specified by the max parameter. Choice of geodesic when multiple are possible is unspecified. The graph
     * only contains the nodes that are in the same component as the starting node (forward component if directed).
     * @param <N> graph node type
     * @param graph the starting graph
     * @param node the starting node
     * @param max the maximum distance to proceed from the starting node
     * @return graph with objects associated to each node that describe the distance from the main node.
     */
    public static <N> Map<N, Integer> geodesicTree(Graph<N> graph, N node, int max) {
        // nodes left to add
        Set<N> remaining = Sets.newHashSet(graph.nodes());
        // nodes added already, by distance
        List<Set<N>> added = Lists.newArrayList();
        // stores size of remaining nodes
        int sRemaining = -1;
        
        int max2 = max == Integer.MAX_VALUE ? max-1 : max;

        remaining.remove(node);
        added.add(new HashSet<>(singletonList(node)));
        while (sRemaining != remaining.size() && added.size() < max2+1) {
            sRemaining = remaining.size();
            added.add(new HashSet<>());
            for (N n1 : added.get(added.size() - 2)) {
                Set<N> toRemove = Sets.newHashSet();
                for (N n2 : remaining) {
                    if (graph.hasEdgeConnecting(n1, n2)) {
                        toRemove.add(n2);
                        added.get(added.size() - 1).add(n2);
                        N[] arr = (N[]) Array.newInstance(n1.getClass(), 2);
                        arr[0] = n1;
                        arr[1] = n2;
                    }
                }
                remaining.removeAll(toRemove);
            }
        }

        Map<N, Integer> result = new HashMap<>();
        for (int i = 0; i < added.size(); i++) {
            for (N n : added.get(i)) {
                result.put(n, i);
            }
        }
        return result;
    }

    /**
     * Finds geodesic distance between two nodes in a graph. For directed graphs, the path must traverse the graph in
     * the direction of the edges.
     * @param <N> graph node type
     * @param graph the graph
     * @param start first node
     * @param end second node
     * @return geodesic distance between the nodes, or 0 if they are the same node, or -1 if they are not connected
     */
    public static <N> int geodesicDistance(Graph<N> graph, N start, N end) {
        if (start.equals(end)) {
            return 0;
        }
        if (!(graph.nodes().contains(start) && graph.nodes().contains(end))) {
            return -1;
        }

        List<N> nodesToAdd = Lists.newArrayList(graph.nodes());
        List<Set<N>> nodesAdded = Lists.newArrayList();
        int nodesToAddCount;

        nodesToAdd.remove(start);
        nodesAdded.add(Sets.newHashSet(start));
        do {
            nodesToAddCount = nodesToAdd.size();
            nodesAdded.add(new HashSet<>());
            for (N n1 : nodesAdded.get(nodesAdded.size() - 2)) {
                Set<N> toRemove = new HashSet<>();
                for (N n2 : nodesToAdd) {
                    if (graph.hasEdgeConnecting(n1, n2)) {
                        if (n2.equals(end)) {
                            return nodesAdded.size() - 1;
                        }
                        toRemove.add(n2);
                        nodesAdded.get(nodesAdded.size() - 1).add(n2);
                    }
                }
                nodesToAdd.removeAll(toRemove);
            }
        } while (nodesToAddCount != nodesToAdd.size());

        return -1;
    }

    //endregion
    
    //region NEIGHBORHOOD & COMPONENT METHODS

    /**
     * Generate ordered set of nodes from an adjacency map.
     * @param <N> graph node type
     * @param adj an adjacency map
     * @return list of nodes
     */
    public static <N> Set<N> nodes(Multimap<N, N> adj) {
        Set<N> result = Sets.newLinkedHashSet();
        result.addAll(adj.keySet());
        result.addAll(adj.values());
        return result;
    }

    /**
     * Compute neighborhood about provided node up to a given radius, as a set of nodes. The result always includes
     * the node itself. For directed graphs, this only traverses the graph in the direction of the edges.
     * @param <N> graph node type
     * @param graph the graph
     * @param node the starting node
     * @param radius the maximum distance to consider
     * @return a list containing the nodes within the neighborhood
     */
    public static <N> Set<N> neighborhood(Graph<N> graph, N node, int radius) {
        return geodesicTree(graph, node, radius).keySet();
    }

    /**
     * Generate connected components from an adjacency map.
     * @param <N> graph node type
     * @param dirAdj an adjacency map (may be directed)
     * @return set of components, as a set of sets
     */
    public static <N> Collection<Set<N>> components(Multimap<N, N> dirAdj) {
        // ensure symmetry
        Multimap<N, N> adj = HashMultimap.create();
        for (Entry<N, N> en : dirAdj.entries()) {
            adj.put(en.getKey(), en.getValue());
            adj.put(en.getValue(), en.getKey());
        }
        
        List<Set<N>> res = Lists.newArrayList();
        Set<N> toAdd = Sets.newHashSet(adj.keySet());
        while (!toAdd.isEmpty()) {
            N next = toAdd.iterator().next();
            Set<N> nComp = component(adj, next);
            res.add(nComp);
            toAdd.removeAll(nComp);
        }
        return res;
    }
    
    private static <N> Set<N> component(Multimap<N, N> adj, N v0) {
        Set<N> toSearch = Sets.newHashSet(v0);
        Set<N> res = Sets.newHashSet();
        while (!toSearch.isEmpty()) {
            Set<N> next = Sets.newHashSet();
            for (N n : toSearch) {
                next.addAll(adj.get(n));
            }
            res.addAll(toSearch);
            next.removeAll(res);
            toSearch = next;
        }
        return res;
    }

    /**
     * Generate connected components from a graph.
     * @param <N> graph node type
     * @param graph the graph
     * @return set of connected components
     */
    public static <N> Collection<Set<N>> components(Graph<N> graph) {
        return components(adjacencies(graph, graph.nodes()));
    }

    /**
     * Generate connected components from a subset of nodes in a graph.
     * @param <N> graph node type
     * @param graph the graph
     * @param nodes subset of nodes
     * @return set of connected components
     */
    public static <N> Collection<Set<N>> components(Graph<N> graph, Set<N> nodes) {
        return components(adjacencies(graph, nodes));
    }

    /**
     * Generate connected components as a list of subgraphs.
     * @param <N> graph node type
     * @param graph the graph of interest
     * @return set of connected component subgraphs
     */
    public static <N> Set<Graph<N>> componentGraphs(Graph<N> graph) {
        return new GraphComponents<>(graph, components(graph)).componentGraphs();
    }

    /**
     * Generate adjacency map from a subgraph.
     * @param <N> graph node type
     * @param graph the graph
     * @param nodes subset of nodes
     * @return adjacency map restricted to the given subset
     */
    public static <N> Multimap<N, N> adjacencies(Graph<N> graph, Set<N> nodes) {
        Multimap<N, N> res = LinkedHashMultimap.create();
        for (N n : nodes) {
            res.putAll(n, Sets.intersection(graph.adjacentNodes(n), nodes));
        }
        return res;
    }

    /**
     * Performs breadth-first search algorithm to enumerate the nodes in a graph, starting from the specified start node.
     * @param <N> graph node type
     * @param graph the graph under consideration
     * @param start the starting node.
     * @param numShortest a map that will be filled with info on the # of shortest paths
     * @param lengths a map that will be filled with info on the lengths of shortest paths
     * @param deque a stack (LIFO) that will be filled with elements in non-increasing order of distance
     * @param adjacencies a map that will be filled with adjacency information for the shortest paths
     */
    public static <N> void breadthFirstSearch(Graph<N> graph, N start, Multiset<N> numShortest, Map<N, Integer> lengths,
                                              Deque<N> deque, Multimap<N, N> adjacencies) {
        Set<N> nodes = graph.nodes();
        numShortest.add(start);
        for (N n : nodes) {
            lengths.put(n, -1);
        }
        lengths.put(start, 0);

        // breadth-first search algorithm
        Deque<N> queue = Queues.newArrayDeque();
        queue.add(start);
        while (!queue.isEmpty()) {
            N n1 = queue.remove();
            deque.add(n1);
            for (N n2 : graph.adjacentNodes(n1)) {
                // if n2 is found for the first time in the tree, add it to the queue, and adjust the length
                if (lengths.get(n2) == -1) {
                    queue.add(n2);
                    lengths.put(n2, lengths.get(n1) + 1);
                }
                // adjust the number of shortest paths to n2 if shortest path goes through n1
                if (lengths.get(n2) == lengths.get(n1) + 1) {
                    numShortest.add(n2, numShortest.count(n1));
                    adjacencies.get(n2).add(n1);
                }
            }
        }
    }

    //endregion
    
    //region CONTRACTED ELEMENTS

    /**
     * Creates a contracted graph from a parent graph, where all of a specified
     * subset of nodes are contracted to a single node
     * @param <N> graph node type
     * @param graph parent graph
     * @param contract nodes to contract
     * @param replace node to replace all contracted nodes
     * @return graph where the specified nodes have been contracted
     */
    public static <N> Graph<N> contractedGraph(Graph<N> graph, Collection<N> contract, N replace) {
        List<EndpointPair<N>> edges = Lists.newArrayList();
        for (EndpointPair<N> e : graph.edges()) {
            N node1 = contract.contains(e.nodeU()) ? replace : e.nodeU();
            N node2 = contract.contains(e.nodeV()) ? replace : e.nodeV();
            edges.add(e.isOrdered() ? EndpointPair.ordered(node1, node2) : EndpointPair.unordered(node1, node2));
        }
        return createFromEdges(graph.isDirected(), contractedNodeSet(graph.nodes(), contract, replace), edges);
    }
    
    /**
     * Contracts list of nodes, replacing all the "contract" nodes with
     * "replace".
     * @param <N> graph node type
     * @param nodes collection of nodes
     * @param contract nodes to contract
     * @param replace node to replace all contracted nodes
     * @return node set
     */
    public static <N> Set<N> contractedNodeSet(Collection<N> nodes, Collection<N> contract, N replace) {
        Set<N> result = Sets.newHashSet(nodes);
        result.removeAll(contract);
        result.add(replace);
        return result;
    }

    /**
     * Contracts list of components, combining all components with nodes in subset.
     * @param <N> graph node type
     * @param components list of components to contract
     * @param subset subset to contract
     * @param node what to replace contracted subset with
     * @return contracted components
     */
    public static <N> Set<Set<N>> contractedComponents(Collection<Set<N>> components, Collection<N> subset, N node) {
        Set<Set<N>> result = Sets.newHashSet();
        Set<N> contracted = Sets.newHashSet();
        contracted.add(node);
        result.add(contracted);
        for (Set<N> c : components) {
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
