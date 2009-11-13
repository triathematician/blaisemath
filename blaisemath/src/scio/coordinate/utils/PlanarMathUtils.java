/**
 * PlanarMathUtils.java
 * Created on Jul 29, 2009
 */
package scio.coordinate.utils;

import java.awt.geom.Point2D;
import static java.lang.Math.*;

/**
 * <p>
 *   <code>PlanarMathUtils</code> is a library of static methods for use on elements of the plane.
 *   All methods work with <code>Point2D.Double</code>s.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlanarMathUtils {

    public static final Point2D.Double ZERO = new Point2D.Double();
    public static final Point2D.Double INFINITY = new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);

    static final double INFINITE_RADIUS = sqrt(Double.MAX_VALUE) / 3;


    /** Non-instantiable class. */
    private PlanarMathUtils() {
    }

    //
    //
    // FACTORY METHODS
    //
    //
    /**
     * Creates and returns a new point with specified radius and angle
     * @param radius the radius (may be negative)
     * @param angle the angle
     * @return a new point with specified radius and angle
     */
    public static Point2D.Double toCartesianFromPolar(double radius, double angle) {
        return new Point2D.Double(radius * Math.cos(angle), radius * Math.sin(angle));
    }

    /**
     * Converts a polar-specified point to a Cartesian-specified point.
     * @param polarPoint a point whose x is the radius and y is the angle (from x-axis)
     * @return a Cartesian version of the point
     */
    public static Point2D.Double toCartesianFromPolar(Point2D.Double polarPoint) {
        return toCartesianFromPolar(polarPoint.x, polarPoint.y);
    }

    /**
     * Converts a Cartesian-specified point to a polar-specified point.
     * @param point the Cartesian-specified point
     * @return a point whose x is the radius, y is the angle (from x-axis) of the point
     */
    public static Point2D.Double toPolarFromCartesian(Point2D.Double point) {
        return new Point2D.Double(point.distance(0,0), angle(point));
    }

    /**
     * Returns point at infinity at the given angle, approximated as a very, very large point
     * (a distance of <code>Double.MAX_VALUE/2</code> from the origin).
     * @param angle the angle for the point, relative to the x-axis
     * @return a new point very close to infinity
     */
    public static Point2D.Double getNearInfinitePointAtAngle(double angle) {
        return toCartesianFromPolar(INFINITE_RADIUS, angle);
    }


    //
    //
    // FUNCTIONAL METHODS : operate on a supplied point; may return a copy of that point
    //
    //
    /**
     * Normalizes the input vector, by dividing by the magnitude, and returns a copy of the vector.
     * @param vector the input vector
     * @return normalization of the vector, i.e. unit vector with the same heading as this vector;
     *      returns the vector if magnitude is 0.
     */
    public static Point2D.Double normalize(Point2D.Double vector) {
        double magn = vector.distance(0, 0);
        if (magn == 0) {
            return vector;
        }
        vector.x /= magn;
        vector.y /= magn;
        return vector;
    }
    
    /**
     * Translates first point by amount of the second. Returns the first point.
     * @param point the first point to translate (value will change)
     * @param dPoint the amount to translate by
     */
    public static Point2D.Double translate(Point2D.Double point, Point2D.Double dPoint) {
        point.x += dPoint.x;
        point.y += dPoint.y;
        return point;
    }

    /**
     * Returns a point that is a copy of the provided point rotated about the specified anchor by a specified amount.
     * @param anchor the anchor point for the rotation
     * @param point the input point
     * @param dAngle the amount of angle change
     * @return a new point
     */
    public static Point2D.Double rotate(Point2D.Double anchor, Point2D.Double point, double dAngle) {
        double newX = anchor.x + cos(dAngle) * (point.x - anchor.x) - sin(dAngle) * (point.y - anchor.y);
        double newY = anchor.y + sin(dAngle) * (point.x - anchor.x) + cos(dAngle) * (point.y - anchor.y);
        return new Point2D.Double(newX, newY);
    }

    /**
     * Returns a point that is a copy of the provided point rotated about the origin by a specified amount.
     * @param point the input point
     * @param dAngle the amount of angle change
     * @return a new point
     */
    public static Point2D.Double rotate(Point2D.Double point, double dAngle) {
        double newX = cos(dAngle) * point.x - sin(dAngle) * point.y;
        double newY = sin(dAngle) * point.x + cos(dAngle) * point.y;
        return new Point2D.Double(newX, newY);
    }


    //
    //
    // PROPERTY METHODS : return result of computation on one or more points
    //
    //

    /**
     * Computes angle formed by given coordinates with the x-axis.
     * @param x the x coord
     * @param y the y coord
     * @return an angle with range between 0 and 2pi
     */
    public static double angle(double x, double y) {
        return (x < 0 ? PI : 0) + atan(y / x);
    }

    /**
     * Computes angle formed by given vector with the x-axis.
     * @param pt the input point
     * @return an angle with range between 0 and 2pi
     */
    public static double angle(Point2D.Double pt) {
        return angle(pt.x, pt.y);
    }

    /**
     * Computes slope between two points.
     * @param p1 the first point
     * @param p2 the second point
     * @return a <code>Double</code> with the slope, which may be infinite (if the points are spaced vertically) or NaN (if they're the same)
     */
    public static Double slope(Point2D.Double p1, Point2D.Double p2) {
        return (p2.y - p1.y) / (p2.x - p1.x);
    }

    /**
     * Computes dot product of two vectors
     * @param v1 first vector
     * @param v2 second vector
     * @return value of dot product
     */
    public static double dotProduct(Point2D.Double v1, Point2D.Double v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    /**
     * Computes magnitude of cross product of two planar vectors.
     * @param v1 first vector
     * @param v2 second vector
     * @return cross product (signed magnitude only)
     */
    public static double crossProduct(Point2D.Double v1, Point2D.Double v2) {
        return v1.x * v2.y - v1.y * v2.x;
    }

    /**
     * Computes signed magnitude of cross product of planar vectors (p2-p1) and (p3-p1)
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return cross product double
     */
    public static double crossProduct(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        return (p2.x - p1.x) * (p3.y - p2.y) - (p2.y - p1.y) * (p3.x - p2.x);
    }

    /**
     * Computes angle between three points, specifically between vectors P2>P1 and P2>P3.
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return angle of that based at p2
     */
    public static double angleBetween(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        return angle(p1.x - p2.x, p1.y - p2.y) - angle(p3.x - p2.x, p3.y - p2.y);
    }

    /**
     * Computes projection of first vector onto second vector.
     * @param vec1 first vector
     * @param vec2 second vector
     * @return vector in direction of vec2
     */
    public static Point2D.Double projectVector(Point2D.Double vec1, Point2D.Double vec2) {
        double factor = dotProduct(vec1, vec2) / vec2.distanceSq(0,0);
        return new Point2D.Double(vec2.x * factor, vec2.y * factor);
    }

    /**
     * Computes component of first vector that is perpendicular to the second vector
     * @param vec1 first vector
     * @param vec2 second vector
     * @return vector in direction of vec2
     */
    public static Point2D.Double perpProjectVector(Point2D.Double vec1, Point2D.Double vec2) {
        Point2D.Double parallel = projectVector(vec1, vec2);
        return new Point2D.Double(vec1.x - parallel.x, vec1.y - parallel.y);
    }

    /**
     * Computes and returns a point near infinity that is along the line equidistant between the two points.
     * Which near-infinite point is chosen depends on the order of the inputs.
     * @param p1 first point
     * @param p2 second point
     * @return a point near infinity, or the "infinite" point if the two inputs are equal
     */
    public static Point2D.Double pointAtInfinityEquidistantTo(Point2D.Double p1, Point2D.Double p2) {
        return p1.equals(p2) 
                ? INFINITY
                : getNearInfinitePointAtAngle(angle(p2.x - p1.x, p2.y - p1.y) + Math.PI / 2);
    }

    /** 
     * Computes and returns center of circle passing through three points. The center is at infinity if points are colinear.
     * If one of the points is infinity then returns point in direction of bisecting line between the two others.
     */
    public static Point2D.Double centerOfCircleThrough(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        // one of the points is infinite
        if (p1.equals(INFINITY)) {
            return pointAtInfinityEquidistantTo(p2, p3);
        } else if (p2.equals(INFINITY)) {
            return pointAtInfinityEquidistantTo(p1, p3);
        } else if (p3.equals(INFINITY)) {
            return pointAtInfinityEquidistantTo(p1, p2);
        }

        // points are colinear, or one of the slopes is infinite
        Double m12 = slope(p1, p2);
        Double m23 = slope(p2, p3);

        // if p1/p2 or p2/p3 coincide
        if (m12.isNaN()) {
            return pointAtInfinityEquidistantTo(p2, p3);
        } else if (m23.isNaN()) {
            return pointAtInfinityEquidistantTo(p1, p2);
        }

        // if points are colinear or vertically spaced
        if (((double)m12) == ((double)m23)) {
            return pointAtInfinityEquidistantTo(p1, p2);
        } else if (m12.isInfinite()) {
            return centerOfCircleThrough(p2, p3, p1);
        } else if (m23.isInfinite()) {
            return centerOfCircleThrough(p3, p1, p2);
        }

        // points are not colinear
        double xCenter = (m12 * m23 * (p1.y - p3.y) + m23 * (p1.x + p2.x) - m12 * (p2.x + p3.x)) / (2 * (m23 - m12));
        double yCenter =
                ((double) m12) == 0.0
                ? (-xCenter + (p1.x + p2.x) / 2) / m12 + (p1.y + p2.y) / 2
                : (-xCenter + (p2.x + p3.x) / 2) / m23 + (p2.y + p3.y) / 2;

        return new Point2D.Double(xCenter, yCenter);
    }

    /**
     * Computes and returns point along the line between point1 and point2 which is closest
     * to itself, aka. the point which makes a perpendicular with the line.
     * Mathematically this can be computed by minimizing the distanceTo between
     * (x,y) and p1+t*(p2-p1) as t varies. Mathematically, the result is
     *
     *       (p1-p).(p1-p2)
     *    t= --------------
     *         ||p1-p2||^2
     *
     * @param original the point considered
     * @param endpt1 first endpoint of the line
     * @param endpt2 second endpoint of the line
     * @return point along the line that forms a perpendicular with the original point; or if the endpoints coincide a point at an endpoint
     **/
    public static Point2D.Double closestOnLine(Point2D.Double original, Point2D.Double endpt1, Point2D.Double endpt2) {
        if (endpt1.equals(endpt2)) {
            return new Point2D.Double(endpt1.x, endpt1.y);
        }
        double t = ((endpt1.x - original.x) * (endpt1.x - endpt2.x) + (endpt1.y - original.y) * (endpt1.y - endpt2.y)) / ((endpt1.x - endpt2.x) * (endpt1.x - endpt2.x) + (endpt1.y - endpt2.y) * (endpt1.y - endpt2.y));
        return new Point2D.Double(endpt1.x + t * (endpt2.x - endpt1.x), endpt1.y + t * (endpt2.y - endpt1.y));
    }

    /**
     * Returns point on line SEGMENT between point1 and point2 which is closest to this point.
     * @param original the point considered
     * @param endpt1 first endpoint of the line segment
     * @param endpt2 second endpoint of the line segment
     * @return point along the line that forms a perpendicular with the original point, or the nearest endpt to that point; or if the endpoints coincide a point at an endpoint
     */
    public static Point2D.Double closestOnSegment(Point2D.Double original, Point2D.Double endpt1, Point2D.Double endpt2) {
        if (endpt1.equals(endpt2)) {
            return new Point2D.Double(endpt1.x, endpt1.y);
        }
        double t = ((endpt1.x - original.x) * (endpt1.x - endpt2.x) + (endpt1.y - original.y) * (endpt1.y - endpt2.y)) / ((endpt1.x - endpt2.x) * (endpt1.x - endpt2.x) + (endpt1.y - endpt2.y) * (endpt1.y - endpt2.y));
        t = t < 0 ? 0 : (t > 1 ? 1 : t);
        return new Point2D.Double(endpt1.x + t * (endpt2.x - endpt1.x), endpt1.y + t * (endpt2.y - endpt1.y));
    }

    /**
     * Returns point on RAY from endpt1 to endpt2 which is closest to this point.
     * @param original the point considered
     * @param endpt1 first endpoint of the ray
     * @param endpt2 second endpoint of the ray
     * @return point along the line that forms a perpendicular with the original point, or the nearest endpt to that point; or if the endpoints coincide a point at an endpoint
     */
    public static Point2D.Double closestOnRay(Point2D.Double original, Point2D.Double endpt1, Point2D.Double endpt2) {
        if (endpt1.equals(endpt2)) {
            return new Point2D.Double(endpt1.x, endpt1.y);
        }
        double t = ((endpt1.x - original.x) * (endpt1.x - endpt2.x) + (endpt1.y - original.y) * (endpt1.y - endpt2.y)) / ((endpt1.x - endpt2.x) * (endpt1.x - endpt2.x) + (endpt1.y - endpt2.y) * (endpt1.y - endpt2.y));
        t = t < 0 ? 0 : t;
        return new Point2D.Double(endpt1.x + t * (endpt2.x - endpt1.x), endpt1.y + t * (endpt2.y - endpt1.y));
    }
}
