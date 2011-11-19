/*
 * NodeMetricSupport.java
 * Created Nov 4, 2011
 */
package org.bm.blaise.scio.graph.metrics;

import java.util.ArrayList;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;

/**
 * Provides some default implementation code for node metrics.
 *
 * @param <N> the type of value returned (usually a number)
 * 
 * @author elisha
 */
public abstract class NodeMetricSupport<N> implements NodeMetric<N> {
   
    /** Name */
    protected final String name;
    /** Supports undirected graphs */
    protected final boolean undirSupport;
    /** Supports directed graphs */
    protected final boolean dirSupport;

    /**
     * Construct the node metric
     * @param name used to identify the metric
     * @param undirSupport whether this metric applies to undirected graphs
     * @param dirSupport whether this metric applies to directed graphs
     */
    public NodeMetricSupport(String name, boolean undirSupport, boolean dirSupport) {
        this.name = name;
        this.undirSupport = undirSupport;
        this.dirSupport = dirSupport;
    }
    
    @Override 
    public String toString() { 
        return name;
    }
    
    public boolean supportsGraph(boolean directed) { 
        return (directed && dirSupport) || (!directed && undirSupport);
    }
    
    public <V> List<N> allValues(Graph<V> graph) {
        List<N> result = new ArrayList<N>(graph.order());
        for (V v : graph.nodes()) 
            result.add(value(graph, v));
        return result;
    }
    
    
}
