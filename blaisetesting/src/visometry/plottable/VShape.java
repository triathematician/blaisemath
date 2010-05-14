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
 * <p>
 *    This class is a dynamically adjustable polygon.
 * </p>
 * @param <C> the underlying system of coordinates
 */
public class VShape<C> extends VPointSet<C> {

    /** The primitive representing the underlying shape. */
    VPrimitiveEntry entry2;

    /** Constructs a dynamic shape that passes through the specified value. */
    public VShape(C... values) {
        super(values);
        entry.visible = false;
        addPrimitive(entry2 = new VPrimitiveEntry(entry.local, new ShapeStyle(Color.BLACK, Color.LIGHT_GRAY)));
    }

    @Override
    public String toString() {
        return "VBasicShape " + Arrays.toString((C[]) entry.local);
    }

    @Override
    public void setPoint(C[] points) {
        if (entry.local != points) {
            entry.local = points;
            entry.needsConversion = true;
            entry2.needsConversion = true;
            firePlottableChanged();
        }
    }

    @Override
    public void setPoint(int i, C value) {
        if (getPoint(i) != value) {
            getPoint()[i] = value;
            entry.needsConversion = true;
            entry2.needsConversion = true;
            firePlottableChanged();
        }
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
