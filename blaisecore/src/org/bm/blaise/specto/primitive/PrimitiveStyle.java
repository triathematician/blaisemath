/**
 * PrimitiveStyle.java
 * Created on Aug 4, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

/**
 * <p>
 *   <code>PrimitiveStyle</code> is used to paint graphics primitives on a 2D Graphics object.
 *   So it is somewhat an extension of the <code>Graphics2D</code> methods for drawing,
 *   changing stroke and fill, etc.
 * </p>
 *
 * @param <P> the type of graphics primitive controlled by this class
 * 
 * @author Elisha Peterson
 */
abstract public class PrimitiveStyle<P> {

    /** Default stroke of 1 unit width. */
    public static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0f);

    /**
     * Draws the provided primitive on the provided canvas.
     * @param canvas the canvas on which to paint
     * @param primitive the primitive graphics object plottable by this style class
     * @param selected whether the primitive is currently "selected"
     */
    abstract public void draw(Graphics2D canvas, P primitive, boolean selected);

    /**
     * Draws a series of primitives on the provided canvas.
     * @param canvas the canvas on which to paint
     * @param primitives the primitive graphics object plottable by this style class
     * @param selected whether the primitive is currently "selected"
     */
    public void draw(Graphics2D canvas, P[] primitives, boolean selected) {
        for(P p : primitives)
            draw(canvas, p, selected);
    }
}
