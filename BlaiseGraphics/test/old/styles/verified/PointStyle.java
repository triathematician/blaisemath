/**
 * PointStyle.java
 * Created on Aug 4, 2009
 */
package old.styles.verified;

import old.styles.transferred.AbstractPointStyle;
import graphics.renderer.ShapeRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.Map;
import graphics.renderer.ShapeLibrary;

/**
 * <p>
 *   <code>PointStyle</code> draws a point on a 2D graphics canvas.
 *   Supports multiple styles of drawArray, encoded by the <code>shape</code> property,
 *   in the sub-class <code>PointStyle.ShapeLibrary</code>. If the radius of a point
 *   is negative, the style will display that as a lighter colored point.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PointStyle extends AbstractPointStyle implements ShapeRenderer<Point2D.Double> {

    /** Default point. */
    public static final PointStyle REGULAR = new PointStyle();
    /** Point that shows up as a small gray dot. */
    public static final PointStyle SMALL = new PointStyle(ShapeLibrary.CIRCLE, null, null, Color.DARK_GRAY, 2);

    /** Construct with defaults. */
    public PointStyle() { super(); }
    /** Construct with colors only. */
    public PointStyle(Color strokeColor, Color fillColor) { super(strokeColor, fillColor); }
    /** Construct with specified elements. */
    public PointStyle(ShapeLibrary shape, int radius) { super(shape, radius); }
    /** Construct with specified elements. */
    public PointStyle(ShapeLibrary shape, BasicStroke stroke, Color strokeColor, Color fillColor, int radius) { super(shape, stroke, strokeColor, fillColor, radius); }

    @Override
    public String toString() {
        return "PointStyle [" + shape + ", r=" + radius + "]";
    }

    public Class<? extends Point2D.Double> getTargetType() {
        return Point2D.Double.class;
    }

    //
    // DRAW METHODS
    //

    public void draw(Graphics2D canvas, Point2D.Double point) {
        Shape s = shape.getShape(point.x, point.y, Math.abs(radius));
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

    public void drawArray(Graphics2D canvas, Point2D.Double[] primitives) {
        for (Point2D.Double p : primitives)
            draw(canvas, p);
    }

    public boolean contained(Point2D.Double primitive, Graphics2D canvas, Point point) {
        return primitive.distance(point) <= radius;
    }

    public int containedInArray(Point2D.Double[] primitives, Graphics2D canvas, Point point) {
        for (int i = 0; i < primitives.length; i++)
            if (contained(primitives[i], canvas, point))
                return i;
        return -1;
    }
}
