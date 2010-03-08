/**
 * PlanarGeometryUtils.java
 * Created on Dec 14, 2009
 */
package scio.coordinate.geometry;

import scio.coordinate.utils.*;
import java.awt.geom.Point2D;

/**
 * <p>
 *    This class is a library of functions to compute formulas in planar geometry,
 *    having to do with points, lines, circles, and more generally the most common planar curves.
 * </p>
 * <p>
 *    Points at infinity are generally permitted, and have the form specified in <code>PlanarMathUtils</code>.
 *    These are represented by a <code>Double.POSITIVE_INFINITY</code>
 *    x-coordinate, and a y-coordinate representing the infinite angle.
 * </p>
 * <p>
 *    Lines are stored in the form of a <code>double[]</code>, where the first entry is the
 *    slope and the second is the y-intercept. In the case of a vertical line, the first entry
 *    is infinity and the second is the x-intercept. In the case of a non-existent line (e.g. that
 *    returned for the line between two identical points), both entries are NaN.
 * </p>
 * <p>
 *   The code is organized as follows:
 *      <li>Test methods
 *      <li>Methods for constructing new lines
 *      <li>Methods for converting lines between various formats
 *      <li>Slope formulas
 *      <li>Intersection formulas
 *      <li>Distance formulas
 * </p>
 * @author Elisha Peterson
 */
public class PlanarGeometryUtils {

    /** Represents a non-existent line. */
    public static final double[] NO_LINE = new double[]{Double.NaN, Double.NaN};
    /** Represents line at infinity. */
    public static final double[] LINE_AT_INFINITY = new double[]{Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY};


    //==================================================================================================
    //
    // 0. TEST METHODS

    /**
     * Determines whether given line is vertical.
     * @param line line as a double pair
     * @return true if <code>line</code> is a vertical line
     */
    public static boolean isVerticalLine(double[] line) {
        return Double.isInfinite(line[0]) && !Double.isInfinite(line[1]);
    }

    /**
     * Determines whether given line is vertical.
     * @param line line as a double pair
     * @return true if <code>line</code> is a valid line
     */
    public static boolean isValidLine(double[] line) {
        return ! (Double.isNaN(line[0]) || Double.isNaN(line[1]));
    }
    
    /**
     * Determines if point is on the line.
     * @param point a point
     * @param line a line
     * @return <code>true</code> if the point is on the line
     */
    public static boolean isPointOnLine(Point2D.Double point, double[] line) {
        if (Double.isNaN(line[0])) {
            return Double.isNaN(point.x) && Double.isNaN(point.y);
        } else if (Double.isInfinite(line[0])) {
            return point.x == line[1];
        } else {
            return point.y == line[0]*point.x + line[1];
        }
    }


    //==================================================================================================
    //
    // 1a. METHODS THAT CONSTRUCT **LINES** AND **PARABOLAS**
    //

    /**
     * Returns instance of a vertical line with specified intercept.
     * @param x the x-intercept
     * @return line
     */
    public static double[] verticalLineAt(double x) {
        return new double[] { Double.POSITIVE_INFINITY, x };
    }

    /**
     * Computes and returns the line through two points.
     * @param p1 first point
     * @param p2 second point
     * @return the line
     */
    public static double[] lineThrough(Point2D.Double p1, Point2D.Double p2) {
        if (p1.equals(p2)) {
            return NO_LINE;
        } else if (p1.x == p2.x) {
            return verticalLineAt(p1.x);
        } else {
            double m = slopeOfLineBetween(p1, p2);
            return new double[]{m, -m * p1.x + p1.y};
        }
    }

    /**
     * Computes and returns line that is equidistant from two points.
     * @param p1 first point
     * @param p2 second point
     * @return the line
     */
    public static double[] lineEquidistantTo(Point2D.Double p1, Point2D.Double p2) {
        if (p1.equals(p2)) {
            return NO_LINE;
        } else if (p1.y == p2.y) {
            return verticalLineAt((p1.x + p2.x) / 2);
        } else {
            double im = (p1.x - p2.x) / (p2.y - p1.y);
            return new double[]{im, -im * (p1.x + p2.x) / 2 + (p1.y + p2.y) / 2};
        }
    }



