/**
 * PrimitiveStyle.java
 * Created on Aug 4, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * <p>
 *   <code>PrimitiveStyle</code> is used to paint graphics primitives on a 2D Graphics object.
 *   So it is somewhat an extension of the <code>Graphics2D</code> methods for drawing,
 *   changing stroke and fill, etc.
 * </p>
 *
 * @param <P> the type of graphics primitive controlled by this class
 * @author Elisha Peterson
 */
public interface PrimitiveStyle<P> {

    /** Default stroke of 1 unit width. */
    public static final Stroke DEFAULT_STROKE = new BasicStroke(1.0f);

    /**
     * Draws the provided primitive on the provided canvas.
     * @param primitive the primitive graphics object plottable by this style class
     * @param canvas the canvas on which to paint
     */
    public void draw(P primitive, Graphics2D canvas);

    /**
     * Draws a series of primitives on the provided canvas.
     * @param primitives the primitive graphics object plottable by this style class
     * @param canvas the canvas on which to paint
     */
    public void draw(P[] primitives, Graphics2D canvas);
}
