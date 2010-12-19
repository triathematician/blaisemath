/*
 * Subgraph.java
 * Created May 26, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Provides a wrapper implementation of a graph that is the restriction of a "parent"
 * graph to a specified subset of nodes.
 * All of its methods use the "parent" graph and the
 * subset to compute results when the methods are called, without storing any
 * additional data about the graph.
 *
 * @param <V> the type of the nodes
 * @see ContractedGraph
 * @author Elisha Peterson
 */
public class Subgraph<V> implements Graph<V> {

    Graph<V> parent;
    List<V> subset;

    /**
     * Factory method returns subgraph of a graph; return type will match the type of the parent object
     * (i.e. if parent is weighted/valued, the result will be also).
     */
    public static <V> Graph<V> getInstance(Graph<V> parent, Collection<V> subset) { 
        if (parent instanceof WeightedGraph && parent instanceof ValuedGraph)
            return getWeightedValuedInstance((WeightedGraph) parent, subset);
        else if (parent instanceof WeightedGraph)
            return getWeightedInstance((WeightedGraph) parent, subset);
        else if (parent instanceof ValuedGraph)
            return getValuedInstance((ValuedGraph) parent, subset);
        else
            return new Subgraph(parent, subset);
    }
    /**
     * Factory method returns subgraph of a weighted graph.
     */
    public static <V,E> WeightedGraph<V,E> getWeightedInstance(WeightedGraph<V,E> parent, Collection<V> subset) { return new Weighted(parent, subset); }
    /**
     * Factory method returns subgraph of a valued graph.
     */
    public static <V,N> ValuedGraph<V,N> getValuedInstance(ValuedGraph<V,N> parent, Collection<V> subset) { return new Valued(parent, subset); }
    /**
     * Factory method returns subgraph of a weighted, valued graph.
     */
    public static <V,E,N> WeightedGraph<V,E> getWeightedValuedInstance(WeightedGraph<V,E> parent, Collection<V> subset) { return new WeightedValued(parent, subset); }

    /**
     * Construct a subgraph comprised of a particular subset of vertices within the parent.
     * @param parent the parent graph
     * @param subset the subset of vertices to include in the subgraph
     */
    public Subgraph(Graph<V> parent, Collection<V> subset) {
        if (parent == null || subset == null)
            throw new IllegalArgumentException("Called constructor with null values!");
        this.parent = parent;
        this.subset = subset instanceof List ? (List<V>) subset : new ArrayList<V>(subset);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < subset.size(); i++)
            for (int j = isDirected() ? 0 : i; j < subset.size(); j++)
                if (adjacent(subset.get(i), subset.get(j)))
                    result.append("[" + subset.get(i) + ", " + subset.get(j) + "], ");
        result.delete(result.length()-2, result.length());
        return result.toString();
    }

    public int order() { return subset.size(); }
    public List<V> nodes() { return Collections.unmodifiableList(subset); }
    public boolean contains(V x) { return subset.contains(x); }
    public boolean isDirected() { return parent.isDirected(); }
    public boolean adjacent(V x, V y) { return subset.contains(x) && subset.contains(y) && parent.adjacent(x, y); }
    public int degree(V x) { 
        List<V> nbhd = neighbors(x);
        return nbhd.size() + (!isDirected() && nbhd.contains(x) ? 1 : 0);
    }
    public List<V> neighbors(V x) {
        if (!contains(x)) return Collections.emptyList();
        ArrayList<V> result = new ArrayList<V>();
        result.addAll(parent.neighbors(x));
        result.retainAll(subset);
        return result;
    }
    public int edgeNumber() {
        int sum = 0;
        for (V v1 : subset)
            for (V v2 : subset)
                if (parent.adjacent(v1, v2))
                    sum += v1==v2 && !isDirected() ? 2 : 1;
        return isDirected() ? sum : sum/2;
    }

    //
    // INNER CLASSES
    //

    /** Wrapper subgraph that passes along information about weighted edges */
    public static class Weighted<V,E> extends Subgraph<V> implements WeightedGraph<V,E> {
        WeightedGraph<V,E> wg;
        public Weighted(Graph<V> parent, Collection<V> subset) {
            super(parent, subset);
            wg = (WeightedGraph<V,E>) parent;
        }
        public E getWeight(V x, V y) {
            return subset.contains(x) && subset.contains(y) ? wg.getWeight(x, y) : null;
        }
        /** This method does nothing. Use the corresponding method in the superclass instead. */
        public void setWeight(V x, V y, E value) {}
    }

    /** Wrapper subgraph that passes along information about valued nodes */
    public static class Valued<V,N> extends Subgraph<V> implements ValuedGraph<V,N> {
        ValuedGraph<V,N> vg;
        public Valued(Graph<V> parent, Collection<V> subset) {
            super(parent, subset);
            vg = (ValuedGraph<V,N>) parent;
        }
        public N getValue(V x) {
            return subset.contains(x) ? vg.getValue(x) : null;
        }
        /** This method does nothing. Use the corresponding method in the superclass instead. */
        public void setValue(V x, N value) {}
    }

    /** Wrapper subgraph that passes along information about weighted edges */
    public static class WeightedValued<V,E,N> extends Weighted<V,E> implements ValuedGraph<V,N> {
        ValuedGraph<V,N> vg;
        public WeightedValued(Graph<V> parent, Collection<V> subset) {
            super(parent, subset);
            vg = (ValuedGraph<V,N>) parent;
        }
        public N getValue(V x) {
            return subset.contains(x) ? vg.getValue(x) : null;
        }
        /** This method does nothing. Use the corresponding method in the superclass instead. */
        public void setValue(V x, N value) {}
    }

}
