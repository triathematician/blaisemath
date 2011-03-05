/*
 * VGraphicEntry.java
 * Created Apr 2010
 * Substantial Revision Jan 28, 2011
 */

package visometry.graphics;

import org.bm.blaise.graphics.GraphicEntry;
import visometry.Visometry;
import visometry.VisometryProcessor;

/**
 * Stores a graphic entry that exists in a local coordinate system. Looks a lot
 * like graphics.GraphicEntry, except that the corresponding methods are in terms
 * of local coordinates instead of window coordinates. This class also adds in
 * support for the conversion steps.
 *
 * @author Elisha Peterson
 *
 * @see graphics.GraphicEntry
 */
public interface VGraphicEntry<C> {

    /** @return the mouse handler that can deal with mouse events for this entry at specified point (may be null) */
    public VGraphicMouseListener getMouseListener();
    /** Sets mouse listener for this entry. */
    public void setMouseListener(VGraphicMouseListener listener);

    /** @return true if entry needs to be converted to window coordinates */
    public boolean isUnconverted();
    /** Sets the flag to true, requiring the entry to be reconverted before drawing again */
    public void setUnconverted(boolean flag);

    /**
     * Performs the conversion from local coordinates to window coordinates
     * @param vis the underlying visometry
     * @param processor provides some basic translation utilities
     */
    public void convert(Visometry<C> vis, VisometryProcessor<C> processor);

    /**
     * Returns the graphic entry. Should never be called unless <code>isNeedsConversion()</code> returns true.
     * The return value should never be null.
     * @return the regular (window coordinates) graphic entry computed after conversion
     */
    public GraphicEntry getWindowEntry();

}
