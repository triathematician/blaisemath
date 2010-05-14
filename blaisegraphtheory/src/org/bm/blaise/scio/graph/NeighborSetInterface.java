/*
 * NeighborSetInterface.java
 * Created on Oct 26, 2009
 */

package org.bm.blaise.scio.graph;

/**
 * <p>
 *  This class represents a set of objects that may or may not be adjacent. It is a
 *  "light" version of a graph. Vertices are assumed to be enumerated from 0 to size-1,
 *  so that adjacency checks by indices.
 *  This class also supports iterating over the vertices (underlying objects), or
 *  returning the label or object corresponding to each index.
 * </p>
 * @author Elisha Peterson
 */
public interface NeighborSetInterface<V> extends Iterable<V> {

    /** @return number of vertices. */
    public int getSize();

    /** @return true if objects at provided indices are adjacent */
    public boolean adjacent(int i1, int i2);

    /**
     * Returns object at specified index
     * @param i index of object to return
     * @return object at specified index
     */
    public V getObject(int index);

    /**
     * Returns label of object at specified index
     * @param i index of object
     * @return label associated with object at specified index, possibly null
     */
    public String getLabel(int index);

}
