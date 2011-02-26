/**
 * PlanarMathUtils.java
 * Created on Jul 29, 2009
 */
package scio.coordinate.utils;

import java.awt.geom.Point2D;
import static java.lang.Math.*;

/**
 * <p>
 *    <code>PlanarMathUtils</code> is a library of static methods for use on elements of the plane.
 *    All methods work with <code>Point2D.Double</code>s.
 * </p>
 * <p>
 *    This library is built to handle a "circle" of points at infinity. These are represented by a <code>Double.POSITIVE_INFINITY</code>
 *    x-coordinate, and a y-coordinate representing the infinite angle.
 * </p>
 * <p>
 *   The code is organized as follows:
 *      <li>Test methods
 *      <li>Methods for constructing new points
 *      <li>Methods for converting points between various formats
 *      <li>Methods that operate on a point (changing a point parameter)
 *      <li>Methods that use properties of existing point to create new points (dot and cross products)
 *      <li>Dot and cross product formulas
 *      <li>Angle formulas
 *      <li>Projection formulas
 *      <li>Distance formulas
 * </p>
 *
 * @author Elisha Peterson
 */
public final class PlanarMathUtils {

    /** The origin. */
    public static final Point2D.Double ZERO = new Point2D.Double();
    /** The point at infinity. */
    public static final Point2D.Double INFINITY = new Point2D.Double(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    /** A non-existent point. */
    public static final Point2D.Double NO_POINT = new Point2D.Double(Double.NaN, Double.NaN);

    /** Non-instantiable class. */
    private PlanarMathUtils() {}


    //==================================================================================================
    //
    // 0. TEST METHODS
    //

    /**
     * Determines whether provided point is infinite.
     * @param point point to check
     * @return <code>true</code> if this represents an infinite point.
     */
    public static boolean isInfinite(Point2D.Double point) {
        return Double.isInfinite(point.x);
    }

    /**
     * Determines whether provided point is valid.
     * @param point point to check
     * @return <code>false</code> if the point is valid (either infinite or finite)
     */
    public static boolean isValidPoint(Point2D.Double point) {
        return ! (Double.isNaN(point.x) || Double.isNaN(point.y) || Double.isInfinite(point.y));
    }


    //==================================================================================================
    //
    // 1a. METHODS TO CONSTRUCT POINTS
    //

    /**
     * Creates a point-at-infinity using a specified angle.
     * @param angle the angle to palce the point at
     * @return a point at infinity, represented in polar terms
     */
    public static Point2D.Double polarPointAtInfinity(double angle) {
        return new Point2D.Double(Double.POSITIVE_INFINITY, angle);
    }
    
    //==================================================================================================
    //
    // 1b. METHODS TO CONVERT POINTS BETWEEN VARIOUS FORMATS
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
     * Converts a Cartesian-specified point to a polar-specified point.
     * @param point the Cartesian-specified point
     * @return a point whose x is the radius, y is the angle (from x-axis) of the point
     */
    public static Point2D.Double toPolarFromCartesian(Point2D.Double point) {
        return new Point2D.Double(point.distance(0,0), angle(point));
    }
    
    //==================================================================================================
    //
    // 2. METHODS OPERATING ON A POINT AND RETURNING THAT POINT
    //

    /**
     * Normalizes the input vector, by dividing by the magnitude, and returns a copy of the vector.
     * @param vector the input vector
     * @return normalization of the vector, i.e. unit vector with the same heading as this vector;
     *      returns the vector if magnitude is 0.
     */
    public static Point2D.Double normalize(Point2D.Double vector) {
        double magn = magnitude(vector);
        if (magn == 0)
            return vector;
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



    //==================================================================================================
    //
    // 3. METHODS TO CREATE POINTS BASED ON EXISTING POINTS
    //

    /**
     * Adds a collection of points, and returns the result.
     * @param points the points to add together
     * @return sum of all the points
     */
    public static Point2D.Double sum(Point2D.Double... points) {
        Point2D.Double result = new Point2D.Double();
        for (int i = 0; i < points.length; i++) {
            result.x += points[i].x;
            result.y += points[i].y;
        }
        return result;
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

    /**
     * Returns a point that is a copy of the provided point rotated about the specified anchor by a specified amount.
     * @param anchor the anchor point for the rotation
     * @param point the input point
     * @param dAngle the amount of (counter-clockwise) angle change
     * @return a new point
     */
    public static Point2D.Double rotate(Point2D.Double anchor, Point2D.Double point, double dAngle) {
        double newX = anchor.x + cos(dAngle) * (point.x - anchor.x) - sin(dAngle) * (point.y - anchor.y);
        double newY = anchor.y + sin(dAngle) * (point.x - anchor.x) + cos(dAngle) * (point.y - anchor.y);
        return new Point2D.Double(newX, newY);
    }



    //==================================================================================================
    //
    // 4a. METHODS THAT RETURN dot/cross product PROPERTIES OF ONE OR MORE POINTS
    //

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
    public static double crossProductOf3Points(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        return (p2.x - p1.x) * (p3.y - p2.y) - (p2.y - p1.y) * (p3.x - p2.x);
    }


    //==================================================================================================
    //
    // 4b. METHODS THAT RETURN angle/magnitude PROPERTIES OF ONE OR MORE POINTS
    //

    /**
     * Computes magnitude of a vector.
     * @param vec the vector
     * @return magnitude
     */
    public static double magnitude(Point2D.Double vec) {
        return vec.distance(0, 0);
    }

    /**
     * Computes "angle" of a vector using the <code>Math.atan</code> method.
     * @param vec the vector
     * @return angle in the range of -pi to pi, as computed by <code>Math.atan</code>
     */
    public static double angle(Point2D.Double vec) {
        if (Double.isInfinite(vec.x)) {
            return vec.y > PI ? vec.y - 2*PI : vec.y;
        }
        return atan2(vec.y, vec.x);
    }

    /**
     * Computes angle formed by vector with the x-axis.
     * @param x the x coord
     * @param y the y coord
     * @return an angle with range between 0 and 2pi
     */
    public static double positiveAngle(double x, double y) {
        double result = atan2(y, x);
        return result < 0 ? result + 2*PI : result;
    }

    /**
     * Computes angle made by given vector with the x-axis
     * @param vec a vector
     * @return an angle with range between 0 and 2pi
     */
    public static double positiveAngle(Point2D.Double vec) {
        if (Double.isInfinite(vec.x))
            return vec.y < 0 ? vec.y + 2*PI : vec.y;
        return positiveAngle(vec.x, vec.y);

    }

    /**
     * Computes angle between two vectors, as comptued by the dot product formula
     * @param vec1 first vector
     * @param vec2 second vector
     * @return angle in the range of 0 to pi.
     */
    public static double angleBetween(Point2D.Double vec1, Point2D.Double vec2) {
        return Math.acos( dotProduct(vec1, vec2) / ( magnitude(vec1) * magnitude(vec2) ) );
    }

    /**
     * Computes (signed) angle between two vectors, in the range of -pi to pi
     * @param vec1 first vector
     * @param vec2 second vector
     * @return angle of vec2 relative to vec1, in the range of -pi to pi
     */
    public static double signedAngleBetween(Point2D.Double vec1, Point2D.Double vec2) {
        double angle1 = atan2(vec1.y, vec1.x);
        double angle2 = atan2(vec2.y, vec2.x);
        double diff = angle2 - angle1;
        if (diff > PI)
            return diff - 2*PI;
        else if (diff < -PI)
            return diff + 2*PI;
        else
            return diff;
    }
        

    /**
     * Computes angle between three points. In particular, this is the angle required to rotate
     * the vector [p2->p1] to the point where it is parallel to the vector [p2->p3]. Allows for points at
     * infinity of the form (infinity, angle)
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return the rotation angle, between 0 and 2pi; or NaN if any of the points coincide
     */
    public static double positiveAngleBetween3Points(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        if (isInfinite(p2)) {
            return 0;
        }
        double angleDiff =
                (isInfinite(p3) ? p3.y : positiveAngle(p3.x - p2.x, p3.y - p2.y))
                - (isInfinite(p1) ? p1.y : positiveAngle(p1.x - p2.x, p1.y - p2.y));
        if (angleDiff < 0) {
            angleDiff += 2*Math.PI;
        }
        return angleDiff;
    }


    //==================================================================================================
    //
    // 5. PROJECTION FORMULAS
    //

    /**
     * Computes scalar projection of first vector onto second vector.
     * @param projVec vector that will be projected
     * @param dir vector to project onto
     * @return length of component of <code>projVec> in the direction of <code>dir</code>
     */
    public static double scalarProjection(Point2D.Double projVec, Point2D.Double dir) {
        return dotProduct(projVec, dir) / magnitude(dir);
    }

    /**
     * Computes projection of first vector onto second vector.
     * @param projVec vector that will be projected
     * @param dir vector to project onto
     * @return vector in the direction of <code>dir</code>
     */
    public static Point2D.Double vectorProjection(Point2D.Double projVec, Point2D.Double dir) {
        double factor = dotProduct(projVec, dir) / pow(magnitude(dir), 2);
        return new Point2D.Double(dir.x * factor, dir.y * factor);
    }

    /**
     * Computes component of first vector that is perpendicular to the second vector
     * @param projVec first vector
     * @param dir vector to project orthogonally to
     * @return vector component that is perpendicular to <code>dir</code>
     */
    public static double perpendicularScalarProjection(Point2D.Double projVec, Point2D.Double dir) {
        return Math.sqrt(projVec.distanceSq(0,0) - pow(scalarProjection(projVec,dir), 2));
    }

    /**
     * Computes component of first vector that is perpendicular to the second vector
     * @param projVec first vector
     * @param dir vector to project orthogonally to
     * @return vector component that is perpendicular to <code>dir</code>
     */
    public static Point2D.Double perpendicularVectorProjection(Point2D.Double projVec, Point2D.Double dir) {
        Point2D.Double parallel = vectorProjection(projVec, dir);
        return new Point2D.Double(projVec.x - parallel.x, projVec.y - parallel.y);
    }


    //==================================================================================================
    //
    // 6. DISTANCE FORMULAS
    //

    private static double t(Point2D.Double original, Point2D.Double endpt1, Point2D.Double endpt2) {
        return ((endpt1.x - original.x) * (endpt1.x - endpt2.x) + (endpt1.y - original.y) * (endpt1.y - endpt2.y)) / endpt1.distanceSq(endpt2);
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
    public static Point2D.Double closestPointOnLine(Point2D.Double original, Point2D.Double endpt1, Point2D.Double endpt2) {
        if (endpt1.equals(endpt2))
            return new Point2D.Double(endpt1.x, endpt1.y);
        double t = t(original, endpt1, endpt2);
        return new Point2D.Double(endpt1.x + t * (endpt2.x - endpt1.x), endpt1.y + t * (endpt2.y - endpt1.y));
    }

    /**
     * Returns point on RAY from endpt1 to endpt2 which is closest to this point.
     * @param original the point considered
     * @param endpt1 first endpoint of the ray
     * @param endpt2 second endpoint of the ray
     * @return point along the line that forms a perpendicular with the original point, or the nearest endpt to that point; or if the endpoints coincide a point at an endpoint
     */
    public static Point2D.Double closestPointOnRay(Point2D.Double original, Point2D.Double endpt1, Point2D.Double endpt2) {
        if (endpt1.equals(endpt2)) {
            return new Point2D.Double(endpt1.x, endpt1.y);
        }
        double t = t(original, endpt1, endpt2);
        t = t < 0 ? 0 : t;
        return new Point2D.Double(endpt1.x + t * (endpt2.x - endpt1.x), endpt1.y + t * (endpt2.y - endpt1.y));
    }

    /**
     * Returns point on line SEGMENT between point1 and point2 which is closest to this point.
     * @param original the point considered
     * @param endpt1 first endpoint of the line segment
     * @param endpt2 second endpoint of the line segment
     * @return point along the line that forms a perpendicular with the original point, or the nearest endpt to that point; or if the endpoints coincide a point at an endpoint
     */
    public static Point2D.Double closestPointOnSegment(Point2D.Double original, Point2D.Double endpt1, Point2D.Double endpt2) {
        if (endpt1.equals(endpt2)) {
            return new Point2D.Double(endpt1.x, endpt1.y);
        }
        double t = t(original, endpt1, endpt2);
        t = t < 0 ? 0 : (t > 1 ? 1 : t);
        return new Point2D.Double(endpt1.x + t * (endpt2.x - endpt1.x), endpt1.y + t * (endpt2.y - endpt1.y));
    }

}
