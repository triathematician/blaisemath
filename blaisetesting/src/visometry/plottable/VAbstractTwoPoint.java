/*
 * VAbstractTwoPoint
 * Created Apr 12, 2010
 */

package visometry.plottable;

import java.awt.geom.Point2D;
import primitive.GraphicString;
import primitive.style.PointLabeledStyle;
import primitive.style.PointStyle;
import primitive.style.PrimitiveStyle;
import visometry.PointDragListener;
import visometry.VDraggablePrimitiveEntry;
import visometry.VPrimitiveEntry;

/**
 * Abstract super-class for plottables that depend on two points. Tracks 3 plottables...
 * one for each point and one for whatever is drawn "between" them.
 * Recognizes the style within the entry as either a <code>PointStyle</code> or a <code>PointLabeledStyle</code>,
 * and adjusts the primitive correspondingly to either a regular point or a <code>GraphicString</code>.
 *
 * @author Elisha Peterson
 */
public abstract class VAbstractTwoPoint<C> extends DynamicPlottable<C>
        implements PointDragListener<C> {

    /** The two points */
    VDraggablePrimitiveEntry entryP1, entryP2;
    /** The segment or line between the points */
    VPrimitiveEntry entrySeg;


    /** Constructs with two points, a point style, and a "between" style. */
    public VAbstractTwoPoint(C[] points, PointStyle pStyle, PrimitiveStyle<Point2D.Double[]> segStyle) {
        if (points.length != 2)
            throw new IllegalArgumentException("TwoPoint element can only be created with 2 values!");
        boolean isL = pStyle instanceof PointLabeledStyle;
        addPrimitive(
                entrySeg = new VPrimitiveEntry(points, segStyle),
                entryP1 = new VDraggablePrimitiveEntry(
                    isL ? new GraphicString<C>(points[0], PlottableConstants.formatCoordinate(points[0])) : points[0],
                    pStyle, this),
                entryP2 = new VDraggablePrimitiveEntry(
                    isL ? new GraphicString<C>(points[1], PlottableConstants.formatCoordinate(points[1])) : points[1],
                    pStyle, this) );
    }

    /** @return current location of first coordinate */
    public C getPoint1() {
        return entryP1.local instanceof GraphicString
                ? ((GraphicString<C>)entryP1.local).getAnchor()
                : (C) entryP1.local;
    }

    public void setPoint1(C value) {
        if (value == null)
            throw new NullPointerException();
        if (!value.equals(getPoint1())) {
            if (entryP1.style instanceof PointLabeledStyle) {
                GraphicString<C> gs = (GraphicString<C>) entryP1.local;
                gs.setAnchor(value);
                gs.setString(PlottableConstants.formatCoordinate(value));
            } else
                entryP1.local = value;
            ((C[]) entrySeg.local)[0] = value;
            entryP1.needsConversion = true;
            entrySeg.needsConversion = true;
            firePlottableChanged();
        }
    }

    /** @return current location of second coordinate */
    public C getPoint2() {
        return entryP2.local instanceof GraphicString
                ? ((GraphicString<C>)entryP2.local).getAnchor()
                : (C) entryP2.local;
    }

    public void setPoint2(C value) {
        if (value == null)
            throw new NullPointerException();
        if (!value.equals(getPoint1())) {
            if (entryP2.style instanceof PointLabeledStyle) {
                GraphicString<C> gs = (GraphicString<C>) entryP2.local;
                gs.setAnchor(value);
                gs.setString(PlottableConstants.formatCoordinate(value));
            } else
                entryP2.local = value;
            ((C[]) entrySeg.local)[1] = value;
            entryP2.needsConversion = true;
            entrySeg.needsConversion = true;
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
