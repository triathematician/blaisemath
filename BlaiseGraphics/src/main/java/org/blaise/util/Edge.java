/**
 * Edge.java
 * Created Aug 5, 2012
 */

package org.blaise.util;

/**
 * An edge in a graph.
 * 
 * @param <V> type of node
 * 
 * @author elisha
 */
public class Edge<V> {
    
    private final V v1;
    private final V v2;

    /**
     * Initialize edge
     * @param v1 first vertex
     * @param v2 second vertex
     */
    public Edge(V v1, V v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    @Override
    public String toString() {
        return String.format("%s:%s",v1,v2);
    }
    
    /**
     * Edge's first node
     * @return 
     */
    public V getNode1() {
        return v1;
    }
    
    /**
     * Edge's second node
     * @return 
     */
    public V getNode2() {
        return v2;
    }
    
    /**
     * Checks if either vertex equals provided vertex
     * @param v test vertex
     * @return true if v1==v or v2==v
     */
    public boolean adjacent(V v) {
        return v1==v || v2==v;
    }
    
    /**
     * Return vertex opposite provided vertex
     * @param v test vertex
     * @return opposite vertex, or null if vertex is not part of this edge
     */
    public V opposite(V v) {
        return v1==v ? v2 : v2==v ? v1 : null;
    }

}
