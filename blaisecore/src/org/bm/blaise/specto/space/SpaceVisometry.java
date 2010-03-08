/*
 * SpaceVisometry.java
 * Created on Oct 18, 2009
 */

package org.bm.blaise.specto.space;

import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bm.blaise.specto.visometry.Visometry;
import scio.coordinate.Point3D;
import scio.random.RandomCoordinateGenerator;

/**
 * <p>
 *    This class describes the transformation between the view screen and a 3-dimensional
 *    scene.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceVisometry implements Visometry<Point3D>, RandomCoordinateGenerator<Point3D> {

    //
    // PROPERTIES
    //

    /** Stores the boundary of the display window. */
    RectangularShape windowBounds;
    /** Stores the desired range of values. The class should ensure that this entire range is displayed. */
    Point3D minPoint = new Point3D(-1, -1, -1);
    /** Stores the desired range of values. The class should ensure that this entire range is displayed. */
    Point3D maxPoint = new Point3D(1, 1, 1);
    /** Stores the window's AspectRatio. First is xy, second is xz. */
    double[] aspectRatio = new double[]{1, 1};
    /** Stores the affine transformation that converts the actual range to the window bounds. */
    SpaceProjection proj = new SpaceProjection();

    /** Stores the delay step in animation speed (ms) */
    int animationDelay = 50;

    //
    // BEAN PATTERNS
    //

    /** @return animationDelay the sleep time between animation fires, in milliseconds */
    public int getAnimationDelay() {
        return animationDelay;
    }

    /** @param animationDelay the sleep time between animation fires, in milliseconds */
    public void setAnimationDelay(int animationDelay) {
        this.animationDelay = animationDelay;
    }

    /** Returns current space projection */
    public SpaceProjection getProj() {
        return proj;
    }

    /** Sets current spacial projection */
    public void setProj(SpaceProjection proj) {
        this.proj = proj;
        fireStateChanged();
    }

    /**
     * Translates the camera from a fixed starting location.
     * @param oldProj the original projection
     * @param diff translation
     */
    public void translateCamera(SpaceProjection oldProj, Point3D diff) {
        proj.setCenter( oldProj.center.minus(diff) );
        fireStateChanged();
    }

    /**
     * Zooms the camera by adjusting the pixels per 3d unit displayed.
     * @param zoom the percentage to scale by
     */
    public void zoomDPI(double factor) {
        if (factor <= 0)
            throw new IllegalArgumentException("Zoom factor must be positive: " + factor);
        proj.setDpi( proj.getDpi()/factor );
        fireStateChanged();
    }

    /**
     * Zooms the camera to or from the center of interest.
     * @param zoom the percentage to scale by
     */
    public void zoomViewDistance(double factor) {
        if (factor <= 0)
            throw new IllegalArgumentException("Zoom factor must be positive: " + factor);
        proj.setViewDist(proj.getViewDist() * factor);
        fireStateChanged();
    }

    /**
     * Zooms the image by adjusting both the screen distance, the view distance, and the clipping distance
     * by a common factor.
     * @param zoom the percentage to scale by
     */
    public void zoomAll(double factor) {
        if (factor <= 0)
            throw new IllegalArgumentException("Zoom factor must be positive: " + factor);
        proj.screenDist *= factor;
        proj.clipDist *= factor;
        proj.viewDist *= factor;
        proj.useCenterCamera();
        fireStateChanged();
    }

    /**
     * @param axis pointer to value that will store the axis of rotation
     * @return { cosphi, sinphi } to describe a 3D rotation
     */
    double[] getRotation(SpaceProjection oldProj, Point3D pt1, Point3D pt2, Point3D axis) {
        Point3D rotVec1 = pt1.minus(oldProj.camera);
        Point3D rotVec2 = pt2.minus(oldProj.camera);
        Point3D tAxis = rotVec1.crossProduct(rotVec2).normalized();
        axis.x = tAxis.x;
        axis.y = tAxis.y;
        axis.z = tAxis.z;
        double cosphi = rotVec1.dotProduct(rotVec2) / (rotVec1.magnitude() * rotVec2.magnitude());
        return new double[] {
            cosphi,
            Math.sqrt(1-cosphi*cosphi)
        };
    }

    /**
     * Formula for the rotation about a specific axis given by a normal vector n
     * @param p the vector/point before rotation
     * @return the vector/point after rotation
     */
    public Point3D eulerRotation (Point3D p, Point3D axis, double cosphi, double sinphi) {
        return p.times(cosphi)
                .plus( axis.times(axis.dotProduct(p)*(1-cosphi)) )
                .plus( (p.crossProduct(axis)).times(sinphi) );
    }

    /**
     * Rotates the camera about the center, with rotation specified by the provided vectors. Uses Euler's formula
     * @param oldProj the original projection
     * @param pt1 first point for rotation
     * @param pt2 second point for rotation
     */
    public void rotateCamera(SpaceProjection oldProj, Point3D pt1, Point3D pt2) {
        Point3D axis = new Point3D();
        double[] angles = getRotation(oldProj, pt1, pt2, axis);
        // can use either single or double rotation here
        proj.tDir = eulerRotation(eulerRotation(oldProj.getTDir(), axis, angles[0], angles[1]), axis, angles[0], angles[1]);
        proj.nDir = eulerRotation(eulerRotation(oldProj.getNDir(), axis, angles[0], angles[1]), axis, angles[0], angles[1]);
        proj.bDir = eulerRotation(eulerRotation(oldProj.getBDir(), axis, angles[0], angles[1]), axis, angles[0], angles[1]);
        proj.useCenterCamera();
        fireStateChanged();
    }

    private static final double ROTATE_THRESHOLD = .01;
    private static final double SNAP_THRESHOLD = .9;

    /** Animates a rotation. */
    public void animateCameraRotation(SpaceProjection oldProj, Point3D pt1, Point3D pt2) {
        Point3D axis = new Point3D();
        double[] angles = getRotation(oldProj, pt1, pt2, axis);
        // don't rotate if the angle is too small
        if (Math.abs(angles[1]) > ROTATE_THRESHOLD) {
            // lock rotation if within certain limits
            if (Math.abs(axis.x) > SNAP_THRESHOLD)
                axis = new Point3D(Math.signum(axis.x), 0, 0);
            else if (Math.abs(axis.y) > SNAP_THRESHOLD)
                axis = new Point3D(0, Math.signum(axis.y), 0);
            else if (Math.abs(axis.z) > SNAP_THRESHOLD)
                axis = new Point3D(0, 0, Math.signum(axis.z));
            animateRotation(oldProj, axis, angles[0], angles[1]);
        }
    }

    transient boolean rotating = false;

    /** Stops a rotation. */
    public void stopRotation() {
        rotating = false;
    }

    /** Animates a rotation. */
    public void animateRotation(final SpaceProjection oldProj, final Point3D n, final double cosphi, final double sinphi) {
        rotating = true;
        Thread runner=new Thread(new Runnable(){
            public void run() {
                while(rotating){
                    try{Thread.sleep(animationDelay);}catch(Exception e){}
                    proj.tDir = eulerRotation(proj.tDir, n, cosphi, sinphi);
                    proj.nDir = eulerRotation(proj.nDir, n, cosphi, sinphi);
                    proj.bDir = eulerRotation(proj.bDir, n, cosphi, sinphi);
                    proj.useCenterCamera();
                    fireStateChanged();
                }
            }
        });
        runner.start();
    }


    /** @return bounding rectangle provided by the window. */
    public RectangularShape getWindowBounds() {
        return windowBounds;
    }

    /**
     * Sets window bounds.
     * Recomputes transformation after setting.
     */
    public void setWindowBounds(RectangularShape newBounds) {
        if (newBounds == null) {
            throw new NullPointerException();
        }
        if (!newBounds.equals(windowBounds)) {
            this.windowBounds = newBounds;
        }
        try {
            computeTransformation();
        } catch (IllegalStateException e) {
        }
        fireStateChanged();
    }

    public Point3D getMinPointVisible() {
        return minPoint;
    }

    public Point3D getMaxPointVisible() {
        return maxPoint;
    }

    public void computeTransformation() throws IllegalStateException {
        proj.winBounds = windowBounds;
        proj.center = new Point3D((minPoint.x + maxPoint.x)/2, (minPoint.y + maxPoint.y)/2, (minPoint.z + maxPoint.z)/2);
        proj.useCenterCamera();
    }

    public Point2D getWindowPointOf(Point3D coordinate) {
        return proj.getWindowPointOf(coordinate);
    }

    public Point3D getCoordinateOf(Point2D point) {
        return proj.getCoordinateOf(point);
    }

    //
    // SAMPLING ALGORITHMS
    //

    public Point3D randomValue() {
        return new Point3D(
                minPoint.x + Math.random() * (maxPoint.x - minPoint.x),
                minPoint.y + Math.random() * (maxPoint.y - minPoint.y),
                minPoint.z + Math.random() * (maxPoint.z - minPoint.z));
    }
    
    //
    // EVENT HANDLING
    //

    /**
     * Handles a change event. By default, passes the ChangeEvent along
     * to interested listeners (particularly the parent class), provided this class
     * itself did not originate the event.
     * @param e the change event
     */
    public void stateChanged(ChangeEvent e) {
        if (!e.getSource().equals(this)) {
            changeEvent = e;
            fireStateChanged();
        }
    }

    // Event handling code copied from DefaultBoundedRangeModel.
    protected ChangeEvent changeEvent = new ChangeEvent(this);
    protected EventListenerList listenerList = new EventListenerList();

    /**
     * Removes a listener from the list of classes receiving <code>ChangeEvent</code>s
     * @param l the listener
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    /**
     * Adds a listener to receive <code>ChangeEvent</code>s
     * @param l the listener
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    /**
     * Fires the change event to listeners.
     */
    protected void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    return;
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

}
