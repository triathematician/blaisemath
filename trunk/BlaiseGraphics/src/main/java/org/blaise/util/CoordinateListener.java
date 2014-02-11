/*
 * CoordinateListener.java
 * Created on Aug 30, 2012
 */

package org.blaise.util;

/**
 * Receives updates regarding the locations of a collection of objects.
 *
 * @author petereb1
 */
public interface CoordinateListener {

    /**
     * Called when coordinates/points are added.
     * @param evt description of what coordinates were added/removed/changed
     */
    void coordinatesChanged(CoordinateChangeEvent evt);

}
