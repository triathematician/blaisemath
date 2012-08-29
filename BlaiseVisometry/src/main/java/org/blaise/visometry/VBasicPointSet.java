/**
 * VBasicPoint.java
 * Created Jan 29, 2011
 */
package org.blaise.visometry;

import java.awt.geom.Point2D;
import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.blaise.graphics.BasicPointSetGraphic;
import org.blaise.style.PointStyle;
import org.blaise.util.Delegator;
import org.blaise.util.PointFormatters;

/**
 * An entry for a draggable point at an arbitrary local coordinate.
 *
 * @param <C> local coordinate type
 * @author Elisha
 */
public class VBasicPointSet<C> extends VGraphicSupport<C> implements PropertyChangeListener {

    /** The points */
    protected C[] point;
    /** The window entry */
    protected final BasicPointSetGraphic window = new BasicPointSetGraphic();

    /** Construct without a drag handler */
    public VBasicPointSet(C[] initialPoint) {
        this.point = initialPoint;
        window.addPropertyChangeListener(this);
    }

    //
    // PROPERTIES
    //

    public BasicPointSetGraphic getWindowEntry() {
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

    public synchronized C getPoint(int i) {
        return point[i];
    }

    public synchronized void setPoint(int i, C point) {
        if (!((this.point == null && point == null) || (this.point != null && this.point.equals(point)))) {
            this.point[i] = point;
            setUnconverted(true);
        }
    }

    public synchronized C[] getPoint() {
        return point;
    }

    public synchronized void setPoint(C[] point) {
        this.point = point;
        setUnconverted(true);
    }

    //
    // CONVERSION
    //
    
    protected transient Point2D winPt = null;
    protected transient int idx = -1;

    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt instanceof IndexedPropertyChangeEvent) {
            IndexedPropertyChangeEvent ipce = (IndexedPropertyChangeEvent) evt;
            winPt = (Point2D) ipce.getNewValue();
            idx = ipce.getIndex();
            setUnconverted(true);
        }
    }

    VisTipDelegate<C> tipper = new VisTipDelegate<C>();

    public synchronized void convert(final Visometry<C> vis, VisometryProcessor<C> processor) {
        if (winPt != null) {
            C loc = vis.toLocal(winPt);
            if (idx >= 0 && idx < point.length) {
                point[idx] = loc;
            }
        } else {
            Point2D[] p = processor.convertToArray(point, vis);
            window.setPoint(p);
        }
        winPt = null;
        idx = -1;
        tipper.vis = vis;
        window.setPointTipDelegate(tipper);
        setUnconverted(false);
    }

    /** Converts a point to a tip string */
    public static class VisTipDelegate<C> implements Delegator<Point2D,String> {
        Visometry<C> vis;
        public String of(Point2D src) {
            C local = vis.toLocal(src);
            return local instanceof Point2D ? PointFormatters.formatPoint((Point2D) local, 2)
                    : local + "";
        }
    }

}
