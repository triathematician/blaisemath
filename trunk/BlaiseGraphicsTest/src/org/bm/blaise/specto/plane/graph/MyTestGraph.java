/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.plane.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.bm.blaise.scio.graph.*;

/**
 *
 * @author elisha
 */
public class MyTestGraph implements WeightedGraph<String,Double> {

    private final List<String> vertices = Collections.synchronizedList(new ArrayList<String>());
    private final Map<String,Set<String>> adj = Collections.synchronizedMap(new TreeMap<String,Set<String>>());
    private final Map<String,Map<String,Long>> wts = Collections.synchronizedMap(new TreeMap<String,Map<String,Long>>());

    public MyTestGraph() {
        for (int i = 1; i < 100; i++) vertices.add(Integer.toString(i));
        for (int i = 0; i < 100; i++) connect(randV(), randV());
    }

    String randV() { return vertices.get((int) (vertices.size() * Math.random())); }
    void disconnect(String v1, String v2) {
        if (adj.containsKey(v1)) {
            adj.get(v1).remove(v2);
            wts.get(v1).remove(v2);
        }
        if (adj.containsKey(v2)) {
            adj.get(v2).remove(v1);
            wts.get(v2).remove(v1);
        }
    }
    void connect(String v1, String v2) {
        long t0 = System.currentTimeMillis();
        if (!adj.containsKey(v1)) {
            adj.put(v1, new TreeSet<String>());
            wts.put(v1, new TreeMap<String,Long>());
        }
        adj.get(v1).add(v2);
        wts.get(v1).put(v2, t0);
        if (!adj.containsKey(v2)) {
            adj.put(v2, new TreeSet<String>());
            wts.put(v2, new TreeMap<String,Long>());
        }
        adj.get(v2).add(v1);
        wts.get(v2).put(v1, t0);
    }

    public int order() { return vertices.size(); }
    public List<String> nodes() { return vertices; }
    public boolean contains(String x) { return vertices.contains(x); }
    public boolean isDirected() { return false; }
    public boolean adjacent(String x, String y) { return adj.containsKey(x) && adj.get(x).contains(y); }
    public int degree(String x) { return neighbors(x).size(); }
    public Set<String> neighbors(String x) { Set<String> res = adj.get(x); return (Set<String>) (res == null ? Collections.emptySet() : res); }
    public int edgeCount() { throw new UnsupportedOperationException(); }
    public Double getWeight(String x, String y) {
        Long t = wts.containsKey(x) ? (wts.get(x).containsKey(y) ? wts.get(x).get(y) : -1) : -1;
        long dt = System.currentTimeMillis() - t;
        return t == -1 ? 0.0 : Math.pow(.9, dt/6000.0);

    }
    public void setWeight(String x, String y, Double value) {}

    public void addVertices(int number) {
        synchronized(adj) { synchronized(vertices) {
            int n = Integer.valueOf(vertices.get(vertices.size()-1)) + 1;
            for (int i = n; i < n + number; i++) vertices.add(Integer.toString(i));
            check();
        } }
    }

    void removeVertices(int number) {
        synchronized(adj) { synchronized(vertices) {
            for (int i = 0; i < number; i++) {
                String n = randV();
                adj.remove(n);
                for (Set<String> s : adj.values()) s.remove(n);
                vertices.remove(n);
            }
            check();
        }}
    }

    void addEdges(int number) {
        synchronized(adj) { synchronized(vertices) {
            for (int i = 0; i < number; i++)
                connect(randV(), randV());
            check();
        }}
    }

    void removeEdges(int number) {
        synchronized(adj) { synchronized(vertices) {
            for (int i = 0; i < number; i++)
                disconnect(randV(), randV());
            check();
        }}
    }

    void rewire(int lost, int gained) {
        synchronized(adj) { synchronized (vertices) {
            for (int i = 0; i < lost; i++) disconnect(randV(), randV());
            for (int i = 0; i < gained; i++) connect(randV(), randV());
            check();
        }}
    }

    private void check() {
        for (String s : adj.keySet())
            if (!vertices.contains(s))
                System.err.println(String.format("%s not found in vertex list: %s", s, vertices));
        for (Set<String> ss : adj.values())
            if (!vertices.containsAll(ss))
                System.err.println(String.format("Connection set %s not found in vertex list: %s", ss, vertices));
    }
}
