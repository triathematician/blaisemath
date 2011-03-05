/*
 * LineStyle.java
 * Created Apr 12, 2010
 */

package primitive.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Draws a line that extends to either edge of the screen. Primitives should be
 * an array of two points.
 *
 * @author Elisha Peterson
 */
public class LineStyle extends PathStylePoints {

    /** Default constructor. */
    public LineStyle() {}
    /** Construct with color */
    public LineStyle(Color color) { super(color); }
    /** Construct with parameters. */
    public LineStyle(Color color, float width){ super(color, width); }
    /** Construct with parameters. */
    public LineStyle(Color color, BasicStroke stroke){ super(color, stroke); }

    @Override
    public void draw(Graphics2D canvas, Point2D.Double[] line) {
        canvas.setColor(strokeColor);
        canvas.setStroke(stroke);
        Rectangle2D bounds = canvas.getClipBounds();
        canvas.draw(new Line2D.Double( boundaryHit(line[0], line[1], bounds), boundaryHit(line[1], line[0], bounds) ));
    }

    @Override
    public void drawArray(Graphics2D canvas, Point2D.Double[][] lines) {
        canvas.setStroke(stroke);
        canvas.setColor(strokeColor);
        canvas.setStroke(stroke);
        Rectangle2D bounds = canvas.getClipBounds();
        for (Point2D.Double[] line : lines)
            canvas.draw(new Line2D.Double( boundaryHit(line[0], line[1], bounds), boundaryHit(line[1], line[0], bounds) ));
    }

    /**
     * Returns points at which the ray beginning at p1 and passing through p2 intersects the boundary of the window.
     * @param p1p first point
     * @param p2p second point
     * @param bounds the window boundaries
     */
    protected static Point2D.Double boundaryHit(Point2D.Double p1p, Point2D.Double p2p, Rectangle2D bounds) {
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
