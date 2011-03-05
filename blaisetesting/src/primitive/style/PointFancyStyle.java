/*
 * PointFancyStyle.java
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
    /** Radius multiplier */
    double rMult = 1.0;

    /** Construct with defaults. */
    public PointFancyStyle() { super(); }
    /** Construct with default point style, specified anchor location */
    public PointFancyStyle(Anchor anchor) { super(anchor); }
    /** Construct with colors only. */
    public PointFancyStyle(Color strokeColor, Color fillColor, Anchor anchor) { super(strokeColor, fillColor, anchor); }
    /** Construct with specified elements. */
    public PointFancyStyle(PointShape shape, Anchor anchor) { super(shape, 6, anchor); }
    /** Construct with specified elements. */
    public PointFancyStyle(PointShape shape, BasicStroke stroke, Color strokeColor, Color fillColor, Anchor anchor) { super(shape, stroke, strokeColor, fillColor, 6, anchor); }

    /** @return current radius drawn, if max supplied radius is 1 */
    public double getMaxRadius() { return maxRadius; }
    /** @param length new max radius, if max supplied radius is 1 */
    public void setMaxRadius(double length) { maxRadius = length; }

    /** @return current radius multiplier */
    public double getRadiusMultiplier() { return rMult; }
    /** @param length new radius multiplier */
    public void setRadiusMultiplier(double length) { rMult = length; }

    @Override
    public void draw(Graphics2D canvas, Point2D.Double point) {
        Color fill = fillColor;
        if (point instanceof GraphicPointFancy) {
            GraphicPointFancy gpf = (GraphicPointFancy) point;
            setRadius((int) (maxRadius * rMult * gpf.rad));
            fillColor = gpf.color == null ? fill : gpf.color;
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
        fillColor = fill;
    }

    @Override
    public boolean contained(Point2D.Double primitive, Graphics2D canvas, Point point) {
        if (primitive instanceof GraphicPointFancy && ((GraphicPointFancy)primitive) instanceof Point2D.Double) {
            GraphicPointFancy<Point2D.Double> gpf = (GraphicPointFancy<Point2D.Double>) primitive;
            return gpf.anchor.distance(point) <= maxRadius * rMult * gpf.rad;
        } else
            return super.contained(primitive, canvas, point);
    }


}
