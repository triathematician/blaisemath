/**
 * R2Transform.java
 * Created on Apr 4, 2008
 */
package deprecated;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;

/**
 * <p>
 * Slight extension of an affine transform of particular use for my code. May be
 * deprecated in the near future.
 * </p>
 *
 * @author Elisha Peterson
 */
@Deprecated
public class R2Transform extends AffineTransform {

    public R2Transform(double m00, double m10, double m01, double m11, double m02, double m12) {
        super(m00, m10, m01, m11, m02, m12);
    }

    public Point2D.Double transform(Point2D.Double input) {
        java.awt.geom.Point2D.Double temp = null;
        temp = (java.awt.geom.Point2D.Double) super.transform(input, null);
        return new Point2D.Double(temp.x, temp.y);
    }

    public List<Point2D.Double> transform(List<? extends Point2D.Double> inputs) {
        Vector<Point2D.Double> result = new Vector<Point2D.Double>();
        for (Point2D.Double p : inputs) {
            result.add(transform(p));
        }
        return result;
    }

    /** Returns affine transformation taking [0,1] to the given line segment; just a rotation, dilation, and shift */
    public static AffineTransform getLineTransform(double x1, double y1, double x2, double y2) {
        return new AffineTransform(x2 - x1, y2 - y1, y1 - y2, x2 - x1, x1, y1);
    }
}

