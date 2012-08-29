/**
 * BasicPointGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.blaise.style.PointStyle;
import org.blaise.util.PointBean;
import org.blaise.util.PointFormatters;

/**
 * A point with position, orientation, and an associated style.
 * Implements {@code PointBean}, allowing the point to be dragged around.
 * ChangeEvents are generated when the graphic's point changes.
 * 
 * @see PointStyle
 * 
 * @author Elisha Peterson
 */
public abstract class AbstractPointGraphic extends GraphicSupport implements PointBean<Point2D> {

    /** The object that will be drawn. */
    Point2D point;

    /** 
     * Construct with given primitive and style.
     * @param p initial point
     */
    public AbstractPointGraphic(Point2D p) {
        this.point = p;
        PointBeanDragger dragger = new PointBeanDragger(this);
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
    }

    @Override
    public String toString() {
        return "Point"+PointFormatters.formatPoint(point, 2);
    }

    public Point2D getPoint() { 
        return point;
    }
    public void setPoint(Point2D p) {
        if (point != p) {
            point = new Point2D.Double(p.getX(), p.getY());
            fireGraphicChanged();
            fireStateChanged();
        }
    }

    public boolean contains(Point p) {
        PointStyle style = drawStyle();
        return p.distance(point) <= style.getRadius();
    }

    public boolean intersects(Rectangle box) {
        PointStyle style = drawStyle();
        double r = (double) style.getRadius();
        return new Ellipse2D.Double(point.getX()-r,point.getY()-r,2*r,2*r).intersects(box);
    }
    
    /**
     * Return draw style for this object.
     * @return draw style
     */
    public abstract PointStyle drawStyle();
    
    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //
    
    protected ChangeEvent changeEvent = new ChangeEvent(this);
    protected EventListenerList listenerList = new EventListenerList();

    public synchronized void addChangeListener(ChangeListener l) { 
        listenerList.add(ChangeListener.class, l);
    }
    
    public synchronized void removeChangeListener(ChangeListener l) { 
        listenerList.remove(ChangeListener.class, l); 
    }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    public synchronized void fireStateChanged() {
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
