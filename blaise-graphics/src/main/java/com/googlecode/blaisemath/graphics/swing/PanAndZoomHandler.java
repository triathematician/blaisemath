package com.googlecode.blaisemath.graphics.swing;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import com.googlecode.blaisemath.graphics.swing.render.ShapeRenderer;
import com.googlecode.blaisemath.util.swing.CanvasPainter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.googlecode.blaisemath.annotation.InvokedFromThread;
import com.googlecode.blaisemath.geom.AffineTransformBuilder;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.util.swing.AnimationStep;
import com.googlecode.blaisemath.util.swing.MoreSwingUtilities;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import static java.util.Objects.requireNonNull;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Enables pan and zoom of a graphics canvas, by changing the {@link AffineTransform}
 * associated with the canvas.
 *
 * @author Elisha Peterson
 */
public final class PanAndZoomHandler extends MouseAdapter implements CanvasPainter<Graphics2D> {

    private static final Logger LOG = Logger.getLogger(PanAndZoomHandler.class.getName());
    
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

    /** Cache of recent animation timers */
    private static final Cache<JGraphicComponent, Timer> TIMERS = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    /** The component for the mouse handling */
    private final JGraphicComponent component;
    /** Hint box for zooming */
    private Rectangle2D.Double zoomBox;
    /** Location mouse was first pressed at. */
    private Point pressedAt = null;
    /** Stores keyboard modifiers for mouse. */
    private String mode = null;
    /** Old bounds for the window. */
    private Rectangle2D oldLocalBounds;

    /**
     * Initialize with given component. This method is private as the
     * {@link #install(com.googlecode.blaisemath.graphics.swing.JGraphicComponent)}
     * method should be used.
     * @param comp component
     */
    private PanAndZoomHandler(JGraphicComponent comp) {
        this.component = requireNonNull(comp);
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

    //region MOUSE HANDLING

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

        zoomPoint(component, component.toGraphicCoordinate(mouseLoc), e.getWheelRotation() > 0 ? 1.05 : 0.95);
    }

    //endregion

    //region TRANSFORMS

    /**
     * Get current boundaries displayed in component.
     * @param gc associated component
     * @return local bounds associated with the component
     */
    public static Rectangle2D.Double getLocalBounds(JGraphicComponent gc) {
        AffineTransform inverse = gc.getInverseTransform();
        if (inverse == null) {
            gc.setTransform(new AffineTransform());
            inverse = new AffineTransform();
        }
        // apply inverse transform to min point of component bounds and max point of component bounds
        Insets insets = gc.getInsets();
        Rectangle bounds = new Rectangle(insets.left, insets.top,
                gc.getWidth() - insets.left - insets.right, gc.getHeight() - insets.top - insets.bottom);
        Point2D min = inverse.transform(new Point2D.Double(bounds.getMinX(), bounds.getMinY()), null);
        Point2D max = inverse.transform(new Point2D.Double(bounds.getMaxX(), bounds.getMaxY()), null);
        return new Rectangle2D.Double(min.getX(), min.getY(), max.getX() - min.getX(), max.getY() - min.getY());
    }

    /**
     * Updates component transform so given rectangle is included within. Updates
     * to the component are made on the EDT.
     * @param comp associated component
     * @param rect local bounds
     */
    @InvokedFromThread("multiple")
    public static void setDesiredLocalBounds(final JGraphicComponent comp, final Rectangle2D rect) {
        Insets insets = comp.getInsets();
        Rectangle bounds = new Rectangle(insets.left, insets.top, comp.getWidth() - insets.left - insets.right, comp.getHeight() - insets.top - insets.bottom);
        setDesiredLocalBounds(comp, bounds, rect);
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
        MoreSwingUtilities.invokeOnEventDispatchThread(() -> comp.setTransform(AffineTransformBuilder.transformingTo(compBounds, rect)));
    }
    
    //endregion
    
    //region ZOOM OPERATIONS

    /**
     * Cancel previous animation timer.
     * @param gc component for the zoom operation
     */
    private static void cancelZoomTimer(JGraphicComponent gc) {
        javax.swing.Timer timer = TIMERS.getIfPresent(gc);
        if (timer != null && timer.isRunning()) {
            timer.stop();
            TIMERS.invalidate(gc);
        }
    }

    /**
     * Caches provided animation timer.
     * @param timer to cache
     * @param gc component for the zoom operation
     */
    private static void cacheZoomTimer(javax.swing.Timer timer, JGraphicComponent gc) {
        TIMERS.put(gc, timer);
    }

