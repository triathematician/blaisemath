/**
 * GraphicPointFancy.java
 * Created on Jul 20, 2010
 */

package primitive;

/**
 * <p>
 *   <code>GraphicPointFancy</code> is a graphics primitive that extends the basic
 *   <code>GraphicString</code>, adding in support for a radius multiplier.
 *   Because of inheritance, one may pass this class to either <code>PointStyle</code>
 *   or <code>PointLabeledStyle</code> to draw, or to the native <code>PointFancyStyle</code>.
 * </p>
 *
 * @param <C> coordinate system used for anchor
 * @see GraphicString, GraphicPointRadius
 * @author Elisha Peterson
 */
public class GraphicPointFancy<C> extends GraphicString<C> {

    public double rad;

    /**
     * Construct graphic string, i.e. a string anchored at a particular point.
     * If the anchor is a <code>Point2D.Double</code>, also updates to ensure
     * that the class's parameters are the anchor point.
     * @param anchor where the string is located
     * @param string the string to be displayed
     */
    public GraphicPointFancy(C anchor, String string, double rad) {
        super(anchor, string);
        setRadius(rad);
    }

    /** @return radius */
    public double getRadius() { return rad; }
    /** @param newValue new radius */
    public void setRadius(double newValue) { rad = newValue; }
}
