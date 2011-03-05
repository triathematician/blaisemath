/**
 * VDimensionHandle.java
 * Created on Apr 8, 2010
 */

package visometry.plottable;

import java.awt.geom.Point2D;
import primitive.style.DimensionHandleStyle;
import visometry.VMouseDragListener;
import visometry.VDraggablePrimitiveEntry;

/**
 * <p>
 *   <code>VDimensionHandle</code> displays an arrow between two points, together
 *   with bars that can be dragged to adjust the distance between them.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VDimensionHandle<C> extends DynamicPlottable<C>
        implements VMouseDragListener<C> {

    /** Entry describing the handle. */
    VDraggablePrimitiveEntry entry;

    /**
     * Construct to specified coordinates
     * @param values the values
     */
    public VDimensionHandle(C[] points, String label) {
        if (points.length != 2)
            throw new IllegalArgumentException("Handle can only be created with 2 values!");
        DimensionHandleStyle dhs = new DimensionHandleStyle();
        addPrimitive(entry = new VDraggablePrimitiveEntry(points, dhs, this));
    }

    /** @return current location of first coordinate */
    public C getPoint1() {
        return ((C[])entry.local)[0];
    }

    public void setPoint1(C value) {
        if (value == null)
            throw new NullPointerException();
        if (!value.equals(getPoint1())) {
            ((C[])entry.local)[0] = value;
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

    /** @return current location of second coordinate */
    public C getPoint2() {
        return ((C[])entry.local)[1];
    }

    public void setPoint2(C value) {
        if (value == null)
            throw new NullPointerException();
        if (!value.equals(getPoint1())) {
            ((C[])entry.local)[1] = value;
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

    public void mouseEntered(Object source, C start) {}
    public void mouseExited(Object source, C start) {}
    public void mouseMoved(Object source, C start) {}
    public void mouseDragInitiated(Object source, C start) {}
    public void mouseDragged(Object source, C current) {
        if (source == entry) {
            DimensionHandleStyle dhs = (DimensionHandleStyle) entry.style;
            Point2D.Double[] pts = (Point2D.Double[]) entry.local;
            if (dhs.getLastContainedIndex() == 0)
                setPoint1(current);
            else if (dhs.getLastContainedIndex() == 1)
                setPoint2(current);
        }
    }
    public void mouseDragCompleted(Object source, C end) {
        mouseDragged(source, end);
    }
}
