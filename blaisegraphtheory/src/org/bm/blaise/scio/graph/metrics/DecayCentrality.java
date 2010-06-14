/*
 * DecayCentrality.java
 * Created May 12, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.Graphs;
import org.bm.blaise.scio.graph.NodeValueGraph;

/**
 * Provides a metric describing the decay centrality of a vertex in a graph.
 * This computation can be slow for large graphs since it uses all vertices
 * in a component.
 *
 * @author Elisha Peterson
 */
public class DecayCentrality implements NodeMetric<Double> {

    double parameter = 0.5;

    /** Construct with default decay parameter of 0.5 */
    public DecayCentrality() { }
    /** @param parameter value of decay parameter */
    public DecayCentrality(double parameter) { setParameter(parameter); }

    /** @return value of decay parameter */
    public double getParameter() { return parameter; }
    /** @param newValue new value of decay parameter */
    public void setParameter(double newValue) { 
        if (! (newValue <= 1 && newValue >= 0)) throw new IllegalArgumentException("Parameter for DecayCentrality must be between 0 and 1: " + newValue);
        parameter = newValue;
    }

    public <V> Double getValue(Graph<V> graph, V vertex) {
        NodeValueGraph<V, Integer> nvg = Graphs.geodesicTree(graph, vertex);
        double total = 0.0;
        for (V v : nvg.nodes())
            total += Math.pow(parameter, nvg.getValue(v));
        return total - 1;
    }
}
