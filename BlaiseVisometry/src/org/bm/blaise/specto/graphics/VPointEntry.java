/**
 * VPointEntry.java
 * Created Jan 29, 2011
 */
package org.bm.blaise.specto.graphics;

import utils.RelativePointBean;
import org.bm.blaise.graphics.BasicPointEntry;
import org.bm.blaise.graphics.renderer.PointRenderer;
import java.awt.geom.Point2D;
import org.bm.blaise.specto.Visometry;
import org.bm.blaise.specto.VisometryProcessor;

/**
 * An entry for a draggable point at an arbitrary local coordinate.
 * @param <C> local coordinate type
 * @author Elisha
 */
public class VPointEntry<C> extends AbstractVGraphicEntry<C> {

    /** Handles the local point */
    RelativePointBean<C> local;
    /** The window entry */
    BasicPointEntry window;

    /** Renderer used to draw the point (may be null) */
    PointRenderer rend;

    /** Construct with specified bean to handle dragging (may be null) */
    public VPointEntry(RelativePointBean local) {
        this.local = local;
    }

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        C loc = local.getPoint();
        Point2D p = processor.convert(loc, vis);
        if (window == null) {
            window = new BasicPointEntry(p, rend);
            window.setMouseListener(new VGraphicMouseListener.PointDragger<C>(local).adapter());
        } else
            window.setPoint(p);
        window.setTooltip(
                loc instanceof Point2D ? String.format("(%.2f, %.2f)", ((Point2D)loc).getX(), ((Point2D)loc).getY())
                : loc + "");
        setUnconverted(false);
    }

    public BasicPointEntry getWindowEntry() {
        return window;
    }

    public PointRenderer getRenderer() {
        return rend;
    }

    public void setRenderer(PointRenderer rend) {
        if (this.rend != rend) {
            this.rend = rend;
            setUnconverted(true);
        }
    }

}
