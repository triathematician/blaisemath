/**
 * VInvisiblePoint.java
 * Created on Sep 23, 2009
 */

package org.bm.blaise.specto.plottable;

import org.bm.blaise.specto.visometry.DynamicPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *   <code>VInvisiblePoint</code> is a base class for points that are displayed
 *   on the plot. No stylization or visual features are included here, merely the
 *   template for storing the value of the point. Subclasses should override the
 *   paintComponent method at the very least.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VInvisiblePoint<C> extends DynamicPlottable<C> {

    //
    // PROPERTIES
    //

    /** The value of the point. */
    protected C value;

    public VInvisiblePoint(C value) {
        this.value = value;
    }

    //
    // BEANS
    //

    public C getPoint() {
        return value;
    }

    public void setPoint(C value) {
        if (value == null)
            throw new NullPointerException();
        if (!value.equals(this.value)) {
            this.value = value;
            fireStateChanged();
        }
    }

    //
    // PAINT METHODS
    //

    /** Subclasses must override the paint method to display the point. */
    @Override
    public void draw(VisometryGraphics<C> vg) {
    }

    //
    // DYNAMIC METHODS
    //

    public boolean isClickablyCloseTo(VisometryMouseEvent<C> e) {
        return e.withinRangeOf(value, 10);
    }

    @Override
    public void mouseDragged(VisometryMouseEvent<C> e) {
        super.mouseDragged(e);
        if (adjusting)
            setPoint(e.getCoordinate());
    }

}
