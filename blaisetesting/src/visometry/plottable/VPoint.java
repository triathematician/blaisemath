/**
 * VPoint.java
 * Created on Sep 23, 2009
 */

package visometry.plottable;

import visometry.PointDragListener;
import primitive.style.PointStyle;

/**
 * <p>
 *   <code>VPoint</code> is a base class for points that are displayed
 *   on the plot. No stylization or visual features are included here, merely the
 *   template for storing the value of the point. Subclasses should override the
 *   paintComponent method at the very least.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VPoint<C> extends VAbstractPoint<C> {

    /** Construct to specified coordinate w/ default point style */
    public VPoint(C value) {
        super(value, new PointStyle());
    }

    /** @return current style of stroke for this plottable */
    public PointStyle getStyle() { return (PointStyle) entry.style; }
    /** Set current style of stroke for this plottable */
    public void setStyle(PointStyle newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }
}
