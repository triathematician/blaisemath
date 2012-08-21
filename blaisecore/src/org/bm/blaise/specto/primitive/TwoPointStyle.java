/**
 * TwoPointStyle.java
 * Created on Sep 18, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 *   <code>TwoPointStyle</code> operates on <code>GraphicArrow</code> primitives,
 *   which are stored as pairs of points.
 * </p>
 *
 * @author Elisha Peterson
 */
public class TwoPointStyle extends ArrowStyle {

    /** Determines location of endpoints. */
    EndStyle endStyle = EndStyle.LINE;
    
    /** Style for drawing the points determining the line. */
    PointStyle pointStyle = PointStyle.SMALL;

    public TwoPointStyle() {
        super();
        anchorShape = Shape.NONE;
        headShape = Shape.NONE;
    }

    public TwoPointStyle(Color color, EndStyle endStyle) {
        super(color, Shape.NONE, 5);
        this.endStyle = endStyle;
    }

    public EndStyle getEndStyle() {
        return endStyle;
    }

    public void setEndStyle(EndStyle endStyle) {
        this.endStyle = endStyle;
    }

    public PointStyle getPointStyle() {
        return pointStyle;
    }

    public void setPointStyle(PointStyle pointStyle) {
        this.pointStyle = pointStyle;
    }    

    @Override
    public void draw(Graphics2D canvas, Point2D[] primitive, boolean selected) {
        canvas.setColor(pathStyle.getColor());
        canvas.setStroke(pathStyle.getStroke());
//        System.out.println("clip bounds: "+canvas.getClipBounds());
        Point2D anchor = endStyle.getStart(primitive, (Rectangle2D) canvas.getClipBounds());
        Point2D head = endStyle.getEnd(primitive, (Rectangle2D) canvas.getClipBounds());
        canvas.draw(new Line2D.Double(anchor, head));
        if (headShape != Shape.NONE)
            canvas.draw(getShape(head, head.getX() - anchor.getX(), head.getY() - anchor.getY(), headSize, headShape));
        if (anchorShape != Shape.NONE)
            canvas.draw(getShape(anchor, anchor.getX() - head.getX(), anchor.getY() - head.getY(), headSize, anchorShape));
        if (pointStyle != null) {
            if (!endStyle.isStartCovering())
                pointStyle.draw(canvas, primitive[0], selected);
            if (!endStyle.isEndCovering())
                pointStyle.draw(canvas, primitive[1], selected);
        }
    }


    /**
     * Describes whether the endpoints used are those on the specified primitive,
     * or whether they are extended to the edge of the viewscreen.
     */
    public enum EndStyle {
        SEGMENT("Segment", false, false) {
            Point2D getStart(Point2D[] arr, Rectangle2D boundary) {
                return arr[0];
            }
            Point2D getEnd(Point2D[] arr, Rectangle2D boundary) {
                return arr[1];
            }
        },
        RAY("Ray", false, false) {
            Point2D getStart(Point2D[] arr, Rectangle2D boundary) {
                return arr[0];
            }
            Point2D getEnd(Point2D[] arr, Rectangle2D boundary) {
                return rayHit(arr[0], arr[1], boundary);
            }
        },
        LINE("Line", false, false) {
            Point2D getStart(Point2D[] arr, Rectangle2D boundary) {
                return rayHit(arr[1], arr[0], boundary);
            }
            Point2D getEnd(Point2D[] arr, Rectangle2D boundary) {
                return rayHit(arr[0], arr[1], boundary);

            }
        };

        String name;
        boolean coversStart;
        boolean coversEnd;

        EndStyle(String name, boolean coversStart, boolean coversEnd) {
            this.name = name;
            this.coversStart = coversStart;
            this.coversEnd = coversEnd;
        }

        /** @return true if the drawn shape terminates at the start point */
        public boolean isStartCovering() {
            return coversStart;
        }

        /** @return true if the drawn shape terminates at the end point */
        public boolean isEndCovering() {
            return coversEnd;
        }

        @Override
        public String toString() {
            return name;
        }

        abstract Point2D getStart(Point2D[] arr, Rectangle2D boundary);
        abstract Point2D getEnd(Point2D[] arr, Rectangle2D boundary);


    }



    /** Returns points at which the ray beginning at p1 and passing through p2 intersects the boundary of the window. */
    protected static Point2D rayHit(Point2D p1p, Point2D p2p, Rectangle2D bounds) {
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
