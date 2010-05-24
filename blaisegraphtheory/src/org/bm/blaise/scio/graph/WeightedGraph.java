/*
 * WeightedGraph.java
 * Created May 21, 2010
 */

package org.bm.blaise.scio.graph;

/**
 * This is a graph <b>mixin</b> indicating support for an edge value in a graph.
 *
 * @param <E> the type of value associated with each edge
 * @param <V> the type of nodes of the graph
 * @author Elisha Peterson
 */
public interface WeightedGraph<E,V> extends Graph<V> {

    /**
     * Returns the value associated with an edge in the graph.
     * @param x the first node of the edge
     * @param y the second node of the edge
     * @return the value associatd with the edge
     */
    public E getWeight(V x, V y);


    /**
     * Returns the weighted degree/outdegree of an edge in the graph, i.e. the sum of weights
     * of edges from x to other nodes
     * @param x base node
     * @return total weight of outgoing edges
     */
    public E weightedDegree(V x);


}
