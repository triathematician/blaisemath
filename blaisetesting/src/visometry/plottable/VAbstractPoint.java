/*
 * VAbstractPoint.java
 * Created Apr 12, 2010
 */

package visometry.plottable;

import java.awt.geom.Point2D;
import primitive.GraphicString;
import primitive.style.PointLabeledStyle;
import primitive.style.PrimitiveStyle;
import visometry.VMouseDragListener;
import visometry.VDraggablePrimitiveEntry;
import visometry.VPrimitiveEntry;

/**
 * Abstract implementation of a point displayed on a window, w/ support for dragging,
 * provided a suitable style is available.
 * Recognizes the style within the entry as either a <code>PointStyle</code> or a <code>PointLabeledStyle</code>,
 * and adjusts the primitive correspondingly to either a regular point or a <code>GraphicString</code>.
 *
 * @author Elisha Peterson
 */
public abstract class VAbstractPoint<C> extends DynamicPlottable<C> 
        implements VMouseDragListener<C> {

    /** Stores the table entry. */
    protected VPrimitiveEntry entry;

    /** Constructs w/ specified style. */
    public VAbstractPoint(C value, PrimitiveStyle<? extends Point2D> style) {
        addPrimitive(entry = new VDraggablePrimitiveEntry(
                style instanceof PointLabeledStyle
                    ? new GraphicString<C>(value, PlottableConstants.formatCoordinate(value))
                    : value,
                style, this));
    }

    @Override
    public String toString() {
        return "Point[" + entry.local + "]";
    }

    /** @return current location of the coordinate */
    public C getPoint() {
        return entry.local instanceof GraphicString
                ? ((GraphicString<C>)entry.local).getAnchor()
                : (C) entry.local;
    }

    public void setPoint(C value) {
        if (value == null)
            throw new NullPointerException();
        if (!value.equals(getPoint())) {
            if (entry.style instanceof PointLabeledStyle) {
                GraphicString<C> gs = (GraphicString<C>) entry.local;
                gs.setAnchor(value);
                gs.setString(PlottableConstants.formatCoordinate(value));
            } else
                entry.local = value;
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

    //
    // MOUSE METHODS
    //

    private transient C start = null, startDrag = null;

    public void mouseEntered(Object source, C start) {}
    public void mouseExited(Object source, C start) {}
    public void mouseMoved(Object source, C start) {}
    public void mouseDragInitiated(Object source, C start) { this.start = getPoint(); this.startDrag = start; }
    public void mouseDragged(Object source, C current) {
        if (current instanceof Point2D && start instanceof Point2D) {
            Point2D ps = (Point2D) start;
            Point2D psd = (Point2D) startDrag;
            Point2D pcd = (Point2D) current;
            Point2D.Double relative = new Point2D.Double(
                    ps.getX() + pcd.getX() - psd.getX(), ps.getY() + pcd.getY() - psd.getY()
                    );
            setPoint((C) relative);
        } else
            setPoint(current);
    }
    public void mouseDragCompleted(Object source, C end) { mouseDragged(source, end); }

}
