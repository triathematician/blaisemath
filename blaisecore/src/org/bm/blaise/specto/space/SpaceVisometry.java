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

/**
 * <p>
 *    This class describes the transformation between the view screen and a 3-dimensional
 *    scene.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceVisometry implements Visometry<Point3D> {

    //
    //
    // PROPERTIES
    //
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

    //
    //
    // BEAN PATTERNS
    //
    //

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
     * Zooms the camera by shifting it toward or away from the scene's center.
     * @param zoom the percentage to multiply the distance between camera and scene by (also scales other distances)
     */
    public void zoomCamera(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Zoom factor must be positive: " + factor);
        }
        proj.dpi /= factor;
        proj.computeTransformation();
        fireStateChanged();
    }

    /**
     * Adjusts the distance to the projection plane (and to the clipping plane)
     * @param zoom the percentage to multiply the distance between camera and scene by (also scales other distances)
     */
    public void zoomPlane(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Zoom factor must be positive: " + factor);
        }
        proj.screenDist *= factor;
        proj.clipDist *= factor;
        proj.viewDist *= factor;
        proj.computeTransformation();
        fireStateChanged();
    }

    /**
     * Rotates the camera about the center, with rotation specified by the provided vectors. Uses Euler's formula
     * @param oldProj the original projection
     * @param pt1 first point for rotation
     * @param pt2 second point for rotation
     * @return sinphi
     */
    public double rotateCamera(SpaceProjection oldProj, Point3D pt1, Point3D pt2) {
        double boost = 1;
        Point3D rotVec1 = pt1.minus(oldProj.center);
        Point3D rotVec2 = pt2.minus(oldProj.center);
        Point3D n = rotVec1.crossProduct(rotVec2).normalized();
        double cosphi = Math.cos(boost * Math.acos(rotVec1.dotProduct(rotVec2)/(rotVec1.magnitude()*rotVec2.magnitude())));
        double sinphi = Math.sqrt(1-cosphi*cosphi);
        // can use either single or double rotation here
        proj.tDir = eulerRotation(oldProj.getTDir(), n, cosphi, sinphi);
        proj.nDir = eulerRotation(oldProj.getNDir(), n, cosphi, sinphi);
        proj.bDir = eulerRotation(oldProj.getBDir(), n, cosphi, sinphi);
        // TESTING STILL -- proj.snapDir();
        proj.computeTransformation();
        fireStateChanged();
        return sinphi;
    }

    /** Formula for the rotation about a specific axis given by a normal vector n */
    public Point3D eulerRotation (Point3D pt, Point3D n, double cosphi, double sinphi) {
        return pt.times(cosphi)
                .plus( n.times(n.dotProduct(pt)*(1-cosphi)) )
                .plus( (pt.crossProduct(n)).times(sinphi) );
    }

    /** Animates a rotation. */
    public void animateCameraRotation(SpaceProjection oldProj, Point3D pt1, Point3D pt2) {
        double boost = 1;
        Point3D rotVec1 = pt1.minus(oldProj.center);
        Point3D rotVec2 = pt2.minus(oldProj.center);
        Point3D n = rotVec1.crossProduct(rotVec2).normalized();
        double cosphi = Math.cos(boost * Math.acos(rotVec1.dotProduct(rotVec2)/(rotVec1.magnitude()*rotVec2.magnitude())));
        double sinphi = Math.sqrt(1-cosphi*cosphi);
        // don't rotate if the angle is too small
        if (Math.abs(sinphi) > .01) {
            // lock rotation if within certain limits
            if (Math.abs(n.x)>.9) { n = new Point3D(n.x/Math.abs(n.x),0,0); }
            else if (Math.abs(n.y)>.9) { n = new Point3D(0,n.y/Math.abs(n.y),0); }
            else if (Math.abs(n.z)>.9) { n = new Point3D(0,0,n.z/Math.abs(n.z)); }
            animateRotation(oldProj, n, Math.sqrt(1-sinphi*sinphi), sinphi);
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
                    try{Thread.sleep(100);}catch(Exception e){}
                    proj.tDir = eulerRotation(proj.tDir, n, cosphi, sinphi);
                    proj.nDir = eulerRotation(proj.nDir, n, cosphi, sinphi);
                    proj.bDir = eulerRotation(proj.bDir, n, cosphi, sinphi);
                    proj.computeTransformation();
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
        proj.computeTransformation();
    }

    public Point2D getWindowPointOf(Point3D coordinate) {
        return proj.getWindowPointOf(coordinate);
    }

    public Point3D getCoordinateOf(Point2D point) {
        return proj.getCoordinateOf(point);
    }
    
    //
    //
    // EVENT HANDLING
    //
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
