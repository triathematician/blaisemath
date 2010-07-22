/*
 * PointLabeledStyle.java
 * Created Jul 20, 2010
 */

package primitive.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import primitive.GraphicPointFancy;

/**
 * Draws a point that comes along with a radius and a label.
 *
 * @author Elisha Peterson
 */
public class PointFancyStyle extends PointLabeledStyle {
    
    /** Multiplier to scale the points. */
    double maxRadius = 5.0;

    /** Construct with defaults. */
    public PointFancyStyle() { super(); }
    /** Construct with default point style, specified anchor location */
    public PointFancyStyle(StringStyle.Anchor anchor) { super(anchor); }
    /** Construct with colors only. */
    public PointFancyStyle(Color strokeColor, Color fillColor, StringStyle.Anchor anchor) { super(strokeColor, fillColor, anchor); }
    /** Construct with specified elements. */
    public PointFancyStyle(PointShape shape, StringStyle.Anchor anchor) { super(shape, 6, anchor); }
    /** Construct with specified elements. */
    public PointFancyStyle(PointShape shape, BasicStroke stroke, Color strokeColor, Color fillColor, StringStyle.Anchor anchor) { super(shape, stroke, strokeColor, fillColor, 6, anchor); }

    /** @return current maximum radius drawn (applies to drawing multiples only) */
    public double getMaxRadius() { return maxRadius; }
    /** @param length new max radius */
    public void setMaxRadius(double length) { maxRadius = length; }

    @Override
    public void draw(Graphics2D canvas, Point2D.Double point) {
        if (point instanceof GraphicPointFancy) {
            GraphicPointFancy gpf = (GraphicPointFancy) point;
            setRadius((int) (maxRadius * gpf.rad));
            if (gpf.anchor instanceof Point2D.Double)
                super.draw(canvas, (Point2D.Double) gpf.anchor);
            else
                super.draw(canvas, point);
            if (labelVisible) {
                setOffset(gpf, sStyle.anchor, 3+radius);
                sStyle.draw(canvas, gpf);
            }
        } else
            super.draw(canvas, point);
    }

    @Override
    public boolean contained(Point2D.Double primitive, Graphics2D canvas, Point point) {
        if (primitive instanceof GraphicPointFancy && ((GraphicPointFancy)primitive) instanceof Point2D.Double) {
            GraphicPointFancy<Point2D.Double> gpf = (GraphicPointFancy<Point2D.Double>) primitive;
            return gpf.anchor.distance(point) <= maxRadius * gpf.rad;
        } else
            return super.contained(primitive, canvas, point);
    }


}
