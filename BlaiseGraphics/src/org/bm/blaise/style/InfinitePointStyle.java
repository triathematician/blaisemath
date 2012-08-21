/*
 * InfinitePointStyle.java
 * Created Oct 1, 2011
 */
package org.bm.blaise.style;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Draws a point along with a ray from the point to the outer edge of the graphics canvas.
 *
 * @author elisha
 */
public class InfinitePointStyle extends BasicPointStyle {

    /** Line style for drawing the ray */
    private PathStyle rayStyle = StyleUtils.DEFAULT_STYLE_PROVIDER.PATH;
    /** Whether to extend in both directions, or just forward */
    private boolean extendBoth = false;

    /** Construct the style */
    public InfinitePointStyle() {
    }

    public PathStyle getRayStyle() {
        return rayStyle;
    }

    public void setRayStyle(PathStyle rayStyle) {
        this.rayStyle = rayStyle;
    }

    public boolean isExtendBoth() {
        return extendBoth;
    }

    public void setExtendBoth(boolean extendBoth) {
        this.extendBoth = extendBoth;
    }



    @Override
    public void draw(Point2D p, double angle, Graphics2D canvas, VisibilityKey visibility) {
        Point2D p2 = new Point2D.Double(p.getX() + Math.cos(angle), p.getY() + Math.sin(angle));
        Point2D endpt = boundaryHit(p, p2, canvas.getClipBounds());
        if (extendBoth) {
            Point2D endpt1 = boundaryHit(p2, p, canvas.getClipBounds());
            rayStyle.draw(new Line2D.Double(endpt1, endpt), canvas, visibility);
        } else
            rayStyle.draw(new Line2D.Double(p, endpt), canvas, visibility);
        super.draw(p, angle, canvas, visibility);
    }




    //
    // UTILITY
    //

    /**
     * Returns points at which the ray beginning at p1 and passing through p2 intersects the boundary of the window.
     * @param p1p first point
     * @param p2p second point
     * @param bounds the window boundaries
     */
    protected static Point2D.Double boundaryHit(Point2D p1p, Point2D p2p, Rectangle2D bounds) {
        Point2D.Double p1 = new Point2D.Double(p1p.getX(), p1p.getY());
        Point2D.Double p2 = new Point2D.Double(p2p.getX(), p2p.getY());
        if (p2.x > p1.x && p1.x <= bounds.getMaxX()) { // line goes to the right
            double slope = (p2.y - p1.y) / (p2.x - p1.x);
            double yRight = slope * (bounds.getMaxX() - p1.x) + p1.y;
            if (yRight <= bounds.getMaxY() && yRight >= bounds.getMinY()) { // point is on the right
                return new Point2D.Double(bounds.getMaxX(), yRight);
            } else if (p2.y > p1.y && p1.y <= bounds.getMaxY()) { // line goes up
                return new Point2D.Double((bounds.getMaxY() - p1.y) / slope + p1.x, bounds.getMaxY());
            } else if (p1.y > p2.y && p1.y >= bounds.getMinY()) { // line goes down
                return new Point2D.Double((bounds.getMinY() - p1.y) / slope + p1.x, bounds.getMinY());
            }
        } else if (p2.x < p1.x && p1.x >= bounds.getMinX()) { // line goes to the left
            double slope = (p2.y - p1.y) / (p2.x - p1.x);
            double yLeft = slope * (bounds.getMinX() - p1.x) + p1.y;
            if (yLeft <= bounds.getMaxY() && yLeft >= bounds.getMinY()) { // point is on the right
                return new Point2D.Double(bounds.getMinX(), yLeft);
            } else if (p2.y > p1.y && p1.y <= bounds.getMaxY()) { // line goes up
                return new Point2D.Double((bounds.getMaxY() - p1.y) / slope + p1.x, bounds.getMaxY());
            } else if (p1.y > p2.y && p1.y >= bounds.getMinY()) { // line goes down
                return new Point2D.Double((bounds.getMinY() - p1.y) / slope + p1.x, bounds.getMinY());
            }
        } else if (p1.x == p2.x) { // line is vertical
            if (p2.y < p1.y && p1.y >= bounds.getMinY()) { // line goes up
                return new Point2D.Double(p1.x, bounds.getMinY());
            } else if (p1.y <= bounds.getMaxY()) {
                return new Point2D.Double(p1.x, bounds.getMaxY());
            }
        }
        return new Point2D.Double(p2.x, p2.y);
    }


}
