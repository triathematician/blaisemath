/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.graph.view;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.util.Edge;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Test graph that supports mutating edges and nodes.
 *
 * @author elisha
 */
public final class MyTestGraph implements Graph<String> {

    private boolean directed = false;
    private final LinkedHashSet<String> nodes = Sets.newLinkedHashSet();
    protected final Set<Edge<String>> edges = new LinkedHashSet<Edge<String>>();
    protected final SetMultimap<String,Edge<String>> edgeIndex = HashMultimap.create();  
    protected final Table<String, String, Set<Edge<String>>> edgeTable = HashBasedTable.create();  
    
    public MyTestGraph() {
        nodes.addAll(intList(1, 100));
        for (int i = 0; i < 100; i++) {
            connect(randV(), randV());
        }
    }
    
    private static List<String> intList(int i0, int i1) {
        List<String> res = Lists.newArrayList();
        for (int i = i0; i <= i1; i++) {
            res.add(Integer.toString(i));
        }
        return res;
    }

    String randV() {
        int idx = (int) (nodes.size() * Math.random());
        return nodes.toArray(new String[0])[idx];
    }

    public void connect(String v1, String v2) {
        addEdge(v1, v2);
    }

    public void disconnect(String v1, String v2) {
        removeEdge(v1, v2);
    }

    private int nextAvailableVx() {
        int i = 1;
        while (nodes.contains("" + i)) {
            i++;
        }
        return i;
    }

    public void addVertices(int number) {
        int added = 0;
        while (added < number) {
            nodes.add("" + nextAvailableVx());
            added++;
        }
        check();
    }

    public void removeVertices(int number) {
        for (int i = 0; i < number; i++) {
            String n = randV();
            nodes.remove(n);
            edges.removeAll(edgesAdjacentTo(n));
            edgeTable.rowKeySet().remove(n);
            edgeTable.columnKeySet().remove(n);
        }
        check();
    }

    public void addEdges(int number) {
        for (int i = 0; i < number; i++) {
            connect(randV(), randV());
        }
    }

    public void removeEdges(int number) {
        for (int i = 0; i < number; i++) {
            disconnect(randV(), randV());
        }
    }

    public void rewire(int lost, int gained) {
        int e0 = edgeCount();
        for (int i = 0; i < lost; i++) {
            removeEdge(randV(), randV());
        }
        for (int i = 0; i < gained; i++) {
            addEdge(randV(), randV());
        }
        check();
//        System.out.printf("rewire -%d +%d with %d edges at end (%d added)\n", lost, gained, edgeCount(), edgeCount()-e0);
    }

    private void check() {
        if (!nodes.containsAll(edgeTable.rowKeySet())) {
            System.err.println(String.format("Adjacency table has row nodes not in the node set."));
        }
        if (!nodes.containsAll(edgeTable.columnKeySet())) {
            System.err.println(String.format("Adjacency table has column nodes not in the node set."));
        }
    }

    public boolean isDirected() {
        return false;
    }

    public int nodeCount() {
        return nodes.size();
    }

    public Set<String> nodes() {
        return nodes;
    }

    public boolean contains(String x) {
        return nodes.contains(x);
    }

    public int edgeCount() {
        return edges.size();
    }

    public Set<Edge<String>> edges() {
        return edges;
    }
    
    
    
    //<editor-fold defaultstate="collapsed" desc="add/remove Edge helpers">

    // TODO - what is the proper object to lock?
    protected final synchronized void addEdge(String x, String y) {
        Edge<String> edge = directed ? addDirectedEdge(x, y) : addUndirectedEdge(x, y);
        edges.add(edge);
        edgeIndex.put(x, edge);
        edgeIndex.put(y, edge);
    }
    
    protected Edge<String> addDirectedEdge(String x, String y) {
        if (!edgeTable.contains(x, y)) {
            edgeTable.put(x, y, new HashSet<Edge<String>>());
        }
        Edge<String> edge = new Edge<String>(x, y);
        edgeTable.get(x, y).add(edge);
        return edge;
    }
    
    protected Edge<String> addUndirectedEdge(String x, String y) {
        if (!edgeTable.contains(x, y)) {
            edgeTable.put(x, y, new HashSet<Edge<String>>());
        }
        if (!edgeTable.contains(y, x)) {
            edgeTable.put(y, x, new HashSet<Edge<String>>());
        }
        Edge.UndirectedEdge<String> edge = new Edge.UndirectedEdge<String>(x, y);
        edgeTable.get(x, y).add(edge);
        edgeTable.get(y, x).add(edge);
        return edge;
    }

    @Override
    public boolean adjacent(String x, String y) {
        if (edgeTable.contains(x, y) && !edgeTable.get(x, y).isEmpty()) {
            return true;
        }
        if (directed && edgeTable.contains(y, x) && !edgeTable.get(y, x).isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Remove edge between two vertices, if it exists
     * @param v1 first vertex
     * @param v2 second vertex
     * @return true if edge was found and removed
     */
    protected boolean removeEdge(String v1, String v2) {
        Edge<String> edge = directed ? new Edge<String>(v1, v2) : new Edge.UndirectedEdge<String>(v1, v2);
        if (edgeTable.contains(v1, v2)) {
            edgeTable.get(v1, v2).remove(edge);
        }
        if (!directed && edgeTable.contains(v2, v1)) {
            edgeTable.get(v2, v1).remove(edge);
        }
        edgeIndex.remove(v1, edge);
        edgeIndex.remove(v2, edge);
        return edges.remove(edge);
    }
    
    //</editor-fold>
    
    //
    // ADJACENCY
    //

    @Override
    public Collection<Edge<String>> edgesAdjacentTo(String x) {
        return edgeIndex.get(x);
    }

    @Override
    public Set<String> outNeighbors(String x) {
        if (!directed) {
            return neighbors(x);
        } else {
            Set<String> result = new HashSet<String>();
            for (Edge<String> e : edgesAdjacentTo(x)) {
                if (x.equals(e.getNode1())) {
                    result.add(e.getNode2());
                }
            }
            return result;
        }
    }

    public Set<String> inNeighbors(String x) {
        if (!directed) {
            return neighbors(x);
        } else {
            Set<String> result = new HashSet<String>();
            for (Edge<String> e : edgesAdjacentTo(x)) {
                if (x.equals(e.getNode2())) {
                    result.add(e.getNode1());
                }
            }
            return result;
        }
    }

    public Set<String> neighbors(String x) {
        Set<String> result = new HashSet<String>();
        for (Edge<String> e : edgesAdjacentTo(x)) {
            result.add(e.opposite(x));
        }
        return result;
    }

    public int outDegree(String x) {
        if (!directed) {
            return degree(x);
        } else {
            int result = 0;
            for (Edge<String> e : edgesAdjacentTo(x)) {
                if (x.equals(e.getNode1())) {
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
            for (Edge<String> e : edgesAdjacentTo(x)) {
                if (x.equals(e.getNode2())) {
                    result++;
                }
            }
            return result;
        }
    }

    public int degree(String x) {
        int result = 0;
        for (Edge<String> e : edgesAdjacentTo(x)) {
            // permit double counting if both vertices of edge are x
            if (x.equals(e.getNode1())) {
                result++;
            }
            if (x.equals(e.getNode2())) {
                result++;
            }
        }
        return result;
    }
}
