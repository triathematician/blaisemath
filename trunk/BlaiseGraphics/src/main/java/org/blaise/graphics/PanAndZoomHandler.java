/*
 * GraphicComponentPanAndZoomHandler.java
 * Created Jun 5, 2013
 */
package org.blaise.graphics;

import static com.google.common.base.Preconditions.*;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.blaise.style.ShapeStyleBasic;
import org.blaise.style.Styles;
import org.blaise.util.CanvasPainter;

/**
 * Mouse handler that allows pan and zoom of a graphics canvas. Works by
 * changing the
 *
 * @author Elisha
 */
public final class PanAndZoomHandler extends MouseAdapter implements CanvasPainter {

    /** Default number of steps to use in animating pan/zoom */
    private static final int ANIM_STEPS = 25;
    /** Mouse mode for rectangle resize */
    private static final String RECTANGLE_RESIZE_MODE = "Alt+Button1";
    /** Mode for restricted movement */
    private static final String RESTRICTED_MOVEMENT_MODE = "Shift+Button1";
    private static final String RESTRICTED_MOVEMENT_MODE_ALT = "Shift";
    /** The component for the mouse handling */
    private final GraphicComponent component;

    /** Hint box for zooming */
    private transient Rectangle2D.Double zoomBox;
    /** Renderer for zoom box */
    private static final ShapeStyleBasic zoomBoxStyle = Styles.fillStroke(new Color(255, 128, 128, 128), new Color(255, 196, 196, 128));

    /** Location mouse was first pressed at. */
    private transient Point pressedAt = null;
    /** Stores keyboard modifiers for mouse. */
    private transient String mode = null;
    /** Old bounds for the window. */
    private transient Rectangle2D oldLocalBounds;
    
    /**
     * Initialize handler for given component
     * @param cpt graphic component
     */
    public PanAndZoomHandler(GraphicComponent cpt) {
        this.component = checkNotNull(cpt);
        cpt.addMouseListener(this);
        cpt.addMouseMotionListener(this);
        cpt.addMouseWheelListener(this);
        cpt.getOverlays().add(this);
    }

    public void paint(Component component, Graphics2D canvas) {
        if (zoomBox != null) {
            zoomBoxStyle.draw(zoomBox, canvas, null);
        }
    }
    
    //
    // MOUSE OPERATIONS
    //

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        pressedAt = e.getPoint();
        mode = MouseEvent.getModifiersExText(e.getModifiersEx());
        if (RECTANGLE_RESIZE_MODE.equals(mode)) {
            zoomBox = new Rectangle2D.Double(pressedAt.x, pressedAt.y, 0, 0);
        } else {
            // pan mode
            oldLocalBounds = getLocalBounds(component);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.isConsumed()) {
            return;
        }
        if (pressedAt != null) {
            Point winPt = e.getPoint();
            if (RECTANGLE_RESIZE_MODE.equals(mode)) {
                // rectangle resize mode (ensure positive values)
                if (winPt.x < pressedAt.x) {
                    zoomBox.x = winPt.x;
                    zoomBox.width = -winPt.x + pressedAt.x;
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
                component.repaint();
            } else { 
                // pan mode
                String curMode = MouseEvent.getModifiersExText(e.getModifiersEx());
                if (RESTRICTED_MOVEMENT_MODE.equals(curMode) || RESTRICTED_MOVEMENT_MODE_ALT.equals(curMode)) {
                    if (Math.abs(winPt.y - pressedAt.y) < Math.abs(winPt.x - pressedAt.x)) {
                        winPt.y = pressedAt.y;
                    } else {
                        winPt.x = pressedAt.x;
                    }
                }
                double dx = (winPt.x - pressedAt.x) * component.getInverseTransform().getScaleX();
                double dy = (winPt.y - pressedAt.y) * component.getInverseTransform().getScaleY();

                try {
                    setDesiredLocalBounds(component,
                            new Rectangle2D.Double(
                                    oldLocalBounds.getX() - dx, oldLocalBounds.getY() - dy, 
                                    oldLocalBounds.getWidth(), oldLocalBounds.getHeight()));
                } catch (Exception ex) {
                    Logger.getLogger(PanAndZoomHandler.class.getName()).log(Level.WARNING, "Unable to set desired local bounds", ex);
                }
            }
        }
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

        zoomPoint(component, toLocal(component, mouseLoc),
                (e.getWheelRotation() > 0) ? 1.05 : 0.95);
    }

    //<editor-fold defaultstate="collapsed" desc="STATIC UTILITIES">
    //
    // DELEGATE TRANSFORM OPERATIONS
    //
    
    /**
     * Convert to local bounds
     */
    private static Point2D toLocal(GraphicComponent gc, Point2D pt) {
        if (gc.getInverseTransform() == null) {
            gc.setTransform(new AffineTransform());
        }
        Point2D res = gc.getInverseTransform().transform(pt, null);
        return res;
    }

    /**
     * Get current range of values displayed in component.
     */
    private static Rectangle2D.Double getLocalBounds(GraphicComponent gc) {
        if (gc.getInverseTransform() == null) {
            gc.setTransform(new AffineTransform());
        }
        // apply inverse transform to min point of component bounds and max point of component bounds
        Point2D.Double min = new Point2D.Double(gc.getX(), gc.getY());
        Point2D.Double max = new Point2D.Double(gc.getBounds().getMaxX(), gc.getBounds().getMaxY());
        Point2D cmin = gc.getInverseTransform().transform(min, null);
        Point2D cmax = gc.getInverseTransform().transform(max, null);
        Rectangle2D.Double res = new Rectangle2D.Double(cmin.getX(), cmin.getY(), cmax.getX() - cmin.getX(), cmax.getY() - cmin.getY());
        return res;
    }

