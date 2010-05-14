/*
 * VAbstractTwoPoint
 * Created Apr 12, 2010
 */

package visometry.plottable;

import java.awt.Color;
import java.awt.geom.Point2D;
import primitive.style.PathStyle;
import primitive.style.PointStyle;
import primitive.style.PrimitiveStyle;
import visometry.PointDragListener;
import visometry.VDraggablePrimitiveEntry;
import visometry.VPrimitiveEntry;

/**
 * Abstract super-class for plottables that depend on two points. Tracks 3 plottables...
 * one for each point and one for whatever is drawn "between" them.
 *
 * @author Elisha Peterson
 */
public abstract class VAbstractTwoPoint<C> extends DynamicPlottable<C> implements PointDragListener<C> {

    /** The two points */
    VDraggablePrimitiveEntry entryP1, entryP2;
    /** The segment or line between the points */
    VPrimitiveEntry entrySeg;


    /** Constructs with two points, a point style, and a "between" style. */
    public VAbstractTwoPoint(C[] points, PointStyle pStyle, PrimitiveStyle<Point2D.Double[]> segStyle) {
        if (points.length != 2)
            throw new IllegalArgumentException("Segment can only be created with 2 values!");
        addPrimitive(
                entrySeg = new VPrimitiveEntry(points, segStyle),
                entryP1 = new VDraggablePrimitiveEntry(points[0], pStyle, this),
                entryP2 = new VDraggablePrimitiveEntry(points[1], pStyle, this) );
    }

    /** @return current location of first coordinate */
    public C getPoint1() {
        return (C) entryP1.local;
    }

    public void setPoint1(C value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (value != getPoint1()) {
            ((C[]) entrySeg.local)[0] = value;
            entrySeg.needsConversion = true;
            entryP1.local = value;
            entryP1.needsConversion = true;
            firePlottableChanged();
        }
    }

    /** @return current location of second coordinate */
    public C getPoint2() {
        return (C) entryP2.local;
    }

    public void setPoint2(C value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if (value != getPoint2()) {
            ((C[]) entrySeg.local)[1] = value;
            entrySeg.needsConversion = true;
            entryP2.local = value;
            entryP2.needsConversion = true;
            firePlottableChanged();
        }
    }

    public void mouseEntered(Object source, C start) {}
    public void mouseExited(Object source, C start) {}
    public void mouseMoved(Object source, C start) {}
    public void mouseDragInitiated(Object source, C start) {}
    public void mouseDragged(Object source, C current) {
        if (source == entryP1)
            setPoint1(current);
        else if (source == entryP2)
            setPoint2(current);
    }
    public void mouseDragCompleted(Object source, C end) {
        mouseDragged(source, end);
    }

}
