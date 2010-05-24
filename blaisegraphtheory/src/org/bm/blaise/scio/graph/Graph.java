/*
 * Graph.java
 * Created May 21, 2010
 */

package org.bm.blaise.scio.graph;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * A graph is a collection of objects together with known "adjacencies" among the objects.
 * The data type of the nodes is specifed by a type parameter. This interface does
 * not provide any methods for altering the graph, so implementations must provide
 * concrete methods for constructing the graph.
 * </p>
 * <p>
 * It is <i>not</i> recommended that multigraphs use this interface. Instead, use the
 * provided <code>Pseudograph</code> interface, which provides proper support for
 * graphs with multiple edges between vertices.
 * </p>
 *
 * @param <V> the type of the nodes
 *
 * @see Pseudograph
 *
 * @author Elisha Peterson
 */
public interface Graph<V> {

    //
    // VERTEX-SPECIFIC METHODS
    //

    /**
     * Returns the order of the graph, the number of nodes.
     * @return the number of nodes in the graph
     */
    public int order();

    /**
     * Returns a view of the nodes in the graph
     * @return a collection of all vertices in the graph, possibly empty
     */
    public Collection<V> nodes();

    /**
     * Tests to see if the graph contains a node
     * @param x a node
     * @return true if the graph contains the node x, otherwise false
     */
    public boolean contains(V x);

    //
    // EDGE METHODS
    //

    /**
     * Determines if there is an edge from the first node to the second.
     * For undirected graphs, all that matters is whether the nodes are adjacent.
     * Return false if either node is not contained in the graph.
     * @param x first node
     * @param y second node
     * @return true if there is an edge from x to y; false if there is not or if the nodes are not in the graph
     */
    public boolean adjacent(V x, V y);

    /**
     * Returns the degree/outdegree of an edge in the graph, i.e. the number of nodes
     * for which there is an edge from x to the node
     * @param x base node
     * @return number of nodes x points to; or 0 if the node is not in the graph
     */
    public int degree(V x);

    /**
     * List all nodes that are adjacent to a given node, i.e. all nodes for which
     * there is an edge from x to that node.
     * For undirected graphs, returns all adjacent vertices.
     * @param x base node
     * @return a list of nodes that x points to; or an empty list if the node is not in the graph. The order of the list is unspecified.
     */
    public List<V> neighbors(V x);

    /**
     * Returns the size of the graph, the number of edges.
     * @return the number of edges in the graph
     */
    public int size();

}
