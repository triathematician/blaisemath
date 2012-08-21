/*
 * FilteredWeightedGraph.java
 * Created Aug 16, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides an implementation of a graph that is based upon an underlying weighted
 * graphs. This is NOT a weighted graph... its edges consist of those that are at
 * least as large as a specified parameter.
 *
 * @author Elisha Peterson
 */
public class FilteredWeightedGraph<V> implements Graph<V> {

    WeightedGraph<V,? extends Number> base;
    double threshold = 1.0;

    /** Constructs a filtered graph instance based upon the specified weighted graph */
    public FilteredWeightedGraph(WeightedGraph<V,? extends Number> baseGraph) {
        this.base = baseGraph;
    }

    /** Constructs a filtered version of the specified graph; will be valued if the underlying graph is valued. */
    public static <V> FilteredWeightedGraph getInstance(WeightedGraph<V,? extends Number> baseGraph) {
        return baseGraph instanceof ValuedGraph ? new Valued(baseGraph) : new FilteredWeightedGraph(baseGraph);
    }

    /** @return threshold parameter */
    public double getThreshold() { return threshold; }
    /** Sets threshold parameter */
    public void setThreshold(double value) { this.threshold = value; }

    /** @return base graph for filter */
    public WeightedGraph<V,? extends Number> getBaseGraph() { return base; }

    public boolean isDirected() { return base.isDirected(); }
    public int order() { return base.order(); }
    public List<V> nodes() { return base.nodes(); }
    public boolean contains(V x) { return base.contains(x); }
    
    public boolean adjacent(V x, V y) {
        return base.adjacent(x, y)
                && base.getWeight(x, y).doubleValue() >= threshold;
    }

    public int degree(V x) {
        int result = 0;
        for (V v : base.neighbors(x))
            if (base.getWeight(x, v).doubleValue() >= threshold)
                result++;
        return result;
    }

    public List<V> neighbors(V x) {
        ArrayList<V> result = new ArrayList<V>();
        for (V v : base.neighbors(x))
            if (base.getWeight(x, v).doubleValue() >= threshold)
                result.add(v);
        return result;
    }

    public int edgeNumber() {
        int sum = 0;
        List<V> nodes = base.nodes();
        int n = nodes.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                if (adjacent(nodes.get(i), nodes.get(j)))
                    sum += (i==j && !isDirected()) ? 2 : 1;
        }
        return isDirected() ? sum : sum/2;
    }

    //
    // INNER CLASSES
    //

    /** An implementation of the filtered graph for a weighted & valued graph */
    public static class Valued<V> extends FilteredWeightedGraph<V>
            implements ValuedGraph<V,Object> {

        ValuedGraph vBase;

        /** Constructs a filtered graph instance based upon the specified weighted graph */
        public Valued(WeightedGraph<V,? extends Number> baseGraph) {
            super(baseGraph);
            if (baseGraph instanceof ValuedGraph)
                vBase = (ValuedGraph) baseGraph;
            else
                throw new IllegalArgumentException("Argument to constructor must be a valued graph!");
        }

        public Object getValue(V x) { return vBase.getValue(x); }
        public void setValue(V x, Object value) { vBase.setValue(x, value); }
    }
}
