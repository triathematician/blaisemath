/**
 * VGraphicCache.java
 * Created Jan 28, 2011
 */
package visometry.graphics;

import org.bm.blaise.graphics.GraphicCache;
import org.bm.blaise.graphics.GraphicEntry;
import javax.swing.event.ChangeEvent;
import visometry.Visometry;
import visometry.VisometryProcessor;
import visometry.plottable.PlottableGroup;

/**
 * Extends basic graphic cache with support for visometry handling.
 * @param <C> type of local coordinates
 * @author Elisha
 */
public class VGraphicCache<C> extends GraphicCache implements VGraphicEntry<C> {

    /** Stores the underlying visometry */
    protected Visometry<C> visometry;
    /** Stores the processor used to translate coordinates */
    protected VisometryProcessor<C> processor;

    /** Stores the local coordinates view */
    public VCompositeGraphicEntry<C> local;
    /** Stores mouse handler for local coordinates */
    protected VGraphicMouseListener localMouse;

    /** Construct with visometry */
    public VGraphicCache(Visometry<C> vis) {
        setVisometry(vis);
        processor = new VisometryProcessor<C>();
    }

    //
    // PROPERTY PATTERNS
    //

    /** @return current visometry */
    public Visometry<C> getVisometry() { return visometry; }
    /** Sets visometry */
    public final void setVisometry(Visometry<C> vis) {
        if (vis == null)
            throw new IllegalArgumentException("Visometry cannot be null!");
        if (visometry != null)
            visometry.removeChangeListener(this);
        visometry = vis;
        visometry.addChangeListener(this);
        mouseFactory = new VGraphicMouseEventFactory(vis);
        setUnconverted(true);
    }

    /** Sets the plottable group (which underlies the local geometry entries) */
    public void setPlottableGroup(PlottableGroup p) {
        if (this.local != null) {
            local.removeChangeListener(this);
            removeEntry(local.windowEntry);
        }
        local = p.getGraphicEntry();
        if (local == null)
            throw new IllegalArgumentException("PlottableGroup should never have null local entry.");
        local.addChangeListener(this);
        addEntry(local.windowEntry);
    }

    /** Updates all graphic entries needing conversion from local to window coordinates */
    public void reconvert() {
        if (local.isUnconverted())
            local.convert(visometry, processor);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == local) {
            fireStateChanged();
        } else if (e.getSource() == visometry) {
            local.setUnconverted(true);
            fireStateChanged();
        }
    }

    //
    // VGraphicEntry METHODS
    //

    public VGraphicMouseListener getMouseListener() { return localMouse; }
    public void setMouseListener(VGraphicMouseListener listener) { localMouse = listener; }
    public boolean isUnconverted() { return local.isUnconverted(); }
    public void setUnconverted(boolean flag) { if (local != null) local.setUnconverted(flag); }
    public void convert(Visometry<C> vis, VisometryProcessor<C> processor) { local.convert(vis, processor); }
    public GraphicEntry getWindowEntry() { return this; }

}
