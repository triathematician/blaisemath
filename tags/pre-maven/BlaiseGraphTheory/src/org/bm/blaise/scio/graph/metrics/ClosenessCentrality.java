/*
 * ClosenessCentrality.java
 * Created Jul 23, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphUtils;

/**
 * <p>
 *   Implements closeness centrality (Sabidussi 1966), the inverse sum of distances from
 *   one node to other nodes. The same calculation can be used to compute the "eccentricity"
 *   of the node, the max distance from this node to any other node, termed <i>graph centrality</i>
 *   by Hage/Harary 1995. Instances of both metrics are provided.
 * </p>
 * @author elisha
 */
public class ClosenessCentrality extends NodeMetricSupport<Double> {

    private final boolean useSum = true;
    
    public ClosenessCentrality() {
        super("Closeness Centrality", true, true);
    }
    
    public double nodeMax(boolean directed, int order) { return useSum ? 1/(order-1.0) : 1.0; }
    public double centralMax(boolean directed, int order) { return Double.NaN; }

    public <V> Double value(Graph<V> graph, V node) {
        int n = graph.order();
        HashMap<V, Integer> lengths = new HashMap<V, Integer>();
        GraphUtils.breadthFirstSearch(graph, node, new HashMap<V,Integer>(), lengths, new Stack<V>(), new HashMap<V,Set<V>>());
        double cptSize = lengths.size();
        if (useSum) {
            double sum = 0.0;
            for (Integer i : lengths.values())
                sum += i;
            return cptSize/n * (n-1.0)/sum;
        } else {
            double max = 0.0;
            for (Integer i : lengths.values())
                max = Math.max(max, i);
            return cptSize/n * 1.0/max;
        }
    }

    public <V> List<Double> allValues(Graph<V> graph) {
        if (graph.order()==0)
            return Collections.emptyList();
        else if (graph.order()==1)
            return Arrays.asList(0.0);
        
        System.out.print("CC-all (" + graph.order() + " nodes, " + graph.edgeCount() + " edges): ");
        long l0 = System.currentTimeMillis();
        int n = graph.order();
        List<Graph<V>> components = GraphUtils.getComponentGraphs(graph);
        HashMap<V,Double> values = new HashMap<V,Double>();
        for (Graph<V> cg : components)
            if (cg.order()==1)
                values.put(cg.nodes().get(0), 0.0);
            else
                computeAllValuesConnected(cg, values);
        for (Graph<V> cg : components) {
            double multiplier = cg.order()/(double)n;
            for (V v : cg.nodes())
                values.put(v, multiplier * values.get(v));
        }
        ArrayList<Double> result = new ArrayList<Double>(n);
        for (V v : graph.nodes())
            result.add(values.get(v));
        System.out.println((System.currentTimeMillis()-l0)+"ms");
        return result;
    }

    /** Computes values for a connected portion of a graph */
    private <V> void computeAllValuesConnected(Graph<V> graph, Map<V,Double> values) {
        List<V> nodes = graph.nodes();
        int n = nodes.size();
        double max = (n-1.0);

        for (V start : nodes) {
            HashMap<V, Integer> lengths = new HashMap<V, Integer>();
            GraphUtils.breadthFirstSearch(graph, start, new HashMap<V,Integer>(), lengths, new Stack<V>(), new HashMap<V,Set<V>>());
            if (useSum) {
                double sum1 = 0.0;
                for (Integer j : lengths.values())
                    sum1 += j;
                values.put(start, max/sum1);
            } else {
                double max1 = 0.0;
                for (Integer j : lengths.values())
                    max1 = Math.max(max1, j);
                values.put(start, 1.0/max1);
            }
        }
    }

}
