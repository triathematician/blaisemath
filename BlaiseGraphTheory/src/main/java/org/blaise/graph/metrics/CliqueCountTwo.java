/*
 * CliqueCount.java
 * Created Nov 4, 2011
 */
package org.blaise.graph.metrics;

import java.util.Set;
import org.blaise.graph.Graph;
import org.blaise.graph.GraphUtils;

/**
 * Computes the clique count of a particular vertex,
 * i.e. the number of connections between edges in the neighborhood
 * of the vertex, not counting the edges adjacent to the vertex itself.
 * Current computation time is linear in the # of edges in the graph (vertex case),
 * and quadratic in the map case (linear in edges * linear in vertices).
 * 
 * @author elisha
 */
public class CliqueCountTwo implements GraphNodeMetric<Integer> {
    
    public <V> Integer value(Graph<V> graph, V vertex) { 
        Set<V> nbhd = GraphUtils.neighborhood(graph, vertex, 2);
        return GraphUtils.subgraph(graph, nbhd).edgeCount() - nbhd.size() + 1;
    }
    
}
