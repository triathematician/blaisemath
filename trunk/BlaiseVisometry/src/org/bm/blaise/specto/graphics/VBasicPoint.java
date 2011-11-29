/**
 * VBasicPoint.java
 * Created Jan 29, 2011
 */
package org.bm.blaise.specto.graphics;

import org.bm.util.DraggablePointBean;
import org.bm.blaise.graphics.BasicPointGraphic;
import org.bm.blaise.style.PointStyle;
import java.awt.geom.Point2D;
import org.bm.util.PointFormatters;

/**
 * An entry for a draggable point at an arbitrary local coordinate.
 * 
 * @param <C> local coordinate type
 * @author Elisha
 */
public class VBasicPoint<C> extends VGraphicSupport<C> implements DraggablePointBean<C> {

    /** The point */
    protected C point;
    /** The window entry */
    protected final BasicPointGraphic window = new BasicPointGraphic();

    /** Construct without a drag handler */
    public VBasicPoint(final C initialPoint) {
        this.point = initialPoint;
        window.setMouseListener(new VGraphicPointDragger<C>(this).adapter());
    }
    
    //
    // PROPERTIES
    //

    public BasicPointGraphic getWindowEntry() {
        return window;
    }

    public PointStyle getStyle() {
        return window.getStyle();
    }

    public void setStyle(PointStyle rend) {
        window.setStyle(rend);
    }
    
    //
    // DraggablePointBean PROPERTIES
    //
    
    public C getPoint() { 
        return point;
    }
    
    public void setPoint(C point) {
        if (!((this.point == null && point == null) || (this.point != null && this.point.equals(point)))) {
            this.point = point;                            
            setUnconverted(true);
        }
    }
    
    public void setPoint(C initial, C dragStart, C dragFinish) {
        setPoint(relativePoint(initial, dragStart, dragFinish));
    }
    
    public static <C> C relativePoint(C initial, C dragStart, C dragFinish) {
        C newPoint;
        if (initial instanceof Point2D) {
            Point2D i = (Point2D) initial, s = (Point2D) dragStart, f = (Point2D) dragFinish;
            newPoint = (C) new Point2D.Double(i.getX()+f.getX()-s.getX(), i.getY()+f.getY()-s.getY());
        } else if (initial instanceof Number) {
            Number i = (Number) initial, s = (Number) dragStart, f = (Number) dragFinish;
            Double nue = i.doubleValue() + f.doubleValue() - s.doubleValue();
            if (i instanceof Double)
                newPoint = (C) nue;
            else if (i instanceof Float)
                newPoint = (C) new Float(nue.floatValue());
            else if (i instanceof Integer)
                newPoint = (C) new Integer(nue.intValue());
            else if (i instanceof Long)
                newPoint = (C) new Long(nue.longValue());
            else if (i instanceof Short)
                newPoint = (C) new Short(nue.shortValue());
            else {
                System.err.println("VPointGraphic: Unsupported type for drag: initial point = " + i);
                newPoint = null;
            }
        } else
            newPoint = dragFinish;
        return newPoint;        
    }
    
    //
    // CONVERSION
    //

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        Point2D p = processor.convert(point, vis);
        window.setPoint(p);
        window.setTooltip(
                point instanceof Point2D ? PointFormatters.formatPoint((Point2D) point, 2)
                : point + "");
        setUnconverted(false);
    }

}
