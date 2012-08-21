/*
 * StyleProvider.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.style;

/**
 * <p>
 *   Provides methods that can be used to retrieve styles specialized for drawing
 *   basic shapes, points, and text on a graphics canvas.
 *   In a sense, this is similar to the functionality provided by a <em>Cascading Style Sheet</em>.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface StyleProvider {

    /** 
     * Return style used for solid shapes (fill and stroke)
     * @return a solid shape style
     */
    public ShapeStyle getShapeStyle();
    
    /** 
     * Return style used for paths (stroke only)
     * @return a path style 
     */
    public PathStyle getPathStyle();
    
    /** 
     * Return style used for points
     * @return a point style 
     */
    public PointStyle getPointStyle();
    
    /**
     * Return style used for strings
     * @return a string style 
     */
    public StringStyle getStringStyle();

}
