/**
 * VSegmentEntry.java
 * Created Jan 29, 2011
 */
package org.bm.blaise.specto.graphics;

import utils.RelativePointBean;
import org.bm.blaise.graphics.BasicShapeEntry;
import org.bm.blaise.graphics.renderer.ShapeRenderer;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import org.bm.blaise.specto.Visometry;
import org.bm.blaise.specto.VisometryProcessor;

/**
 * An entry for a segment drawn between two points.
 * @author Elisha
 */
public class VSegmentEntry<C> extends AbstractVGraphicEntry<C> {

    /** Handles the local point */
    RelativePointBean<C> local1, local2;
    /** Radius (window coords) used to adjust locations upon conversion */
    float rad1 = 0f, rad2 = 0;
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

    /** Construct with specified bean to handle dragging (may be null). Point locations are adjusted by radial parameters. */
    public VSegmentEntry(RelativePointBean<C> local1, float rad1, RelativePointBean<C> local2, float rad2, ShapeRenderer rend) {
        this.local1 = local1;
        this.local2 = local2;
        this.rad1 = rad1;
        this.rad2 = rad2;
        this.rend = rend;
    }

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        Point2D.Double p1 = processor.convert(local1.getPoint(), vis);
        Point2D.Double p2 = processor.convert(local2.getPoint(), vis);
        Line2D.Double line = null;
        if (!(rad1 == 0 && rad2 == 0)) {
            double dx = p2.x-p1.x, dy=p2.y-p1.y;
            double dd = Math.sqrt(dx*dx+dy*dy);
            p1.x += rad1*dx/dd;
            p1.y += rad1*dy/dd;
            p2.x -= rad2*dx/dd;
            p2.y -= rad2*dy/dd;
        }
        line = new Line2D.Double(p1, p2);
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
