/**
 * VPointEntry.java
 * Created Jan 29, 2011
 */
package visometry.graphics;

import utils.IndexedGetter;
import org.bm.blaise.graphics.BasicPointEntry;
import org.bm.blaise.graphics.CompositeGraphicEntry;
import org.bm.blaise.graphics.GraphicEntry;
import org.bm.blaise.graphics.renderer.PointRenderer;
import java.awt.geom.Point2D;
import utils.IndexedGetterSetter;
import visometry.Visometry;
import visometry.VisometryProcessor;
import visometry.VisometryUtils;

/**
 * An entry for a (large) collection of draggable points at arbitrary local coordinates
 * @param <C> local ooordinate type
 * @author Elisha
 */
public class VPointSetEntry<C> extends AbstractVGraphicEntry<C> {

    /** Enables dragging of points by providing "set" methods */
    IndexedGetterSetter.Relative<C> local;
    /** The window group entry */
    CompositeGraphicEntry window;
    /** The window entries */
    BasicPointEntry[] wPoints;

    /** Default renderer used to draw the points (may be null) */
    PointRenderer rend;
    /** Indexed renderer that can be used to customize points */
    IndexedGetter<PointRenderer> rFactory;
    /** Custom tooltips by point */
    IndexedGetter<String> tips;

    /** Construct with specified bean to handle dragging (may be null) */
    public VPointSetEntry(IndexedGetterSetter.Relative local) {
        this.local = local;
    }

    public GraphicEntry getWindowEntry() {
        return window;
    }

    //
    // ENTRY MANAGEMENT
    //

    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) {
        if (window == null)
            window = new CompositeGraphicEntry();
        int n = local.getSize();
        if (wPoints == null)
            wPoints = new BasicPointEntry[n];
        else if (wPoints.length != n) {
            // TODO might think about efficiency when not all need to be removed
            window.clearEntries();
            wPoints = new BasicPointEntry[n];
        }
        for (int i = 0; i < n; i++) {
            C loc = local.getElement(i);
            Point2D p = processor.convert(loc, vis);
            if (wPoints[i] == null)
                createEntry(p, i);
            else
                updateEntry(p, i);
            wPoints[i].setTooltip(
                    tips != null ? tips.getElement(i)
                    : loc instanceof Point2D ? VisometryUtils.formatPoint((Point2D) loc, 2)
                    : loc + "");
        }
        setUnconverted(false);
    }

    /** Set up entry... possibly use custom renderer, add it to the composite, enable dragging. */
    private void createEntry(Point2D p, int i) {
        wPoints[i] = new BasicPointEntry(p,
                rFactory == null ? rend :
                    i < rFactory.getSize() ? rFactory.getElement(i)
                    : null);
        window.addEntry(wPoints[i]);
        wPoints[i].setMouseListener(new VGraphicMouseListener.IndexedPointDragger<C>(local, i).adapter());
    }

    /** Updates the entry, particularly the display point and the renderer if it has changed */
    private void updateEntry(Point2D p, int i) {
        wPoints[i].setPoint(p);
        wPoints[i].setRenderer(rFactory == null ? rend :
                    i < rFactory.getSize() ? rFactory.getElement(i)
                    : null);
    }

    //
    // STYLE PROPERTIES
    //

    public PointRenderer getRenderer() {
        return rend;
    }

    public void setRenderer(PointRenderer rend) {
        if (this.rend != rend) {
            this.rend = rend;
            setUnconverted(true);
        }
    }

    public IndexedGetter<PointRenderer> getIndexedRenderer() { return rFactory; }
    public void setIndexedRenderer(IndexedGetter<PointRenderer> rend) {
        if (this.rFactory != rend) {
            this.rFactory = rend;
            setUnconverted(true);
        }
    }

    public IndexedGetter<String> getTooltips() { return tips; }
    public void setTooltips(IndexedGetter<String> tooltips) {
        if (this.tips != tooltips) {
            this.tips = tooltips;
            setUnconverted(true);
        }
    }

}
