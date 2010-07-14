/*
 * WeightedNodeGraph.java
 * Created May 21, 2010
 */

package org.bm.blaise.scio.graph;

/**
 * Extends the <code>Graph</code> interface, adding in support
 * for associating objects of arbitrary type with each node.
 *
 * @param <V> the type of nodes in the graph
 * @param <N> the type of value associated with each node
 * @see NodeValueGraphWrapper
 *
 * @author Elisha Peterson
 */
public interface ValuedGraph<V,N> extends Graph<V> {

    /**
     * Returns the value associated with a node in the graph.
     * @param x the node
     * @return the value associatd with the node
     */
    public N getValue(V x);

    /**
     * Sets the value associated with specified node.
     * Does nothing if the node is not already in the graph.
     * @param x the node
     * @param value the value
     */
    public void setValue(V x, N value);

}
