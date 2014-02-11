/**
 * ShapeStyle.java
 * Created on Aug 4, 2009
 */

package org.blaise.style;

import java.awt.Color;
import java.awt.Shape;
import javax.annotation.Nullable;

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
    Color getStroke();

    /**
     * Return path thickness
     * @return thickness of path
     */
    float getStrokeWidth();

    /**
     * Return shape of path drawn, useful for testing
     * when the mouse has moved over the path.
     * @param primitive shape primitive
     * @return path shape
     */
    @Nullable Shape shapeOfPath(Shape primitive);

}
