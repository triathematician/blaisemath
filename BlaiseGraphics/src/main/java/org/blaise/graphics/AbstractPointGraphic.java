/**
 * BasicPointGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nonnull;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.blaise.style.PointStyle;
import org.blaise.util.PointBean;
import org.blaise.util.PointFormatters;

/**
 * A point with position, orientation, and an associated style.
 * Implements {@code PointBean}, allowing the point to be dragged around.
 * {@link ChangeEvent}s are generated when the graphic's point changes.
 * 
 * @see PointStyle
 * 
 * @author Elisha Peterson
 */
public abstract class AbstractPointGraphic extends GraphicSupport implements PointBean<Point2D> {

    /** The object that will be drawn. */
    protected Point2D point;

    /**
     * Construct with default point
     */
    protected AbstractPointGraphic() {
        this(new Point2D.Double());
    }

    /** 
     * Construct with given primitive and style.
     * @param p initial point
     */
    protected AbstractPointGraphic(Point2D p) {
        setPoint(p);
        PointBeanDragger dragger = new PointBeanDragger(this);
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
    }

    @Override
    public String toString() {
        return "PointGraphic:"+PointFormatters.formatPoint(point, 2);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    public Point2D getPoint() { 
        return point;
    }
    public final void setPoint(Point2D p) {
        if (point != p) {
            point = new Point2D.Double(p.getX(), p.getY());
            fireGraphicChanged();
            fireStateChanged();
        }
    }
    
    //</editor-fold>
    

    public boolean contains(Point2D p) {
        PointStyle style = drawStyle();
        return p.distance(point) <= style.getMarkerRadius();
    }

    public boolean intersects(Rectangle2D box) {
        PointStyle style = drawStyle();
        double r = (double) style.getMarkerRadius();
        return new Ellipse2D.Double(point.getX()-r,point.getY()-r,2*r,2*r).intersects(box);
    }
    
    /**
     * Return draw style for this object.
     * @return draw style
     */
    public abstract @Nonnull PointStyle drawStyle();
    
    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //
    
    protected ChangeEvent changeEvent = new ChangeEvent(this);
    protected EventListenerList listenerList = new EventListenerList();

    public void addChangeListener(ChangeListener l) { 
        listenerList.add(ChangeListener.class, l);
    }
    
    public void removeChangeListener(ChangeListener l) { 
        listenerList.remove(ChangeListener.class, l); 
    }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    public void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null) {
                    return;
                }
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }
    
    //</editor-fold>
    
}
