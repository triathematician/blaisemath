/*
 * WeightedGraph.java
 * Created May 21, 2010
 */

package org.bm.blaise.scio.graph;

/**
 * Extends the <code>Graph</code> interface, adding in support
 * for associating objects of arbitrary type with each edge/adjacency.
 *
 * @param <V> the type of nodes of the graph
 * @param <E> the type of value associated with each edge
 * @see WeightedGraphWrapper
 * 
 * @author Elisha Peterson
 */
public interface WeightedGraph<V,E> extends Graph<V> {

    /**
     * Returns the value associated with an edge in the graph.
     * @param x the first node of the edge
     * @param y the second node of the edge
     * @return the value associatd with the edge
     */
    public E getWeight(V x, V y);

}
