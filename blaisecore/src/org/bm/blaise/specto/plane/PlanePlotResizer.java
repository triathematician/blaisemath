/**
 * PlanePlotResizer.java
 * Created on Aug 4, 2009
 */
package org.bm.blaise.specto.plane;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import org.bm.blaise.specto.primitive.ArrowStyle;
import org.bm.blaise.specto.primitive.GraphicArrow;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.primitive.ShapeStyle;
import org.bm.blaise.specto.visometry.AbstractDynamicPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *   <code>PlanePlotResizer</code> handles the mouse behavior on the planar visometry, when there is no plottable
 *   to handle the events.
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
public class PlanePlotResizer extends AbstractDynamicPlottable<Point2D.Double> implements MouseWheelListener {

    PlanePlotComponent plot;
    PlaneVisometry vis;

    //
    //
    // CONSTRUCTORS
    //
    //

    public PlanePlotResizer(PlaneVisometry vis, PlanePlotComponent plot) {
        this.vis = vis;
        this.plot = plot;
    }
    
    //
    //
    // PAINT METHODS
    //
    //

    /** This is a box showing where the mouse was clicked first and where it was clicked last. */
    transient Rectangle2D.Double overlayBox;
    final static PathStyle pathStyle = new PathStyle(new Color(255, 128, 128, 128), 2f);
    final static ShapeStyle overlayStyle = new ShapeStyle(pathStyle, new Color(255, 128, 128, 128));
    /** An arrow showing where the mouse has been clicked. */
    transient GraphicArrow overlayVec;
    final static ArrowStyle arrStyle = new ArrowStyle(pathStyle, ArrowStyle.ArrowShape.TRIANGLE, 15);

    @Override
    public void paintComponent(VisometryGraphics<Double> vg) {
        if (overlayBox != null) {
            vg.setShapeStyle(overlayStyle);
            vg.drawWinShape(overlayBox);
        }
        if (overlayVec != null) {
            vg.setArrowStyle(arrStyle);
            vg.drawWinArrow(overlayVec);
        }
    }

    //
    //
    // MOUSE METHODS
    //
    //

    /** Location mouse was first pressed at. */
    transient protected Point pressedAt = null;
    /** Stores keyboard modifiers for mouse. */
    transient protected String mode = null;
    /** Old bounds for the window. */
    transient protected Point2D.Double oldMin = null;
    /** Old bounds for the window. */
    transient protected Point2D.Double oldMax = null;

    /**
     * Always returns true, because this is the default mouse handler.
     */
    public boolean isClickablyCloseTo(VisometryMouseEvent<Point2D.Double> e) {
        return true;
    }

    /**
     * When the mouse is pressed, prepare for resizing or panning.
     * @param e
     */
    @Override
    public void mousePressed(VisometryMouseEvent<Point2D.Double> e) {
        pressedAt = e.getWindowPoint();
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
//        System.out.println("mode = " + mode);
        if (mode.equals("Alt+Button1")) { // rectangle resize mode
            overlayBox = new Rectangle2D.Double(pressedAt.x, pressedAt.y, 0, 0);
        } else { // pan mode
            overlayVec = new GraphicArrow(pressedAt, pressedAt);
            oldMin = (Point2D.Double) vis.desiredMin.clone();
            oldMax = (Point2D.Double) vis.desiredMax.clone();
        }
    }

