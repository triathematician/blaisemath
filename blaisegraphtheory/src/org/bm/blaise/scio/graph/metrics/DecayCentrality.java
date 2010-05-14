/*
 * DecayCentrality.java
 * Created May 12, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.ArrayList;
import java.util.Set;
import org.bm.blaise.scio.graph.GraphInterface;
import org.bm.blaise.scio.graph.GraphUtils;

/**
 * Computes the decay centrality of a vertex in a graph
 *
 * @author Elisha Peterson
 */
public class DecayCentrality implements VertexMetricInterface<Double> {

    double parameter = 0.5;

    /** Construct with default decay parameter of 0.5 */
    public DecayCentrality() { }
    /** @param parameter value of decay parameter */
    public DecayCentrality(double parameter) { setParameter(parameter); }

    /** @return value of decay parameter */
    public double getParameter() { return parameter; }
    /** @param newValue new value of decay parameter */
    public void setParameter(double newValue) { if (newValue < 1 && newValue > 0) parameter = newValue; }

    public Double getValue(GraphInterface graph, int vertex) {
        // this puts every vertex in the vertex's nbhd at the appropriate distance
        ArrayList<Set<Integer>> nbhds = GraphUtils.allDistances(vertex, graph, Integer.MAX_VALUE);
        double total = 0.0;
        int i = 0;
        for (Set<Integer> set : nbhds)
            total += set.size() * Math.pow(parameter, i++);
        return total - 1;
    }
}
