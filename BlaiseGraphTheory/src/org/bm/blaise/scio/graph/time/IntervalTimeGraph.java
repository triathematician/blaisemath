/*
 * IntervalTimeGraph.java
 * Created Jul 5, 2010
 */

package org.bm.blaise.scio.graph.time;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphSupport;
import org.bm.blaise.scio.graph.GraphUtils;

/**
 * A graph whose nodes and edges all come with associated time intervals. When queried
 * for a slice, this class returns a view of just the elements of the underlying graph
 * that exist at the specified time. Implemented as a sparse graph.
 *
 * @param <V> the type of the nodes
 *
 * @author Elisha Peterson
 */
public class IntervalTimeGraph<V> implements TimeGraph<V> {

    //
    // FACTORY METHODS
    //

    /**
     * Factory method to generate longitudinal graph with given properties.
     * @param directed whether graph is directed
     * @param timeSteps how many time steps to use
     * @param nodeTimes mapping of vertices together with time intervals
     * @param edgeTimes mapping of edges together with time intervals
     */
    public static <V> IntervalTimeGraph<V> getInstance(boolean directed, int timeSteps,
            Map<V, double[]> nodeTimes,
            Map<V, Map<V, double[]>> edgeTimes) {
        if (nodeTimes == null || edgeTimes == null)
            throw new NullPointerException("getInstance: nodeTimes and edgeTimes must be non-null!");

        IntervalTimeGraph<V> result = new IntervalTimeGraph<V>(directed);
        result.timeSteps = timeSteps;
        for (Entry<V, double[]> en : nodeTimes.entrySet()) {
            ArrayList<double[]> ivs = new ArrayList<double[]>();
            ivs.add(en.getValue());
            result.nodeTimes.put(en.getKey(), ivs);
        }
        for (Entry<V, Map<V, double[]>> en1 : edgeTimes.entrySet()) {
            HashMap<V, List<double[]>> map2 = new HashMap<V, List<double[]>>();
            for (Entry<V, double[]> en2 : en1.getValue().entrySet()) {
                ArrayList<double[]> ivs = new ArrayList<double[]>();
                ivs.add(en2.getValue());
                map2.put(en2.getKey(), ivs);
            }
            result.edgeTimes.put(en1.getKey(), map2);
        }
        result.adjustDomain();

        // ensure symmetric if undirected
        if (!directed) {
            ArrayList<Object[]> pairs = new ArrayList<Object[]>();
            for (V v1 : result.edgeTimes.keySet())
                for (V v2 : result.edgeTimes.get(v1).keySet()) {
                    if (result.edgeTimes.get(v2) == null || result.edgeTimes.get(v2).get(v1) == null)
                        pairs.add(new Object[]{v1, v2});
                }
            for (Object[] pair : pairs) {
                if (result.edgeTimes.get((V)pair[1]) == null)
                    result.edgeTimes.put((V)pair[1], new HashMap<V, List<double[]>>());
                result.edgeTimes.get((V)pair[1]).put((V)pair[0], result.edgeTimes.get((V)pair[0]).get((V)pair[1]));
            }
        }
        return result;
    }

    /**
     * Factory method to generate longitudinal graph with given properties.
     * @param directed whether graph is directed
     * @param timeSteps how many time steps to use
     * @param nodeTimes mapping of vertices together with list of node time intervals
     * @param edgeTimes mapping of edges together with list of edge time intervals
     */
    public static <V> IntervalTimeGraph<V> getInstance2(boolean directed, int timeSteps,
            Map<V, List<double[]>> nodeTimes,
            Map<V, Map<V, List<double[]>>> edgeTimes) {
        if (nodeTimes == null || edgeTimes == null)
            throw new NullPointerException("getInstance: nodeTimes and edgeTimes must be non-null!");

        IntervalTimeGraph<V> result = new IntervalTimeGraph<V>(directed);
        result.timeSteps = timeSteps;
        result.nodeTimes.putAll(nodeTimes);
        result.edgeTimes.putAll(edgeTimes);
        result.adjustDomain();

        // ensure symmetric if undirected
        if (!directed) {
            ArrayList<Object[]> pairs = new ArrayList<Object[]>();
            for (V v1 : edgeTimes.keySet())
                for (V v2 : edgeTimes.get(v1).keySet()) {
                    if (edgeTimes.get(v2) == null || edgeTimes.get(v2).get(v1) == null)
                        pairs.add(new Object[]{v1, v2});
                }
            for (Object[] pair : pairs) {
                V p0 = (V) pair[0];
                V p1 = (V) pair[1];
                if (!edgeTimes.containsKey(p1))
                    edgeTimes.put(p1, new HashMap<V, List<double[]>>());
                edgeTimes.get(p1).put(p0, edgeTimes.get(p0).get(p1));
            }
        }
        return result;
    }

    //
    // IMPLEMENTATION
    //

    /** Whether graph is directed. */
    boolean directed;
    /** Stores the start/stop times for each node; also stores the vertices. */
    private TreeMap<V, List<double[]>> nodeTimes;
    /** Stores the edges with associated start/stop times. */
    private TreeMap<V, Map<V, List<double[]>>> edgeTimes;
    /** Stores the time domain via the minimum time and maximum time. */
    double minTime, maxTime;

    /** # of time steps to use (impacts how time slices are done) */
    int timeSteps = 100;

    /** Do not permit instantiation */
    private IntervalTimeGraph(boolean directed) {
        this.directed = directed;
        nodeTimes = new TreeMap<V, List<double[]>>();
        edgeTimes = new TreeMap<V, Map<V, List<double[]>>>();
    }