    @Override
    public void mouseDragged(VisometryMouseEvent<Point2D.Double> e) {
        if (pressedAt != null) {
            if (mode.equals("Alt+Button1")) { // rectangle resize mode (ensure positive values)
                if (e.getWindowPoint().x < pressedAt.x) {
                    overlayBox.x = e.getWindowPoint().x;
                    overlayBox.width = - e.getWindowPoint().x + pressedAt.x;
                } else {
                    overlayBox.x = pressedAt.x;
                    overlayBox.width = e.getWindowPoint().x - pressedAt.x;
                }
                if (e.getWindowPoint().y < pressedAt.y) {
                    overlayBox.y = e.getWindowPoint().y;
                    overlayBox.height = - e.getWindowPoint().y + pressedAt.y;
                } else {
                    overlayBox.y = pressedAt.y;
                    overlayBox.height = e.getWindowPoint().y - pressedAt.y;
                }
                plot.repaint();
            } else {
                Point winPt = e.getWindowPoint();
//                System.out.println(MouseEvent.getModifiersExText(e.getModifiersEx()));
                String curMode = MouseEvent.getModifiersExText(e.getModifiersEx());
                if (curMode.equals("Shift+Button1") || curMode.equals("Shift")) { // restricted movement mode
                    if (Math.abs(winPt.y - pressedAt.y) < Math.abs(winPt.x - pressedAt.x)) {
                        winPt.y = pressedAt.y;
                    } else {
                        winPt.x = pressedAt.x;
                    }
                }
                overlayVec.setHead(winPt);
                Point2D.Double newC = vis.getCoordinateOf(winPt);
                Point2D.Double oldC = vis.getCoordinateOf(pressedAt);
                vis.setDesiredRange(
                        oldMin.x - newC.x + oldC.x, oldMin.y - newC.y + oldC.y,
                        oldMax.x - newC.x + oldC.x, oldMax.y - newC.y + oldC.y);
//                System.out.println(vis.getDesiredRange());
            }
        }
    }

    /**
     * Performs action based upon the current mode.
     * @param e
     */
    @Override
    public void mouseReleased(VisometryMouseEvent<Point2D.Double> e) {
        mouseDragged(e);
        if (pressedAt != null && mode.equals("Alt+Button1")) { // rectangle resize mode
            zoomBoxAnimated(overlayBox);
        } else { // pan mode
        }
        overlayBox = null;
        overlayVec = null;
        pressedAt = null;
        oldMin = null;
        oldMax = null;
        mode = null;
    }

    /**
     * Changes the mouse cursor depending upon the keyboard modifier currently pressed.
     * @param e
     */
    @Override
    public void mouseMoved(VisometryMouseEvent<Point2D.Double> e) {
        if (MouseEvent.getModifiersExText(e.getModifiersEx()).equals("Alt")) {
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            plot.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
    }

    //
    //
    // MOUSE-WHEEL METHODS
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
    public void zoomPoint(Point2D.Double cPoint, double factor) {
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

    public void zoomCenter(double factor) {
        Point2D.Double center = new Point2D.Double(
                0.5 * (vis.getMinPointVisible().x + vis.getMaxPointVisible().x),
                0.5 * (vis.getMinPointVisible().y + vis.getMaxPointVisible().y));
        zoomPointAnimated(center, factor);
    }

    private static final int ZOOM_STEPS = 100;

    /** Creates an animating zoom using a particular timer. */
    public void zoomPointAnimated(Point2D.Double p, final double factor) {
        final double cx = .1 * p.x + .45 * (vis.getMinPointVisible().x + vis.getMaxPointVisible().x);
        final double cy = .1 * p.y + .45 * (vis.getMinPointVisible().y + vis.getMaxPointVisible().y);
        final double xMultiplier = vis.getWindowBounds().getWidth() / 2.0;
        final double yMultiplier = vis.getWindowBounds().getHeight();

        Thread runner = new Thread(new Runnable() {
            public void run() {
                double zoomValue = 0.0;
                for (int i = 0; i <= ZOOM_STEPS; i++) {
                    try { Thread.sleep(1); } catch (Exception e) { } // wait a bit
                    zoomValue = 1.0 + (factor - 1.0) / ZOOM_STEPS;
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
    public void zoomBoxAnimated(Rectangle2D winBoundary) {
        final Point2D.Double min = vis.getMinPointVisible();
        final Point2D.Double max = vis.getMaxPointVisible();
        final Point2D.Double newMin = vis.getCoordinateOf(new Point2D.Double(winBoundary.getMinX(), winBoundary.getMinY()));
        final Point2D.Double newMax = vis.getCoordinateOf(new Point2D.Double(winBoundary.getMaxX(), winBoundary.getMaxY()));

        Thread runner = new Thread(new Runnable() {
            public void run() {
                for (double factor = 0; factor < 1.0; factor += 1.0 / ZOOM_STEPS) {
                    try { Thread.sleep(2); } catch (Exception e) { } // wait a bit
                    vis.setDesiredRange(
                            min.x + (newMin.x - min.x) * factor,
                            min.y + (newMin.y - min.y) * factor,
                            max.x + (newMax.x - max.x) * factor,
                            max.y + (newMax.y - max.y) * factor);
                    plot.repaint();
                }
            }
        });
        runner.start();
    }
}
