/**
 * PathStylePoints.java
 * Created on Apr 12, 2010
 */

package primitive.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * <p>
 *   <code>PathStylePoints</code> mimics behavior of <code>PathStyle</code>, but for an array of points
 *   instead of a shape. *
 * </p>
 *
 * @see PathStyle
 * @author Elisha Peterson
 */
public class PathStylePoints extends AbstractPathStyle implements PrimitiveStyle<Point2D.Double[]> {

    /** Construct with default stroke and color black */
    public PathStylePoints() {}
    /** Construct with specific color. */
    public PathStylePoints(Color color) { super(color); }
    /** Construct with specific color/width. */
    public PathStylePoints(Color color, float width){ super(color, width); }
    /** Construct with specified color/stroke. */
    public PathStylePoints(Color color, BasicStroke stroke){ super(color, stroke); }

    @Override
    public String toString() {
        return "PointPathStyle [wid=" + getThickness() + "]";
    }

    public Class<? extends Point2D.Double[]> getTargetType() {
        return Point2D.Double[].class;
    }

    public void draw(Graphics2D canvas, Point2D.Double[] path) {
        drawPath(canvas, path);
    }

    public void draw(Graphics2D canvas, Point2D.Double[][] paths) {
        drawPaths(canvas, paths);
    }

    public boolean contained(Point2D.Double[] primitive, Graphics2D canvas, Point point) {
        // TODO - add logic
        return false;
    }

    public int containedInArray(Point2D.Double[][] primitives, Graphics2D canvas, Point point) {
        return -1;
    }

}
