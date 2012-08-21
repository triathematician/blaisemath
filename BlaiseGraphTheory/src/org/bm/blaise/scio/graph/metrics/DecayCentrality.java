/*
 * DecayCentrality.java
 * Created May 12, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.ArrayList;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphUtils;
import org.bm.blaise.scio.graph.ValuedGraph;

/**
 * <p>
 * Provides a metric describing the decay centrality of a vertex in a graph.
 * This computation can be slow for large graphs since it uses all vertices
 * in a component.
 * </p>
 * <p>
 * The computation depends on a single <i>decay parameter</i>... a node at distance
 * 1 contributes this parameter, at distance 2 the parameter squared, and so on.
 * As the parameter approaches 1, the metric's value approaches the size of the
 * node's component. As the parameter approaches 0, the metric's value also
 * approaches the parameter times the size of the node's neighborhood.
 * </p>
 *
 * @author Elisha Peterson
 */
public class DecayCentrality extends NodeMetricSupport<Double> {

    /** Decay parameter */
    protected double parameter = 0.5;

    /** Construct with default decay parameter of 0.5 */
    public DecayCentrality() { 
        this(0.5);
    }
    
    /** 
     * @param parameter value of decay parameter
     * @throws IllegalArgumentException if value is outside of the range [0,1]
     */
    public DecayCentrality(double parameter) { 
        super("Decay Centrality", true, true);
        setParameter(parameter);
    }

    @Override 
    public String toString() { 
        return "Decay Centrality (" + parameter + ")"; 
    }

    /** @return value of decay parameter */
    public double getParameter() { return parameter; }
    /** @param newValue new value of decay parameter */
    public void setParameter(double newValue) { 
        if (! (newValue <= 1 && newValue >= 0)) throw new IllegalArgumentException("Parameter for DecayCentrality must be between 0 and 1: " + newValue);
        parameter = newValue;
    }

    public double nodeMax(boolean directed, int order) {
        // maximum occurs if all elements are at distance 1
        return parameter*(order-1);
    }
    public double centralMax(boolean directed, int order) { return Double.NaN; }

    public <V> Double value(Graph<V> graph, V vertex) {
        ValuedGraph<V, Integer> nvg = GraphUtils.geodesicTree(graph, vertex);
        double total = 0.0;
        for (V v : nvg.nodes())
            total += Math.pow(parameter, nvg.getValue(v));
        return total;
    }

}
