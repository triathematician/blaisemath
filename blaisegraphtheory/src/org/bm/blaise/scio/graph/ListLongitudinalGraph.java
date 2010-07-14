/*
 * ListLongitudinalGraph.java
 * Created Jul 5, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * Stores a sequence of graphs marked to specific times. The graphs are stored in a
 * sorted map, whose key values are the times, so an attempt to add two graphs at the
 * same time will overwrite the previous one.
 *
 * @param <V> the type of the nodes
 * 
 * @author Elisha Peterson
 */
public class ListLongitudinalGraph<V> implements LongitudinalGraph<V> {

    /** Stores the graphs in the time series. */
    private TreeMap<Double,Graph<V>> graphs;

    /** Constructs with no graphs. */
    public ListLongitudinalGraph() {
        graphs = new TreeMap<Double,Graph<V>>();
    }

    @Override
    public String toString() {        
        return graphs.toString();
    }

    /**
     * Adds a graph to the list.
     * @param graph a graph to add to the end of the list; if null, nothing happens
     * @param time the time associated with the graph
     */
    public void addGraph(Graph<V> graph, double time) {
        if (graph == null)
            throw new IllegalArgumentException("addGraph: graph should not be null");
        graphs.put(time, graph);
    }

    /**
     * Returns the number of graphs in the list
     * @return number of graphs
     */
    public int size() {
        return graphs.size();
    }

    public Graph<V> slice(double time) {
        return graphs.containsKey(time) ? graphs.get(time) : null;
    }

    public double getMinimumTime() {
        if (graphs.size() == 0) return -Double.MAX_VALUE;
        return graphs.firstKey();
    }

    public double getMaximumTime() {
        if (graphs.size() == 0) return Double.MAX_VALUE;
        return graphs.lastKey();
    }

    public List<Double> getTimes() {
        return new ArrayList<Double>(graphs.keySet());
    }

}
