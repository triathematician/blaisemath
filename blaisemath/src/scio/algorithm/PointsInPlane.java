/**
 * PointsInPlane.java
 * Created on Jun 3, 2008
 */
package scio.algorithm;

import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import scio.coordinate.utils.PlanarMathUtils;

/**
 * Library of computational geometry algorithms relating to points in the plane.
 * 
 * @author Elisha Peterson
 */
public class PointsInPlane {

    /** Returns convex hull of a given set of points. */
    public static List<Point2D.Double> convexHull(List<Point2D.Double> points) {
        // STEP 1: sort points by a particular pivot element (first element)
        TreeSet<Point2D.Double> sortedPoints = new TreeSet<Point2D.Double>(new PivotCompare(points.get(0)));
        sortedPoints.addAll(points);

        Vector<Point2D.Double> result = new Vector<Point2D.Double>();
        throw new UnsupportedOperationException();
    }

    // STATIC HELPER CLASSES
    //
    //
    /** Comparator for sorting points (by angle) relative to a specified pivot point. */
    public static class PivotCompare implements Comparator<Point2D.Double> {

        Point2D.Double pivot;

        public PivotCompare(Point2D.Double pivot) {
            this.pivot = pivot;
        }

        public int compare(Point2D.Double p1, Point2D.Double p2) {
            return Double.compare(
                    PlanarMathUtils.angle(new Point2D.Double(pivot.x - p1.x, pivot.y - p1.y)),
                    PlanarMathUtils.angle(new Point2D.Double(pivot.x - p2.x, pivot.y - p2.y)));
        }
    }

    /** Comparator for sorting points by x value. */
    public static class XCompare implements Comparator<Point2D.Double> {

        public int compare(Point2D.Double p1, Point2D.Double p2) {
            return Double.compare(p1.x, p2.x);
        }
    }
}
