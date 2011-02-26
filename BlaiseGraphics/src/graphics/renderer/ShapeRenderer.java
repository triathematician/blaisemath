/**
 * PrimitiveRenderer.java
 * Created on Aug 4, 2009
 */

package graphics.renderer;

import graphics.GraphicVisibility;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * <p>
 *   Used to draw a shape (or several shapes) on a Graphics2D object.
 *   This class will provide visual effects that do not determine the shape of the object.
 * </p>
 *
 * @param <P> the type of graphics primitive controlled by this class
 * 
 * @author Elisha Peterson
 */
public interface ShapeRenderer {

    /**
     * Draws a shape on the provided canvas.
     * @param canvas the canvas on which to paint
     * @param primitive the shape to draw
     * @param visibility whether drawn result should be "highlighted" or otherwise emphasized
     */
    public void draw(Shape primitive, Graphics2D canvas, GraphicVisibility visibility);

    /**
     * Draws several shape on the provided canvas
     * @param canvas the canvas on which to paint
     * @param primitive the shape to draw
     * @param visibility whether drawn result should be "highlighted" or otherwise emphasized
     */
    public void drawAll(Iterable<Shape> primitives, Graphics2D canvas, GraphicVisibility visibility);

}
