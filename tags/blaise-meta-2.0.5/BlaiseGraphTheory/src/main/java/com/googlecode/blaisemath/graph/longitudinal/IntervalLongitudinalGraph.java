/*
 * IntervalLongitudinalGraph.java
 * Created Jul 5, 2010
 */

package com.googlecode.blaisemath.graph.longitudinal;

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

import com.google.common.collect.Sets;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.SparseGraph;
import com.googlecode.blaisemath.util.Edge;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
    private Map<V, List<double[]>> nodeTimes;
    /** Stores the edges with associated start/stop times. */
    private Set<IntervalTimeEdge<V>> edges;
    /** Stores the time domain via the minimum time and maximum time. */
    double minTime, maxTime;

    /** # of time steps to use (impacts how time slices are done) */
    int timeSteps = 100;

    /** Do not permit instantiation */
    private IntervalLongitudinalGraph(boolean directed) {
        this.directed = directed;
        nodeTimes = new HashMap<V, List<double[]>>();
        edges = new HashSet<IntervalTimeEdge<V>>();
    }

    
    //<editor-fold defaultstate="collapsed" desc="FACTORY METHODS">
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
    public static <V> IntervalLongitudinalGraph<V> getInstance(boolean directed, int timeSteps,
            Map<V, double[]> nodeTimes, Map<V, Map<V, double[]>> edgeTimes) {
        
        if (nodeTimes == null || edgeTimes == null) {
            throw new NullPointerException("getInstance: nodeTimes and edgeTimes must be non-null!");
        }

        IntervalLongitudinalGraph<V> result = new IntervalLongitudinalGraph<V>(directed);
        result.timeSteps = timeSteps;
        for (Entry<V, double[]> en : nodeTimes.entrySet()) {
            result.nodeTimes.put(en.getKey(), Arrays.asList(en.getValue()));
        }
        
        Map<V,Set<V>> added = new HashMap<V,Set<V>>();
        for (V x : edgeTimes.keySet()) {
            Map<V, double[]> getx = edgeTimes.get(x);
            for (V y : getx.keySet()) {
                if (!directed && added.containsKey(y) && added.get(y).contains(x)) {
                    continue;
                }
                if (!added.containsKey(x)) {
                    added.put(x, new HashSet<V>());
                }
                added.get(x).add(y);
                result.edges.add(new IntervalTimeEdge(x,y,getx.get(y)));
            }
        }
        result.adjustDomain();
        
        return result;
    }

    /**
     * Factory method to generate longitudinal graph with given properties.
     * @param directed whether graph is directed
     * @param timeSteps how many time steps to use
     * @param nodeTimes mapping of vertices together with list of node time intervals
     * @param edgeTimes mapping of edges together with list of edge time intervals
     */
    public static <V> IntervalLongitudinalGraph<V> getInstance2(boolean directed, int timeSteps,
            Map<V, List<double[]>> nodeTimes, Map<V, Map<V, List<double[]>>> edgeTimes) {
        
        if (nodeTimes == null || edgeTimes == null) {
            throw new NullPointerException("getInstance: nodeTimes and edgeTimes must be non-null!");
        }

        IntervalLongitudinalGraph<V> result = new IntervalLongitudinalGraph<V>(directed);
        result.timeSteps = timeSteps;
        result.nodeTimes.putAll(nodeTimes);
        for (Entry<V, Map<V, List<double[]>>> en1 : edgeTimes.entrySet()) {
            for (Entry<V, List<double[]>> en2 : en1.getValue().entrySet()) {
                result.edges.add(new IntervalTimeEdge(en1.getKey(),en2.getKey(), en2.getValue()));
                if (!directed) {
                    result.edges.add(new IntervalTimeEdge(en2.getKey(),en1.getKey(), en2.getValue()));
                }
            }
        }
        
        result.adjustDomain();

        // ensure symmetric if undirected
        if (!directed) {
            ArrayList<Object[]> pairs = new ArrayList<Object[]>();
            for (V v1 : edgeTimes.keySet()) {
                for (V v2 : edgeTimes.get(v1).keySet()) {
                    if (edgeTimes.get(v2) == null || edgeTimes.get(v2).get(v1) == null) {
                        pairs.add(new Object[]{v1, v2});
                    }
                }
            }
            for (Object[] pair : pairs) {
                V p0 = (V) pair[0];
                V p1 = (V) pair[1];
                if (!edgeTimes.containsKey(p1)) {
                    edgeTimes.put(p1, new HashMap<V, List<double[]>>());
                }
                edgeTimes.get(p1).put(p0, edgeTimes.get(p0).get(p1));
            }
        }
        return result;
    }
    
    //</editor-fold>
    

    @Override public String toString() {
        return "NODES: " + nodeTimes.keySet() + "; EDGES: " + edges;
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
    
    /** 
     * Return edge between specified vertices. 
     * @param v1 first vertex
     * @param v2 second vertex
     * @return edge if it exists, null otherwise
     */
    public IntervalTimeEdge<V> getEdge(V v1, V v2) {
        for (IntervalTimeEdge<V> e : edges) {
            if (e.getNode1() == v1 && e.getNode2() == v2) {
                return e;
            }
        }
        return null;
    }

    public List<double[]> getEdgeIntervals(V v1, V v2) {
        IntervalTimeEdge<V> edge = getEdge(v1,v2);
        return edge == null ? null : edge.getTimes();
    }

    public Graph<V> slice(double time, boolean exact) {
        Set<V> nodes = nodeSlice(time);
        Set<Edge<V>> edges = Sets.newHashSet();
        for (IntervalTimeEdge<V> e : edgeSlice(time)) {
            edges.add(new Edge<V>(e));
        }
        return SparseGraph.createFromEdges(directed, nodes, edges);
    }

    public double getMinimumTime() {
        return minTime;
    }

    public double getMaximumTime() {
        return maxTime;
    }

    public List<Double> getTimes() {
        return new AbstractList<Double>() {
            public Double get(int index) { 
                return minTime + index*(maxTime-minTime)/(double)size(); 
            }
            public int size() {
                return timeSteps; 
            }
        };
    }

    /** Return set of nodes at specified time. */
    public Set<V> nodeSlice(double time) {
        Set<V> nodes = new HashSet<V>();
        for (V v : nodeTimes.keySet()) {
            List<double[]> intervals = nodeTimes.get(v);
            if (in(time, intervals)) {
                nodes.add(v);
            }
        }
        return nodes;
    }

    /** Return set of edges at specified time */
    public Set<IntervalTimeEdge<V>> edgeSlice(double time) {
        Set<IntervalTimeEdge<V>> edges = new HashSet<IntervalTimeEdge<V>>();
        for (IntervalTimeEdge<V> e : this.edges) {
            if (in(time, e.getTimes())) {
                edges.add(e);
            }
        }
        return edges;
    }


    //<editor-fold defaultstate="collapsed" desc="UTILITIES">
    //
    // UTILITIES
    //

    /**
     * Computes the min and max times and uses them for the time domain
     * @throws IllegalArgumentException if any of the provided time intervals are not
     *      properly formatted (2 elements, first is min, second is max)
     */
    private void adjustDomain() throws IllegalArgumentException {
        minTime = Double.POSITIVE_INFINITY;
        maxTime = Double.NEGATIVE_INFINITY;

        for (List<double[]> vv : nodeTimes.values()) {
            for (double[] v : vv) {
                if (v == null || v.length != 2) {
                    throw new IllegalArgumentException("Bad array in set; must be of length 2: " + Arrays.toString(v));
                }
                if (v[0] > v[1]) {
                    throw new IllegalArgumentException("Time interval must have first argument <= second: " + Arrays.toString(v));
                }
                if (!Double.isInfinite(v[0])) {
                    minTime = Math.min(minTime, v[0]);
                }
                if (!Double.isInfinite(v[1])) {
                    maxTime = Math.max(maxTime, v[1]);
                }
            }
        }

        for (IntervalTimeEdge<V> edge : edges) {
            for (double[] v : edge.getTimes()) {
                if (v == null || v.length != 2) {
                    throw new IllegalArgumentException("Bad array in set; must be of length 2: " + Arrays.toString(v));
                }
                if (v[0] > v[1]) {
                    throw new IllegalArgumentException("Time interval must have first argument <= second: " + Arrays.toString(v));
                }
                if (!Double.isInfinite(v[0])) {
                    minTime = Math.min(minTime, v[0]);
                }
                if (!Double.isInfinite(v[1])) {
                    maxTime = Math.max(maxTime, v[1]);
                }
            }
        }
    }
    
    /**
     * Checks to see if time is in stated interval(s)
     * @param time the time
     * @param ivs list of intervals
     * @return true if current time is in any of the intervals, false otherwise & if argument is null
     */
    private static boolean in(double time, List<double[]> ivs) {
        if (ivs == null) return false;
        for (double[] iv : ivs) {
            if (iv[0] <= time && iv[1] >= time) {
                return true;
            }
        }
        return false;
    }
    
    //</editor-fold>

    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    //
    // INNER CLASSES
    //
    
    /** Class annotation an edge with a collection of time intervals. */
    public static class IntervalTimeEdge<V> extends Edge<V> {
        private final List<double[]> times;
        public IntervalTimeEdge(V v1, V v2, double[] interval) {
            this(v1, v2, Arrays.asList(interval));
        }
        public IntervalTimeEdge(V v1, V v2, List<double[]> times) {
            super(v1, v2);
            this.times = times;
        }
        public List<double[]> getTimes() {
            return times;
        }
    }
    
    //</editor-fold>
    
}
