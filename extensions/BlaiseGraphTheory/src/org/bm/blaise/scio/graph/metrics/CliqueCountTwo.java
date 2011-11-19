/*
 * CliqueCount.java
 * Created Nov 4, 2011
 */
package org.bm.blaise.scio.graph.metrics;

import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphUtils;
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
public class CliqueCountTwo extends NodeMetricSupport<Integer> {

    public CliqueCountTwo() {
        super("2nd-Order Clique Count", true, false);
    }
    
    public <V> Integer value(Graph<V> graph, V vertex) { 
        List<V> nbhd = GraphUtils.neighborhood(graph, vertex, 2);
        return new Subgraph(graph, nbhd).edgeCount() - nbhd.size() + 1;
    }
    
    public double nodeMax(boolean directed, int order) { 
        return (order-1)*(order-2)*(directed ? 1.0 : 0.5);
    }
    
    public double centralMax(boolean directed, int order) {
        throw new UnsupportedOperationException("Not yet implemented...");
    }
}
