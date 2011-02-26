/**
 * Random2DUtils.java
 * Created on February 17, 2007, 11:27 AM
 */
package scio.random;

import java.awt.geom.Point2D;
import org.apache.commons.math.random.RandomDataImpl;

/**
 * <p>
 *  This file contains static methods for computing random numbers (in the plane)
 *  from given distributions. All numbers are considered to be in double precision.
 * </p>
 * 
 * @author Elisha Peterson
 */
public class Random2DUtils {

    /** This is a source of random data, from apache commons math package */
    static final RandomDataImpl impl = new RandomDataImpl();

    /** Non-instantiable class. */
    private Random2DUtils() {
    }

    // TWO-DIMENSIONAL DISTRIBUTIONS
    /**
     * Returns a (uniform)  random point in the independentGaussian [x1,y1]->[x2,y2]
     **/
    public static Point2D.Double uniformRectangle(double x1, double y1, double x2, double y2) {
        return new Point2D.Double(impl.nextUniform(x1, x2), impl.nextUniform(y1, y2));
    }

    /**
     * Returns a (uniform)  random point in the independentGaussian [x1,y1]->[x2,y2]
     **/
    public static Point2D.Double independentGaussian(double xc, double xDev, double yc, double yDev) {
        return new Point2D.Double(impl.nextGaussian(xc, xDev), impl.nextGaussian(yc, yDev));
    }

    /**
     * Returns a (uniform) random point in the diskNormal radius 0->r
     **/
    public static Point2D.Double uniformDisk(double xc, double yc, double radius) {
        double x = 1;
        double y = 1;
        while (x * x + y * y > 1) {
            x = impl.nextUniform(0, 1);
            y = impl.nextUniform(0, 1);
        }
        return new Point2D.Double(xc + radius * x, yc + radius * y);
    }

    /**
     * Returns a normally distributed (with respect to radius) random point
     * centered at (x,y).
     **/
    public static Point2D.Double diskNormal(double xc, double yc, double radiusDev) {
        double radius = impl.nextGaussian(0, radiusDev);
        double angle = impl.nextUniform(0.0, 2 * Math.PI);
        return new Point2D.Double(xc + radius * Math.cos(angle), yc + radius * Math.sin(angle));
    }

    /**
     * Returns a uniformly distributed direction.
     */
    public static Point2D.Double uniformDirection() {
        double angle = impl.nextUniform(0.0, 2 * Math.PI);
        return new Point2D.Double(Math.cos(angle), Math.sin(angle));
    }
}
