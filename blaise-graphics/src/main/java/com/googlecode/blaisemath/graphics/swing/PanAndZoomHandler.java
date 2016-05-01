/*
 * GraphicComponentPanAndZoomHandler.java
 * Created Jun 5, 2013
 */
package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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

import static com.google.common.base.Preconditions.checkNotNull;
import com.googlecode.blaisemath.annotation.InvokedFromThread;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.CanvasPainter;
import com.googlecode.blaisemath.util.swing.AnimationStep;
import com.googlecode.blaisemath.util.swing.BSwingUtilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

/**
 * Enables pan and zoom of a graphics canvas, by changing the {@link AffineTransform}
 * associated with the canvas.
 *
 * @author Elisha
 */
public final class PanAndZoomHandler extends MouseAdapter implements CanvasPainter<Graphics2D> {

    /** Default number of steps to use in animating pan/zoom */
    private static final int ANIM_STEPS = 25;
    /** How long between animation steps */
    private static final int ANIM_DELAY_MILLIS = 5;
    
    /** Basic pan mode */
    private static final String PAN_MODE = "Button1";
    /** Mouse mode for rectangle resize */
    private static final String RECTANGLE_RESIZE_MODE = "Alt+Button1";
    /** Mode for restricted movement */
    private static final String RESTRICTED_MOVEMENT_MODE = "Shift+Button1";
    /** Allow user to release mouse button and still do movement */
    private static final String RESTRICTED_MOVEMENT_MODE_ALT = "Shift";
    
    /** Renderer for zoom box */
    private static final AttributeSet ZOOM_BOX_STYLE = Styles.fillStroke(
            new Color(255, 128, 128, 128), new Color(255, 196, 196, 128));
    
    /** The component for the mouse handling */
    private final JGraphicComponent component;
    /** Hint box for zooming */
    private transient Rectangle2D.Double zoomBox;
    /** Location mouse was first pressed at. */
    private transient Point pressedAt = null;
    /** Stores keyboard modifiers for mouse. */
    private transient String mode = null;
    /** Old bounds for the window. */
    private transient Rectangle2D oldLocalBounds;

    /**
     * Initialize with given component. This method is private as the
     * {@link #install(com.googlecode.blaisemath.graphics.swing.JGraphicComponent)}
     * method should be used.
     * @param comp component
     */
    private PanAndZoomHandler(JGraphicComponent comp) {
        this.component = checkNotNull(comp);
    }
    
    /**
     * Initialize handler for given component
     * @param cpt graphic component
     */
    public static void install(JGraphicComponent cpt) {
        PanAndZoomHandler handler = new PanAndZoomHandler(cpt);
        cpt.addMouseListener(handler);
        cpt.addMouseMotionListener(handler);
        cpt.addMouseWheelListener(handler);
        cpt.getOverlays().add(handler);
    }
    
    

    @Override
    public void paint(Component component, Graphics2D canvas) {
        if (zoomBox != null) {
            ShapeRenderer.getInstance().render(zoomBox, ZOOM_BOX_STYLE, canvas);
        }
    }
    
    //
    // MOUSE OPERATIONS
    //
    
