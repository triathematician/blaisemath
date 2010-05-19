/**
 * PointStyle.java
 * Created on Aug 4, 2009
 */
package primitive.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * <p>
 *   <code>PointStyle</code> draws a point on a 2D graphics canvas.
 *   Supports multiple styles of draw, encoded by the <code>shape</code> property,
 *   in the sub-class <code>PointStyle.PointShape</code>. If the radius of a point
 *   is negative, the style will display that as a lighter colored point.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PointStyle extends AbstractPointStyle implements PrimitiveStyle<Point2D.Double> {

    /** Default point. */
    public static final PointStyle REGULAR = new PointStyle();
    /** Point that shows up as a small gray dot. */
    public static final PointStyle SMALL = new PointStyle(PointShape.CIRCLE, null, null, Color.DARK_GRAY, 2);

    /** Construct with defaults. */
    public PointStyle() { super(); }
    /** Construct with colors only. */
    public PointStyle(Color strokeColor, Color fillColor) { super(strokeColor, fillColor); }
    /** Construct with specified elements. */
    public PointStyle(PointShape shape, int radius) { super(shape, radius); }
    /** Construct with specified elements. */
    public PointStyle(PointShape shape, BasicStroke stroke, Color strokeColor, Color fillColor, int radius) { super(shape, stroke, strokeColor, fillColor, radius); }

    @Override
    public String toString() {
        return "PointStyle [" + shape + ", r=" + radius + "]";
    }

    public Class<? extends Point2D.Double> getTargetType() {
        return Point2D.Double.class;
    }

    public void draw(Graphics2D canvas, Point2D.Double point) {
        Shape s = getShape(shape, point.x, point.y, Math.abs(radius));
        if (fillColor != null) {
            canvas.setColor( radius < 0 ? lighterVersionOf(fillColor) : fillColor );
            canvas.fill(s);
        }
        if (stroke != null && strokeColor != null) {
            canvas.setColor( radius < 0 ? lighterVersionOf(strokeColor) : strokeColor );
            canvas.setStroke(stroke);
            canvas.draw(s);
        }
    }

    public boolean contained(Point2D.Double primitive, Graphics2D canvas, Point point) {
        return primitive.distance(point) <= radius;
    }

    public void draw(Graphics2D canvas, Point2D.Double[] primitives) {
        for (Point2D.Double p : primitives)
            draw(canvas, p);
    }

    public int containedInArray(Point2D.Double[] primitives, Graphics2D canvas, Point point) {
        for (int i = 0; i < primitives.length; i++)
            if (contained(primitives[i], canvas, point))
                return i;
        return -1;
    }


    private static Color lighterVersionOf(Color c) {
        return new Color(lighten(c.getRed()), lighten(c.getGreen()), lighten(c.getBlue()));
    }

    private static int lighten(int i) {
        return i + Math.min(64, (255-i)/2);
    }
}
