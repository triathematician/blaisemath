/**
 * GraphicPointFancy.java
 * Created on Jul 20, 2010
 */

package old.primitives.transferred;

import old.styles.verified.GraphicString;
import java.awt.Color;

/**
 * <p>
 *   <code>GraphicPointFancy</code> is a graphics primitive that extends the basic
 *   <code>GraphicString</code>, adding in support for a radius multiplier & color.
 *   Because of inheritance, one may pass this class to either <code>PointStyle</code>
 *   or <code>PointLabeledStyle</code> to draw, or to the native <code>PointFancyStyle</code>.
 * </p>
 *
 * @param <C> coordinate system used for anchor
 * @see GraphicString, PointFancyStyle
 * @author Elisha Peterson
 */
public class GraphicPointFancy<C> extends GraphicString<C> {

    /** Size of point */
    public double rad = 1.0;
    /** Color of point */
    public Color color = null;
    /** Opacity */
    public float opacity = 1.0f;

    /**
     * Construct GraphicPointFancy with specified anchor, string label, and radius.
     * If the anchor is a <code>Point2D.Double</code>, also updates to ensure
     * that the class's parameters are the anchor point.
     * @param anchor where the string is located
     * @param string the string to be displayed
     * @param rad custom radius of the point
     */
    public GraphicPointFancy(C anchor, String string, double rad) {
        super(anchor, string);
        this.rad = rad;
    }

    /**
     * Construct GraphicPointFancy with specified anchor, string label, and color.
     * If the anchor is a <code>Point2D.Double</code>, also updates to ensure
     * that the class's parameters are the anchor point.
     * @param anchor where the string is located
     * @param string the string to be displayed
     * @param color color of the point
     */
    public GraphicPointFancy(C anchor, String string, Color color) {
        super(anchor, string);
        this.color = color;
    }

    /**
     * Construct GraphicPointFancy with specified anchor, string label, color, and radius.
     * If the anchor is a <code>Point2D.Double</code>, also updates to ensure
     * that the class's parameters are the anchor point.
     * @param anchor where the string is located
     * @param string the string to be displayed
     * @param rad custom radius of the point
     * @param color color of the point
     */
    public GraphicPointFancy(C anchor, String string, double rad, Color color) {
        super(anchor, string);
        this.rad = rad;
        this.color = color;
    }

    /** @return radius */
    public double getRadius() { return rad; }
    /** @param newValue new radius */
    public void setRadius(double newValue) { rad = newValue; }

    /** @return point color */
    public Color getColor() { return color; }
    /** @param newColor new color */
    public void setColor(Color newColor) { color = newColor; }
}
