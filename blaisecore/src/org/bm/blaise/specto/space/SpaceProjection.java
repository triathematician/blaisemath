/*
 * SpaceProjection.java
 * Created on Oct 18, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.util.Comparator;
import scio.coordinate.Point3D;

/**
 * <p>
 *  This class plays a role similar to java's standard <code>AffineTransform</code>.
 *  Here, we encode a projection of a three-dimensional scene onto the 2D camera window.
 * </p>
 * 
 * @author Elisha Peterson
 */
public class SpaceProjection implements Cloneable, Comparator<Point3D> {

    //
    //
    // CAMERA SETTINGS
    //
    //

    /** Point of interest. */
    Point3D center = new Point3D(0, 0, 0);

    /** Total distance from camera to center of interest. */
    double viewDist = 29.0;

    /** Camera angle ("front" of camera). */
    Point3D tDir = new Point3D(-3, -3, -1).normalized();
    
    /** Camera normal vector ("top" of camera). */
    Point3D nDir = new Point3D(-1, -1, 6).normalized();

    /** Camera location. */
    transient Point3D camera = derivedPoint(center, -viewDist, tDir);

    /** Camera binormal ("side" of camera). */
    transient Point3D bDir = tDir.crossProduct(nDir);

    //
    //
    // PROJECTION SETTINGS
    //
    //

    /** Distance from camera to view plane (in cm) */
    double screenDist = 30.0;

    /** Center of screen */
    transient Point3D screenCenter = derivedPoint(camera, screenDist, tDir);

    /** Distance from camera to clipping plane (in cm) */
    double clipDist = 2;

    /** Point representing the clipping plane. */
    transient Point3D clipPoint = derivedPoint(camera, clipDist, tDir);

    //
    //
    // DUAL CAMERA SETTINGS
    //
    //

    /** Distance between cameras */
    double camSep = 0.5;


    //
    //
    // WINDOW COORDINATES
    //
    //

    /** Window bounding box. */
    RectangularShape winBounds;

    /** Pixels per unit */
    double dpi = 270;

    /** Center of window, in window coordinates. */
    transient Point2D.Double winCenter;


    //
    //
    // BEAN PATTERNS
    //
    //

    public Point3D getCenter() {
        return center;
    }

    public void setCenter(Point3D center) {
        this.center = center;
        computeTransformation();
    }

    public Point3D getCamera() {
        return camera;
    }

    public void setCamera(Point3D camera) {
        setCenter( camera.plus(tDir.times(viewDist)) );
    }

    public double getViewDist() {
        return viewDist;
    }

    public void setViewDist(double viewDist) {
        this.viewDist = viewDist;
        computeTransformation();
    }

    public Point3D getTDir() {
        return tDir;
    }

    public void setTDir(Point3D tDir) {
        if (tDir.magnitudeSq() == 1) {
            this.tDir = tDir;
        } else {
            this.tDir = tDir.normalized();
        }
        computeTransformation();
    }

    public Point3D getNDir() {
        return nDir;
    }

    public void setNDir(Point3D nDir) {
        if (nDir.magnitudeSq() == 1) {
            this.nDir = nDir;
        } else {
            this.nDir = nDir.normalized();
        }
        this.nDir = nDir;
        computeTransformation();
    }

    public Point3D getBDir() {
        return bDir;
    }

    public void setBDir(Point3D bDir) {
        setNDir(bDir.crossProduct(tDir));
    }

    public double getClipDist() {
        return clipDist;
    }

    public void setClipDist(double clipDist) {
        this.clipDist = clipDist;
        computeTransformation();
    }

    public double getCamSep() {
        return camSep;
    }

    public void setCamSep(double camSep) {
        this.camSep = camSep;
    }

    public double getScreenDist() {
        return screenDist;
    }

    public void setScreenDist(double projDist) {
        this.screenDist = projDist;
        computeTransformation();
    }

    public RectangularShape getWinBounds() {
        return winBounds;
    }

