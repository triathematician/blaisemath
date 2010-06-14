/*
 * WeightedNodeValueGraphWrapper.java
 * Created May 29, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.TreeMap;

/**
 * Provides a wrapper implementation of a <code>WeightedGraph</code> that delegates its
 * standard graph behavior to its "parent" graph. As secondary behavior, this class also implements the
 * <code>NodeValueGraph</code> interface, providing default support for custom-labeled nodes.
 *
 * @param <V> the type of nodes in the graph
 * @param <E> the type of value associated with each edge
 * @param <N> the type of value associated with each node
 * @author Elisha Peterson
 */
public class WeightedNodeValueGraphWrapper<V,E,N> extends WeightedGraphWrapper<V,E>
        implements NodeValueGraph<V,N> {

    TreeMap<V,N> labels;

    /**
     * Construct nodal value graph using specified graph as the base.
     * Initializes all node values to "null".
     * @param parent the base graph; may be either directed or undirected
     */
    public WeightedNodeValueGraphWrapper(Graph<V> parent) {
        super(parent);
        labels = new TreeMap<V,N>();
        for (V v : parent.nodes())
            labels.put(v, null);
    }

    public N getValue(V x) { return labels.get(x); }
    /**
     * Sets the value associated with specified node.
     * Does nothing if the node is not already in the graph.
     * @param x the node
     * @param value the value
     */
    public void setValue(V x, N value) { if (parent.contains(x)) labels.put(x, value); }

    @Override
    public String toString() {
        return "NODES: " + labels + "  EDGES: " + weights;
    }

}
