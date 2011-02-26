/*
 * WeightedGraphWrapper.java
 * Created May 27, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a wrapper implementation of a <code>WeightedGraph</code> that delegates its
 * standard graph behavior to its "parent" graph.
 *
 * @param <V> the type of nodes in the graph
 * @param <E> the type of value associated with each edge
 * @author Elisha Peterson
 */
public class WeightedGraphWrapper<V,E> extends AbstractWrapperGraph<V>
        implements WeightedGraph<V,E> {

    TreeMap<V,Map<V,E>> weights;

    /**
     * Construct nodal value graph using specified graph as the base.
     * Initializes all node values to "null".
     * @param parent the base graph; may be either directed or undirected
     */
    public WeightedGraphWrapper(Graph<V> parent) {
        super(parent);
        weights = new TreeMap<V,Map<V,E>>();
        for (V v : parent.nodes())
            weights.put(v, new TreeMap<V,E>());
    }

    public E getWeight(V x, V y) { 
        return weights.get(x).get(y);
    }

    /**
     * Sets the value associated with specified edge, provided the edge exists.
     * Does nothing if the specified edge does not exist.
     * @param x the node
     * @param value the value
     */
    public void setWeight(V x, V y, E value) {
        if (parent.adjacent(x, y)) {
            weights.get(x).put(y, value);
            if (!isDirected()) weights.get(y).put(x, value);
        }
    }

    @Override
    public String toString() {
        return "NODES: " + nodes() + "  EDGES: " + weights;
    }

}
