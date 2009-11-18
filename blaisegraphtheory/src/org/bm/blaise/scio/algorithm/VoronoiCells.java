/**
 * VoronoiCells.java
 * Created on May 27, 2008
 */
package org.bm.blaise.scio.algorithm;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;
import scio.coordinate.utils.PlanarMathUtils;
import org.bm.blaise.scio.graph.Graph;

/**
 * This class takes in several points and generates the Voronoi cells, consisting
 * of the set of points closest to each in the set of cells.
 * @author Elisha Peterson
 */
public class VoronoiCells {

    public Vector<Equidistant> equis;
    public Graph<Equidistant> connections;
    public Vector<Point2D.Double> hull = new Vector<Point2D.Double>();
    public HashMap<Point2D.Double, Double> spokes;

    public VoronoiCells(Vector<Point2D.Double> points) {

        // Step 0: compute convex hull
        Point2D.Double pivot = minPoint(points);
        TreeSet<Point2D.Double> sorted = new TreeSet<Point2D.Double>(new AngleSort(pivot));
        sorted.addAll(points);
        hull = new Vector<Point2D.Double>();
        Point2D.Double pt = sorted.first();
        hull.add(pt);
        sorted.remove(pt);
        pt = sorted.first();
        hull.add(pt);
        sorted.remove(pt);
        while (!sorted.isEmpty()) {
            while (PlanarMathUtils.crossProduct(hull.get(hull.size() - 2), hull.lastElement(), sorted.first()) <= 0) {
                hull.remove(hull.size() - 1);
            }
            pt = sorted.first();
            hull.add(pt);
            sorted.remove(pt);
        }

        // Step 1: generate set of equidistant points
        equis = new Vector<Equidistant>();
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                for (int k = j + 1; k < points.size(); k++) {
                    equis.add(new Equidistant(points.get(i), points.get(j), points.get(k)));
                }
            }
        }

        // Step 2: toss unnecessary points
        HashSet<Equidistant> remove = new HashSet<Equidistant>();
        for (Equidistant ep : equis) {
            for (Point2D.Double opt : points) {
                if (ep.closerTo(opt)) {
                    remove.add(ep);
                }
            }
        }
        equis.removeAll(remove);

        // Step 3: construct graph with connections supplied by equidistant points
        connections = new Graph<Equidistant>();
        for (int i = 0; i < equis.size(); i++) {
            for (int j = i + 1; j < equis.size(); j++) {
                if (equis.get(i).adjacent(equis.get(j))) {
                    connections.addEdge(equis.get(i), equis.get(j));
                }
            }
        }

        // Step 4: generate spokes
        spokes = new HashMap<Point2D.Double, Double>();
        for (int i = 0; i < hull.size(); i++) {
            Point2D.Double p1 = hull.get(i);
            Point2D.Double p2 = hull.get((i + 1) % hull.size());
            for (Equidistant ep : equis) {
                if (ep.adjacent(p1, p2)) {
                    spokes.put(ep.point, PlanarMathUtils.angle(p2.x - p1.x, p2.y - p1.y) - Math.PI / 2);
                }
            }
        }
    }

    /** Prints the tesselation. */
    @Override
    public String toString() {
        return equis.toString();
    }

    /** Inner class stores the point which is equidistant from three other specified points. */
    public static class Equidistant {

        Point2D.Double p1, p2, p3;
        public Point2D.Double point;
        Double distance;

        public Equidistant(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            point = PlanarMathUtils.centerOfCircleThrough(p1, p2, p3);
            distance = p1.distance(point);
        }

        /** Returns true if the specified point is closer to the center than any of these. */
        public boolean closerTo(Point2D.Double pt) {
            if (pt.equals(p1) || pt.equals(p2) || pt.equals(p3)) {
                return false;
            }
            if (distance.equals(Double.MAX_VALUE)) {
                return false;
            }
            return pt.distance(point) < distance;
        }

        /** Returns true if this point is adjacent to another (having at least two points in common) */
        public boolean adjacent(VoronoiCells.Equidistant e2) {
            int adj = 0;
            if (p1.equals(e2.p1) || p1.equals(e2.p2) || p1.equals(e2.p3)) {
                adj++;
            }
            if (p2.equals(e2.p1) || p2.equals(e2.p2) || p2.equals(e2.p3)) {
                adj++;
            }
//            if (distance.equals(Double.MAX_VALUE)) { return (adj >= 2); }
            if (p3.equals(e2.p1) || p3.equals(e2.p2) || p3.equals(e2.p3)) {
                adj++;
            }
            return (adj >= 2);
        }

        /** Returns true if this point is adjacent to both specified points */
        public boolean adjacent(Point2D.Double ep1, Point2D.Double ep2) {
            int adj = 0;
            if (p1.equals(ep1) || p1.equals(ep2)) {
                adj++;
            }
            if (p2.equals(ep1) || p2.equals(ep2)) {
                adj++;
            }
            if (p3.equals(ep1) || p3.equals(ep2)) {
                adj++;
            }
            return (adj >= 2);
        }

        /** Returns string representation of the point. */
        @Override
        public String toString() {
            return point.toString() + " amid " + p1.toString() + " " + p2.toString() + " " + p3.toString();
        }
    }

    /** Returns maximum magnitude point. */
    public Point2D.Double minPoint(Collection<Point2D.Double> points) {
        double minY = Double.MAX_VALUE;
        Point2D.Double minPt = null;
        for (Point2D.Double p : points) {
            if (p.y < minY || (p.y == minY && p.x < minPt.x)) {
                minY = p.y;
                minPt = p;
            }
        }
        return minPt;
    }

    /** Sorts points by angle. */
    private static class AngleSort implements Comparator<Point2D.Double> {

        Point2D.Double pivot;

        public AngleSort(Point2D.Double pivot) {
            this.pivot = pivot;
        }

        public int compare(Point2D.Double o1, Point2D.Double o2) {
            if (o1.equals(o2)) {
                return 0;
            }
            if (o1.equals(pivot)) {
                return -1;
            }
            if (o2.equals(pivot)) {
                return +1;
            }
            double angleDiff = PlanarMathUtils.angleBetween(o1, pivot, o2);
            return angleDiff == 0
                    ? Double.compare(o1.distance(pivot), o2.distance(pivot))
                    : (int) Math.signum(angleDiff);
        }
    }
}