    //==================================================================================================
    //
    // 1b. METHODS FOR CONVERTING LINES
    //

    /**
     * Returns projective form of a line, where the result is a triple [a,b,c] with the line given by a*x+b*y+c=0.
     * @param line the line
     * @return the projective form of the line
     */
    public static double[] toProjectiveFromLine(double[] line) {
        if (Double.isNaN(line[0])) {
            return new double[]{0, 0, 0};
        } else if (Double.isInfinite(line[0])) {
            return new double[]{1, 0, -line[1]};
        } else {
            return new double[]{line[0], -1, line[1]};
        }
    }


    //==================================================================================================
    //
    // 2. SLOPE-RELATED PROPERTIES
    //

    /**
     * Computes slope between two points.
     * @param p1 the first point
     * @param p2 the second point
     * @return a <code>double</code> with the slope,
     *    which may be infinite (if the points are spaced vertically) or NaN (if they're the same)
     */
    public static double slopeOfLineBetween(Point2D.Double p1, Point2D.Double p2) {
        if (p1.equals(p2)) {
            return Double.NaN;
        } else if (p1.x == p2.x) {
            return Double.POSITIVE_INFINITY;
        } else {
            return (p2.y - p1.y) / (p2.x - p1.x);
        }
    }


    //==================================================================================================
    //
    // 3. INTERSECTION FORMULAS
    //

    /**
     * Returns point of intersection of two given lines, in the form y=m*x+b. If the value of m is <i>infinite</i>,
     * then the line is assumed to be a vertical line of the form x=b. So this will work for any input.
     * @param l1 first line; parameter 0 is slope, parameter 1 is intersection point
     * @param l2 second line
     * @return the point of intersection; the point at infinity if the lines are parallel; <code>NO_POINT</code> if lines coincide
     */
    public static Point2D.Double intersectionOfLines(double[] l1, double[] l2) {
        if (Double.isNaN(l1[0]) || Double.isNaN(l2[0]) || (l1[0]==l2[0] && l1[1]==l2[1]) ) {
            return PlanarMathUtils.NO_POINT;
        } else if (l1[0] == l2[0]) {
            return PlanarMathUtils.INFINITY;
        } else if (Double.isInfinite(l1[0])) {
            return new Point2D.Double(l1[1], l2[0] * l1[1] + l2[1]);
        } else if (Double.isInfinite(l2[0])) {
            return new Point2D.Double(l2[1], l1[0] * l2[1] + l1[1]);
        } else {
            double x = (l2[1] - l1[1]) / (l1[0] - l2[0]);
            return new Point2D.Double(x, l1[0] * x + l1[1]);
        }
    }

    /**
     * Computes and returns center of circle passing through three points. The center is at infinity if points are colinear.
     * If one of the points is infinity then returns point in direction of bisecting line between the two others.
     * @param p1 first point
     * @param p2 second point
     * @param p3 third point
     * @return point equidistant to three points
     */
    public static Point2D.Double pointEquidistantTo(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        return intersectionOfLines(lineEquidistantTo(p1, p2), lineEquidistantTo(p1, p3));
    }


    //==================================================================================================
    //
    // 4. DISTANCE-RELATED FORMULAS
    //

    /**
     * Computes and returns distance from a point to a line.
     * @param point a point
     * @param line a line
     * @return distance from point to line
     */
    public static double distanceBetween(Point2D.Double point, double[] line) {
        if (point == PlanarMathUtils.INFINITY) {
            return 0;
        } else if (point == PlanarMathUtils.NO_POINT) {
            return Double.NaN;
        } else {
            double[] pLine = toProjectiveFromLine(line);
            return Math.abs(pLine[0]*point.x + pLine[1]*point.y + pLine[2]) / Math.sqrt(pLine[0]*pLine[0]+pLine[1]*pLine[1]);
        }
    }


































    //
    // CONIC SECTIONS PROBLEMS
    // ... conic sections are stored as an array of 6 parameters of the form [a,b,c,d,e,f] where a*x^2+b*x*y+c*y^2+d*x+e*y+f=0.
    //

