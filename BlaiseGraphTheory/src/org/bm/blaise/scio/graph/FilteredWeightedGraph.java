/*
 * FilteredWeightedGraph.java
 * Created Aug 16, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides an implementation of a graph that is based upon an underlying weighted
 * graphs. This is NOT a weighted graph... its edges consist of those that are at
 * least as large as a specified parameter.
 *
 * @author Elisha Peterson
 */
public class FilteredWeightedGraph<V> extends AbstractWrapperGraph<V> {
    
    //
    // FACTORY METHODS
    //

    /** Constructs a filtered version of the specified graph; will be valued if the underlying graph is valued. */
    public static <V> FilteredWeightedGraph getInstance(WeightedGraph<V,? extends Number> baseGraph) {
        return baseGraph instanceof ValuedGraph ? new Valued(baseGraph) : new FilteredWeightedGraph(baseGraph);
    }
    
    //
    // IMPLEMENTATION
    //
    
    protected final WeightedGraph<V,? extends Number> base;
    protected double threshold;

    /** Constructs a filtered graph instance based upon the specified weighted graph */
    public FilteredWeightedGraph(WeightedGraph<V,? extends Number> baseGraph) {
        this(baseGraph, 1.0);
    }

    /** Constructs a filtered graph instance based upon the specified weighted graph */
    public FilteredWeightedGraph(WeightedGraph<V,? extends Number> baseGraph, double thresh) {
        super(baseGraph);
        this.base = baseGraph;
        this.threshold = thresh;
    }

    /** @return threshold parameter */
    public double getThreshold() { return threshold; }
    /** Sets threshold parameter */
    public void setThreshold(double value) { this.threshold = value; }

    /** @return base graph for filter */
    public WeightedGraph<V,? extends Number> getBaseGraph() { return base; }

    @Override
    public boolean adjacent(V x, V y) {
        return base.adjacent(x, y)
                && Math.abs(base.getWeight(x, y).doubleValue()) >= threshold;
    }

    @Override
    public int degree(V x) {
        int result = 0;
        for (V v : base.neighbors(x))
            if (Math.abs(base.getWeight(x, v).doubleValue()) >= threshold)
                result++;
        return result;
    }

    @Override
    public Set<V> neighbors(V x) {
        HashSet<V> result = new HashSet<V>();
        for (V v : base.neighbors(x))
            if (Math.abs(base.getWeight(x, v).doubleValue()) >= threshold)
                result.add(v);
        return result;
    }

    @Override
    public int edgeCount() {
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
