/**
 * VBasicPoint.java
 * Created Jan 29, 2011
 */
package org.bm.blaise.specto.graphics;

import org.bm.blaise.style.PointStyle;
import java.awt.geom.Point2D;
import org.bm.blaise.graphics.BasicPointSetGraphic;
import org.bm.util.Delegator;
import org.bm.util.DraggableIndexedPointBean;
import org.bm.util.PointFormatters;

/**
 * An entry for a draggable point at an arbitrary local coordinate.
 *
 * @param <C> local coordinate type
 * @author Elisha
 */
public class VBasicPointSet<C> extends VGraphicSupport<C> implements DraggableIndexedPointBean<C> {

    /** The points */
    protected C[] point;
    /** The window entry */
    protected final BasicPointSetGraphic window = new BasicPointSetGraphic();

    /** Construct without a drag handler */
    public VBasicPointSet(C[] initialPoint) {
        this.point = initialPoint;
        window.clearMouseListeners();
        window.addMouseListener(new VGMouseIndexedDragger<C>(this).adapter());
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

    public synchronized int getPointCount() {
        return point.length;
    }

    public synchronized int indexOf(Point2D pt, C dragStart) {
        return window.indexOf(pt, pt);
    }

    public synchronized void setPoint(int index, C initial, C dragStart, C dragFinish) {
        setPoint(index, VBasicPoint.relativePoint(initial, dragStart, dragFinish));
    }

    //
    // CONVERSION
    //

    VisTipDelegate<C> tipper = new VisTipDelegate<C>();

    public void convert(final Visometry<C> vis, VisometryProcessor<C> processor) {
        Point2D[] p = processor.convertToArray(point, vis);
        window.setPoint(p);
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
