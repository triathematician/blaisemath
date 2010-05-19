/*
 * DerivedMetrics.java
 * Created May 18, 2010
 */

package org.bm.blaise.scio.graph.metrics;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bm.blaise.scio.graph.GraphInterface;
import org.bm.blaise.scio.graph.SimpleGraph;
import org.bm.blaise.scio.graph.creation.GraphCreation;

/**
 * Contains utilities for computing 'derived' metrics, i.e. generating metrics for subsets using metrics for individual vertices.
 * @author elisha
 */
public class DerivedMetrics {

    /** Extends a vertex metric by adding up the values of subsets. */
    public static class Additive<N extends Number> implements SubsetMetricInterface<N> {
        VertexMetricInterface<N> baseMetric;
        public Additive(VertexMetricInterface<N> baseMetric) {
            this.baseMetric = baseMetric;
        }

        public N getValue(GraphInterface graph, Collection<Integer> vertices) {
            Double result = 0.0;
            Number val = null;
            for (Integer i : vertices) {
                val = (Number) baseMetric.getValue(graph, i);
                result += val.doubleValue();
            }
            if (val instanceof Integer)
                return (N) (Integer) result.intValue();
            else if (val instanceof Double)
                return (N) result;
            else if (val instanceof Float)
                return (N) (Float) result.floatValue();
            return null;
        }
    }

    /** Extends a vertex metric by contracting a subset of vertices to a single vertex. */
    public static class Contractive<N extends Number> implements SubsetMetricInterface<N> {
        VertexMetricInterface<N> baseMetric;
        public Contractive(VertexMetricInterface<N> baseMetric) {
            this.baseMetric = baseMetric;
        }

        public N getValue(GraphInterface graph, Collection<Integer> vertices) {
            SimpleGraph derived = GraphCreation.deriveContractedGraph((SimpleGraph) graph, vertices, "*");
            int index = derived.indexOfLabel("*");
            return baseMetric.getValue(derived, index);
        }
    }

    /** Enumerates all subsets of the integer subset [0,1,2,...,n-1] as an abstract list. */
    public static List<List<Integer>> enumerateSubsets(final int n) {
        return new AbstractList<List<Integer>>() {
            @Override
            public List<Integer> get(int index) {
                ArrayList<Integer> summand = new ArrayList<Integer>();
                for (int bit = 0; bit < n; bit++)
                    if ((index >> bit) % 2 == 1) summand.add(bit);
                return summand;
            }
            @Override
            public int size() { return (int) Math.pow(2, n); }
        };
    }

    /** Print out derived metrics for all subsets in a graph. */
    public static void printAll(SimpleGraph graph, SubsetMetricInterface smi) {
        int n = graph.size();
        if (n > 15)
            System.out.println("WARNING: excessive computation forthcoming!");
        for (List<Integer> vertices : enumerateSubsets(n))
            System.out.println(vertices + " --> " + smi.getValue(graph, vertices));
    }

    public static void main(String[] args) {
        SimpleGraph graph = GraphCreation.generateRandomGraph(10, .5, false);
        SubsetMetricInterface smi = new Contractive(GraphMetrics.DEGREE);
        printAll(graph, smi);
    }

}
