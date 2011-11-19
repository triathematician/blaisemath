/*
 * CliqueCount.java
 * Created Nov 4, 2011
 */
package org.bm.blaise.scio.graph.metrics;

import java.util.Set;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.Subgraph;

/**
 * Computes the clique count of a particular vertex,
 * i.e. the number of connections between edges in the neighborhood
 * of the vertex, not counting the edges adjacent to the vertex itself.
 * Current computation time is linear in the # of edges in the graph (vertex case),
 * and quadratic in the map case (linear in edges * linear in vertices).
 * 
 * @author elisha
 */
public class CliqueCount extends NodeMetricSupport<Integer> {

    public CliqueCount() {
        super("Clique Count", true, false);
    }
    
    public <V> Integer value(Graph<V> graph, V vertex) { 
        Set<V> nbhd = graph.neighbors(vertex);
        return new Subgraph(graph, nbhd).edgeCount();
    }
    
    public double nodeMax(boolean directed, int order) { 
        return (order-1)*(order-2)*(directed ? 1.0 : 0.5);
    }
    
    public double centralMax(boolean directed, int order) {
        throw new UnsupportedOperationException("Not yet implemented...");
    }
    
    public double standardization(Graph graph) { 
        return 1.0/((graph.order()-1)*(graph.order()-2)*(graph.isDirected()?2:1)); 
    }
}
