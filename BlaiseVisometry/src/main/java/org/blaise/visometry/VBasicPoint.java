/**
 * VBasicPoint.java
 * Created Jan 29, 2011
 */
package org.blaise.visometry;

import com.google.common.base.Objects;
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
public class VBasicPoint<C> extends VGraphicSupport<C> {

    /** The point */
    protected C point;
    /** The windowGraphic entry */
    protected final BasicPointGraphic windowGraphic = new BasicPointGraphic();

    /**
     * Initialize point
     * @param initialPoint point's local coords
     */
    public VBasicPoint(final C initialPoint) {
        this.point = initialPoint;
        windowGraphic.addChangeListener(new ChangeListener(){
            public synchronized void stateChanged(ChangeEvent e) {
                setPoint(parent.getVisometry().toLocal(windowGraphic.getPoint()));
                windowGraphic.setDefaultTooltip(
                        point instanceof Point2D ? PointFormatters.formatPoint((Point2D) point, 2)
                        : point + "");
            }
        });
    }

    //
    // PROPERTIES
    //

    public final synchronized BasicPointGraphic getWindowGraphic() {
        return windowGraphic;
    }

    public final synchronized C getPoint() {
        return point;
    }

    public final synchronized void setPoint(C point) {
        if (!Objects.equal(this.point, point)) {
            this.point = point;
            setUnconverted(true);
        }
    }

    public final PointStyle getStyle() {
        return windowGraphic.getStyle();
    }

    public final void setStyle(PointStyle rend) {
        windowGraphic.setStyle(rend);
    }

    //
    // CONVERSION
    //


    public synchronized void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        windowGraphic.setPoint(processor.convert(point, vis));
        windowGraphic.setDefaultTooltip(
                point instanceof Point2D ? PointFormatters.formatPoint((Point2D) point, 2)
                : point + "");
        setUnconverted(false);
    }

}
