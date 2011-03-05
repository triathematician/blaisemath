/**
 * VSegmentEntry.java
 * Created Jan 29, 2011
 */
package org.bm.blaise.specto.graphics;

import utils.RelativePointBean;
import org.bm.blaise.graphics.BasicShapeEntry;
import org.bm.blaise.graphics.renderer.ShapeRenderer;
import java.awt.geom.Line2D;
import org.bm.blaise.specto.Visometry;
import org.bm.blaise.specto.VisometryProcessor;

/**
 * An entry for a segment drawn between two points.
 * @author Elisha
 */
public class VSegmentEntry<C> extends AbstractVGraphicEntry<C> {

    /** Handles the local point */
    RelativePointBean<C> local1, local2;
    /** The window entries */
    BasicShapeEntry window;

    /** Tooltip (may be null) */
    String tooltip;
    /** Default renderer used to draw the points (may be null) */
    ShapeRenderer rend;

    /** Construct with specified bean to handle dragging (may be null) */
    public VSegmentEntry(RelativePointBean<C> local1, RelativePointBean<C> local2) {
        this.local1 = local1;
        this.local2 = local2;
    }

    /** Construct with specified bean to handle dragging (may be null) */
    public VSegmentEntry(RelativePointBean<C> local1, RelativePointBean<C> local2, ShapeRenderer rend) {
        this.local1 = local1;
        this.local2 = local2;
        this.rend = rend;
    }

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        Line2D.Double line = new Line2D.Double(processor.convert(local1.getPoint(), vis), processor.convert(local2.getPoint(), vis));
        if (window == null)
            window = new BasicShapeEntry(line, rend);
        else
            window.setPrimitive(line);
        window.setTooltip(tooltip == null ? "Segment" : tooltip);
        setUnconverted(false);
    }

    public BasicShapeEntry getWindowEntry() { return window; }

    public ShapeRenderer getRenderer() { return rend; }
    public void setRenderer(ShapeRenderer rend) {
        if (this.rend != rend) {
            this.rend = rend;
            setUnconverted(true);
        }
    }

    public String getTooltip() { return tooltip; }
    public void setTooltip(String tip) {
        if (this.tooltip == null ? tip != null : !this.tooltip.equals(tip)) {
            this.tooltip = tip;
            setUnconverted(true);
        }
    }
}
