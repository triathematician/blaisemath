/*
 * PlanePlotMouseHandler.java
 * Created on Aug 4, 2009
 */

package com.googlecode.blaisemath.visometry.plane;

/*
 * #%L
 * BlaiseVisometry
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */







import com.googlecode.blaisemath.graphics.swing.ShapeRenderer;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.animation.AnimationStep;
import com.googlecode.blaisemath.util.CanvasPainter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.concurrent.TimeUnit;

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
public final class PlanePlotMouseHandler extends MouseAdapter implements CanvasPainter<Graphics2D> {

    /** Renderer for zoom box */
    private static final AttributeSet BOX_STYLE = Styles.fillStroke(new Color(255, 128, 128, 128), new Color(255, 196, 196, 128));
    /** Mouse mode for rectangle resize */
    private static final String RESIZE_MODE = "Alt+Button1";
    /** Mouse mode for rectangle resize */
    private static final String RESTRICT_MODE = "Shift+Button1";
    /** Mouse mode for rectangle resize */
    private static final String RESTRICT_MODE_2 = "Shift";
    /** Number of steps in zoom animation */
    private static final int ANIM_STEPS = 25;
    /** Delay between steps in animation */
    private static final int ANIM_DELAY_MILLIS = 5;
    /** Number of pixels required to be in "snap range" for restricted resize mdoe */
    private static final int SNAP_RANGE = 30;
    
    //
    // STATE VARIABLES
    //
    
    /** The visometry of the component */
    private final PlaneVisometry vis;
    /** Determines whether to snap to the coordinate axes. */
    boolean snapEnabled = true;
    /** Hint box for zooming */
    private Rectangle2D.Double zoomBox;

    /** Location mouse was first pressed at. */
    private Point pressedAt = null;
    /** Stores keyboard modifiers for mouse. */
    private String mode = null;
    /** Old bounds for the window. */
    private Point2D.Double oldMin = null;
    /** Old bounds for the window. */
    private Point2D.Double oldMax = null;

    /**
     * Initialize handler for the provided visometry.
     * @param vis the visometry
     */
    public PlanePlotMouseHandler(PlaneVisometry vis) {
        this.vis = vis;
    }

    @Override
    public void paint(Component component, Graphics2D canvas) {
        if (zoomBox != null) {
            ShapeRenderer.getInstance().render(zoomBox, BOX_STYLE, canvas);
        }
    }

    //
    // MOUSE OPERATIONS
    //
    
