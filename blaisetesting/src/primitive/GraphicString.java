/**
 * GraphicString.java
 * Created on Sep 6, 2009
 */

package primitive;

import java.awt.geom.Point2D;

/**
 * <p>
 *   <code>GraphicString</code> is a graphics primitive that contains a string,
 *   an anchor point, and an orientation. The class extends a 2d point so that it
 *   can be used with point style methods.
 * </p>
 *
 * @author Elisha Peterson
 * @param <C> coordinate system used for anchor
 */
public class GraphicString<C> extends Point2D.Double {

    /** Anchor point (coincides with location if C is a Point2D.Double) */
    public C anchor;
    /** String to draw at point */
    public String string;
    /** Location of string relative to anchor point */
    public Point2D.Double offset = new Point2D.Double(0, 0);

    /**
     * Construct graphic string, i.e. a string anchored at a particular point.
     * If the anchor is a <code>Point2D.Double</code>, also updates to ensure
     * that the class's parameters are the anchor point.
     * @param anchor where the string is located
     * @param string the string to be displayed
     */
    public GraphicString(C anchor, String string) {
        setAnchor(anchor);
        setString(string);
    }

    /** @return the string associated with the graphic string primitive */
    public String getString() { return string; }
    /** Set the style associated with the graphic string primitive. */
    public void setString(String string) { this.string = string; }
    /** @return offset from anchor point to draw the primitive */
    public Point2D.Double getOffset() { return offset; }
    /** Sets offset from anchor point to draw the primitive */
    public void setOffset(Point2D.Double newValue) { offset = newValue; }
    
    /** @return the coordinate anchor point of the graphic string primitive */
    public C getAnchor() { return anchor; }
    /** Set the anchor point of the graphic string primitive.
     * If the anchor is a <code>Point2D.Double</code>, also updates to ensure
     * that the class's parameters are the anchor point. */
    public void setAnchor(C newAnchor) {
        anchor = newAnchor;
        if (anchor instanceof Point2D)
            setLocation((Point2D) newAnchor);
    }
    @Override
    public void setLocation(double x, double y) {
        super.setLocation(x, y);
        if (anchor instanceof Point2D)
            ((Point2D)anchor).setLocation(x, y);
    }
    @Override
    public void setLocation(Point2D p) {
        super.setLocation(p);
        if (anchor instanceof Point2D)
            ((Point2D)anchor).setLocation(x, y);
    }
}