    @Override public String toString() {
        return "NODES: " + nodeTimes + "; EDGES: " + edgeTimes;
    }


    public boolean isDirected() {
        return directed;
    }

    public Collection<V> getAllNodes() {
        return nodeTimes.keySet();
    }

    public List<double[]> getNodeIntervals(V v) {
        return nodeTimes.containsKey(v) ? nodeTimes.get(v) : null;
    }

    public List<double[]> getEdgeIntervals(V v1, V v2) {
        Map<V, List<double[]>> map1 = edgeTimes.get(v1);
        if (map1 != null && map1.containsKey(v2))
            return map1.get(v2);
        else if (!directed) {
            map1 = edgeTimes.get(v2);
            return (map1 != null && map1.containsKey(v1)) ? map1.get(v1) : null;
        }
        return null;
    }

    public Graph<V> slice(double time, boolean exact) {
        return new SliceGraph(this, time); // here this option is ignored
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
            public int size() { return timeSteps; }
        };
    }

    /**
     * Computes the min and max times and uses them for the time domain
     * @throws IllegalArgumentException if any of the provided time intervals are not
     *      properly formatted (2 elements, first is min, second is max)
     */
    private void adjustDomain() throws IllegalArgumentException {
        minTime = Double.POSITIVE_INFINITY;
        maxTime = Double.NEGATIVE_INFINITY;

        for (List<double[]> vv : nodeTimes.values())
            for (double[] v : vv) {
                if (v == null || v.length != 2)
                    throw new IllegalArgumentException("Bad array in set; must be of length 2: " + Arrays.toString(v));
                if (v[0] > v[1])
                    throw new IllegalArgumentException("Time interval must have first argument <= second: " + Arrays.toString(v));
                if (!Double.isInfinite(v[0]))
                    minTime = Math.min(minTime, v[0]);
                if (!Double.isInfinite(v[1]))
                    maxTime = Math.max(maxTime, v[1]);
            }

        for (Map<V,List<double[]>> map : edgeTimes.values())
            for (List<double[]> vv : map.values())
                for (double[] v : vv) {
                    if (v == null || v.length != 2)
                        throw new IllegalArgumentException("Bad array in set; must be of length 2: " + Arrays.toString(v));
                    if (v[0] > v[1])
                        throw new IllegalArgumentException("Time interval must have first argument <= second: " + Arrays.toString(v));
                    if (!Double.isInfinite(v[0]))
                        minTime = Math.min(minTime, v[0]);
                    if (!Double.isInfinite(v[1]))
                        maxTime = Math.max(maxTime, v[1]);
                }
    }

    //
    // INNER CLASSES
    //

    /**
     * Inner class represents a slice of the graph at a specified time.
     * All calculations are done using the underlying graph.
     */
    private static class SliceGraph<V> extends GraphSupport<V> {

        /** Parent graph */
        protected final IntervalTimeGraph<V> parent;
        /** Time of the slice. */
        protected final double time;
        /** Components */
        protected final Collection<Set<V>> components;

        /** Constructs slice at the specified time. */
        private SliceGraph(IntervalTimeGraph<V> parent, double time) {
            super(nodeSlice(parent, time), parent.isDirected());
            this.parent = parent;
            this.time = time;
            this.components = GraphUtils.components(this, nodes);
        }

        public Collection<Set<V>> components() {
            return components;
        }

        @Override
        public boolean adjacent(V x, V y) {
            if (!(contains(x) && contains(y)))
                return false;
            boolean dirOK = parent.edgeTimes.containsKey(x) && parent.edgeTimes.get(x).containsKey(y);
            boolean undirOK = dirOK || (parent.edgeTimes.containsKey(y) && parent.edgeTimes.get(y).containsKey(x));
            if ((directed && !dirOK) || (!directed && !undirOK))
                return false;
            return dirOK ? in(time, parent.edgeTimes.get(x).get(y)) : in(time, parent.edgeTimes.get(y).get(x));
        }

        public Set<V> neighbors(V x) {
            if (!contains(x) || !parent.edgeTimes.containsKey(x))
                return Collections.emptySet();
            Map<V, List<double[]>> adj = parent.edgeTimes.get(x);
            HashSet<V> nbhd = new HashSet<V>();
            for (Entry<V, List<double[]>> en : adj.entrySet())
                if (in(time, en.getValue()))
                    nbhd.add(en.getKey());
            return nbhd;
        }

        public int edgeCount() {
            int total = 0;
            for (V v1 : nodes)
                for (V v2 : nodes)
                    if (adjacent(v1, v2))
                        total += v1==v2 ? 2 : 1;
            return directed ? total : total / 2;
        }
    } // INNER CLASS SliceGraph


    //
    // UTILITIES
    //

    /** Construct nodes at specified time. */
    private static <V> List<V> nodeSlice(IntervalTimeGraph<V> parent, double time) {
        ArrayList<V> nodes = new ArrayList<V>();
        for (V v : parent.nodeTimes.keySet()) {
            List<double[]> intervals = parent.nodeTimes.get(v);
            if (in(time, intervals))
                nodes.add(v);
        }
        return nodes;
    }

    /**
     * Checks to see if time is in stated interval(s)
     * @param time the time
     * @param ivs list of intervals
     * @return true if current time is in any of the intervals, false otherwise & if argument is null
     */
    private static boolean in(double time, List<double[]> ivs) {
        if (ivs == null) return false;
        for (double[] iv : ivs)
            if (iv[0] <= time && iv[1] >= time)
                return true;
        return false;
    }
}
