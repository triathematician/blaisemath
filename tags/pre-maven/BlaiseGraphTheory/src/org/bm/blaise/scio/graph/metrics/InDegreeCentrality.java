/*
 * InDegreeCentrality.java
 * Created Nov 4, 2011
 */
package org.bm.blaise.scio.graph.metrics;

import org.bm.blaise.scio.graph.Graph;

/**
 * Computes in-degree of vertices (only directed graphs).
 * 
 * @author elisha
 */
public class InDegreeCentrality extends NodeMetricSupport<Integer> {

    public InDegreeCentrality() {
        super("In-Degree", false, true);
    }
    
    public <V> Integer value(Graph<V> graph, V vertex) { 
        return graph.degree(vertex); 
    }
    
    public double nodeMax(boolean directed, int order) { 
        return order-1; 
    }
    
    public double centralMax(boolean directed, int order) {
        // theoretical max occurs for a star graph
        return (order-1)*(order-1);
    }
}
