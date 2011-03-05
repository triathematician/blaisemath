/*
 * SpacePlotMouseHandler.java
 * Created on Aug 4, 2009
 */

package later.visometry.space;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import org.bm.blaise.scio.coordinate.Point3D;

/**
 * <p>
 *   <code>SpacePlotMouseHandler</code> handles the mouse behavior on the spacial visometry.
 * </p>
 * <p>
 *   Here are behaviors that alter the <b>CAMERA</b> without changing the underlying scene:
 *   <ul>
 *      <li><code>Drag</code>: rotate the plot, by translating the camera's location relative to the center of interest</li>
 *      <li><code>Ctrl-Drag</code>: animate rotation, based on the difference between the two mouse coordinates;
 *          does not rotate if the computed angle of rotation is too small.</li>
 *
 *      <li><code>MouseWheel</code>: change the 2D-scale of the view (does not change the 3D projection)</li>
 *      <li><code>Shift-MouseWheel</code>: change the focal length, e.g. move the object of interest to/from the camera</li>
 *   </ul>
 *   Here are behaviors that alter the <b>BOUNDING BOX</b> of the visometry, which determines the default domains
 *   and boundaries of the axes, etc:
 *   <ul>
 *      <li><code>Alt-Drag</code>: translates the center of interest by (i) shifting the boundaries of the default box,
 *          and (ii) shifting the camera. Both translations are taken in the plane perpendicular to the camera's angle.</li>
 *      <li><code>Alt-MouseWheel</code>: scale the scene's bounding box, and alter the camera's dpi accoridngly.</li>
 *   </ul>
 * </p>
 *
 * @author Elisha Peterson
 */
public class SpacePlotMouseHandler 
        implements MouseListener, MouseMotionListener, MouseWheelListener {

    SpacePlotComponent plot;
    SpaceVisometry vis;

    public SpacePlotMouseHandler(SpaceVisometry vis, SpacePlotComponent plot) {
        this.vis = vis;
        this.plot = plot;
    }

    //
    // MOUSE OPERATIONS
    //

    /** Press/release moves the plot window around */
    transient protected Point pressedAt = null;
    /** Press location in 3D */
    transient protected Point3D pressedAt3D = null;
    /** Stores keyboard modifiers for mouse. */
    transient protected String mode = null;
    /** Old projection */
    transient protected SpaceProjection oldProj = null;
    /** Old min, max pts of visometry. */
    transient protected Point3D oldMin = null, oldMax = null;

    public void mousePressed(MouseEvent e) {
        vis.stopRotation();
        pressedAt = e.getPoint();
        pressedAt3D = vis.getProjection().getCoordinateOf(pressedAt);
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        oldProj = (SpaceProjection) vis.getProjection().clone();
        oldMin = (Point3D) vis.minPoint.clone();
        oldMax = (Point3D) vis.maxPoint.clone();
    }

    public void mouseDragged(MouseEvent e) {
        Point cur = e.getPoint();
        if (pressedAt != null) {
            if (mode.equals("Alt+Button1")) // pan camera and scene
                vis.translateScene(oldProj, oldMin, oldMax,
                        vis.getProjection().getVectorOf( cur.x - pressedAt.x, cur.y - pressedAt.y));
            else // rotate mode
                vis.rotateCamera(oldProj, pressedAt3D, oldProj.getCoordinateOf(cur));
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (pressedAt != null && mode.equals("Ctrl+Button1")) // rotate mode
            vis.animateCameraRotation(oldProj, pressedAt3D, oldProj.getCoordinateOf(e.getPoint()));
        pressedAt = null;
        pressedAt3D = null;
        oldProj = null;
        mode = null;
    }

    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

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
