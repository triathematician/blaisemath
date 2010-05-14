/*
 * LinePlotMouseHandler.java
 * Created Sep 19, 2009
 */

package visometry.line;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import primitive.style.PathStyle;
import primitive.style.ShapeStyle;
import visometry.PaintsCanvas;

/**
 * <p>
 *  This class handles default mouse behavior for a line plot.
 * </p>
 * <p>
 *   Supported behavior is as follows:
 *   <ul>
 *     <li> Drag: mouse drags the line back and forth
 *     <li> Shift-Drag: allows user to move the endpoints of the axis interval around the screen
 *     <li> Alt-Drag: creates a zoom interval
 *     <li> Mouse wheel: zooms in and out
 *   </ul>
 * </p>
 *
 * @author Elisha Peterson
 */
public class LinePlotMouseHandler
        implements MouseListener, MouseMotionListener, MouseWheelListener, PaintsCanvas {

    LinePlotComponent plot;
    LineVisometry vis;

    /** Width of zooming box, on either side of center */
    int ZOOM_WIDTH = 10;
    /** Determines whether to snap to the coordinate axes. */
    boolean SNAP_ENABLED = true;

    LinePlotMouseHandler(LineVisometry vis, LinePlotComponent plot) {
        this.vis = vis;
        this.plot = plot;
    }

    /** Stores vertices of zoom box. */
    transient double[][] cZoom;
    /** Style of zoom box */
    final static ShapeStyle zoomBoxStyle = new ShapeStyle(new PathStyle(new Color(255, 128, 128, 128), 2f), new Color(255, 216, 216, 128));

    public void paint(Graphics2D canvas) {
        if (cZoom != null) {
            GeneralPath gp = new GeneralPath();
            gp.moveTo((float) cZoom[0][0], (float) cZoom[0][1]);
            gp.lineTo((float) cZoom[1][0], (float) cZoom[1][1]);
            gp.lineTo((float) cZoom[2][0], (float) cZoom[2][1]);
            gp.lineTo((float) cZoom[3][0], (float) cZoom[3][1]);
            gp.closePath();
            canvas.setColor(zoomBoxStyle.getFillColor());
            canvas.fill(gp);
            
            zoomBoxStyle.draw(canvas, new Line2D.Double(cZoom[0][0], cZoom[0][1], cZoom[1][0], cZoom[1][1]) );
            if (cZoom[2] != null)
                zoomBoxStyle.draw(canvas, new Line2D.Double(cZoom[2][0], cZoom[2][1], cZoom[3][0], cZoom[3][1]) );
        }
    }

    //
    // MOUSE OPERATIONS
    //

    /** Location mouse was first pressed at. */
    transient protected Point pressedAt = null;
    /** Stores keyboard modifiers for mouse. */
    transient protected String mode = null;
    /** Old bounds for the window. */
    transient protected Double oldMin = null, oldMax = null;
    /** New bounds for the window. */
    transient protected Double newMin = null, newMax = null;

    /** Whether dragging moves axis position or just shifts it back and forth */
    transient boolean dragMin = false, dragMax = false;

    /**
     * When the mouse is pressed, prepare for resizing or panning.
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        pressedAt = e.getPoint();
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        oldMin = vis.min;
        oldMax = vis.max;
        
        if (mode.equals("Alt+Button1")) { // interval resize mode
            Point2D.Double pt = vis.getWindowPointOf(vis.getCoordinateOf(pressedAt));
            cZoom = new double[4][2];
            cZoom[0][0] = pt.x + ZOOM_WIDTH * vis.perpDir.x;
            cZoom[0][1] = pt.y + ZOOM_WIDTH * vis.perpDir.y;
            cZoom[1][0] = pt.x - ZOOM_WIDTH * vis.perpDir.x;
            cZoom[1][1] = pt.y - ZOOM_WIDTH * vis.perpDir.y;
        } else {
            if (pressedAt.distance(vis.minPoint) < 5) {
                dragMin = true;
                dragMax = false;
            } else if (pressedAt.distance(vis.maxPoint) < 5) {
                dragMin = false;
                dragMax = true;
            } else {
                dragMin = false;
                dragMax = false;
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        Double coord = vis.getCoordinateOf(e.getPoint());
        Point2D.Double pt = vis.getWindowPointOf(coord);
        if (pressedAt != null) {
            if (mode.equals("Alt+Button1")) { // interval resize mode
                cZoom[2][0] = pt.x - ZOOM_WIDTH * vis.perpDir.x;
                cZoom[2][1] = pt.y - ZOOM_WIDTH * vis.perpDir.y;
                cZoom[3][0] = pt.x + ZOOM_WIDTH * vis.perpDir.x;
                cZoom[3][1] = pt.y + ZOOM_WIDTH * vis.perpDir.y;
                plot.repaint();
            } else if (dragMin) { // move min pt
                vis.setMinPoint(e.getPoint());
            } else if (dragMax) { // move max pt
                vis.setMaxPoint(e.getPoint());
            } else { // pan mode
                double diff = vis.getCoordinateOf(pressedAt) - coord;
                vis.setDesiredRange(newMin = oldMin + diff, newMax = oldMax + diff);
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
        mouseDragged(e);
        if (pressedAt != null && mode.equals("Alt+Button1")) { // interval resize mode
            double c1 = vis.getCoordinateOf(pressedAt);
            double c2 = vis.getCoordinateOf(e.getPoint());
            zoomBoxAnimated( Math.min(c1, c2), Math.max(c1, c2) );
            cZoom = null;
        }
        pressedAt = null;
        mode = null;
    }

    public void mouseMoved(MouseEvent e) {
        if (MouseEvent.getModifiersExText(e.getModifiersEx()).equals("Alt"))
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        else if ( e.getPoint().distance(vis.minPoint) < 5 )
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else if ( e.getPoint().distance(vis.maxPoint) < 5 )
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        else
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        Point2D.Double mouseLoc = new Point2D.Double(e.getPoint().x, e.getPoint().y);

        // ensure the point is within the window
        RectangularShape bounds = vis.getWindowBounds();
        mouseLoc.x = Math.max(mouseLoc.x, bounds.getMinX());
        mouseLoc.x = Math.min(mouseLoc.x, bounds.getMaxX());
        mouseLoc.y = Math.max(mouseLoc.y, bounds.getMinY());
        mouseLoc.y = Math.min(mouseLoc.y, bounds.getMaxY());

        zoomPoint(vis.getCoordinateOf(mouseLoc),
                (e.getWheelRotation() > 0) ? 1.05 : 0.95);
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    //
    // ZOOM & ANIMATION METHODS
    //

    private static final int ZOOM_STEPS = 100;

    /** Sets visometry bounds based on the zoom about a given point. */
    private void zoomPoint(Double cPoint, double factor) {
        /** effective zoom point is between current center and mouse position...
         * close to center => 100% at the given point, close to edge => 10% at the given point. */
        double cx = .1 * cPoint + .45 * (vis.min + vis.max);
        double wx = vis.max - vis.min;
        vis.setDesiredRange(cx - factor * wx / 2.0, cx + factor * wx / 2.0);
    }

    /** Zooms line to new boundaries. */
    private void zoomBoxAnimated(final Double newMin, final Double newMax) {
        if (newMin == null || newMax == null || oldMin == null || oldMax == null)
            throw new IllegalStateException();

        Thread runner = new Thread(new Runnable() {
            public void run() {
                for (double factor = 0; factor < 1.0; factor += 1.0 / ZOOM_STEPS) {
                    try { Thread.sleep(2); } catch (Exception e) { } // wait a bit
                    vis.setDesiredRange(oldMin + (newMin - oldMin) * factor, oldMax + (newMax - oldMax) * factor);
                    plot.repaint();
                }
            }
        });
        runner.start();
    }
}
