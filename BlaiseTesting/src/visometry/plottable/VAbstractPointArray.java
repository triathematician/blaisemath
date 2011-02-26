/*
 * VAbstractPointArray.java
 * Created Apr 12, 2010
 */

package visometry.plottable;

import java.util.ArrayList;
import primitive.GraphicString;
import primitive.style.PointLabeledStyle;
import primitive.style.PrimitiveStyle;
import visometry.VDraggablePrimitiveEntry;

/**
 * Abstract super-type of plottables with an underlying array of points. Contains basic support
 * for getting and setting points in the array. Events are fired when the point locations are
 * changed through the bean patterns.
 *
 * @author Elisha Peterson
 */
public abstract class VAbstractPointArray<C> extends DynamicPlottable<C> {

    /** Stores the point-array and the style. */
    protected VDraggablePrimitiveEntry entry;
    /** Stores the points. */
    C[] points;

    /** Construct with specified point(s) and style. */
    public VAbstractPointArray(PrimitiveStyle style, C[] points) {
        this.points = points;
        addPrimitive(entry = new VDraggablePrimitiveEntry(
                style instanceof PointLabeledStyle
                    ? convertToGraphicStrings(points)
                    : points,
                style, null));
    }

    /** Converts list of points to list of graphic strings */
    private GraphicString<C>[] convertToGraphicStrings(C[] points) {
        GraphicString[] result = new GraphicString[points.length];
        for (int i = 0; i < result.length; i++)
            result[i] = new GraphicString<C>(points[i], PlottableConstants.formatCoordinate(points[i]));
        return result;
    }

    /** @return list of points being drawn */
    public C[] getPoint() {
        return points;
    }

    /** @return i'th point */
    public C getPoint(int i) {
        return points[i];
    }

    /** Sets list of points being drawn */
    public void setPoint(C[] points) {
        if (this.points != points) {
            this.points = points;
            entry.local = entry.style instanceof PointLabeledStyle
                    ? convertToGraphicStrings(points)
                    : points;
            entry.needsConversion = true;
            firePlottableChanged();
        }
    }

    /** Sets the i'th point */
    public void setPoint(int i, C value) {
        if (value == null)
            throw new NullPointerException();
        points[i] = value;
        if (entry.style instanceof PointLabeledStyle) {
            GraphicString<C> gs = ((GraphicString[]) entry.local)[i];
            gs.setAnchor(value);
            gs.setString(PlottableConstants.formatCoordinate(value));
        } else
            ((C[])entry.local)[i] = value;
        entry.needsConversion = true;
        firePlottableChanged();
    }

}
