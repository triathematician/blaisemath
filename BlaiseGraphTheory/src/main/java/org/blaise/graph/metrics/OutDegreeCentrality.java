/*
 * OutDegreeCentrality.java
 * Created Nov 4, 2011
 */
package org.blaise.graph.metrics;

import org.blaise.graph.Graph;


/**
 * Computes out-degree of vertices (only directed graphs).
 * 
 * @author elisha
 */
public class OutDegreeCentrality implements GraphNodeMetric<Integer> {

    public <V> Integer value(Graph<V> graph, V vertex) { 
        return graph.outDegree(vertex); 
    }

}