/**
 * GraphUtils.java Created on Oct 14, 2009
 */
package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.googlecode.blaisemath.linear.Matrices;
import com.googlecode.blaisemath.util.Edge;
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
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

/**
 * Contains several utility methods for creating and analyzing graphs.
 *
 * @author Elisha Peterson
 */
public class GraphUtils {

    /** A completely empty graph */
    public static final Graph EMPTY_GRAPH = SparseGraph.createFromEdges(false, Collections.EMPTY_SET, Collections.EMPTY_SET);
    
    /** Used to sort graphs in descending order by size */
    public static final Comparator<Graph> GRAPH_SIZE_DESCENDING = new Comparator<Graph>() {
        @Override
        public int compare(Graph o1, Graph o2) {
            return -(o1.nodeCount() == o2.nodeCount() && o1.edgeCount() == o2.edgeCount() 
                        ? o1.nodes().toString().compareTo(o2.nodes().toString())
                    : o1.nodeCount() == o2.nodeCount() 
                        ? o1.edgeCount() - o2.edgeCount()
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
            result.append(" ").append(v).append(": ")
                    .append(sortable ? new TreeSet(graph.outNeighbors(v)) : graph.outNeighbors(v));
        }
        return result.toString();
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="COPY/DUPLICATION">
    //
    // DUPLICATION METHODS
    //
    
    /**
     * Creates a copy of a set of edges.
     * @param <V> graph node type
     * @param edges source edges
     * @return copy of edges
     */
    public static <V> Iterable<Edge<V>> copyEdges(Iterable<? extends Edge<V>> edges) {
        List<Edge<V>> res = Lists.newArrayList();
        for (Edge<V> e : edges) {
            res.add(new Edge<V>(e));
        }
        return res;
    }
    
    /**
     * Creates a copy of the specified graph by iterating through all possible
     * adjacencies. Also optimizes the underlying representation by choosing
     * either a sparse graph or a matrix graph representation. If the input
     * graph has extra properties, such as node labels, they are not included in
     * the copy.
     * @param <V> graph node type
     * @param graph a graph
     * @return an instance of the graph with the same vertices and edges, but a new copy of it
     */
    public static <V> Graph<V> copyAsSparseGraph(Graph<V> graph) {
        return SparseGraph.createFromEdges(graph.isDirected(), graph.nodes(), copyEdges(graph.edges()));
    }

    /**
     * Creates an undirected copy of the specified graph.
     * @param <V> graph node type
     * @param graph a graph
     * @return undirected copy with the same collection of edges
     */
    public static <V> Graph<V> copyAsUndirectedSparseGraph(Graph<V> graph) {
        return SparseGraph.createFromEdges(false, graph.nodes(), copyEdges(graph.edges()));
    }

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="SUBGRAPHS">
    //
    // SUBGRAPHS
    //
    
    /**
     * Create a subgraph of a parent graph.
     * @param <V> graph node type
     * @param parent parent graph
     * @param nodes nodes of parent to keep
     * @return new graph
     */
    public static <V> Graph<V> copySubgraph(Graph<V> parent, final Set<V> nodes) {
        Iterable<Edge<V>> filteredEdges = Iterables.filter(parent.edges(),
            new Predicate<Edge<V>>(){
                @Override
                public boolean apply(Edge<V> input) {
                    return nodes.contains(input.getNode1())
                            && nodes.contains(input.getNode2());
                }
            });
        return SparseGraph.createFromEdges(parent.isDirected(), nodes, filteredEdges);
    }
    
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
            cNodes.addAll(og.getCoreNodes());
            cNodes.addAll(og.getConnectorNodes());
        } else {
            for (V v : parent.nodes()) {
                if (parent.degree(v) >= 2) {
                    cNodes.add(v);
                }
            }
        }
        return copySubgraph(parent, cNodes);
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="ADJACENCY MATRIX">
    //
    // ADJACENCY MATRIX METHODS
    //
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
                result[i1][i2] = graph.isDirected() ? graph.outNeighbors(order.get(i1)).contains(order.get(i2))
                        : graph.adjacent(order.get(i1), order.get(i2));
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

    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="DEGREE">
    //
    // DEGREE METHODS
    //
    
    /**
     * Return function calculating degree of elements in the given graph.
     * @param <V> graph node type
     * @param graph graph
     * @return function providing degree of vertices
     */
    public static <V> Function<V,Integer> degreeFunction(final Graph<V> graph) {
        return new Function<V,Integer>() {
            @Override
            public Integer apply(V input) {
                return graph.degree(input);
            }
        };
    }

    /**
     * Computes and returns degree distribution.
     * @param <V> graph node type
     * @param graph the graph
     * @return map associating degree #s with counts, sorted by degree
     */
    public static <V> Multiset<Integer> degreeDistribution(Graph<V> graph) {
        return HashMultiset.create(Iterables.transform(graph.nodes(), degreeFunction(graph)));
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

        remaining.remove(vertex);
        added.add(new HashSet<V>(Arrays.asList(vertex)));
        while (sRemaining != remaining.size() && added.size() < (max == Integer.MAX_VALUE ? max : max + 1)) {
            sRemaining = remaining.size();
            added.add(new HashSet<V>());
            for (V v1 : added.get(added.size() - 2)) {
                Set<V> toRemove = Sets.newHashSet();
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
     * @param adj an adjacency map
     * @return set of components, as a set of sets
     */
    public static <V> Collection<Set<V>> components(Multimap<V,V> adj) {
        Map<V,Set<V>> setMap = Maps.newLinkedHashMap();
        for (Entry<V,V> en : adj.entries()) {
            V v1 = en.getKey();
            V v2 = en.getValue();
            boolean v1InSet = setMap.containsKey(v1);
            boolean v2InSet = setMap.containsKey(v2);
            
            if (v1InSet && v2InSet) {
                // check if components need to be merged
                if (setMap.get(v1) != setMap.get(v2)) {
                    Set<V> nue = Sets.newHashSet(Iterables.concat(setMap.get(v1), setMap.get(v2)));
                    for (V v : nue) {
                        setMap.put(v, nue);
                    }
                }
            } else if (v1InSet) {
                // v2 hasn't been seen before
                Set<V> set = setMap.get(v1);
                set.add(v2);
                setMap.put(v2, set);
            } else if (v2InSet) {
                // v1 hasn't been seen before
                Set<V> set = setMap.get(v2);
                set.add(v1);
                setMap.put(v1, set);
            } else {
                // create new set with v1 and v2
                Set<V> set = Sets.newHashSet(v1, v2);
                setMap.put(v1, set);
                setMap.put(v2, set);
            }
        }
        return Sets.newLinkedHashSet(setMap.values());
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
        if (graph instanceof SparseGraph) {
            return ((SparseGraph<V>) graph).getComponentInfo().getComponents();
        } else {
            return components(adjacencies(graph, graph.nodes()));
        }
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
        int id = GAInstrument.start("componentGraphs", "" + graph.nodeCount());
        Set<Graph<V>> result = graph instanceof SparseGraph 
                ? ((SparseGraph<V>) graph).getComponentInfo().getComponentGraphs()
                : new GraphComponents<V>(graph, components(graph)).getComponentGraphs();
        GAInstrument.end(id);
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
            res.putAll(v, Sets.intersection(graph.neighbors(v), nodes));
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
     * @param stack a stack that will be filled with elements in non-increasing
     *  order of distance
     * @param pred a map that will be filled with adjacency information for the
     *  shortest paths
     */
    public static <V> void breadthFirstSearch(Graph<V> graph, V start,
            Multiset<V> numShortest, Map<V, Integer> lengths,
            Stack<V> stack, Multimap<V,V> pred) {

        Set<V> nodes = graph.nodes();
        numShortest.add(start);
        for (V v : nodes) {
            lengths.put(v, -1);
        }
        lengths.put(start, 0);

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
                    numShortest.add(w, numShortest.count(v));
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
     * @param <V> graph node type
     * @param graph parent graph
     * @param contract nodes to contract
     * @param replace node to replace all contracted nodes
     * @return graph where the specified nodes have been contracted
     */
    public static <V> Graph<V> contractedGraph(Graph<V> graph, Collection<V> contract, V replace) {
        List<Edge<V>> edges = Lists.newArrayList();
        for (Edge<V> e : graph.edges()) {
            Edge<V> edge = new Edge<V>(
                    contract.contains(e.getNode1()) ? replace : e.getNode1(),
                    contract.contains(e.getNode2()) ? replace : e.getNode2());
            edges.add(edge);
        }
        return SparseGraph.createFromEdges(graph.isDirected(), contractedNodeSet(graph.nodes(), contract, replace), edges);
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
