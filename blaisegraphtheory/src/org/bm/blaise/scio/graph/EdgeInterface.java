/**
 * EdgeInterface.java
 * Created on Oct 14, 2009
 */

package org.bm.blaise.scio.graph;

/**
 * <p>
 *   <code>EdgeInterface</code> is an interface for graph edges.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface EdgeInterface<V> {

    /** 
     * @return first vertex of the edge
     */
    public V getSource();

    /** 
     * @return second vertex of the edge
     */
    public V getSink();

    /**
     * @return weight of the edge
     */
    public double getWeight();

    /**
     * @param vs one or more vertices of the graph
     * @return true if the edge is adjacent to all of the specified vertices.
     */
    public boolean isAdjacent(V... vs);
    
    /** 
     * @return true if the edge is directed.
     */
    public boolean isDirected();

    /**
     * @return true if the edge is weighted
     */
    public boolean isWeighted();

    /**
     * @return true if edge is equal to another specified edge
     */
    public boolean equals(EdgeInterface e2);
}
