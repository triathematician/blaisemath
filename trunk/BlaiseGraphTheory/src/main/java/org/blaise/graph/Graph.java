/*
 * Graph.java
 * Created May 21, 2010
 */

package org.blaise.graph;

import java.util.Collection;
import java.util.Set;
import org.blaise.util.Edge;

/**
 * <p>
 *  A graph is an arbitrary set of objects called <i>nodes</i> together with an
 *  arbitrary set of ordered (<i>directed</i> graphs) or unordered (<i>undirected</i> graphs)
 *  pairs of these objects called <i>edges</i>.
 *  The data type of the nodes is specified by a type parameter.
 * </p>
 * <p>
 *  Edges are <i>adjacent</i> to nodes they contains, and two vertices are
 *  <i>adjacent</i> if they share a common edge.
 * </p>
 * <p>
 *  For directed graphs:
 *  the <i>in-degree</i> of a node is the number of <i>incoming</i> edges, where
 *  the node is the second part of the edge;
 *  the <i>out-degree</i> of a node is the number of <i>outgoing</i> edges, where
 *  the node is the first part of the edge;
 *  the <i>total degree</i> of a node is the sum of these two.
 *  For undirected graphs, all three of these methods should return the same
 *  result and be equal to the count of non-loop edges, plus two times the number
 *  of loop edges.
 * </p>
 *
 * @param <V> the type of the nodes
 *
 * @author Elisha Peterson
 */
public interface Graph<V> {
    
    //
    // GENERAL PROPERTIES
    //

    /**
     * Says whether graph is directed
     * @return true if graph is directed, false otherwise
     */
    public boolean isDirected();

    
    //
    // NODE METHODS
    //

    /**
     * Returns the number of nodes in the graph
     * @return the number of nodes in the graph
     */
    public int nodeCount();

    /**
     * Returns an (unordered) view of nodes in the graph.
     * @return nodes in graph, may be empty
     */
    public Set<V> nodes();

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
     * Returns the number of edges in the graph
     * @return the number of edges in the graph
     */
    public int edgeCount();
    
    /**
     * Returns an (unordered) view of edges in the graph
     * @return edges in graph, may be empty
     */
    public Set<? extends Edge<V>> edges();
    
    
    //
    // ADJACENCY METHODS
    //

    /**
     * Determines if there is an edge shared by the two vertices. It does not
     * matter what the direction of the edge is.
     * @param x a node
     * @param y another node
     * @return true if there's an x:y or a y:x edge, false otherwise
     */
    public boolean adjacent(V x, V y);
    
    /**
     * List the edges that are adjacent to a given node. Should return an empty
     * set for nodes that are not in the graph.
     * @param x a node
     * @return adjacent edges
     */
    public Collection<? extends Edge<V>> edgesAdjacentTo(V x);

    /**
     * List all nodes with an edge from x to the node.
     * @param x a node
     * @return neighborhood
     */
    public Set<V> outNeighbors(V x);

    /**
     * List all nodes with an edge from the node to x.
     * @param x a node
     * @return neighborhood
     */
    public Set<V> inNeighbors(V x);

    /**
     * List all nodes that are adjacent to a given node, comprised of all vertices
     * that share an edge with x. The set will include x only if there is a self-loop.
     * @param x a node
     * @return neighborhood
     */
    public Set<V> neighbors(V x);
    
    /**
     * The out-degree of a node, the number of edges with x as the first vertex
     * if directed, or either vertex if undirected.
     * @param x a node
     * @return out-degree, 0 if an isolate or not in the graph
     */
    public int outDegree(V x);
    
    /**
     * The in-degree of a node, the number of edges with x as the second vertex
     * if directed, or either vertex if undirected.
     * @param x a node
     * @return in-degree, 0 if an isolate or not in the graph
     */
    public int inDegree(V x);

    /**
     * The total degree of a node, equal to the sum of {@link #outDegree(java.lang.Object)}
     * and {@link #inDegree(java.lang.Object)}
     * @param x a node
     * @return total degree
     */
    public int degree(V x);

}
