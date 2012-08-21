/**
 * CustomPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.blaise.style.LabeledPointStyle;
import org.blaise.style.ObjectStyler;
import org.blaise.style.PointStyle;
import org.blaise.style.StringStyle;
import org.blaise.style.VisibilityHint;
import org.blaise.util.Delegator;
import org.blaise.util.PointManager;

/**
 * Provides a set of points that are customizable in appearance and behavior.
 * In contract to {@link BasicPointSetGraphic}, where the points are provided
 * explicitly, this class stores a list of arbitrary objects and defers to
 * <i>delegates</i> to provide the functionality of the graphic.
 *
 * @param <Src> the type of object being displayed
 *
 * @see PointStyle
 *
 * @author Elisha Peterson
 */
public class CustomPointSetGraphic<Src> extends GraphicSupport implements IndexedVisibilityGraphic<Point2D> {

    /** Manages points and their styles */
    protected ObjectStyler<Src, PointStyle> styler = new ObjectStyler<Src, PointStyle>();
    /** Optional delegate for visibility keys */
    protected Delegator<Point2D, VisibilityHint> visibilityDelegate;

    /** Point manager. Maintains objects and their locations, and enables mouse dragging. */
    protected final PointManager<Src, Point2D> pointManager =
        new PointManager<Src, Point2D>( new Delegator<Src, Point2D>(){ public Point2D of(Src src) { return new Point2D.Double(); } } ) {
            @Override
            public void setPoint(int i, Point2D p) {
                super.setPoint(i, p);
                fireGraphicChanged();
            }
            @Override
            public int indexOf(Point2D point, Point2D dragPoint) {
                return CustomPointSetGraphic.this.indexOf(point, dragPoint);
            }
        };

    //
    // CONSTRUCTORS
    //

    /**
     * Construct with no points
     */
    public CustomPointSetGraphic() {
        this((Set<Src>) Collections.emptySet(), null);
    }

    /**
     * Construct with no style (will use the default)
     * @param objects the source objects
     * @param delegate used for point placement
     */
    public CustomPointSetGraphic(Set<? extends Src> objects, Delegator<Src, Point2D> delegate) {
        if (delegate != null)
            pointManager.setInitialPointDelegate(delegate);
        pointManager.setObjects(objects);
        IndexedPointBeanDragger dragger = new IndexedPointBeanDragger(pointManager);
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
        addMouseListener(new IndexedGraphicHighlighter());
    }

    @Override
    public String toString() {
        return "Point Set";
    }

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public int getPointCount() {
        return pointManager.getPointCount();
    }

    public Point2D getPoint(int i) {
        return pointManager.getPoint(i);
    }

    public void setPoint(int i, Point2D p) {
        pointManager.setPoint(i, p);
    }

    /**
     * Return source objects
     * @return source objects
     */
    public Set<? extends Src> getObjects() {
        return pointManager.getObjects();
    }

    /**
     * Sets source objects
     * @param objects the objects
     */
    public void setObjects(Set<? extends Src> objects) {
        pointManager.setObjects(objects);
        fireGraphicChanged();
    }

    /**
     * Returns object that handles point locations
     * @return point location manager
     */
    public PointManager<Src, Point2D> getPointManager() {
        return pointManager;
    }

    /**
     * Returns the current point location delegate
     * @return  point location delegate
     */
    public Delegator<Src, Point2D> getPointDelegate() {
        return pointManager.getInitialPointDelegate();
    }

    /**
     * Sets the current point location delegate
     * @param pointer the new point location delegate
     */
    public void setPointDelegate(Delegator<Src, Point2D> pointer) {
        if (getPointDelegate() != pointer) {
            pointManager.setInitialPointDelegate(pointer);
            fireGraphicChanged();
        }
    }

    /**
     * Returns object used to style points
     * @return styler object styler
     */

    public ObjectStyler<Src, PointStyle> getStyler() {
        return styler;
    }

    /**
     * Sets object used to style points
     * @param styler object styler
     */
    public void setStyler(ObjectStyler<Src, PointStyle> styler) {
        this.styler = styler;
        fireGraphicChanged();
    }

    public Delegator<Point2D, VisibilityHint> getVisibilityDelegate() {
        return visibilityDelegate;
    }

    public void setVisibilityDelegate(Delegator<Point2D, VisibilityHint> visibilityDelegate) {
        this.visibilityDelegate = visibilityDelegate;
    }

    //</editor-fold>


    //
    // GRAPHIC METHODS
    //

    public int indexOf(Point2D nearby, Point2D dragPoint) {
        // identify individual points at specific locations
        Src[] objects = pointManager.getObjectArray();
        for (int i = objects.length-1; i >= 0; i--) {
            if (drawStyle(objects[i]).shape(pointManager.of(objects[i])).contains(nearby)) {
                return i;
            }
        }
        return -1;
    }


    public void setVisibility(final int i, final VisibilityHint key) {
        if (i == -1) {
            visibilityDelegate = null;
            visibility = key;
        } else {
            if (i < getPointCount()) {
            setVisibilityDelegate(new Delegator<Point2D, VisibilityHint>(){
                public VisibilityHint of(Point2D src) { return src == getPoint(i) ? key : visibility; }
                });
            } else
                Logger.getLogger(BasicPointSetGraphic.class.getName()).log(Level.SEVERE, "Visibility index out of range: {0}", i);
        }
        fireGraphicChanged();
    }

    public synchronized boolean contains(Point p) {
        Src[] objects = pointManager.getObjectArray();
        for (int i = objects.length-1; i >= 0; i--) {
            if (drawStyle(objects[i]).shape(pointManager.of(objects[i])).contains(p))
                return true;
        }
        return false;
    }

    public boolean intersects(Rectangle box) {
        Src[] objects = pointManager.getObjectArray();
        for (int i = objects.length-1; i >= 0; i--) {
            if (drawStyle(objects[i]).shape(pointManager.of(objects[i])).intersects(box))
                return true;
        }
        return false;
    }

    @Override
    public synchronized String getTooltip(Point p) {
        int i = indexOf(p, null);
        return i == -1 ? null
                : styler.getTipDelegate() == null ? tooltip
                : styler.getTipDelegate().of(pointManager.getObjectArray()[i]);
    }


    //
    // DRAW METHODS
    //

    /**
     * Return the actual style used for drawing a particular object.
     * @param source the source object used to render
     * @return the style... if not provided by the styler, defaults to the parent style
     */
    private PointStyle drawStyle(Src source) {
        PointStyle rend = styler.getStyleDelegate() == null ? null : styler.getStyleDelegate().of(source);
        return rend == null ? parent.getStyleProvider().getPointStyle(this) : rend;
    }

    public synchronized void draw(Graphics2D canvas) {
        // prevent changes during draw
        synchronized (pointManager) {
            for (Src o : pointManager.getObjects()) {
                Point2D pt = pointManager.of(o);
                PointStyle style = drawStyle(o);
                VisibilityHint vis = visibilityDelegate == null ? visibility : visibilityDelegate.of(pt);
                if (styler.getLabelDelegate() != null) {
                    String label = styler.getLabelDelegate().of(o);
                    if (style instanceof LabeledPointStyle)
                        ((LabeledPointStyle) style).draw(pt, label, canvas, vis);
                    else
                        style.draw(pt, canvas, vis);
                    if (styler.getLabelStyleDelegate() != null) {
                        StringStyle labelStyle = styler.getLabelStyleDelegate().of(o);
                        if (labelStyle != null)
                            labelStyle.draw(pt, label, canvas, vis);
                    }
                } else
                    style.draw(pt, canvas, vis);
            }
        }
    }

}