    private boolean interestedIn(MouseEvent e) {
        return !e.isConsumed() && !e.isShiftDown();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!interestedIn(e)) {
            return;
        }
        pressedAt = e.getPoint();
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        if (mode.equals(RESIZE_MODE)) { 
            zoomBox = new Rectangle2D.Double(pressedAt.x, pressedAt.y, 0, 0);
        } else { 
            // pan mode
            oldMin = (Point2D.Double) vis.getDesiredMin().clone();
            oldMax = (Point2D.Double) vis.getDesiredMax().clone();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!interestedIn(e) || pressedAt == null) {
            return;
        }
        Point winPt = e.getPoint();
        if (mode.equals(RESIZE_MODE)) {
            mouseDraggedResize(winPt);
        } else { 
            mouseDraggedPan(winPt, MouseEvent.getModifiersExText(e.getModifiersEx()));
        }
    }
    
    public void mouseDraggedResize(Point winPt) {
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
        vis.fireStateChanged();
    }
    
    private void mouseDraggedPan(Point winPt, String curMode) {
        // pan mode
        if (curMode.equals(RESTRICT_MODE) || curMode.equals(RESTRICT_MODE_2)) {
            if (Math.abs(winPt.y - pressedAt.y) < Math.abs(winPt.x - pressedAt.x)) {
                winPt.y = pressedAt.y;
            } else {
                winPt.x = pressedAt.x;
            }
        }
        // original coordinate in current visometry
        Point2D.Double oldC = vis.toLocal(pressedAt); 
        // new coordinate in current visometry
        Point2D.Double newC = vis.toLocal(winPt); 

        vis.setDesiredRange(
                oldMin.x - newC.x + oldC.x, oldMin.y - newC.y + oldC.y,
                oldMax.x - newC.x + oldC.x, oldMax.y - newC.y + oldC.y);

        // second pass to snap to boundaries of window
        if (snapEnabled) {
            snapToCoordinates(oldC, newC);
        }
    }
    
    private void snapToCoordinates(Point2D.Double oldC, Point2D.Double newC) {
        // current location of origin
        Point2D.Double winOrigin = vis.toWindow(new Point2D.Double(0, 0)); 
        double shiftX = horizontalSnap(winOrigin);
        double shiftY = verticalSnap(winOrigin);

        if (shiftX != 0 || shiftY != 0) {
            vis.setDesiredRange(
                oldMin.x - newC.x + oldC.x + shiftX, oldMin.y - newC.y + oldC.y + shiftY,
                oldMax.x - newC.x + oldC.x + shiftX, oldMax.y - newC.y + oldC.y + shiftY);

        }
    }
    
    private double horizontalSnap(Point2D.Double winOrigin) {        
        RectangularShape winBounds = vis.getWindowBounds();
        if (winOrigin.x >= winBounds.getMinX() && winOrigin.x <= winBounds.getMinX() + SNAP_RANGE) {
            return -(winBounds.getMinX() + SNAP_RANGE/2 - winOrigin.x) / vis.getHorizontalScale();
        } else if (winOrigin.x >= winBounds.getMaxX() - SNAP_RANGE && winOrigin.x <= winBounds.getMaxX()) {
            return -(winBounds.getMaxX() - SNAP_RANGE/2 - winOrigin.x) / vis.getHorizontalScale();
        } else {
            return 0;
        }
    }
    
    private double verticalSnap(Point2D.Double winOrigin) {     
        RectangularShape winBounds = vis.getWindowBounds();
        if (winOrigin.y >= winBounds.getMinY() && winOrigin.y <= winBounds.getMinY() + SNAP_RANGE) {
            return -(winBounds.getMinY() + SNAP_RANGE/2 - winOrigin.y) / vis.getVerticalScale();
        } else if (winOrigin.y >= winBounds.getMaxY() - SNAP_RANGE && winOrigin.y <= winBounds.getMaxY()) {
            return -(winBounds.getMaxY() - SNAP_RANGE/2 - winOrigin.y) / vis.getVerticalScale();
        } else {
            return 0;
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if (interestedIn(e)) {
            mouseDragged(e);
            if (pressedAt != null && mode.equals(RESIZE_MODE)) { 
                // rectangle resize mode
                zoomBoxAnimated(vis, zoomBox);
            } else { 
                // pan mode
            }
        }
        zoomBox = null;
        pressedAt = null;
        oldMin = null;
        oldMax = null;
        mode = null;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!interestedIn(e)) {
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


    //
    // STATIC METHODS FOR BOUNDS RESET & ANIMATED SETTINGS CHANGE
    //

    /**
     * Sets visometry bounds based on the zoom about a given point.
     * @param vis
     * @param cPoint
     * @param factor
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
     * @param vis
     * @param factor how far to zoom, representing the new scale
     */
    public static void zoomCenterAnimated(PlaneVisometry vis, double factor) {
        Point2D.Double center = new Point2D.Double(
                0.5 * (vis.getMinPointVisible().x + vis.getMaxPointVisible().x),
                0.5 * (vis.getMinPointVisible().y + vis.getMaxPointVisible().y));
        zoomPointAnimated(vis, center, factor);
    }

    /**
     * Creates an animating zoom using a particular timer.
     * @param vis
     * @param p the coordinate of the point to center zoom about, in local coordiantes
     * @param factor how far to zoom, representing the new scale
     */
    public static void zoomPointAnimated(final PlaneVisometry vis, Point2D.Double p, final double factor) {
        final double cx = .1 * p.x + .45 * (vis.getMinPointVisible().x + vis.getMaxPointVisible().x);
        final double cy = .1 * p.y + .45 * (vis.getMinPointVisible().y + vis.getMaxPointVisible().y);
        final double xMultiplier = vis.getVisibleRange().getWidth() / 2;
        final double yMultiplier = vis.getVisibleRange().getHeight() / 2;

        AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, TimeUnit.MILLISECONDS, new AnimationStep() {
            @Override
            public void run(int i, double pct) {
                double zoomValue = 1.0 + (factor - 1.0) * pct;
                vis.setDesiredRange(
                        cx - zoomValue * xMultiplier, cy - zoomValue * yMultiplier,
                        cx + zoomValue * xMultiplier, cy + zoomValue * yMultiplier);
            }
        });
    }

    /**
     * Zooms to the boundaries of a particular box.
     * @param vis
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
        
        AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, TimeUnit.MILLISECONDS, new AnimationStep() {
            @Override
            public void run(int i, double factor) {
                vis.setDesiredRange(
                        min.x + (newMin.x - min.x) * factor,
                        min.y + (newMin.y - min.y) * factor,
                        max.x + (newMax.x - max.x) * factor,
                        max.y + (newMax.y - max.y) * factor);
            }
        });
    }

    /**
     * Animates an asepct ratio change.
     * @param vis associated visometry
     * @param aspect the new aspect ratio.
     */
    public static void aspectAnimated(final PlaneVisometry vis, final double aspect) {
        if (aspect <= 0) {
            throw new IllegalArgumentException("Aspect <= 0: " + aspect);
        }
        final double oldAspect = vis.getAspectRatio();

        
        AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, TimeUnit.MILLISECONDS, new AnimationStep() {
            @Override
            public void run(int i, double pct) {
                vis.setAspectRatio(oldAspect + pct * (aspect - oldAspect));
            }
        });
    }
    
}
