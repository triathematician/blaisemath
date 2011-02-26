/*
 * PointRadiusStyle.java
 * Created Jun 14, 2010
 */

package old.styles.verified;

import old.styles.verified.AbstractPrimitiveStyle;
import old.primitives.transferred.GraphicPointRadius;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Draws a point that comes along with a radius.
 *
 * @author Elisha Peterson
 */
public class PointRadiusStyle extends AbstractPrimitiveStyle<GraphicPointRadius<Point2D.Double>> {

    /** Base style used to draw. */
    PointStyle baseStyle = new PointStyle();
    /** Multiplier to scale the points. */
    double maxRadius = 1.0;

    /**
     * Construct with default point style
     */
    public PointRadiusStyle() {
    }

    /** 
     * Construct with specified point style 
     * @param base the base style
     */
    public PointRadiusStyle(PointStyle base) {
        this.baseStyle = base;
    }

    /** @return current maximum radius drawn (applies to drawing multiples only) */
    public double getMaxRadius() { return maxRadius; }
    /** @param length new max radius */
    public void setMaxRadius(double length) { maxRadius = length; }
    /** @return base style for drawing */
    public PointStyle getBaseStyle() { return baseStyle; }
    /** Sets base style for drawing */
    public void setBaseStyle(PointStyle style) { this.baseStyle = style; }

    public Class getTargetType() {
        return GraphicPointRadius.class;
    }

    public void draw(Graphics2D canvas, GraphicPointRadius<Point2D.Double> primitive) {
        baseStyle.setRadius((int) primitive.rad);
        baseStyle.draw(canvas, primitive.anchor);
    }

    public boolean contained(GraphicPointRadius<Point2D.Double> primitive, Graphics2D canvas, Point point) {
        return primitive.anchor.distance(point) <= primitive.rad;
    }

}
