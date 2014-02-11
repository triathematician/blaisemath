/*
 * IndexedPointBean.java
 * Created Jan 2011
 */
package org.blaise.util;

import java.awt.geom.Point2D;

/**
 * An object that can get and set an array of points (used to simplify point dragging).
 *
 * @param <C> the type of point
 * 
 * @author Elisha Peterson
 */
public interface IndexedPointBean<C> {
    
    /**
     * Return number of points
     * @return number of points
     */
    int getPointCount();

    /**
     * Return the point.
     * @param i the index
     * @return the point
     */
    C getPoint(int i);

    /**
     * Set the point.
     * @param i the index
     * @param p the new point
     */
    void setPoint(int i, C p);

    /**
     * Return the index of the specified point
     * @param point window location
     * @param testPoint point to look up
     * @return index at the specified location, or -1 if there is no point at that location
     */
    int indexOf(Point2D point, C testPoint);
    
}
