/*
 * InDegreeCentrality.java
 * Created Nov 4, 2011
 */
package org.blaise.graph.metrics;

import org.blaise.graph.Graph;


/**
 * Computes in-degree of vertices (only directed graphs).
 * 
 * @author elisha
 */
public class InDegreeCentrality implements GraphNodeMetric<Integer> {

    public <V> Integer value(Graph<V> graph, V vertex) { 
        return graph.inDegree(vertex); 
    }

}
