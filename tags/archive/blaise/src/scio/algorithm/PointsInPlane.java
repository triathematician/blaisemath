/**
 * PointsInPlane.java
 * Created on Jun 3, 2008
 */

package scio.algorithm;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.Vector;
import scio.coordinate.R2;

/**
 * Library of computational geometry algorithms relating to points in the plane.
 * 
 * @author Elisha Peterson
 */
public class PointsInPlane {

    /** Returns convex hull of a given set of points. */
    public static Vector<R2> convexHull(Vector<R2> points) {        
        // STEP 1: sort points by a particular pivot element (first element)
        TreeSet<R2> sortedPoints = new TreeSet<R2> (new PivotCompare(points.firstElement()));
        sortedPoints.addAll(points);
        
        Vector<R2> result = new Vector<R2>();
        return result;
    }
    
    // STATIC HELPER CLASSES
    
    /** Comparator for sorting points (by angle) relative to a specified pivot point. */
    public static class PivotCompare implements Comparator<R2> {
        R2 pivot;        
        public PivotCompare(R2 pivot) { this.pivot = pivot; }
        public int compare(R2 p1, R2 p2) { return Double.compare( pivot.minus(p1).angle(), pivot.minus(p2).angle() ); }
    }    
    
    /** Comparator for sorting points by x value. */
    public static class XCompare implements Comparator<R2> {
        public int compare(R2 p1, R2 p2) { return Double.compare(p1.x, p2.x); }
    }
}
