/*
 * DegreeCentrality.java
 * Created Nov 4, 2011
 */
package org.blaise.graph.metrics;

import org.blaise.graph.Graph;
import org.blaise.graph.GraphUtils;


/**
 * Computes the second-order degree of a vertex in a graph, i.e. how many vertices are within two hops.
 * Does not include the vertex itself.
 * 
 * @author elisha
 */
public class DegreeTwoCentrality implements GraphNodeMetric<Integer> {
    
    public <V> Integer value(Graph<V> graph, V vertex) { 
        return GraphUtils.neighborhood(graph, vertex, 2).size() - 1;
    }
    
}
