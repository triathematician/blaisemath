/**
 * SpacePlotResizer.java
 * Created on Oct 21, 2009
 */
package org.bm.blaise.specto.space;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;
import org.bm.blaise.specto.visometry.VisometryMouseInputListener;
import scio.coordinate.Point3D;

/**
 * <p>
 *   <code>PlanePlotResizer</code> handles the mouse behavior on the spacial visometry.
 * </p>
 * <p>
 *   Here are behaviors that alter the direction/position of the camera, without changing its location
 *   <ul>
 *      <li><code>Drag</code>: rotate the plot</li>
 *      <li><code>Ctrl-Drag</code>: animate rotation, based on the difference between the two mouse coordinates;
 *          does not rotate if the computed angle of rotation is too small.</li>
 *      <li><code>Alt-Drag</code>: translate the center of the interest in directions perpendicular to the view direction</li>
 *      <li><code>Alt-MouseWheel</code>: moves the camera to or from the center of interest
 *   </ul>
 *   Here are the zoom behaviors:
 *   <ul>
 *      <li><code>MouseWheel</code>: change the 2D-scale of the view (does not change the 3D projection)</li>
 *      <li><code>Shift-MouseWheel</code>: change the focal length, e.g. move the object of interest to/from the camera</li>
 *   </ul>
 * </p>
 *
 * @author Elisha Peterson
 */
public class SpacePlotResizer implements VisometryMouseInputListener<Point3D>, MouseWheelListener {

    SpacePlotComponent plot;
    SpaceVisometry vis;

    public SpacePlotResizer(SpaceVisometry vis, SpacePlotComponent plot) {
        this.vis = vis;
        this.plot = plot;
    }
    /** Press/release moves the plot window around */
    transient protected Point pressedAt = null;
    transient protected Point3D pressedAt3D = null;
    transient protected String mode = null;
    transient protected SpaceProjection oldProj = null;

    public boolean isClickablyCloseTo(VisometryMouseEvent<Point3D> e) {
        return true;
    }

    /**
     * When the mouse is pressed, prepare for resizing or panning.
     * @param e
     */
    public void mousePressed(VisometryMouseEvent<Point3D> e) {
        vis.stopRotation();
        pressedAt = e.getWindowPoint();
        pressedAt3D = vis.getProj().getCoordinateOf(pressedAt);
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        oldProj = (SpaceProjection) vis.getProj().clone();
    }

    public void mouseDragged(VisometryMouseEvent<Point3D> e) {
        if (pressedAt != null) {
            if (mode.equals("Alt+Button1")) // pan mode
                vis.translateCamera(oldProj, 
                        vis.getProj().getVectorOf( e.getWindowPoint().x - pressedAt.x, e.getWindowPoint().y - pressedAt.y));
            else // rotate mode
                vis.rotateCamera(oldProj, pressedAt3D, oldProj.getCoordinateOf(e.getWindowPoint()));
        }
    }

    public void mouseReleased(VisometryMouseEvent<Point3D> e) {
        if (pressedAt != null && mode.equals("Ctrl+Button1")) // rotate mode
            vis.animateCameraRotation(oldProj, pressedAt3D, oldProj.getCoordinateOf(e.getWindowPoint()));
        pressedAt = null;
        pressedAt3D = null;
        oldProj = null;
        mode = null;
    }

    public void mouseMoved(VisometryMouseEvent<Point3D> e) {
        if (MouseEvent.getModifiersExText(e.getModifiersEx()).equals("Alt")) // pan mode
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        else // rotate mode (need better coursor!)
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
    }

    public void mouseClicked(VisometryMouseEvent<Point3D> e) {}
    public void mouseEntered(VisometryMouseEvent<Point3D> e) {}
    public void mouseExited(VisometryMouseEvent<Point3D> e) {}

    //
    // ZOOM METHODS
    //

    /** 
     * Zoom about the specified point when the mouse wheel is moved.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        if (mode.equals("Shift"))
            vis.zoomAll((e.getWheelRotation() > 0) ? 1.1 : 0.9);
        else if (mode.equals("Alt"))
            vis.zoomViewDistance((e.getWheelRotation() > 0) ? 1.1 : 0.9);
        else // default
            vis.zoomDPI((e.getWheelRotation() > 0) ? 1.05 : 0.95);
    }
}
