/*
 * NeighborSetUtils.java
 * Created May 18, 2010
 */

package org.bm.blaise.scio.graph;

/**
 * Provides some static methods for working with the generic <code>NeighborSetInterface</code> class.
 *
 * @author Elisha Peterson
 */
public class NeighborSetUtils {

    /**
     * Computes degree of specified vertex in a neighbor set.
     * Computation time is linear in number of vertices.
     * @param graph the neighbor set interface object
     * @param vertex the index of the vertex
     * @return number of connections for that vertex in the neighbor set
     */
    public static int degree(NeighborSetInterface graph, int vertex) {
        int count = 0;
        for (int i = 0; i < graph.size(); i++)
            if (graph.adjacent(vertex, i))
                count++;
        return count;
    }

}
