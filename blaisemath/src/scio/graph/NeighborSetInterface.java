/*
 * NeighborSetInterface.java
 * Created on Oct 26, 2009
 */

package scio.graph;

/**
 * <p>
 *  This class represents a set of objects that may or may not be adjacent. It is a
 *  "light" version of a graph.
 * </p>
 * @author ae3263
 */
public interface NeighborSetInterface<V> extends Iterable<V> {

    /** @return number of vertices. */
    public int getSize();

    /** @return true if the two objects are adjacent. */
    public boolean adjacent(V v1, V v2);

}
