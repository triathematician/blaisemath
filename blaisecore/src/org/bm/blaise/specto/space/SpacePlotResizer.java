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
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        oldProj = (SpaceProjection) vis.getProj().clone();
        if (mode.equals("Alt+Button1")) { // pan mode
        } else { // rotate mode
        }
    }

    public void mouseDragged(VisometryMouseEvent<Point3D> e) {
        if (pressedAt != null) {
            if (mode.equals("Alt+Button1")) { // pan mode
                Point3D diffC = vis.getProj().getVectorOf( e.getWindowPoint().x - pressedAt.x, e.getWindowPoint().y - pressedAt.y);
                vis.translateCamera(oldProj, diffC);
                plot.repaint();
            } else { // rotate mode
                vis.rotateCamera(oldProj, oldProj.getCoordinateOf(pressedAt), oldProj.getCoordinateOf(e.getWindowPoint()));
            }
        }
    }

    public void mouseReleased(VisometryMouseEvent<Point3D> e) {
        //mouseDragged(e);
        if (pressedAt != null && mode.equals("Alt+Button1")) { // pan mode
        } else if (pressedAt != null && mode.equals("Ctrl+Button1")) { // rotate mode
            vis.animateCameraRotation(oldProj, oldProj.getCoordinateOf(pressedAt), oldProj.getCoordinateOf(e.getWindowPoint()));
        }
        pressedAt = null;
        oldProj = null;
        mode = null;
    }

    public void mouseClicked(VisometryMouseEvent<Point3D> e) {
    }

    public void mouseEntered(VisometryMouseEvent<Point3D> e) {
    }

    public void mouseExited(VisometryMouseEvent<Point3D> e) {
    }

    public void mouseMoved(VisometryMouseEvent<Point3D> e) {
        if (MouseEvent.getModifiersExText(e.getModifiersEx()).equals("Alt")) { // pan mode
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        } else { // rotate mode (need better coursor!)
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        }
    }

    //
    //
    // ZOOM METHODS
    //
    //
    /** 
     * Zoom about the specified point when the mouse wheel is moved.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        if (mode.equals("Shift")) {
            vis.zoomPlane((e.getWheelRotation() > 0) ? 1.25 : 0.8);
        } else {
            vis.zoomCamera((e.getWheelRotation() > 0) ? 1.05 : 0.95);
        }
    }
}