    //
    // PROJECTIVE GEOMETRY UTILITIES
    //


    /**
     * Returns parabola with given point as its focus.
     * @param focus focsu of the parabola
     * @param directrix x-value of the directrix
     * @return an array [a,b,c] where the parabola is x = a*y^2 + b*y + c
     */
    public static double[] parabolaByFocusAndDirectrix(Point2D.Double focus, double directrix) {
        return new double[] {
            -.5 / (directrix - focus.x),
            focus.y / (directrix - focus.x),
            .5 * (focus.x + directrix - focus.y * focus.y / (directrix - focus.x))
        };
    }
    
    /**
     * Finds the points of intersection of two parabolas
     * @param parabola1 the first parabola as an array [a,b,c], where the parabola is y = a*x^2 + b*x + c
     * @param parabola2 the second parabola
     * @return a pair of roots giving the lower root and the upper root; the first root is the "negative" root and
     *   the second is the "positive" root... if a1-a2>0, the negative root has the lower x-value, otherwise the
     *   positive root has the lower x-value; if a1=a2, then the parabolas only intersect once and the method returns a single value instead of 2
     */
    public static double[] intersectionsOfParabolas(double[] parabola1, double[] parabola2) {
        return BasicMathUtils.quadraticRoots(parabola1[0] - parabola2[0], parabola1[1] - parabola2[1], parabola1[2] - parabola2[2]);
    }

    /**
     * Returns <b>points</b> of intersection of two parabolas with a common vertical directrix line. If one of the x-values
     * of the foci is equal to the directrix, finds the y-value of the other parabola at that x-value
     * @param focus1 the first focus
     * @param focus2 the second focus
     * @param directrix the common directrix
     * @return an array with two points containing the intersections
     */
    public static Point2D.Double[] getIntersectionOfParabolasWithCommonDirectrix(Point2D.Double focus1, Point2D.Double focus2, double directrix) {
        double[] parabola1 = parabolaByFocusAndDirectrix(focus1, directrix);
        double[] parabola2 = parabolaByFocusAndDirectrix(focus2, directrix);
        if (focus1.x == directrix && focus2.x == directrix) {
            return focus1.y == focus2.y ? new Point2D.Double[] { new Point2D.Double(directrix, focus1.y) } : null;
        } else if (focus1.x == directrix) {
            return new Point2D.Double[] { new Point2D.Double(parabola2[0]*focus1.y*focus1.y + parabola2[1]*focus1.y + parabola2[2], focus1.y) };
        } else if (focus2.x == directrix) {
            return new Point2D.Double[] { new Point2D.Double(parabola1[0]*focus2.y*focus2.y + parabola1[1]*focus2.y + parabola1[2], focus2.y) };
        }
        double[] yRoots = intersectionsOfParabolas(parabola1, parabola2);
        if (yRoots.length == 1)
            return new Point2D.Double[] {
                new Point2D.Double(parabola1[0]*yRoots[0]*yRoots[0] + parabola1[1]*yRoots[0] + parabola1[2], yRoots[0])
            };
        else return new Point2D.Double[] {
                new Point2D.Double(parabola1[0]*yRoots[0]*yRoots[0] + parabola1[1]*yRoots[0] + parabola1[2], yRoots[0]),
                new Point2D.Double(parabola1[0]*yRoots[1]*yRoots[1] + parabola1[1]*yRoots[1] + parabola1[2], yRoots[1])
            };
    }

    /**
     * Computes and returns point equidistant from 2 points and a line.
     * @param p1 first point
     * @param p2 second point
     * @param the line
     * @return the two points that are equidistant from the line and the given points
     */
    public static Point2D.Double[] pointsEquidistantTo(Point2D.Double p1, Point2D.Double p2, double[] line) {
        if (isVerticalLine(line)) {
            return getIntersectionOfParabolasWithCommonDirectrix(p1, p2, line[1]);
        }
        throw new UnsupportedOperationException("This feature has not been implemented yet.");
    }
    
}
