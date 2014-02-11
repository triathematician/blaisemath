/*
 * DraggableIndexedPointBean.java
 * Created Jan 22, 2011
 */
package org.blaise.util;

/**
 * Interface that can get and set a point in an arbitrary coordinate system.
 * A third method allows the point to be set based on an initial point, and
 * coordinates for the start and end of a drag gesture.
 * 
 * @see PointBean
 * 
 * @author Elisha Peterson
 */
public interface DraggableIndexedPointBean<C> extends IndexedPointBean<C> {
    
    /** 
     * Sets the point by movement from an initial point 
     * @param i point index
     * @param initial starting position
     * @param dragStart start of drag
     * @param dragFinish end of drag
     */
    void setPoint(int i, C initial, C dragStart, C dragFinish);
    
}
