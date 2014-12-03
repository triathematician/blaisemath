/*
 * SliceLongitudinalGraph.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import com.googlecode.blaisemath.graph.Graph;

/**
 * Stores a sequence of graphs marked to specific times. The graphs are stored in a
 * sorted map, whose key values are the times, so an attempt to add two graphs at the
 * same time will overwrite the previous one.
 *
 * @param <V> the type of the nodes
 * 
 * @author Elisha Peterson
 */
public class SliceLongitudinalGraph<V> implements LongitudinalGraph<V> {

    /** Stores the graphs in the time series. */
    private TreeMap<Double,Graph<V>> graphs;

    /** Constructs with no graphs. */
    public SliceLongitudinalGraph() {
        graphs = new TreeMap<Double,Graph<V>>();
    }

    @Override
    public String toString() {        
        return "SliceLongitudinalGraph " + graphs.toString();
    }

    /**
     * Adds a graph to the list.
     * @param graph a graph to add to the end of the list; if null, nothing happens
     * @param time the time associated with the graph
     */
    public void addGraph(Graph<V> graph, double time) {
        if (graph == null) {
            throw new IllegalArgumentException("addGraph: graph should not be null");
        }
        graphs.put(time, graph);
    }

    /**
     * Returns the number of graphs in the list
     * @return number of graphs
     */
    public int size() {
        return graphs.size();
    }

    public boolean isDirected() {
        return graphs.size() == 0 ? false : graphs.get(graphs.firstKey()).isDirected();
    }

    public Collection<V> getAllNodes() {
        Set<V> result = new HashSet<V>();
        for (Graph<V> g : graphs.values()) {
            result.addAll(g.nodes());
        }
        return result;
    }

    public List<double[]> getNodeIntervals(V v) {
        boolean found = false;
        List<double[]> ivs = new ArrayList<double[]>();
        for (Entry<Double, Graph<V>> en : graphs.entrySet()) {
            Graph<V> graph = en.getValue();
            if (graph.contains(v)) {
                found = true;
                ivs.add(new double[]{en.getKey(), en.getKey()});
            }
        }
        return found ? ivs : null;
    }

    public List<double[]> getEdgeIntervals(V v1, V v2) {
        boolean found = false;
        List<double[]> ivs = new ArrayList<double[]>();
        for (Entry<Double, Graph<V>> en : graphs.entrySet()) {
            Graph<V> graph = en.getValue();
            if (graph.outNeighbors(v1).contains(v2)) {
                found = true;
                ivs.add(new double[]{en.getKey(), en.getKey()});
            }
        }
        return found ? ivs : null;
    }

    public Graph<V> slice(double time, boolean exact) {
        if (graphs.containsKey(time)) {
            return graphs.get(time);
        } else if (!exact) {
            // return closest graph to given time
            double minDist = Double.MAX_VALUE;
            Double minKey = null;
            for (double d : graphs.keySet()) {
                if (Math.abs(d-time) < minDist) {
                    minDist = Math.abs(d-time);
                    minKey = d;
                }
            }
            return minKey == null ? null : graphs.get(minKey);
        } else
            return null;
    }

    public double getMinimumTime() {
        if (graphs.isEmpty()) {
            return -Double.MAX_VALUE;
        }
        return graphs.firstKey();
    }

    public double getMaximumTime() {
        if (graphs.isEmpty()) {
            return Double.MAX_VALUE;
        }
        return graphs.lastKey();
    }

    public List<Double> getTimes() {
        return new ArrayList<Double>(graphs.keySet());
    }

}
