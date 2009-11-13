/**
 * PlanePlotResizer.java
 * Created on Aug 4, 2009
 */
package org.bm.blaise.specto.line;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;
import org.bm.blaise.specto.visometry.VisometryMouseInputListener;

/**
 * <p>
 *   <code>PlanePlotResizer</code> handles the mouse behavior on the planar visometry, when there is no plottable
 *   to handle the events.
 * </p>
 *
 * @author Elisha Peterson
 */
public class LinePlotResizer implements VisometryMouseInputListener<Double>, MouseWheelListener {

    LinePlotComponent plot;
    LineVisometry vis;

    public LinePlotResizer(LineVisometry vis, LinePlotComponent plot) {
        this.vis = vis;
        this.plot = plot;
    }
    
    /** Press/release moves the plot window around */
    protected Point pressedAt = null;
    protected String mode = null;
    protected double oldMin;
    protected double oldMax;


    public boolean isClickablyCloseTo(VisometryMouseEvent<Double> e) {
        return true;
    }

    /**
     * When the mouse is pressed, prepare for resizing or panning.
     * @param e
     */
    public void mousePressed(VisometryMouseEvent<Double> e) {
        pressedAt = e.getWindowPoint();
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        if (mode.equals("Alt+Button1")) { // rectangle resize mode
            plot.overlayBox = new Rectangle2D.Double(pressedAt.x, pressedAt.y, 0, 0);
        } else { // pan mode
            oldMin = vis.getMinPointVisible();
            oldMax = vis.getMaxPointVisible();
        }
    }

    public void mouseDragged(VisometryMouseEvent<Double> e) {
        if (pressedAt != null) {
            if (mode.equals("Alt+Button1")) { // rectangle resize mode (ensure positive values)
                if (e.getWindowPoint().x < pressedAt.x) {
                    plot.overlayBox.x = e.getWindowPoint().x;
                    plot.overlayBox.width = - e.getWindowPoint().x + pressedAt.x;
                } else {
                    plot.overlayBox.x = pressedAt.x;
                    plot.overlayBox.width = e.getWindowPoint().x - pressedAt.x;
                }
                if (e.getWindowPoint().y < pressedAt.y) {
                    plot.overlayBox.y = e.getWindowPoint().y;
                    plot.overlayBox.height = - e.getWindowPoint().y + pressedAt.y;
                } else {
                    plot.overlayBox.y = pressedAt.y;
                    plot.overlayBox.height = e.getWindowPoint().y - pressedAt.y;
                }
                plot.repaint();
            } else { // pan mode
                Double newC = vis.getCoordinateOf(pressedAt);
                Double oldC = vis.getCoordinateOf(e.getWindowPoint());
                vis.setDesiredRange(oldMin + newC - oldC, oldMax + newC - oldC);
            }
        }
    }

    public void mouseReleased(VisometryMouseEvent<Double> e) {
        mouseDragged(e);
        if (pressedAt != null && mode.equals("Alt+Button1")) { // rectangle resize mode
            zoomBoxAnimated(plot.overlayBox);
            plot.overlayBox = null;
        }
        pressedAt = null;
        oldMin = -1;
        oldMax = -1;
        mode = null;
    }

    public void mouseClicked(VisometryMouseEvent<Double> e) {
    }

    public void mouseEntered(VisometryMouseEvent<Double> e) {
    }

    public void mouseExited(VisometryMouseEvent<Double> e) {
    }

    public void mouseMoved(VisometryMouseEvent<Double> e) {
        if (MouseEvent.getModifiersExText(e.getModifiersEx()).equals("Alt")) {
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        } else {
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
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

    /** 
     * Sets visometry bounds based on the zoom about a given point.
     */
    public void zoomPoint(Double cPoint, double factor) {
        /** effective zoom point is between current center and mouse position...
         * close to center => 100% at the given point, close to edge => 10% at the given point. */
        double cx = .1 * cPoint + .45 * (vis.getMinPointVisible() + vis.getMaxPointVisible());
        double wx = vis.getMaxPointVisible() - vis.getMinPointVisible();
        vis.setDesiredRange(cx - factor * wx / 2.0, cx + factor * wx / 2.0);
    }

    public void zoomCenter(double factor) {
        Double center = 0.5 * (vis.getMinPointVisible() + vis.getMaxPointVisible());
        zoomPointAnimated(center, factor);
    }

    private static final int ZOOM_STEPS = 100;

    /** Creates an animating zoom using a particular timer. */
    public void zoomPointAnimated(Double p, final double factor) {
        final double cx = .1 * p + .45 * (vis.getMinPointVisible() + vis.getMaxPointVisible());
        final double xMultiplier = vis.getWindowBounds().getWidth() / 2.0;

        Thread runner = new Thread(new Runnable() {
            public void run() {
                double zoomValue = 0.0;
                for (int i = 0; i <= ZOOM_STEPS; i++) {
                    try { Thread.sleep(1); } catch (Exception e) { } // wait a bit
                    zoomValue = 1.0 + (factor - 1.0) / ZOOM_STEPS;
                    vis.setDesiredRange(cx - zoomValue * xMultiplier, cx + zoomValue * xMultiplier);
                }
            }
        });
        runner.start();
    }

    /** 
     * Zooms to the boundaries of a particular box.
     * @param winBoundary the boundary of the zoom box (in window coordinates)
     */
    public void zoomBoxAnimated(Rectangle2D winBoundary) {
        final Double min = vis.getMinPointVisible();
        final Double max = vis.getMaxPointVisible();
        final Double newMin = vis.getCoordinateOf(new Point2D.Double(winBoundary.getMinX(), winBoundary.getMinY()));
        final Double newMax = vis.getCoordinateOf(new Point2D.Double(winBoundary.getMaxX(), winBoundary.getMaxY()));

        Thread runner = new Thread(new Runnable() {
            public void run() {
                for (double factor = 0; factor < 1.0; factor += 1.0 / ZOOM_STEPS) {
                    try { Thread.sleep(2); } catch (Exception e) { } // wait a bit
                    vis.setDesiredRange(min + (newMin - min) * factor, max + (newMax - max) * factor);
                    plot.repaint();
                }
            }
        });
        runner.start();
    }
}
