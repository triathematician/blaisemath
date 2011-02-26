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
public class SpaceProjection
        implements Cloneable, Comparator<Point3D> {

    //
    // CAMERA SETTINGS
    //

    /** Point of interest. */
    Point3D center = new Point3D(0, 0, 0);

    /** Camera angle ("front" of camera). */
    Point3D tDir = new Point3D(-3, -3, -1).normalized();
    
    /** Camera normal vector ("top" of camera). */
    Point3D nDir = new Point3D(-1, -1, 6).normalized();

    /** Total distance from camera to center of interest. */
    double viewDist = 25.0;

    /** Distance from camera to view plane (in cm) */
    double screenDist = 25.0;

    /** Distance from camera to clipping plane (in cm) */
    double clipDist = 2;
    
    /** Distance between left and right cameras */
    double camSep = 0.5;
    
    //
    // COMPUTED VALUES
    //

    /** Camera location. */
    transient Point3D camera;

    /** Camera binormal ("side" of camera). */
    transient Point3D bDir;

    /** Center of screen */
    transient Point3D screenCenter;

    /** Point representing the clipping plane. */
    transient Point3D clipPoint;

    //
    // WINDOW PARAMETERS
    //

    /** Window bounding box. */
    RectangularShape winBounds;

    /** Center of window, in window coordinates. */
    transient Point2D.Double winCenter;

    /** Pixels per unit */
    double dpi = 100.0;


    //
    // BEAN PATTERNS
    //

    public Point3D getCenter() {
        return center;
    }

    public void setCenter(Point3D center) {
        this.center = center;
        useCenterCamera();
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
        useCenterCamera();
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
        useCenterCamera();
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
        useCenterCamera();
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
        useCenterCamera();
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
        useCenterCamera();
    }

    public RectangularShape getWinBounds() {
        System.out.println("getWinBounds: " + winBounds);
        return winBounds;
    }

    public void setWinBounds(RectangularShape winBounds) {
        this.winBounds = winBounds;
        useCenterCamera();
    }

    public double getDpi() {
        return dpi;
    }

    public void setDpi(double dpi) {
        this.dpi = dpi;
        useCenterCamera();
    }

    //
    // CAMERA SWITCHING
    //

    public void useLeftCamera() {
        bDir = tDir.crossProduct(nDir);
        Point3D centerCam = pointInRelativeDirection(center, -viewDist, tDir);
        camera = pointInRelativeDirection(centerCam, -camSep/2, bDir);
        screenCenter = pointInRelativeDirection(camera, screenDist, tDir);
        clipPoint = pointInRelativeDirection(camera, clipDist, tDir);
        winCenter = centerOf(winBounds);
        winCenter.x -= camSep/2 * dpi;
    }

    public void useRightCamera() {
        bDir = tDir.crossProduct(nDir);
        Point3D centerCam = pointInRelativeDirection(center, -viewDist, tDir);
        camera = pointInRelativeDirection(centerCam, camSep/2, bDir);
        screenCenter = pointInRelativeDirection(camera, screenDist, tDir);
        clipPoint = pointInRelativeDirection(camera, clipDist, tDir);
        winCenter = centerOf(winBounds);
        winCenter.x += camSep/2 * dpi;
    }

    public void useCenterCamera() {
        bDir = tDir.crossProduct(nDir);
        camera = pointInRelativeDirection(center, -viewDist, tDir);
        screenCenter = pointInRelativeDirection(camera, screenDist, tDir);
        clipPoint = pointInRelativeDirection(camera, clipDist, tDir);
        winCenter = centerOf(winBounds);
    }
    

    //
    // STATIC UTILITY COMPUTATIONS
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
    static Point3D pointInRelativeDirection(Point3D start, double dist, Point3D dir) {
        return new Point3D(
                start.x + dist * dir.x,
                start.y + dist * dir.y,
                start.z + dist * dir.z );
    }

    static Point2D.Double centerOf(RectangularShape rr) {
        return new Point2D.Double(
                rr.getMinX() + rr.getWidth() / 2,
                rr.getMinY() + rr.getHeight() / 2 );
    }



    //
    // PROJECTION METHODS
    //

    /** Converts a spacial coordinate to a window coordinate. */
    public Point2D.Double getWindowPointOf(Point3D coordinate) {
        Point3D cc = coordinate.minus(camera);
        double dc = cc.dotProduct(tDir);
        double factor = dpi * screenDist / dc;
        return new Point2D.Double(
                winCenter.x + factor * cc.dotProduct(bDir),
                winCenter.y - factor * cc.dotProduct(nDir));
    }

    /** Stores viewsccreen coordinates of last point request. */
    Point2D.Double vp;

    /**
     * Converts a window coordinate to a spacial coordinate. The coordinate
     * returned is the point on the viewing plane.
     * @param p a point in window pixel coordinates
     * @return point on the viewing plane
     */
    public Point3D getCoordinateOf(Point2D p) {
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
   public Point3D getVectorOf(double x, double y) {
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
     * Returns average distance from camera to a set of points
     */
    public double getAverageDist(Point3D[] points) {
        double sum = 0.0;
        for (int i = 0; i < points.length; i++)
            sum += points[i].distance(camera);
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
        if (p1 == p2)
            return 0; // only return 0 with strict equality of pointers
        double d1 = p1.distance(camera);
        double d2 = p2.distance(camera);
        if (d1 != d2)
            return (int) Math.signum(d2 - d1);
        else {
            if (p1.x != p2.x)
                return (int) Math.signum (p2.x - p1.x);
            else if (p1.y != p2.y)
                return (int) Math.signum (p2.y - p1.y);
            else if (p1.z != p2.z)
                return (int) Math.signum (p2.z - p1.z);
            else
                return 1;
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
                if (d1 == d2)
                    return SpaceProjection.this.compare(o1[0], o2[0]);
                else
                    return (int) Math.signum(d2 - d1);
            }
        };
    }

}
