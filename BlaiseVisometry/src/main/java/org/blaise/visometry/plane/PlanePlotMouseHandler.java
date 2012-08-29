/*
 * PlanePlotMouseHandler.java
 * Created on Aug 4, 2009
 */

package org.blaise.visometry.plane;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import org.blaise.style.BasicShapeStyle;
import org.blaise.style.VisibilityHint;

/**
 * <p>
 *  This class handles default mouse behavior for a plane plot.
 * </p>
 * <p>
 *   Supported behavior is as follows:
 *   <ul>
 *     <li> Drag: mouse drags the canvas around
 *     <li> Shift-Drag: force dragging to keep x or y constant
 *     <li> Alt-Drag: creates a zoom box
 *     <li> Mouse wheel: zooms in and out
 *   </ul>
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlanePlotMouseHandler 
        implements MouseListener, MouseMotionListener, MouseWheelListener {

    PlanePlotComponent plot;
    PlaneVisometry vis;

    /** Determines whether to snap to the coordinate axes. */
    boolean SNAP_ENABLED = true;

    public PlanePlotMouseHandler(PlaneVisometry vis, PlanePlotComponent plot) {
        this.vis = vis;
        this.plot = plot;
    }

    /** Hint box for zooming */
    transient Rectangle2D.Double zoomBox;
    /** Renderer for zoom box */
    final static BasicShapeStyle rend = new BasicShapeStyle(new Color(255, 128, 128, 128), new Color(255, 196, 196, 128));

    public void paint(Graphics2D canvas) {
        if (zoomBox != null)
            rend.draw(zoomBox, canvas, null);
    }

    //
    // MOUSE OPERATIONS
    //
    
    /** Location mouse was first pressed at. */
    transient protected Point pressedAt = null;
    /** Stores keyboard modifiers for mouse. */
    transient protected String mode = null;
    /** Old bounds for the window. */
    transient protected Point2D.Double oldMin = null;
    /** Old bounds for the window. */
    transient protected Point2D.Double oldMax = null;

    public void mousePressed(MouseEvent e) {
        if (e.isConsumed()) { 
            return;
        }
        pressedAt = e.getPoint();
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        if (mode.equals("Alt+Button1")) { // rectangle resize mode
            zoomBox = new Rectangle2D.Double(pressedAt.x, pressedAt.y, 0, 0);
        } else { // pan mode
            oldMin = (Point2D.Double) vis.desiredMin.clone();
            oldMax = (Point2D.Double) vis.desiredMax.clone();
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (e.isConsumed()) { 
            return;
        }
        if (pressedAt != null) {
            try {
            Point winPt = e.getPoint();
            if (mode.equals("Alt+Button1")) { // rectangle resize mode (ensure positive values)
                if (winPt.x < pressedAt.x) {
                    zoomBox.x = winPt.x;
                    zoomBox.width = - winPt.x + pressedAt.x;
                } else {
                    zoomBox.x = pressedAt.x;
                    zoomBox.width = winPt.x - pressedAt.x;
                }
                if (winPt.y < pressedAt.y) {
                    zoomBox.y = winPt.y;
                    zoomBox.height = -winPt.y + pressedAt.y;
                } else {
                    zoomBox.y = pressedAt.y;
                    zoomBox.height = winPt.y - pressedAt.y;
                }
                plot.repaint();
            } else { // pan mode
                String curMode = MouseEvent.getModifiersExText(e.getModifiersEx());
                if (curMode.equals("Shift+Button1") || curMode.equals("Shift")) { // restricted movement mode
                    if (Math.abs(winPt.y - pressedAt.y) < Math.abs(winPt.x - pressedAt.x))
                        winPt.y = pressedAt.y;
                    else
                        winPt.x = pressedAt.x;
                }
                Point2D.Double newC = vis.toLocal(winPt); // new coordinate in current visometry
                Point2D.Double oldC = vis.toLocal(pressedAt); // original coordinate in current visometry

                vis.setDesiredRange(
                        oldMin.x - newC.x + oldC.x, oldMin.y - newC.y + oldC.y,
                        oldMax.x - newC.x + oldC.x, oldMax.y - newC.y + oldC.y);

                // second pass to snap to boundaries of window
                if (SNAP_ENABLED) {
                    final int SNAP_RANGE = 30;
                    Point2D.Double winOrigin = vis.toWindow(new Point2D.Double(0, 0)); // current location of origin
                    RectangularShape win = vis.getWindowBounds();
                    double shiftX = 0, shiftY = 0;
                    if (winOrigin.x >= win.getMinX() && winOrigin.x <= win.getMinX() + SNAP_RANGE)
                        shiftX = -(win.getMinX() + SNAP_RANGE/2 - winOrigin.x) / vis.getHorizontalScale();
                    else if (winOrigin.x >= win.getMaxX() - SNAP_RANGE && winOrigin.x <= win.getMaxX())
                        shiftX = -(win.getMaxX() - SNAP_RANGE/2 - winOrigin.x) / vis.getHorizontalScale();
                    if (winOrigin.y >= win.getMinY() && winOrigin.y <= win.getMinY() + SNAP_RANGE)
                        shiftY = -(win.getMinY() + SNAP_RANGE/2 - winOrigin.y) / vis.getVerticalScale();
                    else if (winOrigin.y >= win.getMaxY() - SNAP_RANGE && winOrigin.y <= win.getMaxY())
                        shiftY = -(win.getMaxY() - SNAP_RANGE/2 - winOrigin.y) / vis.getVerticalScale();

                    if (shiftX != 0 || shiftY != 0) {
                        vis.setDesiredRange(
                            oldMin.x - newC.x + oldC.x + shiftX, oldMin.y - newC.y + oldC.y + shiftY,
                            oldMax.x - newC.x + oldC.x + shiftX, oldMax.y - newC.y + oldC.y + shiftY);

                    }
                }
            }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    public void mouseReleased(MouseEvent e) {
        if (e.isConsumed()) { 
            return;
        }
        mouseDragged(e);
        if (pressedAt != null && mode.equals("Alt+Button1")) // rectangle resize mode
            zoomBoxAnimated(vis, zoomBox);
        else { // pan mode
        }
        zoomBox = null;
        pressedAt = null;
        oldMin = null;
        oldMax = null;
        mode = null;
    }

    public void mouseMoved(MouseEvent e) {
        if (e.isConsumed()) { 
            return;
        }
        if (MouseEvent.getModifiersExText(e.getModifiersEx()).equals("Alt"))
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        else
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isConsumed()) { 
            return;
        }
        Point2D.Double mouseLoc = new Point2D.Double(e.getPoint().x, e.getPoint().y);

        // ensure the point is within the window
        RectangularShape bounds = vis.getWindowBounds();
        mouseLoc.x = Math.max(mouseLoc.x, bounds.getMinX());
        mouseLoc.x = Math.min(mouseLoc.x, bounds.getMaxX());
        mouseLoc.y = Math.max(mouseLoc.y, bounds.getMinY());
        mouseLoc.y = Math.min(mouseLoc.y, bounds.getMaxY());

        zoomPoint(vis, vis.toLocal(mouseLoc),
                (e.getWheelRotation() > 0) ? 1.05 : 0.95);
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}


    //
    // STATIC METHODS FOR BOUNDS RESET & ANIMATED SETTINGS CHANGE
    //

    /**
     * Sets visometry bounds based on the zoom about a given point.
     */
    public static void zoomPoint(PlaneVisometry vis, Point2D.Double cPoint, double factor) {
        /** effective zoom point is between current center and mouse position...
         * close to center => 100% at the given point, close to edge => 10% at the given point. */
        double cx = .1 * cPoint.x + .45 * (vis.getMinPointVisible().x + vis.getMaxPointVisible().x);
        double cy = .1 * cPoint.y + .45 * (vis.getMinPointVisible().y + vis.getMaxPointVisible().y);
        double wx = vis.getVisibleRange().getWidth();
        double wy = vis.getVisibleRange().getHeight();
        vis.setDesiredRange(
                cx - factor * wx / 2.0, cy - factor * wy / 2.0,
                cx + factor * wx / 2.0, cy + factor * wy / 2.0);
    }

    /**
     * Creates an animating zoom using a particular timer, about the center of the screen
     * @param factor how far to zoom, representing the new scale
     */
    public static void zoomCenterAnimated(PlaneVisometry vis, double factor) {
        Point2D.Double center = new Point2D.Double(
                0.5 * (vis.getMinPointVisible().x + vis.getMaxPointVisible().x),
                0.5 * (vis.getMinPointVisible().y + vis.getMaxPointVisible().y));
        zoomPointAnimated(vis, center, factor);
    }

    private static final int ANIM_STEPS = 25;

    /**
     * Creates an animating zoom using a particular timer.
     * @param p the coordinate of the point to center zoom about, in local coordiantes
     * @param factor how far to zoom, representing the new scale
     */
    public static void zoomPointAnimated(final PlaneVisometry vis, Point2D.Double p, final double factor) {
        final double cx = .1 * p.x + .45 * (vis.getMinPointVisible().x + vis.getMaxPointVisible().x);
        final double cy = .1 * p.y + .45 * (vis.getMinPointVisible().y + vis.getMaxPointVisible().y);
        final double xMultiplier = vis.getVisibleRange().getWidth() / 2;
        final double yMultiplier = vis.getVisibleRange().getHeight() / 2;

        Thread runner = new Thread(new Runnable() {
            public void run() {
                double zoomValue = 0.0;
                for (int i = 0; i <= ANIM_STEPS; i++) {
                    try { Thread.sleep(5); } catch (Exception e) { } // wait a bit
                    zoomValue = 1.0 + (factor - 1.0) * i / (double) ANIM_STEPS;
                    vis.setDesiredRange(
                            cx - zoomValue * xMultiplier, cy - zoomValue * yMultiplier,
                            cx + zoomValue * xMultiplier, cy + zoomValue * yMultiplier);
                }
            }
        });
        runner.start();
    }

    /**
     * Zooms to the boundaries of a particular box.
     * @param winBoundary the boundary of the zoom box (in window coordinates)
     */
    public static void zoomBoxAnimated(PlaneVisometry vis, Rectangle2D winBoundary) {
        zoomCoordBoxAnimated(vis,
                vis.toLocal(new Point2D.Double(winBoundary.getMinX(), winBoundary.getMinY())),
                vis.toLocal(new Point2D.Double(winBoundary.getMaxX(), winBoundary.getMaxY())) );
    }

    /**
     * Zooms to the boundaries of a particular box.
     * @param vis associated visometry
     * @param newMin min of zoom box
     * @param newMax max of zoom box
     */
    public static void zoomCoordBoxAnimated(final PlaneVisometry vis, final Point2D.Double newMin, final Point2D.Double newMax) {
        final Point2D.Double min = vis.getMinPointVisible();
        final Point2D.Double max = vis.getMaxPointVisible();

        Thread runner = new Thread(new Runnable() {
            public void run() {
                for (double factor = 0; factor < 1.0; factor += 1.0 / ANIM_STEPS) {
                    try { Thread.sleep(5); } catch (Exception e) { } // wait a bit
                    vis.setDesiredRange(
                            min.x + (newMin.x - min.x) * factor,
                            min.y + (newMin.y - min.y) * factor,
                            max.x + (newMax.x - max.x) * factor,
                            max.y + (newMax.y - max.y) * factor);
                }
            }
        });
        runner.start();
    }

    /**
     * Animates an asepct ratio change.
     * @param vis associated visometry
     * @param aspect the new aspect ratio.
     */
    public static void aspectAnimated(final PlaneVisometry vis, final double aspect) {
        if (aspect <= 0)
            throw new IllegalArgumentException("Aspect <= 0: " + aspect);
        final double oldAspect = vis.aspectRatio;

        Thread runner = new Thread(new Runnable() {
            public void run() {
                for (double factor = 0; factor < 1.0; factor += 1.0 / ANIM_STEPS) {
                    try { Thread.sleep(5); } catch (Exception e) { } // wait a bit
                    vis.setAspectRatio(oldAspect + factor * (aspect - oldAspect));
                }
            }
        });
        runner.start();
    }

}
