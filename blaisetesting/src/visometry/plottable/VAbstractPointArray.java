/*
 * VAbstractPointArray.java
 * Created Apr 12, 2010
 */

package visometry.plottable;

import primitive.style.PrimitiveStyle;
import visometry.VDraggablePrimitiveEntry;

/**
 * Abstract super-type of plottables with an underlying array of points. Contains basic support
 * for getting and setting points in the array. The primitive entry is draggable, so that points
 * can be moved around if a sub-class updates the listener property of the entry.
 *
 * @author Elisha Peterson
 */
public abstract class VAbstractPointArray<C> extends DynamicPlottable<C> {

    /** Stores the point-array and the style. */
    protected VDraggablePrimitiveEntry entry;

    /** Construct with specified point(s) and style. */
    public VAbstractPointArray(PrimitiveStyle style, C[] points) {
        addPrimitive(entry = new VDraggablePrimitiveEntry(points, style, null));
    }

    /** @return list of points being drawn */
    public C[] getPoints() {
        return (C[]) entry.local;
    }

    /** @return i'th point */
    public C getPoints(int i) {
        return getPoints()[i];
    }

    /** Sets list of points being drawn */
    public void setPoints(C[] points) {
        if (entry.local != points) {
            entry.local = points;
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

    /** Sets the i'th point */
    public void setPoints(int i, C value) {
        if (getPoints(i) != value) {
            ((C[])entry.local)[i] = value;
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

}
