/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.graph.testui;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

import com.google.common.collect.Lists;
import com.googlecode.blaisemath.graph.SparseGraph;
import java.util.Collections;
import java.util.List;

/**
 * Test graph that supports mutating edges and nodes.
 *
 * @author elisha
 */
public final class MyTestGraph extends SparseGraph<String> {

    public MyTestGraph() {
        super(false, intList(1, 100), Collections.EMPTY_SET);
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

    public synchronized void connect(String v1, String v2) {
        addEdge(v1, v2);
    }

    public synchronized void disconnect(String v1, String v2) {
        removeEdge(v1, v2);
    }

    private int nextAvailableVx() {
        int i = 1;
        while (nodes.contains("" + i)) {
            i++;
        }
        return i;
    }

    public synchronized void addVertices(int number) {
        int added = 0;
        while (added < number) {
            nodes.add("" + nextAvailableVx());
            added++;
        }
        check();
    }

    public synchronized void removeVertices(int number) {
        for (int i = 0; i < number; i++) {
            String n = randV();
            nodes.remove(n);
            edges.removeAll(edgesAdjacentTo(n));
            edgeTable.rowKeySet().remove(n);
            edgeTable.columnKeySet().remove(n);
        }
        check();
    }

    public synchronized void addEdges(int number) {
        for (int i = 0; i < number; i++) {
            connect(randV(), randV());
        }
    }

    public synchronized void removeEdges(int number) {
        for (int i = 0; i < number; i++) {
            disconnect(randV(), randV());
        }
    }

    public synchronized void rewire(int lost, int gained) {
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
}