    /**
     * Updates component transform so given rectangle is included within
     */
    private static void setDesiredLocalBounds(GraphicComponent gc, Rectangle2D rect) {
        Rectangle bds = gc.getBounds();
        double scalex = rect.getWidth() / bds.getWidth();
        double scaley = rect.getHeight() / bds.getHeight();
        double scale = Math.max(scalex, scaley);
        AffineTransform res = new AffineTransform();
        res.translate(bds.getCenterX(), bds.getCenterY());
        res.scale(1 / scale, 1 / scale);
        res.translate(-rect.getCenterX(), -rect.getCenterY());
        gc.setTransform(res);
    }

    /**
     * Sets visometry bounds based on the zoom about a given point.
     * The effective zoom point is between current center and mouse position...
     * close to center => 100% at the given point, close to edge => 10% at
     * the given point.
     */
    public static void zoomPoint(GraphicComponent gc, Point2D localZoomPoint, double factor) {
        Rectangle2D.Double localBounds = getLocalBounds(gc);
        double cx = .1 * localZoomPoint.getX() + .9 * localBounds.getCenterX();
        double cy = .1 * localZoomPoint.getY() + .9 * localBounds.getCenterY();
        double wx = localBounds.getWidth();
        double wy = localBounds.getHeight();
        setDesiredLocalBounds(gc, new Rectangle2D.Double(cx - .5 * factor * wx, cy - .5 * factor * wy, factor * wx, factor * wy));
    }

    /**
     * Creates an animating zoom using a particular timer, about the center of
     * the screen
     *
     * @param factor how far to zoom, representing the new scale
     */
    public static void zoomCenterAnimated(GraphicComponent gc, double factor) {
        Rectangle2D.Double rect = getLocalBounds(gc);
        Point2D.Double center = new Point2D.Double(rect.getCenterX(), rect.getCenterY());
        zoomPointAnimated(gc, center, factor);
    }

    /**
     * Creates an animating zoom using a particular timer.
     *
     * @param p the coordinate of the point to center zoom about, in local
     * coordiantes
     * @param factor how far to zoom, representing the new scale
     */
    public static void zoomPointAnimated(final GraphicComponent gc, Point2D.Double p, final double factor) {
        Rectangle2D.Double rect = getLocalBounds(gc);
        final double cx = .1 * p.x + .9 * rect.getCenterX();
        final double cy = .1 * p.y + .9 * rect.getCenterY();
        final double wx = rect.getWidth();
        final double wy = rect.getHeight();

        Thread runner = new Thread(new Runnable() {
            public void run() {
                double zoomValue = 0.0;
                for (int i = 0; i <= ANIM_STEPS; i++) {
                    if (i > 0) {
                        pause(5);
                    }
                    zoomValue = 1.0 + (factor - 1.0) * i / (double) ANIM_STEPS;
                    setDesiredLocalBounds(gc, new Rectangle2D.Double(cx - .5 * zoomValue * wx, cy - .5 * zoomValue * wy, wx + zoomValue * wx, wy + zoomValue * wy));
                }
            }
        });
        runner.start();
    }

    /**
     * Zooms to the boundaries of a particular box.
     *
     * @param zoomBoxWinCoords the boundary of the zoom box (in window
     * coordinates)
     */
    public static void zoomBoxAnimated(GraphicComponent vis, Rectangle2D zoomBoxWinCoords) {
        zoomCoordBoxAnimated(vis,
                toLocal(vis, new Point2D.Double(zoomBoxWinCoords.getMinX(), zoomBoxWinCoords.getMinY())),
                toLocal(vis, new Point2D.Double(zoomBoxWinCoords.getMaxX(), zoomBoxWinCoords.getMaxY())));
    }

    /**
     * Zooms to the boundaries of a particular box.
     *
     * @param gc associated visometry
     * @param newMin min of zoom box
     * @param newMax max of zoom box
     */
    public static void zoomCoordBoxAnimated(final GraphicComponent gc, final Point2D newMin, final Point2D newMax) {
        final Rectangle2D.Double rect = getLocalBounds(gc);
        final double xMin = rect.getX();
        final double yMin = rect.getY();
        final double xMax = rect.getMaxX();
        final double yMax = rect.getMaxY();
        final double nxMin = newMin.getX();
        final double nyMin = newMin.getY();
        final double nxMax = newMax.getX();
        final double nyMax = newMax.getY();

        Thread runner = new Thread(new Runnable() {
            public void run() {
                for (double factor = 0; factor < 1.0; factor += 1.0 / ANIM_STEPS) {
                    if (factor > 0) {
                        pause(5);
                    }
                    double x1 = xMin + (nxMin - xMin) * factor;
                    double y1 = yMin + (nyMin - yMin) * factor;
                    double x2 = xMax + (nxMax - xMax) * factor;
                    double y2 = yMax + (nyMax - yMax) * factor;
                    setDesiredLocalBounds(gc, new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1));
                }
            }
        });
        runner.start();
    }
    
    private static void pause(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Logger.getLogger(PanAndZoomHandler.class.getName()).log(Level.WARNING, "Interrupted", e);
        }
    }
    
    //</editor-fold> 
}
