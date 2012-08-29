/**
 * ShapeStyle.java
 * Created on Aug 4, 2009
 */

package org.blaise.style;

import java.awt.Color;

/**
 * <p>
 *   Used to draw a path (or several paths) on a {@code java.awt.Graphics2D} object.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface PathStyle extends ShapeStyle {

    /**
     * Return path color
     * @return color
     */
    public Color getStroke();
    
    /**
     * Return path thickness
     * @return thickness of path
     */
    public float getThickness();
    
}