    private void initMouseGesture(MouseEvent e) {
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        if (RECTANGLE_RESIZE_MODE.equals(mode) || PAN_MODE.equals(mode) || RESTRICTED_MOVEMENT_MODE.equals(mode)) {
            pressedAt = e.getPoint();
        }
        if (RECTANGLE_RESIZE_MODE.equals(mode)) {
            zoomBox = new Rectangle2D.Double(pressedAt.x, pressedAt.y, 0, 0);
        } else if (PAN_MODE.equals(mode) || RESTRICTED_MOVEMENT_MODE.equals(mode)) {
            // pan mode
            oldLocalBounds = getLocalBounds(component);
        } else {
            // ignore
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!e.isConsumed()) {
            initMouseGesture(e);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        if (pressedAt == null) {
            initMouseGesture(e);
        }
        String mouseMods = MouseEvent.getModifiersExText(e.getModifiersEx());
        if (RECTANGLE_RESIZE_MODE.equals(mode)) {
            mouseDraggedResizeMode(e.getPoint());
        } else if (PAN_MODE.equals(mode) || RESTRICTED_MOVEMENT_MODE.equals(mode)) { 
            boolean restrictedMovementMode = RESTRICTED_MOVEMENT_MODE.equals(mouseMods) 
                    || RESTRICTED_MOVEMENT_MODE_ALT.equals(mouseMods);
            mouseDraggedPanMode(e.getPoint(), restrictedMovementMode);
        } else {
            // ignore
        }
    }
    
    private void mouseDraggedResizeMode(Point winPt) {
        if (winPt.x < pressedAt.x) {
            zoomBox.x = winPt.x;
            zoomBox.width = (double) -winPt.x + pressedAt.x;
        } else {
            zoomBox.x = pressedAt.x;
            zoomBox.width = (double) winPt.x - pressedAt.x;
        }
        if (winPt.y < pressedAt.y) {
            zoomBox.y = winPt.y;
            zoomBox.height = (double) -winPt.y + pressedAt.y;
        } else {
            zoomBox.y = pressedAt.y;
            zoomBox.height = (double) winPt.y - pressedAt.y;
        }
        component.repaint();
    }
    
    private void mouseDraggedPanMode(Point winPt, boolean restrictedMovementMode) {
        if (restrictedMovementMode) {
            if (Math.abs(winPt.y - pressedAt.y) < Math.abs(winPt.x - pressedAt.x)) {
                winPt.y = pressedAt.y;
            } else {
                winPt.x = pressedAt.x;
            }
        }
        double dx = (winPt.x - pressedAt.x) * component.getInverseTransform().getScaleX();
        double dy = (winPt.y - pressedAt.y) * component.getInverseTransform().getScaleY();

        setDesiredLocalBounds(component,
                new Rectangle2D.Double(
                        oldLocalBounds.getX() - dx, oldLocalBounds.getY() - dy, 
                        oldLocalBounds.getWidth(), oldLocalBounds.getHeight()));
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (!e.isConsumed()) {
            mouseDragged(e);
            if (pressedAt != null && RECTANGLE_RESIZE_MODE.equals(mode)) {
                zoomBoxAnimated(component, zoomBox);
            } else {
                 // pan mode
            }
        }
        zoomBox = null;
        pressedAt = null;
        oldLocalBounds = null;
        mode = null;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isConsumed()) {
            return;
        }
        Point2D.Double mouseLoc = new Point2D.Double(e.getPoint().x, e.getPoint().y);

        // ensure the point is within the window
        RectangularShape bounds = component.getBounds();
        mouseLoc.x = Math.max(mouseLoc.x, bounds.getMinX());
        mouseLoc.x = Math.min(mouseLoc.x, bounds.getMaxX());
        mouseLoc.y = Math.max(mouseLoc.y, bounds.getMinY());
        mouseLoc.y = Math.min(mouseLoc.y, bounds.getMaxY());

        zoomPoint(component, component.toGraphicCoordinate(mouseLoc),
                (e.getWheelRotation() > 0) ? 1.05 : 0.95);
    }

    //<editor-fold defaultstate="collapsed" desc="STATIC UTILITIES">
    //
    // DELEGATE TRANSFORM OPERATIONS
    //

    /**
     * Get current boundaries displayed in component.
     * @param gc associated component
     * @return local bounds associated with the component
     */
    public static Rectangle2D.Double getLocalBounds(JGraphicComponent gc) {
        if (gc.getInverseTransform() == null) {
            gc.setTransform(new AffineTransform());
        }
        // apply inverse transform to min point of component bounds and max point of component bounds
        Point2D.Double min = new Point2D.Double(0, 0);
        Point2D.Double max = new Point2D.Double(gc.getWidth(), gc.getHeight());
        Point2D cmin = gc.getInverseTransform().transform(min, null);
        Point2D cmax = gc.getInverseTransform().transform(max, null);
        return new Rectangle2D.Double(cmin.getX(), cmin.getY(), cmax.getX() - cmin.getX(), cmax.getY() - cmin.getY());
    }

    /**
     * Updates component transform so given rectangle is included within. Updates
     * to the component are made on the EDT.
     * @param comp associated component
     * @param rect local bounds
     */
    @InvokedFromThread("multiple")
    public static void setDesiredLocalBounds(final JGraphicComponent comp, final Rectangle2D rect) {
        setDesiredLocalBounds(comp, comp.getBounds(), rect);
    }
    
    /**
     * Updates component transform so given rectangle is included within. Updates
     * to the component are made on the EDT. Allows setting custom bounds for the
     * component, in case the component is not yet visible or sized.
     * @param comp associated component
     * @param compBounds bounds to use for the component
     * @param rect local bounds
     */
    @InvokedFromThread("multiple")
    public static void setDesiredLocalBounds(final JGraphicComponent comp, final Rectangle compBounds, final Rectangle2D rect) {
        BSwingUtilities.invokeOnEventDispatchThread(new Runnable(){
            @Override
            public void run() {
                comp.setTransform(scaleRectTransform(compBounds, rect));
            }
        });
    }
    
    /**
     * Create a transform that maps the "scaleFrom" rectangle into the "scaleTo" region.
     * @param scaleTo region to scale to
     * @param scaleFrom region to scale from
     * @return transform
     */
    public static AffineTransform scaleRectTransform(Rectangle2D scaleTo, final Rectangle2D scaleFrom) {
        double scalex = scaleFrom.getWidth() / scaleTo.getWidth();
        double scaley = scaleFrom.getHeight() / scaleTo.getHeight();
        double scale = Math.max(scalex, scaley);
        AffineTransform res = new AffineTransform();
        res.translate(scaleTo.getWidth()/2, scaleTo.getHeight()/2);
        res.scale(1 / scale, 1 / scale);
        res.translate(-scaleFrom.getCenterX(), -scaleFrom.getCenterY());
        return res;
    }