    public void setWinBounds(RectangularShape winBounds) {
        this.winBounds = winBounds;
        computeTransformation();
    }

    public double getDpi() {
        return dpi;
    }

    public void setDpi(double dpi) {
        this.dpi = dpi;
        computeTransformation();
    }

    

    //
    //
    // STATIC UTILITY COMPUTATIONS
    //
    //

    /**
     * Computes point a specified distance/direction from a starting point.
     * Direction is assumed to be a unit vector
     *
     * @param start starting point
     * @param dist distance
     * @param dir direction
     *
     * @return new point at start + dist * dir
     */
    static Point3D derivedPoint(Point3D start, double dist, Point3D dir) {
        return new Point3D(
                start.x + dist * dir.x,
                start.y + dist * dir.y,
                start.z + dist * dir.z );
    }

    static Point2D.Double computeWinCenter(RectangularShape rr) {
        return new Point2D.Double(
                rr.getMinX() + rr.getWidth() / 2,
                rr.getMinY() + rr.getHeight() / 2 );
    }



    //
    //
    // PROJECTION METHODS
    //
    //

    Point2D.Double getCenterClone() {
        return (Point2D.Double) winCenter.clone();
    }

//    /** Snaps the directions onto coordinate axes if they're suitably close. */
//    public void snapDir() {
//        boolean t = snap(tDir);
//        boolean n = snap(nDir);
//        bDir = tDir.crossProduct(nDir);
//    }
//
//    double SNAP_FACTOR = 0.05;
//
//    boolean snap(P3D dir) {
//        boolean result = false;
//        if (Math.abs(dir.x) < SNAP_FACTOR) {
//            dir.x = 0;
//            result = true;
//        }
//        if (Math.abs(dir.y) < SNAP_FACTOR) {
//            dir.y = 0;
//            result = true;
//        }
//        if (Math.abs(dir.z) < SNAP_FACTOR) {
//            result = true;
//            if (dir.x == 0 && dir.y == 0) {
//                dir.z = 1;
//            } else {
//                dir.z = 0;
//                if (dir.x == 0) {
//                    dir.y = 1;
//                } else if (dir.y == 0) {
//                    dir.x = 1;
//                } else {
//                    dir.y = Math.sqrt(1 - dir.x * dir.x);
//                }
//            }
//        }
//        return result;
//    }

    public void computeTransformation() {
        bDir = tDir.crossProduct(nDir);
        camera = derivedPoint(center, -viewDist, tDir);
        screenCenter = derivedPoint(camera, screenDist, tDir);
        clipPoint = derivedPoint(camera, clipDist, tDir);
        winCenter = computeWinCenter(winBounds);
    }

    /** Converts a spacial coordinate to a window coordinate. */
    Point2D getWindowPointOf(Point3D coordinate) {
        Point3D cc = coordinate.minus(camera);
        double dc = cc.dotProduct(tDir);
        if (dc < clipDist) {
            // handle clipping... point should not be displayed
        }
        double factor = dpi * screenDist / dc;
        Point2D.Double result = getCenterClone();
        result.x += factor * cc.dotProduct(bDir);
        result.y -= factor * cc.dotProduct(nDir);
        return result;
    }

    /**
     * Converts a spacial coordinate into two window coordinates (for a left and right), where the cameras
     * are shifted by the specified amount.
     * 
     * @param coordinate the coordinate for projection
     */
    Point2D[] getDoubleWindowPointOf(Point3D coordinate) {
        Point3D cc = coordinate.minus(camera);
        double dc = cc.dotProduct(tDir);
        if (dc < clipDist) {
            // handle clipping... point should not be displayed
        }
        double factor = screenDist / dc;
        Point2D.Double[] result = new Point2D.Double[] { getCenterClone(), null };
        result[0].x += dpi * factor * cc.dotProduct(bDir);
        result[0].y -= dpi * factor * cc.dotProduct(nDir);
        result[1] = new Point2D.Double(result[0].x + camSep * dpi * (1-factor), result[0].y);
        result[0].x -= camSep * dpi * (1 - factor);
        return result;
    }

