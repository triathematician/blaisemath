/**
 * VLine.java
 * Created on Sep 18, 2009
 */
package org.bm.blaise.specto.plottable;

import org.bm.blaise.specto.primitive.TwoPointStyle;
import org.bm.blaise.specto.visometry.DynamicPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *   <code>VLine</code> represents the straight line passing through two points.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VLine<C> extends DynamicPlottable<C> {

    //
    // PROPERTIES
    //

    /** First point */
    C value1;
    /** Second point */
    C value2;
    /** Style option */
    TwoPointStyle style = new TwoPointStyle();

    //
    // CONSTRUCTORS
    //

    /**
     * Construct with points
     * @param value1 first point
     * @param value2 second point
     */
    public VLine(C value1, C value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    //
    // OBJECT UTILITIES
    //

    @Override
    public String toString() {
        return style.getEndStyle().name() + " ["+value1+", "+value2+"]";
    }

    //
    // BEAN PATTERNS
    //

    /** @return current style for the path */
    public TwoPointStyle getStyle() {
        return style;
    }

    /**
     * Sets the style for the path
     * @param style new style
     */
    public void setStyle(TwoPointStyle style) {
        this.style = style;
    }

    public C getValue1() {
        return value1;
    }

    public void setValue1(C value1) {
        if (this.value1 != value1 && value1 != null) {
            this.value1 = value1;
            fireStateChanged();
        }
    }

    public C getValue2() {
        return value2;
    }

    public void setValue2(C value2) {
        if (this.value2 != value2 && value2 != null) {
            this.value2 = value2;
            fireStateChanged();
        }
    }

    //
    // PAINTING
    //

    @Override
    public void draw(VisometryGraphics<C> vg) {
        vg.drawArrow(value1, value2, style);
    }

    //
    // DYNAMIC EDITING
    //

    private transient C selectedPoint = null;

    public boolean isClickablyCloseTo(VisometryMouseEvent<C> e) {
        if (e.withinRangeOf(value1, 10)) {
            selectedPoint = value1;
            return true;
        } else if (e.withinRangeOf(value2, 10)) {
            selectedPoint = value2;
            return true;
        }
        return false;
    }

    @Override
    public void mouseDragged(VisometryMouseEvent<C> e) {
        if (adjusting) {
            if (selectedPoint == value1) {
                setValue1(e.getCoordinate());
                selectedPoint = value1;
            } else if (selectedPoint == value2) {
                setValue2(e.getCoordinate());
                selectedPoint = value2;
            }
        }
    }
}