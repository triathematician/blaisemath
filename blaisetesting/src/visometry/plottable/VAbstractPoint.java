/*
 * VAbstractPoint.java
 * Created Apr 12, 2010
 */

package visometry.plottable;

import java.awt.geom.Point2D;
import primitive.style.PrimitiveStyle;
import visometry.PointDragListener;
import visometry.VDraggablePrimitiveEntry;
import visometry.VPrimitiveEntry;

/**
 * Abstract implementation of a point displayed on a window, w/ support for dragging,
 * provided a suitable style is available.
 *
 * @author Elisha Peterson
 */
public abstract class VAbstractPoint<C> extends DynamicPlottable<C> 
        implements PointDragListener<C> {

    /** Stores the table entry. */
    protected VPrimitiveEntry entry;

    /** Constructs w/ specified style. */
    public VAbstractPoint(C value, PrimitiveStyle<? extends Point2D> style) {
        addPrimitive(entry = new VDraggablePrimitiveEntry(value, style, this));
    }

    @Override
    public String toString() {
        return "Point[" + entry.local + "]";
    }

    /** @return current location of the coordinate */
    public C getPoint() {
        return (C) entry.local;
    }

    public void setPoint(C value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (!value.equals(entry.local)) {
            entry.local = value;
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

    //
    // MOUSE METHODS
    //

    public void mouseEntered(Object source, C start) {}
    public void mouseExited(Object source, C start) {}
    public void mouseMoved(Object source, C start) {}
    public void mouseDragInitiated(Object source, C start) {}
    public void mouseDragged(Object source, C current) { setPoint(current); }
    public void mouseDragCompleted(Object source, C end) { setPoint(end); }

}
