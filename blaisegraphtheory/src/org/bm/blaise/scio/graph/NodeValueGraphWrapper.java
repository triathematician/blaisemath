/*
 * NodeValueGraphWrapper.java
 * Created May 26, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.TreeMap;

/**
 * Provides a wrapper implementation of a <code>NodeValueGraph</code> that delegates its
 * standard graph behavior to its "parent" graph. Also maintains a map of values
 * associated to the vertices in the graph.
 *
 * @param <V> the type of nodes in the graph
 * @param <N> the type of value associated with each node
 * @author Elisha Peterson
 */
public class NodeValueGraphWrapper<V,N> extends AbstractWrapperGraph<V>
        implements NodeValueGraph<V,N> {

    TreeMap<V,N> values;

    /**
     * Construct nodal value graph using specified graph as the base.
     * Initializes all node values to "null".
     * @param parent the base graph
     */
    public NodeValueGraphWrapper(Graph<V> parent) {
        super(parent);
        values = new TreeMap<V,N>();
        for (V v : parent.nodes())
            values.put(v, null);
    }

    public N getValue(V x) { return values.get(x); }    
    /**
     * Sets the value associated with specified node.
     * Does nothing if the node is not already in the graph.
     * @param x the node
     * @param value the value
     */
    public void setValue(V x, N value) { if (parent.contains(x)) values.put(x, value); }

    @Override
    public String toString() {
        return "NODES: " + values + "  " + Graphs.printGraph(parent, false, true);
    }

}
