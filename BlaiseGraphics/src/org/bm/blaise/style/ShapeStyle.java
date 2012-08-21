/**
 * ShapeStyle.java
 * Created on Aug 4, 2009
 */

package org.bm.blaise.style;

/**
 * <p>
 *   Used to draw a shape (or several shapes) on a {@code java.awt.Graphics2D} object.
 *   This class will provide visual effects that do not determine the shape of the object.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface ShapeStyle {

    /**
     * Draws a shape on the provided canvas.
     * @param primitive the shape to draw
     * @param canvas the canvas on which to paint
     * @param visibility whether drawn result should be "highlighted" or otherwise emphasized
     */
    public void draw(java.awt.Shape primitive, java.awt.Graphics2D canvas, VisibilityKey visibility);

}
