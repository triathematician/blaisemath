/**
 * ComplexMathUtils.java
 * Created on Jul 29, 2009
 */

package scio.coordinate.utils;

import java.awt.geom.Point2D;
import org.apache.commons.math.complex.Complex;

/**
 * <p>
 *   <code>ComplexMathUtils</code> is a library of methods for use on elements of the complex plane.
 *   All methods work with <code>Point2D.Double</code>s.
 * </p>
 *
 * @author Elisha Peterson
 */
public class ComplexMathUtils {

    /** Returns magnitude of complex value. */
    public static double magnitude(Point2D.Double c) {
        return Math.sqrt(c.x*c.x + c.y*c.y);
    }

    /** Returns angle point makes with the positive real axis. */
    public static double angle(Point2D.Double c) {
        return PlanarMathUtils.angle(c);
    }

    /** Returns sum of two complex values. */
    public static Point2D.Double sum(Point2D.Double c1, Point2D.Double c2) {
        return new Point2D.Double(c1.x + c2.x, c1.y + c2.y);
    }

    /** Returns product of two complex values. */
    public static Point2D.Double product(Point2D.Double c1, Point2D.Double c2) {
        return new Point2D.Double(c1.x * c2.x - c1.y * c2.y, c1.x * c2.y + c1.y * c2.x);
    }

    /** Returns inverse of a complex value. */
    public static Point2D.Double inverse(Point2D.Double c) {
        if (c.x == 0 && c.y == 0) {
            return null;
        }
        double m = magnitude(c);
        double angle = -Math.atan2(c.y, c.x); // angle from -pi to pi
        return new Point2D.Double( Math.cos(angle)/m, Math.sin(angle)/m );
    }
    
}
