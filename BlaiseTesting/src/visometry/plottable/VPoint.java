/**
 * VPoint.java
 * Created on Sep 23, 2009
 */

package visometry.plottable;

import primitive.style.PointLabeledStyle;

/**
 * <p>
 *   <code>VPoint</code> is a class for points that are displayed
 *   on the plot.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VPoint<C> extends VAbstractPoint<C> {

    /** Construct to specified coordinate w/ default point style */
    public VPoint(C value) {
        super(value, new PointLabeledStyle());
    }

    /** @return current style of stroke for this plottable */
    public PointLabeledStyle getStyle() { return (PointLabeledStyle) entry.style; }
    /** Set current style of stroke for this plottable */
    public void setStyle(PointLabeledStyle newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }
}
