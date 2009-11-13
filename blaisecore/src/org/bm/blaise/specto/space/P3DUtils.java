/*
 * P3DUtils.java
 * Created on Oct 21, 2009
 */
package org.bm.blaise.specto.space;

import scio.coordinate.P3D;

/**
 * <p>
 *  This class contains static methods for handling three-dimensional graphics.
 * </p>
 * @author Elisha Peterson
 */
public class P3DUtils {

    static public double planeDist(P3D point, P3D normal, P3D testPoint) {
        return normal.x * (testPoint.x - point.x) + normal.y * (testPoint.y - point.y) + normal.z * (testPoint.z - point.z);
    }

    /**
     * Determines if any points in array are clipped by a plane.
     * 
     * @return true if any of the supplied points are clipped by the plane
     */
    static public boolean clips(P3D point, P3D normal, P3D[] pts) {
        for (int i = 0; i < pts.length; i++) {
            if (planeDist(point, normal, pts[i]) < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns point at given percentage of distance between two other points
     */
    static public P3D onSegment(P3D p1, P3D p2, double dist) {
        return new P3D(
                p1.x + (p2.x - p1.x) * dist,
                p1.y + (p2.y - p1.y) * dist,
                p1.z + (p2.z - p1.z) * dist);
    }

    /**
     * Returns point at given percentage of distance between two indexed points
     *
     * @param pp list of points
     * @param dd list of distances from plane to points
     * @param i1 index of first point
     * @param i2 index of second point
     */
    static public P3D onSegment(P3D[] pp, double[] dd, int i1, int i2) {
        return onSegment(pp[i1], pp[i2], dd[i1] / (dd[i1] - dd[i2]));
    }

    /**
     * Clips a line segment at a plane, and returns the clipped version of the segment,
     * or null if the line is on the side of the plane opposite the normal.
     *
     * @param point a point on the plane
     * @param normal a unit normal vector for the plane
     * @param segment the segment as a list of two points
     * 
     * @return the clipped segment
     */
    static public P3D[] clipSegment(P3D point, P3D normal, P3D[] segment) {
        if (segment == null || segment.length != 2) {
            throw new IllegalArgumentException("Segment must have 2 points!");
        }
        double[] dists = new double[] { planeDist(point, normal, segment[0]), planeDist(point, normal, segment[1]) };
        if (dists[0] > 0 && dists[1] > 0) { // no clipping
            return segment;
        } else if (dists[0] > 0 && dists[1] < 0) { // clip second point
            return new P3D[]{segment[0], onSegment(segment, dists, 0, 1)};
        } else if (dists[0] < 0 && dists[1] > 0) { // clip first point
            return new P3D[]{onSegment(segment, dists, 1, 0), segment[1]};
        }
        // default when both points are on the other side of the plane
        return null;
    }

    /**
     * Clips a triangle against a plane, and returns a clipped version of the triangle.
     * The result will be a vector of points containing either 3 points (if the triangle clips
     * to another triangle), or 4 points (if the triangle is unclipped or if it clips to a
     * four-sided polygon). In the unclipped case, the first and last points will coincide.
     * If the entire triangle is hidden, returns a null object.
     *
     * @param point a point on the plane
     * @param normal a unit normal vector for the plane
     * @param triangle three points of a triangle
     *
     * @return the clipped triangle
     */
    static public P3D[] clipTriangle(P3D point, P3D normal, P3D[] triangle) {
        if (triangle == null || triangle.length != 3) {
            throw new IllegalArgumentException("Triangle must have 2 points!");
        }
        double[] dists = new double[] { planeDist(point, normal, triangle[0]), planeDist(point, normal, triangle[1]), planeDist(point, normal, triangle[2]) };
        if (dists[0] > 0) {
            if (dists[1] > 0) {
                if (dists[2] > 0) { // no clipping
                    return new P3D[]{triangle[0], triangle[1], triangle[2], triangle[0]};
                } else { // clip t2 off
                    return new P3D[]{onSegment(triangle, dists, 0, 2), triangle[0], triangle[1], onSegment(triangle, dists, 1, 2)};
                }
            } else {
                if (dists[2] > 0) { // clip t1 off
                    return new P3D[]{onSegment(triangle, dists, 2, 1), triangle[2], triangle[0], onSegment(triangle, dists, 0, 1)};
                } else { // clip t1 and t2 off
                    return new P3D[]{onSegment(triangle, dists, 0, 2), triangle[0], onSegment(triangle, dists, 0, 1)};
                }
            }
        } else {
            if (dists[1] > 0) {
                if (dists[2] > 0) { // clip t0 off
                    return new P3D[]{onSegment(triangle, dists, 1, 0), triangle[1], triangle[2], onSegment(triangle, dists, 2, 0)};
                } else { // clip t0 and t2 off
                    return new P3D[]{onSegment(triangle, dists, 1, 0), triangle[1], onSegment(triangle, dists, 1, 2)};
                }
            } else {
                if (dists[2] > 0) { // clip t0 and t1 off
                    return new P3D[]{onSegment(triangle, dists, 2, 1), triangle[2], onSegment(triangle, dists, 2, 0)};
                } else { // all hidden
                    return null;
                }
            }
        }
    }
}
