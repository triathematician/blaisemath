/*
 * StyleContext.java
 * Created Jan 22, 2011
 */
package org.blaise.style;

/**
 * <p>
 *   Provides methods that can be used to retrieve styles specialized for drawing
 *   basic shapes, points, and text on a graphics canvas.
 *   In a sense, this is similar to the functionality provided by a <em>Cascading Style Sheet</em>.
 * </p>
 * 
 * @param <Src> type of object to be styled
 *
 * @author Elisha Peterson
 */
public interface StyleContext<Src> {

    /** 
     * Return style used for solid shapes (fill and stroke)
     * @param src object to be styled
     * @return a solid shape style
     */
    ShapeStyle getShapeStyle(Src src);
    
    /** 
     * Return style used for paths (stroke only)
     * @param src object to be styled
     * @return a path style 
     */
    PathStyle getPathStyle(Src src);
    
    /** 
     * Return style used for points
     * @param src object to be styled
     * @return a point style 
     */
    PointStyle getPointStyle(Src src);
    
    /**
     * Return style used for strings
     * @param src object to be styled
     * @return a string style 
     */
    TextStyle getStringStyle(Src src);

}