    /** Stores viewsccreen coordinates of last point request. */
    Point2D.Double vp;

    /**
     * Converts a window coordinate to a spacial coordinate. The coordinate
     * returned is the point on the viewing plane.
     * @param p a point in window pixel coordinates
     * @return point on the viewing plane
     */
    Point3D getCoordinateOf(Point2D p) {
        vp = new Point2D.Double(
                (p.getX() - winBounds.getWidth()/2) / dpi,
                (-p.getY() + winBounds.getHeight()/2) / dpi);
        return new Point3D(
                screenCenter.x + vp.x * bDir.x + vp.y * nDir.x,
                screenCenter.y + vp.x * bDir.y + vp.y * nDir.y,
                screenCenter.z + vp.x * bDir.z + vp.y * nDir.z
                );
    }

    /**
     * Converts a window coordinate vector into a spacial vector (perpendicular to the view direction).
     * Used to translate the camera.
     * @param x x-coordinate of vector
     * @param y y-coordinate of vector
     */
    Point3D getVectorOf(double x, double y) {
        double dx = x / dpi;
        double dy = -y / dpi;
        return new Point3D(
                dx * bDir.x + dy * nDir.x,
                dx * bDir.y + dy * nDir.y,
                dx * bDir.z + dy * nDir.z
                );
    }

    @Override
    public Object clone() {
	try {
            return super.clone();
	} catch (CloneNotSupportedException e) {
	    // this shouldn't happen, since we are Cloneable
	    throw new InternalError();
	}
    }

    //
    // SORTING & COMPARISON ALGORITHMS
    //

    /**
     * Returns z value of a point, i.e. tangential distance to camera.
     */
    public double getDistance(Point3D point) {
        return point.distanceSq(camera);
    }

    /**
     * Returns average distance from camera to a set of points
     */
    public double getAverageDist(Point3D[] points) {
        double sum = 0.0;
        for (int i = 0; i < points.length; i++) {
            sum += getDistance(points[i]);
        }
        return sum / points.length;
    }

    /**
     * Returns relative z order of two points. "Larger" points are closer to the camera.
     *
     * @param p1 first point
     * @param p2 second point
     * 
     * @return a negative integer, zero, or a positive integer as the first argument's z-value is less than, equal to, or greater than the second
     */
    public int compare(Point3D p1, Point3D p2) {
        if (p1 == p2) {
            return 0; // only return 0 with strict equality of pointers
        }
        double d1 = getDistance(p2);
        double d2 = getDistance(p2);
        if (d1 != d2) {
            return (int) Math.signum(d2 - d1);
        } else {
            if (p1.x != p2.x) {
                return (int) Math.signum (p2.x - p1.x);
            } else if (p2.y != p2.y) {
                return (int) Math.signum (p2.y - p1.y);
            } else if (p2.z != p2.z) {
                return (int) Math.signum (p2.z - p1.z);
            } else {
                return 1;
            }
        }
    }

    /**
     * Used to measure distances between polygons, using the average distance of coordinates from the camera plane.
     * "Larger" points are closer to the camera.
     *
     * @param o1 first set of points
     * @param o2 second set of points
     * 
     * @return a negative integer, zero, or a positive integer as the first argument's z-value is less than, equal to, or greater than the second
     */
    public Comparator<Point3D[]> getPolygonZOrderComparator() {
        return new Comparator<Point3D[]>() {
            public int compare(Point3D[] o1, Point3D[] o2) {
                double d1 = getAverageDist(o1);
                double d2 = getAverageDist(o2);
                if (d1 == d2) {
                    return SpaceProjection.this.compare(o1[0], o2[0]);
                } else {
                    return (int) Math.signum(d2 - d1);
                }
            }
        };
    }

}
