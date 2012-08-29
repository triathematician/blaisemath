/**
 * VBasicPoint.java
 * Created Jan 29, 2011
 */
package org.blaise.visometry;

import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.blaise.graphics.BasicPointGraphic;
import org.blaise.style.PointStyle;
import org.blaise.util.PointFormatters;

/**
 * An entry for a draggable point at an arbitrary local coordinate.
 *
 * @param <C> local coordinate type
 * @author Elisha
 */
public class VBasicPoint<C> extends VGraphicSupport<C> implements ChangeListener {

    /** The point */
    protected C point;
    /** The window entry */
    protected final BasicPointGraphic window = new BasicPointGraphic();

    /** Construct without a drag handler */
    public VBasicPoint(final C initialPoint) {
        this.point = initialPoint;
        window.addChangeListener(this);
    }
    
    //
    // PROPERTIES
    //
    
    public synchronized BasicPointGraphic getWindowEntry() {
        return window;
    }

    public synchronized C getPoint() {
        return point;
    }

    public synchronized void setPoint(C point) {
        if (!((this.point == null && point == null) || (this.point != null && this.point.equals(point)))) {
            this.point = point;
            setUnconverted(true);
        }
    }

    public PointStyle getStyle() {
        return window.getStyle();
    }

    public void setStyle(PointStyle rend) {
        window.setStyle(rend);
    }
    
    //
    // CONVERSION
    //
    
    protected transient Point2D winPt = null;

    public synchronized void stateChanged(ChangeEvent e) {
        if (e.getSource() == window) {
            winPt = window.getPoint();
            setUnconverted(true);
        }
    }

    public synchronized void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        if (winPt != null) {
            C loc = vis.toLocal(winPt);
            this.point = loc;
        } else {
            window.setPoint(processor.convert(point, vis));
        }
        winPt = null;
        window.setDefaultTooltip(
                point instanceof Point2D ? PointFormatters.formatPoint((Point2D) point, 2)
                : point + "");
        setUnconverted(false);
    }

}
