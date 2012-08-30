/*
 * CoordinateListener.java
 * Created on Aug 30, 2012
 */

package org.blaise.util;

import java.util.Map;
import java.util.Set;

/**
 * Receives updates regarding the locations of a collection of objects.
 *
 * @param <Coord> coordinate type
 * @author petereb1
 */
public interface CoordinateListener<Coord> {

    /**
     * Called when coordinates/points are added.
     * @param added coordinates that were added
     */
    void coordinatesAdded(Map<?,Coord> added);

    /**
     * Called when coordinates have been removed
     * @param removed objects whose coordinates were removed
     */
    void coordinatesRemoved(Set<?> removed);

}