    /**
     * Sets bounds based on the zoom about a given point.
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

    /**
     * Zooms in for the given component (about the center), animated.
     * @param gc associated component
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomIn(JGraphicComponent gc) {
        return zoomIn(gc, true);
    }
    
    /**
     * Zooms in for the given component (about the center).
     * @param gc associated component
     * @param animate if true, result will animate
     * @return timer running the animation (null if not animating)
     */
    public static javax.swing.Timer zoomIn(JGraphicComponent gc, boolean animate) {
        Rectangle2D.Double rect = getLocalBounds(gc);
        Point2D.Double center = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
        if (animate) {
            return zoomCoordBoxAnimated(gc, 
                new Point2D.Double(center.x-.25*rect.getWidth(), center.y-.25*rect.getHeight()), 
                new Point2D.Double(center.x+.25*rect.getWidth(), center.y+.25*rect.getHeight()));
        } else {
            setDesiredLocalBounds(gc, new Rectangle2D.Double(center.x-.25*rect.getWidth(), 
                    center.y-.25*rect.getHeight(), .5*rect.getWidth(), .5*rect.getHeight()));
            return null;
        }
    }

    /**
     * Zooms out for the given component (about the center), animated.
     * @param gc associated component
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomOut(JGraphicComponent gc) {
        return zoomOut(gc, true);
    }
    
    /**
     * Zooms out for the given component (about the center).
     * @param gc associated component
     * @param animate if true, result will animate
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomOut(JGraphicComponent gc, boolean animate) {
        Rectangle2D.Double rect = getLocalBounds(gc);
        Point2D.Double center = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
        if (animate) {
            return zoomCoordBoxAnimated(gc, 
                    new Point2D.Double(center.x-rect.getWidth(), center.y-rect.getHeight()), 
                    new Point2D.Double(center.x+rect.getWidth(), center.y+rect.getHeight())); 
        } else {
            setDesiredLocalBounds(gc, new Rectangle2D.Double(center.x-rect.getWidth(), 
                    center.y-rect.getHeight(), 2*rect.getWidth(), 2*rect.getHeight()));
            return null;
        }
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
        cancelZoomTimer(gc);

        Rectangle2D.Double rect = getLocalBounds(gc);
        final double cx = .1 * p.x + .9 * rect.getCenterX();
        final double cy = .1 * p.y + .9 * rect.getCenterY();
        final double wx = rect.getWidth();
        final double wy = rect.getHeight();

        javax.swing.Timer timer = AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, new AnimationStep(){
            @Override
            @InvokedFromThread("AnimationStep")
            public void run(int idx, double pct) {
                double zoomValue = 1.0 + (factor - 1.0) * pct;
                setDesiredLocalBounds(gc, new Rectangle2D.Double(
                        cx - .5 * zoomValue * wx, cy - .5 * zoomValue * wy, 
                        wx + zoomValue * wx, wy + zoomValue * wy));
            }
        });
        cacheZoomTimer(timer, gc);
        return timer;
    }

    /**
     * Zooms to the boundaries of a particular box.
     * @param gc associated component
     * @param zoomBoxWindow the boundary of the zoom box (in window coordinates)
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomBoxAnimated(JGraphicComponent gc, Rectangle2D zoomBoxWindow) {
        return zoomCoordBoxAnimated(gc,
                gc.toGraphicCoordinate(new Point2D.Double(zoomBoxWindow.getMinX(), zoomBoxWindow.getMinY())),
                gc.toGraphicCoordinate(new Point2D.Double(zoomBoxWindow.getMaxX(), zoomBoxWindow.getMaxY())));
    }

    /**
     * Zooms to the boundaries of a particular box.
     * @param gc associated component
     * @param newMin min of zoom box
     * @param newMax max of zoom box
     * @return timer running the animation
     */
    public static javax.swing.Timer zoomCoordBoxAnimated(final JGraphicComponent gc, final Point2D newMin, final Point2D newMax) {
        cancelZoomTimer(gc);

        final Rectangle2D.Double rect = getLocalBounds(gc);
        final double xMin = rect.getX();
        final double yMin = rect.getY();
        final double xMax = rect.getMaxX();
        final double yMax = rect.getMaxY();
        final double nxMin = newMin.getX();
        final double nyMin = newMin.getY();
        final double nxMax = newMax.getX();
        final double nyMax = newMax.getY();

        javax.swing.Timer timer = AnimationStep.animate(0, ANIM_STEPS, ANIM_DELAY_MILLIS, new AnimationStep(){
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
        cacheZoomTimer(timer, gc);
        return timer;
    }
    
    //endregion
}
