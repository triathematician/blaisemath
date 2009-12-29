/**
 * MeshRoot2D.java
 * Created on May 23, 2008
 */
package scio.function;

import deprecated.Function;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Vector;
import org.apache.commons.math.FunctionEvaluationException;
import scio.coordinate.formal.EuclideanPoint2D;
import scio.coordinate.formal.EuclideanPoint3D;
import scio.coordinate.Point3D;
import scio.coordinate.utils.SpacialMathUtils;

/**
 * This class is intended to solve equations of the form f(x,y)=C. Returns a path of points representing the solution curve.
 * 
 * @author Elisha Peterson
 */
public class MeshRoot2D {

    public static GeneralPath findRoots(
            Function<EuclideanPoint2D, Double> function,
            double zValue, EuclideanPoint2D p1, EuclideanPoint2D p2,
            double refinement, double precision, int maxIterations)
            throws FunctionEvaluationException {
        return findRoots(function, zValue, p1.x, p1.y, p2.x, p2.y, refinement, precision, maxIterations);
    }

    /** Returns complete solution curve to f(x,y)=C, as approximated by the <b>addFlatTriangles</b> method below. */
    public static GeneralPath findRoots(
            Function<EuclideanPoint2D, Double> function,
            double zValue,
            double x1, double y1, double x2, double y2,
            double refinement, double precision, int maxIterations)
            throws FunctionEvaluationException {

        int xSteps = (int) Math.ceil((x2 - x1) / refinement);
        int ySteps = (int) Math.ceil((y2 - y1) / refinement);

        Vector<EuclideanPoint2D> inputs = new Vector<EuclideanPoint2D>(xSteps * ySteps);
        for (int i = 0; i <= xSteps; i++) {
            for (int j = 0; j <= ySteps; j++) {
                inputs.add(new EuclideanPoint2D(x1 + refinement * i, y1 + refinement * j));
            }
        }
        List<Double> outputs = function.getValue(inputs);

        Vector<EuclideanPoint2D> midInputs = new Vector<EuclideanPoint2D>((xSteps - 1) * (ySteps - 1));
        for (int i = 0; i < xSteps; i++) {
            for (int j = 0; j < ySteps; j++) {
                midInputs.add(new EuclideanPoint2D(x1 + refinement * (i + .5), y1 + refinement * (j + .5)));
            }
        }
        List<Double> midOutputs = function.getValue(midInputs);

        // TODO use above function values to compute the "flat triangle" elements below...

        GeneralPath path = new GeneralPath();
        for (double x = x1; x <= x2; x += refinement) {
            for (double y = y1; y <= y2; y += refinement) {
                addFlatTriangles(path, function, zValue, x, y, x + refinement, y + refinement);
            }
        }

        return path;
    }

    /** Returns solution to f(x,y)=C on a given rectangle, where the function is approximated by four planes in space,
     * corresponding to four triangles of the rectangle. */
    public static GeneralPath addFlatTriangles(GeneralPath path,
            Function<EuclideanPoint2D, Double> function, double zValue,
            double x1, double y1, double x2, double y2) throws FunctionEvaluationException {
        //System.out.println("Inputs: z=" + zValue + " and (" + x1 + "," + y1 + ") and (" + x2 + "," + y2 + ")");

        // initialize points

        EuclideanPoint3D pll = new EuclideanPoint3D(x1, y1, function.getValue(new EuclideanPoint2D(x1, y1)));
        EuclideanPoint3D pul = new EuclideanPoint3D(x1, y2, function.getValue(new EuclideanPoint2D(x1, y2)));
        EuclideanPoint3D pur = new EuclideanPoint3D(x2, y2, function.getValue(new EuclideanPoint2D(x2, y2)));
        EuclideanPoint3D plr = new EuclideanPoint3D(x2, y1, function.getValue(new EuclideanPoint2D(x2, y1)));
        EuclideanPoint3D pmid = new EuclideanPoint3D((x1 + x2) / 2, (y1 + y2) / 2, function.getValue(new EuclideanPoint2D((x1 + x2) / 2, (y1 + y2) / 2)));

        // find points of intersection

        Point2D.Double upper = findPointOnSegment(pul, pur, zValue);
        Point2D.Double right = findPointOnSegment(pur, plr, zValue);
        Point2D.Double lower = findPointOnSegment(plr, pll, zValue);
        Point2D.Double left = findPointOnSegment(pll, pul, zValue);
        Point2D.Double upperLeft = findPointOnSegment(pmid, pul, zValue);
        Point2D.Double upperRight = findPointOnSegment(pmid, pur, zValue);
        Point2D.Double lowerRight = findPointOnSegment(pmid, plr, zValue);
        Point2D.Double lowerLeft = findPointOnSegment(pmid, pll, zValue);

        // construct result

        addSegment(path, upperLeft, upper, upperRight);
        addSegment(path, upperRight, right, lowerRight);
        addSegment(path, lowerRight, lower, lowerLeft);
        addSegment(path, lowerLeft, left, upperLeft);

        return path;
    }

