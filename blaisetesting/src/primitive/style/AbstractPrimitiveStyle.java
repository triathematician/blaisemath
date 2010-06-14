/*
 * AbstractPrimitiveStyle.java
 * Created May 13, 2010
 */

package primitive.style;

import java.awt.Graphics2D;
import java.awt.Point;

/**
 * Implements default behavior for drawing/checking containment for arrays of primitives.
 * Can be used as a superclass for primitive styles that do not inherit from another type.
 * @author Elisha Peterson
 */
public abstract class AbstractPrimitiveStyle<C> implements PrimitiveStyle<C> {

    /**
     * Draws a series of primitives on the provided canvas, using the class's <code>draw</code>
     * method.
     * @param canvas the canvas on which to paint
     * @param primitives the primitive graphics object plottable by this style class
     */
    public void drawArray(Graphics2D canvas, C[] primitives) {
        for (C p : primitives)
            draw(canvas, p);
    }

    public int containedInArray(C[] primitives, Graphics2D canvas, Point point) {
        for (int i = 0; i < primitives.length; i++)
            if (contained(primitives[i], canvas, point))
                return i;
        return -1;
    }
}
