/*
 * SpaceVisometry.java
 * Created on Oct 18, 2009
 */

package visometry.space;

import coordinate.RealIntervalSamplerProvider;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.coordinate.Point3D;
import util.ChangeBroadcaster;
import util.DefaultChangeBroadcaster;
import visometry.Visometry;

/**
 * <p>
 *    This class describes the transformation between the view screen and a 3-dimensional
 *    scene.
 * </p>
 * @author Elisha Peterson
 */
public class SpaceVisometry implements Visometry<Point3D>,
        ChangeBroadcaster, ChangeListener {

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
    public SpaceProjection getProjection() {
        return proj;
    }

    /** Sets current spacial projection */
    public void setProjection(SpaceProjection proj) {
        this.proj = proj;
        changer.fireStateChanged();
    }

    /**
     * Translates the scene by specified amount, and also translates the projection
     * to keep the same point at the center of the screen.
     * @param oldProj the original projection
     * @param oldMin original minimum
     * @param oldMax original maximum
     * @param diff translation
     */
    public void translateScene(SpaceProjection oldProj, Point3D oldMin, Point3D oldMax, Point3D diff) {
        minPoint = oldMin.minus(diff);
        maxPoint = oldMax.minus(diff);
        proj.setCenter( oldProj.center.minus(diff) );
        changer.fireStateChanged();
    }

    /**
     * Zooms the camera by adjusting the pixels per 3d unit displayed.
     * @param factor the percentage to scale by
     */
    public void zoomDPI(double factor) {
        if (factor <= 0)
            throw new IllegalArgumentException("Zoom factor must be positive: " + factor);
        proj.setDpi( proj.getDpi()/factor );
        changer.fireStateChanged();
    }

    /**
     * Zooms the camera to or from the center of interest.
     * @param factor the percentage to scale by
     */
    public void zoomViewDistance(double factor) {
        if (factor <= 0)
            throw new IllegalArgumentException("Zoom factor must be positive: " + factor);
        proj.setViewDist(proj.getViewDist() * factor);
        changer.fireStateChanged();
    }

    /**
     * Zooms the image by adjusting both the screen distance, the view distance, and the clipping distance
     * by a common factor.
     * @param factor the percentage to scale by
     */
    public void zoomAll(double factor) {
        if (factor <= 0)
            throw new IllegalArgumentException("Zoom factor must be positive: " + factor);
        proj.screenDist *= factor;
        proj.clipDist *= factor;
        proj.viewDist *= factor;
        proj.useCenterCamera();
        changer.fireStateChanged();
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
        changer.fireStateChanged();
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
                    changer.fireStateChanged();
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
        changer.fireStateChanged();
    }

    public Point2D.Double getWindowPointOf(Point3D coordinate) {
        return proj.getWindowPointOf(coordinate);
    }

    public Point3D getCoordinateOf(Point2D point) {
        return proj.getCoordinateOf(point);
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
        if (!e.getSource().equals(this))
            changer.fireStateChanged();
    }

    protected DefaultChangeBroadcaster changer = new DefaultChangeBroadcaster(this);
    public void addChangeListener(ChangeListener l) { changer.addChangeListener(l); }
    public void removeChangeListener(ChangeListener l) { changer.removeChangeListener(l); }

    //
    // DOMAIN METHODS
    //

    RealIntervalSamplerProvider domain1, domain2, domain3;
//    PlaneDomain domain12, domain13, domain23;

    /** @return domain associated with the horizontal axis */
    public RealIntervalSamplerProvider getDomain1() {
        if (domain1 == null)
            domain1 = new RealIntervalSamplerProvider(this) {
                public double getNewMinimum() { return SpaceVisometry.this.minPoint.x; }
                public double getNewMaximum() { return SpaceVisometry.this.maxPoint.x; }
                public double getScale(float pixSpacing) { return Math.abs(pixSpacing / SpaceVisometry.this.proj.dpi); }
            };
        return domain1;
    }

    /** @return domain associated with the horizontal axis */
    public RealIntervalSamplerProvider getDomain2() {
        if (domain2 == null)
            domain2 = new RealIntervalSamplerProvider(this) {
                public double getNewMinimum() { return SpaceVisometry.this.minPoint.y; }
                public double getNewMaximum() { return SpaceVisometry.this.maxPoint.y; }
                public double getScale(float pixSpacing) { return Math.abs(pixSpacing / SpaceVisometry.this.proj.dpi); }
            };
        return domain2;
    }

    /** @return domain associated with the horizontal axis */
    public RealIntervalSamplerProvider getDomain3() {
        if (domain3 == null)
            domain3 = new RealIntervalSamplerProvider(this) {
                public double getNewMinimum() { return SpaceVisometry.this.minPoint.z; }
                public double getNewMaximum() { return SpaceVisometry.this.maxPoint.z; }
                public double getScale(float pixSpacing) { return Math.abs(pixSpacing / SpaceVisometry.this.proj.dpi); }
            };
        return domain3;
    }

}