    /**
     * Sets visometry bounds based on the zoom about a given point.
     * The effective zoom point is between current center and mouse position...
     * close to center =%gt; 100% at the given point, close to edge =%gt; 10% at
     * the given point.
     * @param gc associated component
     * @param localZoomPoint focal point for zoom
     * @param factor how much to zoom
     */
    public static void zoomPoint(JGraphicComponent gc, Point2D localZoomPoint, double factor) {
        Rectangle2D.Double localBounds = getLocalBounds(gc);
        double cx = .1 * localZoomPoint.getX() + .9 * localBounds.getCenterX();
        double cy = .1 * localZoomPoint.getY() + .9 * localBounds.getCenterY();
        double wx = localBounds.getWidth();
        double wy = localBounds.getHeight();
        setDesiredLocalBounds(gc, new Rectangle2D.Double(
                cx - .5 * factor * wx, cy - .5 * factor * wy, 
                factor * wx, factor * wy));
    }
    
    // ANIMATED ZOOMS

    /**
     * Zooms in for the given component (about the center).
     * @param gc associated component
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomIn(JGraphicComponent gc) {
        Rectangle2D.Double rect = getLocalBounds(gc);
        Point2D.Double center = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
        return zoomCoordBoxAnimated(gc, 
                new Point2D.Double(center.x-.25*rect.getWidth(), center.y-.25*rect.getHeight()), 
                new Point2D.Double(center.x+.25*rect.getWidth(), center.y+.25*rect.getHeight())); 
    }
    
    /**
     * Zooms in for the given component (about the center).
     * @param gc associated component
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomOut(JGraphicComponent gc) {
        Rectangle2D.Double rect = getLocalBounds(gc);
        Point2D.Double center = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
        return zoomCoordBoxAnimated(gc, 
                new Point2D.Double(center.x-rect.getWidth(), center.y-rect.getHeight()), 
                new Point2D.Double(center.x+rect.getWidth(), center.y+rect.getHeight())); 
    }
    
    /**
     * Creates an animating zoom using a particular timer, about the center of
     * the screen.
     * @param gc associated component
     * @param factor how far to zoom, representing the new scale
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomCenterAnimated(JGraphicComponent gc, double factor) {
        Rectangle2D.Double rect = getLocalBounds(gc);
        Point2D.Double center = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
        return zoomPointAnimated(gc, center, factor);
    }

    /**
     * Creates an animating zoom using a particular timer.
     * @param gc associated component
     * @param p the coordinate of the point to center zoom about, in local
     * coordinates
     * @param factor how far to zoom, representing the new scale
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomPointAnimated(final JGraphicComponent gc, Point2D.Double p, final double factor) {
        Rectangle2D.Double rect = getLocalBounds(gc);
        final double cx = .1 * p.x + .9 * rect.getCenterX();
        final double cy = .1 * p.y + .9 * rect.getCenterY();
        final double wx = rect.getWidth();
        final double wy = rect.getHeight();

        return AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, new AnimationStep(){
            @Override
            @InvokedFromThread("AnimationStep")
            public void run(int idx, double pct) {
                double zoomValue = 1.0 + (factor - 1.0) * pct;
                setDesiredLocalBounds(gc, new Rectangle2D.Double(
                        cx - .5 * zoomValue * wx, cy - .5 * zoomValue * wy, 
                        wx + zoomValue * wx, wy + zoomValue * wy));
            }
        });
    }

    /**
     * Zooms to the boundaries of a particular box.
     * @param gc associated component
     * @param zoomBoxWinCoords the boundary of the zoom box (in window
     * coordinates)
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomBoxAnimated(JGraphicComponent gc, Rectangle2D zoomBoxWinCoords) {
        return zoomCoordBoxAnimated(gc,
                gc.toGraphicCoordinate(new Point2D.Double(zoomBoxWinCoords.getMinX(), zoomBoxWinCoords.getMinY())),
                gc.toGraphicCoordinate(new Point2D.Double(zoomBoxWinCoords.getMaxX(), zoomBoxWinCoords.getMaxY())));
    }

    /**
     * Zooms to the boundaries of a particular box.
     * @param gc associated component
     * @param newMin min of zoom box
     * @param newMax max of zoom box
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomCoordBoxAnimated(final JGraphicComponent gc, final Point2D newMin, final Point2D newMax) {
        final Rectangle2D.Double rect = getLocalBounds(gc);
        final double xMin = rect.getX();
        final double yMin = rect.getY();
        final double xMax = rect.getMaxX();
        final double yMax = rect.getMaxY();
        final double nxMin = newMin.getX();
        final double nyMin = newMin.getY();
        final double nxMax = newMax.getX();
        final double nyMax = newMax.getY();

        return AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, new AnimationStep(){
            @Override
            @InvokedFromThread("AnimationStep")
            public void run(int idx, double pct) {
                double x1 = xMin + (nxMin - xMin) * pct;
                double y1 = yMin + (nyMin - yMin) * pct;
                double x2 = xMax + (nxMax - xMax) * pct;
                double y2 = yMax + (nyMax - yMax) * pct;
                setDesiredLocalBounds(gc, new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1));
            }
        });
    }
    
    //</editor-fold> 
}
