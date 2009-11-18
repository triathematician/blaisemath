/**
 * GraphInterface.java
 * Created on Oct 14, 2009
 */

package org.bm.blaise.scio.graph;

import java.util.Collection;

/**
 * <p>
 *   <code>GraphInterface</code> is a data structure that contains edges between
 *   vertices of type <code>V</code>.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface GraphInterface<V> extends NeighborSetInterface<V> {

    /** 
     * Adds a vertex to the graph, without any edges.
     * @param v1 the vertex
     */
    public void addVertex(V v1);

    /**
     * Removes specified vertex.
     * @param v1 the vertex
     * @return true if the graph contains the specified vertex
     */
    public boolean removeVertex(V v1);

    /** 
     * Returns true if the graph contains the specified vertex.
     * @return true if the graph contains the specified vertex
     */
    public boolean containsVertex(V v1);

    /**
     * Returns all vertices in the graph
     * @return collection of all vertices in the graph
     */
    public Collection<V> getVertices();

    /**
     * Adds an edge between the two vertices.
     * @param edge the edge to add
     */
    public void addEdge(EdgeInterface<V> edge);

    /**
     * Removes an edge between the two vertices.
     * @param edge the edge to add
     * @return true if the graph contains an edge between the specified vertices
     */
    public boolean removeEdge(EdgeInterface<V> edge);

    /**
     * Returns true if the graph contains an edge between the specified vertices.
     * @param edge the edge to add
     */
    public boolean containsEdge(EdgeInterface<V> edge);

        /**
         * Returns the edges of the graph as a collection.
         */
    public Collection<EdgeInterface<V>> getEdges();

    /**
     * Adds an edge between two vertices.
     * @param v1 first vertex of edge
     * @param v2 second vertex of edge
     */
    public void addEdge(V v1, V v2);

    /**
     * Returns the edge between the two vertices.
     * @param v1 first vertex of edge
     * @param v2 second vertex of edge
     * @return the edge between the vertices, or <code>null</code> if there is no connection between them.
     */
    public EdgeInterface<V> getEdge(V v1, V v2);

    /**
     * Returns the weight associated with the edge between the two vertices.
     * @param v1 first vertex of edge
     * @param v2 second vertex of edge
     * @return the weight of edge between the vertices, or <code>Double.NaN</code> if there is no connection between them.
     */
    public double getEdgeWeight(V v1, V v2);
    
    //
    //
    // TYPES
    //
    //

    /**
     * Describes whether the graph is directed.
     * @return true if the graph is directed, false if undirected
     */
    public boolean isDirected();

    /**
     * Describes whether the graph's edges are weighted
     * @return true if the graph is weighted, false otherwise
     */
    public boolean isWeighted();

    /**
     * Describes whether the graph allows multiple edges between the same two vertices
     * @return true if the graph allows multiple edges, false if undirected
     */
    public boolean isMultiEdge();
}
