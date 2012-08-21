/*
 * DegreeCentrality.java
 * Created Nov 4, 2011
 */
package org.bm.blaise.scio.graph.metrics;

import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphUtils;

/**
 * Computes the second-order degree of a vertex in a graph, i.e. how many vertices are within two hops.
 * 
 * @author elisha
 */
public class DegreeTwoCentrality extends NodeMetricSupport<Integer> {

    public DegreeTwoCentrality() {
        super("2nd-Order Degree", true, false);
    }
    
    public <V> Integer value(Graph<V> graph, V vertex) { 
        return GraphUtils.neighborhood(graph, vertex, 2).size() - 1;
    }
    
    public double nodeMax(boolean directed, int order) { 
        return order-1; 
    }
    
    public double centralMax(boolean directed, int order) {
        throw new UnsupportedOperationException("Not yet implemented...");
    }
}
