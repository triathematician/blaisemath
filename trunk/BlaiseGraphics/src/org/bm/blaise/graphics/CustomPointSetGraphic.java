/**
 * CustomPointSetGraphic.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.graphics;

import org.bm.blaise.style.PointStyle;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.List;
import org.bm.blaise.style.LabeledPointStyle;
import org.bm.blaise.style.ObjectStyler;
import org.bm.blaise.style.StringStyle;
import org.bm.util.Delegator;
import org.bm.util.PointManager;

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
public class CustomPointSetGraphic<Src> extends GraphicSupport {

    /** Manages points and their styles */
    protected ObjectStyler<Src, PointStyle> styler = new ObjectStyler<Src, PointStyle>();
    
    /** Point manager. Maintains objects and their locations, and enables mouse dragging. */
    protected final PointManager<Src, Point2D> pointManager = new PointManager<Src, Point2D>(
                new Delegator<Src, Point2D>(){ public Point2D of(Src src) { return new Point2D.Double(); } }
            ) {
                @Override
                public void setPoint(int i, Point2D p) {
                    super.setPoint(i, p);
                    fireGraphicChanged();
                }
                @Override
                public int indexOf(Point2D point, Point2D dragPoint) {
                    return CustomPointSetGraphic.this.indexOf(point);
                }
            };
    
    //
    // CONSTRUCTORS
    //
    
    /**
     * Construct with no points
     */
    public CustomPointSetGraphic() {
        this((List<Src>) Collections.emptyList(), null);
    }

    /** 
     * Construct with no style (will use the default) 
     * @param objects the source objects
     * @param delegate used for point placement
     */
    public CustomPointSetGraphic(List<? extends Src> objects, Delegator<Src, Point2D> delegate) { 
        if (delegate != null)
            pointManager.setInitialPointDelegate(delegate);
        pointManager.setObjects(objects);
        addMouseListener(new GMouseIndexedDragger(pointManager));
    }

    @Override
    public String toString() {
        return "Point Set";
    }

    //
    // PROPERTIES
    //

    /**
     * Return source objects
     * @return source objects
     */
    public List<? extends Src> getObjects() {
        return pointManager.getObjects();
    }
    
    /**
     * Sets source objects
     * @param objects the objects
     */
    public synchronized void setObjects(List<? extends Src> objects) {
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
        fireAppearanceChanged();
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
        return rend == null ? parent.getStyleProvider().getPointStyle() : rend;
    }

    public synchronized void draw(Graphics2D canvas) {
        for (Src o : pointManager.getObjects()) {
            Point2D pt = pointManager.of(o);
            PointStyle style = drawStyle(o);
            if (styler.getLabelDelegate() != null) {
                String label = styler.getLabelDelegate().of(o);
                if (style instanceof LabeledPointStyle)
                    ((LabeledPointStyle) style).draw(pt, label, canvas, visibility);
                else
                    style.draw(pt, canvas, visibility);
                if (styler.getLabelStyleDelegate() != null) {
                    StringStyle labelStyle = styler.getLabelStyleDelegate().of(o);
                    if (labelStyle != null)
                        labelStyle.draw(pt, label, canvas, visibility);
                }
            } else
                style.draw(pt, canvas, visibility);
        }
    }

    public int indexOf(Point2D nearby) {
        List<? extends Src> objects = pointManager.getObjects();
        for (int i = objects.size()-1; i >= 0; i--) {
            Src o = objects.get(i);
            if (drawStyle(o).shape(pointManager.of(o)).contains(nearby))
                return i;
        }
        return -1;
    }

    public synchronized boolean contains(Point p) {
        return indexOf(p) != -1;
    }

    @Override
    public synchronized String getTooltip(Point p) {
        int i = indexOf(p);
        return i == -1 ? null 
                : styler.getTipDelegate() == null ? tooltip 
                : styler.getTipDelegate().of(pointManager.getObjects().get(i));
    }
    
}
