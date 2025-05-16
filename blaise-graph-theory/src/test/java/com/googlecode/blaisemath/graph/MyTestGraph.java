package com.googlecode.blaisemath.graph;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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
import com.google.common.graph.ElementOrder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;

import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@SuppressWarnings({"UnstableApiUsage", "NullableProblems"})
final class MyTestGraph implements Graph<String> {

    private final boolean directed = false;
    private final LinkedHashSet<String> nodes = Sets.newLinkedHashSet();
    private final Set<EndpointPair<String>> edges = new LinkedHashSet<>();
    private final SetMultimap<String, EndpointPair<String>> edgeIndex = HashMultimap.create();
    private final Table<String, String, Set<EndpointPair<String>>> edgeTable = HashBasedTable.create();

    public MyTestGraph() {
        nodes.addAll(intList(1, 100));
        for (int i = 0; i < 100; i++) {
            connect(randomNode(), randomNode());
        }
    }
    
    @SuppressWarnings("SameParameterValue")
    private static List<String> intList(int i0, int i1) {
        return IntStream.rangeClosed(i0, i1).mapToObj(Integer::toString).collect(toList());
    }

    private void connect(String v1, String v2) {
        addEdge(v1, v2);
    }

    private void disconnect(String v1, String v2) {
        removeEdge(v1, v2);
    }

    public void addNodes(int number) {
        int added = 0;
        while (added < number) {
            nodes.add("" + nextAvailableNode());
            added++;
        }
        check();
    }

    public void removeNodes(int number) {
        for (int i = 0; i < number; i++) {
            String n = randomNode();
            nodes.remove(n);
            edges.removeAll(incidentEdges(n));
            edgeTable.rowKeySet().remove(n);
            edgeTable.columnKeySet().remove(n);
        }
        check();
    }

    public void addEdges(int number) {
        for (int i = 0; i < number; i++) {
            connect(randomNode(), randomNode());
        }
    }

    public void removeEdges(int number) {
        for (int i = 0; i < number; i++) {
            disconnect(randomNode(), randomNode());
        }
    }

    public void rewire(int lost, int gained) {
        for (int i = 0; i < lost; i++) {
            removeEdge(randomNode(), randomNode());
        }
        for (int i = 0; i < gained; i++) {
            addEdge(randomNode(), randomNode());
        }
        check();
    }

    private String randomNode() {
        int idx = (int) (nodes.size() * Math.random());
        return nodes.toArray(new String[0])[idx];
    }

    private int nextAvailableNode() {
        int i = 1;
        while (nodes.contains("" + i)) {
            i++;
        }
        return i;
    }

    private void check() {
        if (!nodes.containsAll(edgeTable.rowKeySet())) {
            System.err.println("Adjacency table has row nodes not in the node set.");
        }
        if (!nodes.containsAll(edgeTable.columnKeySet())) {
            System.err.println("Adjacency table has column nodes not in the node set.");
        }
    }

    public boolean isDirected() {
        return false;
    }

    public Set<String> nodes() {
        return nodes;
    }

    private int edgeCount() {
        return edges.size();
    }

    public Set<EndpointPair<String>> edges() {
        return edges;
    }
    
    //region MUTATORS

    private synchronized void addEdge(String x, String y) {
        EndpointPair<String> edge = directed ? addDirectedEdge(x, y) : addUndirectedEdge(x, y);
        edges.add(edge);
        edgeIndex.put(x, edge);
        edgeIndex.put(y, edge);
    }
    
    private EndpointPair<String> addDirectedEdge(String x, String y) {
        if (!edgeTable.contains(x, y)) {
            edgeTable.put(x, y, new HashSet<>());
        }
        EndpointPair<String> edge =  EndpointPair.ordered(x, y);
        edgeTable.get(x, y).add(edge);
        return edge;
    }
    
    private EndpointPair<String> addUndirectedEdge(String x, String y) {
        if (!edgeTable.contains(x, y)) {
            edgeTable.put(x, y, new HashSet<>());
        }
        if (!edgeTable.contains(y, x)) {
            edgeTable.put(y, x, new HashSet<>());
        }
        EndpointPair<String> edge = EndpointPair.unordered(x, y);
        edgeTable.get(x, y).add(edge);
        edgeTable.get(y, x).add(edge);
        return edge;
    }

    @Override
    public boolean hasEdgeConnecting(String x, String y) {
        if (edgeTable.contains(x, y) && !edgeTable.get(x, y).isEmpty()) {
            return true;
        }
        return directed && edgeTable.contains(y, x) && !edgeTable.get(y, x).isEmpty();
    }

    @Override
    public boolean hasEdgeConnecting(EndpointPair<String> edge) {
        return hasEdgeConnecting(edge.nodeU(), edge.nodeV());
    }

    /**
     * Remove edge between two nodes, if it exists
     * @param n1 first node
     * @param n2 second node
     */
    private void removeEdge(String n1, String n2) {
        EndpointPair<String> edge = directed ? EndpointPair.ordered(n1, n2) : EndpointPair.unordered(n1, n2);
        if (edgeTable.contains(n1, n2)) {
            edgeTable.get(n1, n2).remove(edge);
        }
        if (!directed && edgeTable.contains(n2, n1)) {
            edgeTable.get(n2, n1).remove(edge);
        }
        edgeIndex.remove(n1, edge);
        edgeIndex.remove(n2, edge);
        edges.remove(edge);
    }
    
    //endregion

    @Override
    public boolean allowsSelfLoops() {
        return false;
    }

    @Override
    public ElementOrder<String> nodeOrder() {
        return ElementOrder.insertion();
    }

    @Override
    public ElementOrder<String> incidentEdgeOrder() {
        return ElementOrder.insertion();
    }

    @Override
    public Set<EndpointPair<String>> incidentEdges(String x) {
        return edgeIndex.get(x);
    }

    @Override
    public Set<String> successors(String x) {
        if (!directed) {
            return adjacentNodes(x);
        } else {
            Set<String> result = new HashSet<>();
            for (EndpointPair<String> e : incidentEdges(x)) {
                if (x.equals(e.nodeU())) {
                    result.add(e.nodeV());
                }
            }
            return result;
        }
    }

    public Set<String> predecessors(String x) {
        if (!directed) {
            return adjacentNodes(x);
        } else {
            Set<String> result = new HashSet<>();
            for (EndpointPair<String> e : incidentEdges(x)) {
                if (x.equals(e.nodeV())) {
                    result.add(e.nodeU());
                }
            }
            return result;
        }
    }

    @Override
    public Set<String> adjacentNodes(String x) {
        Set<String> result = new HashSet<>();
        for (EndpointPair<String> e : incidentEdges(x)) {
            result.add(e.adjacentNode(x));
        }
        return result;
    }

    public int outDegree(String x) {
        if (!directed) {
            return degree(x);
        } else {
            int result = 0;
            for (EndpointPair<String> e : incidentEdges(x)) {
                if (x.equals(e.nodeU())) {
                    result++;
                }
            }
            return result;
        }
    }

    public int inDegree(String x) {
        if (!directed) {
            return degree(x);
        } else {
            int result = 0;
            for (EndpointPair<String> e : incidentEdges(x)) {
                if (x.equals(e.nodeV())) {
                    result++;
                }
            }
            return result;
        }
    }

    public int degree(String x) {
        int result = 0;
        for (EndpointPair<String> e : incidentEdges(x)) {
            // permit double counting if both nodes of edge are x
            if (x.equals(e.nodeU())) {
                result++;
            }
            if (x.equals(e.nodeV())) {
                result++;
            }
        }
        return result;
    }
}