    /** Returns point along line segment between p1 and p2 at which z=C, or null if the point is not on the segment.
     * @param p1 first point of segment
     * @param p2 second point of segment
     * @param zValue z value
     * @return point along segment at given z value, or null if no such value exists
     */
    public static Point2D.Double findPointOnSegment(EuclideanPoint3D p1, EuclideanPoint3D p2, double zValue) {
        //System.out.println("p1: " + p1 + ", p2: " + p2 + ", z: " + zValue);
        if (p1.getZ() == p2.getZ()) {
            return p1.getZ() == zValue ? new Point2D.Double(p1.x, p1.y) : null;
        } else {
            double t = (zValue - p1.getZ()) / (p2.getZ() - p1.getZ());
            if (t < 0 || t > 1) {
                return null;
            }
            Point3D rTemp = new Point3D(p1.x + t*(p2.x-p1.x), p1.y + t*(p2.y-p1.y), p1.z + t*(p2.z-p1.z));
            return new Point2D.Double(rTemp.x, rTemp.y);
        }
    }

    /** Returns segment between two of three points (if not null) */
    public static Shape addSegment(GeneralPath shape, Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
        try {
            if (p1 == null) {
                shape.append(new Line2D.Double(p2, p3), false);
            //System.out.println("appending: "+p2 +", "+p3);
            } else if (p2 == null) {
                shape.append(new Line2D.Double(p1, p3), false);
            //System.out.println("appending: "+p1 +", "+p3);
            } else if (p3 == null) {
                shape.append(new Line2D.Double(p1, p2), false);
            //System.out.println("appending: "+p1 +", "+p2);
            }
        } catch (NullPointerException e) {
        }
        return shape;
    }

    /** Returns segment of intersection of z=C with the portion of a plane bounded by three particular points. */
    public static GeneralPath lineOnPlane(double zValue, EuclideanPoint3D p1, EuclideanPoint3D p2, EuclideanPoint3D p3) {
        double tripleProd = SpacialMathUtils.tripleProduct(p1, p2, p3);
        Point3D sum1 = p1.crossProduct(p2);
        Point3D sum2 = p2.crossProduct(p3);
        Point3D sum3 = p3.crossProduct(p1);
        Point3D crossSum = new Point3D(sum1.x + sum2.x + sum3.x, sum1.y + sum2.y + sum3.y, sum1.z + sum2.z + sum3.z);
        double slope = -crossSum.getX() / crossSum.getY();
        double intercept = (tripleProd - zValue * crossSum.getZ()) / crossSum.getY();
        GeneralPath result = new GeneralPath();
        // need to produce intersection of line with given slope/intercept (with respect to x/y) and triangle bounded by p1,p2,p3
        throw new UnsupportedOperationException();
    }
}
