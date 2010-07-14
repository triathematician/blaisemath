/*
 * IntervalLongitudinalGraph.java
 * Created Jul 5, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * A graph whose nodes and edges all come with associated time intervals. When queried
 * for a slice, this class returns a view of just the elements of the underlying graph
 * that exist at the specified time. Implemented as a sparse graph.
 *
 * @param <V> the type of the nodes
 * 
 * @author Elisha Peterson
 */
public class IntervalLongitudinalGraph<V> implements LongitudinalGraph<V> {

    /** Whether graph is directed. */
    boolean directed;
    /** Stores the start/stop times for each node; also stores the vertices. */
    private TreeMap<V, double[]> nodeTimes;
    /** Stores the edges with associated start/stop times. */
    private TreeMap<V, Map<V, double[]>> edgeTimes;
    /** Stores the time domain via the minimum time and maximum time. */
    double minTime, maxTime;

    /** Do not permit instantiation */
    private IntervalLongitudinalGraph(boolean directed) {
        this.directed = directed;
        nodeTimes = new TreeMap<V, double[]>();
        edgeTimes = new TreeMap<V, Map<V, double[]>>();
    }

    @Override public String toString() {
        return "NODES: " + nodeTimes + "; EDGES: " + edgeTimes;
    }

    /** 
     * Factory method to generate longitudinal graph with given properties.
     * @param boolean whether graph is directed
     * @param nodeTimes mapping of vertices together with list of node time intervals
     * @param edgeTimes mapping of edges together with list of edge time intervals
     */
    public static <V> IntervalLongitudinalGraph<V> getInstance(boolean directed,
            Map<V, double[]> nodeTimes,
            Map<V, Map<V, double[]>> edgeTimes) {
        if (nodeTimes == null || edgeTimes == null)
            throw new NullPointerException("getInstance: nodeTimes and edgeTimes must be non-null!");

        IntervalLongitudinalGraph<V> result = new IntervalLongitudinalGraph<V>(directed);
        result.nodeTimes.putAll(nodeTimes);
        result.edgeTimes.putAll(edgeTimes);
        result.adjustDomain();
        
        // ensure symmetric if undirected
        if (!directed) {
            ArrayList<Object[]> pairs = new ArrayList<Object[]>();
            for (V v1 : edgeTimes.keySet())
                for (V v2 : edgeTimes.get(v1).keySet()) {
                    if (edgeTimes.get(v2).get(v1) == null)
                        pairs.add(new Object[]{v1, v2});
                }
            for (Object[] pair : pairs)
                edgeTimes.get((V)pair[1]).put((V)pair[0], edgeTimes.get((V)pair[0]).get((V)pair[1]));
        }
        return result;
    }


    public Graph<V> slice(double time) {
        return new SliceGraph(time);
    }

    public double getMinimumTime() {
        return minTime;
    }

    public double getMaximumTime() {
        return maxTime;
    }

    public List<Double> getTimes() {
        return new AbstractList<Double>() {
            public Double get(int index) { return minTime + index*(maxTime-minTime)/(double)size(); }
            public int size() { return 100; }
        };
    }

    /**
     * Computes the min and max times and uses them for the time domain
     * @throws IllegalArgumentException if any of the provided time intervals are not
     *      properly formatted (2 elements, first is min, second is max)
     */
    private void adjustDomain() throws IllegalArgumentException {
        minTime = Double.MAX_VALUE;
        maxTime = -Double.MAX_VALUE;

        for (double[] v : nodeTimes.values()) {
            if (v == null || v.length != 2)
                throw new IllegalArgumentException("Bad array in set; must be of length 2: " + Arrays.toString(v));
            if (v[0] > v[1])
                throw new IllegalArgumentException("Time interval must have first argument <= second: " + Arrays.toString(v));
            minTime = Math.min(minTime, v[0]);
            maxTime = Math.max(maxTime, v[1]);
        }

        for (Map<V,double[]> map : edgeTimes.values()) {
            for (double[] v : map.values()) {
                if (v == null || v.length != 2)
                    throw new IllegalArgumentException("Bad array in set; must be of length 2: " + Arrays.toString(v));
                if (v[0] > v[1])
                    throw new IllegalArgumentException("Time interval must have first argument <= second: " + Arrays.toString(v));
                minTime = Math.min(minTime, v[0]);
                maxTime = Math.max(maxTime, v[1]);
            }
        }
    }

    //
    // INNER CLASSES
    //

    /**
     * Inner class represents a slice of the graph at a specified time.
     * All calculations are done using the underlying graph.
     */
    private class SliceGraph implements Graph<V> {
        /** Time of the slice. */
        double time;

        /** Constructs slice at the specified time. */
        SliceGraph(double time) {
            this.time = time;
        }

        private boolean in(double[] interval) {
            return interval != null && interval[0] <= time && interval[1] >= time;
        }

        public int order() {
            return nodes().size();
        }

        public List<V> nodes() {
            ArrayList<V> nodes = new ArrayList<V>();
            for (Entry<V,double[]> v : nodeTimes.entrySet()) {
                if (v.getValue()[0] <= time && v.getValue()[1] >= time)
                    nodes.add(v.getKey());
            }
            return nodes;
        }

        public boolean contains(V x) {
            return in(nodeTimes.get(x));
        }

        public boolean isDirected() {
            return directed;
        }

        public boolean adjacent(V x, V y) {
            if (!contains(x) || !contains(y) || !edgeTimes.containsKey(x))
                return false;
            return in(edgeTimes.get(x).get(y));
        }

        public int degree(V x) {
            return neighbors(x).size();
        }

        public List<V> neighbors(V x) {
            if (!contains(x) || !edgeTimes.containsKey(x))
                return Collections.emptyList();
            Map<V, double[]> adj = edgeTimes.get(x);
            ArrayList<V> nbhd = new ArrayList<V>();
            for (Entry<V, double[]> en : adj.entrySet())
                if (in(en.getValue()))
                    nbhd.add(en.getKey());
            return nbhd;
        }

        public int edgeNumber() {
            int total = 0;
            List<V> nodes = nodes();
            for (V v1 : nodes)
                for (V v2 : nodes)
                    if (adjacent(v1, v2))
                        total += v1==v2 ? 2 : 1;                    
            return directed ? total : total / 2;                   
        }
    } // INNER CLASS SliceGraph

}
