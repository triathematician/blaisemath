/*
 * DraggablePointBean.java
 * Created Jan 229, 2011
 */
package org.blaise.util;

/**
 * Interface that can get and set a point in an arbitrary coordinate system.
 * A third method allows the point to be set based on an initial point, and
 * coordinates for the start and end of a drag gesture.
 * 
 * @see PointBean
 * 
 * @param <C> coordinate of the point
 * 
 * @author Elisha Peterson
 */
public interface DraggablePointBean<C> extends PointBean<C> {
    
    /** 
     * Sets the point by movement from an initial point 
     * @param initial starting position
     * @param dragStart start of drag
     * @param dragFinish end of drag
     */
    public void setPoint(C initial, C dragStart, C dragFinish);
    
}
