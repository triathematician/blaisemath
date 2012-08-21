/*
 * DegreeCentrality.java
 * Created Nov 4, 2011
 */
package org.blaise.graph.metrics;

import org.blaise.graph.Graph;


/**
 * Vertex degree as a metric.
 * 
 * @author elisha
 */
public class DegreeCentrality implements GraphNodeMetric<Integer> {
    
    public <V> Integer value(Graph<V> graph, V vertex) { 
        return graph.degree(vertex); 
    }
    
}
