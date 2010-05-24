/*
 * WeightedNodeGraph.java
 * Created May 21, 2010
 */

package org.bm.blaise.scio.graph;

/**
 * This is a graph <b>mixin</b> indicating support for a nodal value in a graph. It may
 * be used with either <code>Graph</code> or <code>Pseudograph</code> implementations.
 *
 * @param <N> the type of value associated with each node
 * @parma <V> the type of nodes in the graph
 * @author Elisha Peterson
 */
public interface WeightedNodeGraph<N,V> {

    /**
     * Returns the value associated with a node in the graph.
     * @param x the node
     * @return the value associatd with the node
     */
    public N getWeight(V x);


}
