/*
 * VShape.java
 * Created on Nov 30, 2009
 */

package visometry.plottable;

import java.awt.Color;
import java.util.Arrays;
import primitive.style.ShapeStyle;
import visometry.VPrimitiveEntry;

/**
 * This class displays a shape whose outline is specified by a list of points.
 * The points may be drawn along with the shape's outline. Separate styling
 * is supported for both display styles, and the points may be visible or invisible.
 * (By default the points are not visible.)
 *
 * @param <C> the underlying system of coordinates
 */
public class VShape<C> extends VPointSet<C> {

    /** The primitive representing the underlying shape. */
    VPrimitiveEntry entry2;

    /** Constructs a dynamic shape that passes through the specified value. */
    public VShape(C... values) {
        super(values);
        entry.visible = false;
        addPrimitive(entry2 = new VPrimitiveEntry(points, new ShapeStyle(Color.BLACK, Color.LIGHT_GRAY)));
    }

    @Override
    public String toString() {
        return "VShape " + Arrays.toString((C[]) points);
    }

    @Override
    protected void firePlottableChanged() {
        entry2.local = points;
        entry2.needsConversion = true;
        super.firePlottableChanged();
    }

    /** @return true if points at vertices of polygon are visible */
    public boolean isPointsVisible() { return entry.visible; }
    /** Sets visiblity of the vertices. */
    public void setPointsVisible(boolean value) { if (entry.visible != value) { entry.visible = value; firePlottableStyleChanged(); } }
    
    /** @return current style of stroke for this plottable */
    public ShapeStyle getShapeStyle() { return (ShapeStyle) entry2.style; }
    /** Set current style of stroke for this plottable */
    public void setShapeStyle(ShapeStyle newValue) { if (entry2.style != newValue) { entry2.style = newValue; firePlottableStyleChanged(); } }

}
