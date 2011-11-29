/*
 * PointBean.java
 * Created Jan 2011
 */
package org.bm.util;

/**
 * An object that can get and set a point (used to simplify point dragging).
 *
 * @param <C> the type of point
 * 
 * @author Elisha Peterson
 */
public interface PointBean<C> {

    /**
     * Return the point.
     * @return the point
     */
    public C getPoint();

    /**
     * Set the point.
     * @param p the new point
     */
    public void setPoint(C p);
    
}
