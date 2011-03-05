/**
 * PrimitiveStyle.java
 * Created on Aug 4, 2009
 */

package primitive.style;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Map;

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
public interface PrimitiveStyle<P> {

    /** @return target class of the style */
    public abstract Class<? extends P> getTargetType();
    /** @return map with customization keys and corresponding data types */
    public abstract Map<String, Class> getCustomKeys();

    /** Default stroke of 1 unit width. */
    public static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0f);

    /**
     * Draws the provided primitive on the provided canvas.
     * @param canvas the canvas on which to paint
     * @param primitive the primitive graphics object plottable by this style class
     * @param customizer a style customization class that provides alternate, overriding styles
     */
    public abstract void draw(Graphics2D canvas, P primitive, StyleCustomizer customizer);

    /**
     * Draws a series of primitives on the provided canvas.
     * @param canvas the canvas on which to paint
     * @param primitives the primitive graphics object plottable by this style class
     * @param customizer a style customization class that provides alternate, overriding styles
     */
    public void drawArray(Graphics2D canvas, P[] primitives, Map<?, StyleCustomizer> customizer);
//    {
//        for(P p : primitives)
//            drawArray(canvas, p);
//    }

    /**
     * Checks to see if the provided window point is covered by the primitive, when drawn in this style.
     * @param point the window point
     * @param primitive the primitive to check against
     * @param canvas the canvas on which objects are drawn
     * @param customizer a style customization class that provides alternate, overriding styles
     * @return true if the point is part of the primitive, else false
     */
    public abstract boolean contained(Point point, P primitive, Graphics2D canvas, StyleCustomizer customizer);

    /**
     * Checks to see if the provided window point is covered by one of the provided primitives, when drawn in this style.
     * @param point the window point
     * @param primitives list of primitives to check against
     * @param canvas the canvas on which objects are drawn
     * @param customizer a style customization class that provides alternate, overriding styles
     * @return index of the first primitive matching the window point, or -1 if none of them do
     */
    public int containedInArray(Point point, P[] primitives, Graphics2D canvas, Map<?, StyleCustomizer> customizer);
//    {
//        for (int i = 0; i < primitives.length; i++)
//            if (contained(primitives[i], canvas, point))
//                return i;
//        return -1;
//    }
}
