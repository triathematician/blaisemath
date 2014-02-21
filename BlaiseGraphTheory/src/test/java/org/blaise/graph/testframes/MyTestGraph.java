/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blaise.graph.testframes;

import com.google.common.collect.HashMultimap;
import java.util.Map;
import java.util.Set;
import org.blaise.graph.SparseGraph;
import org.blaise.util.Edge;

/**
 * Test graph that supports mutating edges and nodes.
 *
 * @author elisha
 */
public final class MyTestGraph extends SparseGraph<String> {

    public MyTestGraph() {
        super(false, HashMultimap.<String,String>create());
        for (int i = 1; i < 100; i++) {
            nodes.add(Integer.toString(i));
        }
        for (int i = 0; i < 100; i++) {
            connect(randV(), randV());
        }
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
            adjacencies.remove(n);
            for (Map<String,Set<Edge<String>>> em : adjacencies.values()) {
                em.remove(n);
            }
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
        for (String s : adjacencies.keySet()) {
            if (!nodes.contains(s)) {
                System.err.println(String.format("%s not found in vertex list: %s", s, nodes));
            }
        }
        for (Map<String,Set<Edge<String>>> em : adjacencies.values()) {
            if (!nodes.containsAll(em.keySet())) {
                System.err.println(String.format("Connection set %s not found in vertex list: %s", em.keySet(), nodes));
            }
        }
    }
}
