/**
 * VPoint.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.specto.plottable;

import java.awt.Color;
import org.bm.blaise.specto.primitive.ShapeStyle;
import org.bm.blaise.specto.visometry.AbstractDynamicPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *   <code>VRectangle</code> is a rectangle between a maximum point and a minimum point,
 *   drawn by the underlying <code>VisometryGraphics</code>. The rectangle's sides are
 *   straight in the window's coordinate system, but not necessarily in the underlying coordinates.
 *   All four corners may be dragged.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VRectangle<C> extends AbstractDynamicPlottable<C> {

    ShapeStyle style = new ShapeStyle(Color.BLACK, Color.GRAY);

    protected C value1;
    protected C value2;

    /** Construct with two of rectangle's vertices.
     * @param value1 first vertex of rectangle
     * @param value2 second vertex of rectangle
     */
    public VRectangle(C value1, C value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    /** @return coordinates of 1st vertex of the rectangle */
    public C getPoint1() {
        return value1;
    }

    public void setPoint1(C value1) {
        if (value1 == null) {
            throw new NullPointerException();
        }
        if (!value1.equals(this.value1)) {
            this.value1 = value1;
            fireStateChanged();
        }
    }

    /** @return coordinates of 2nd vertex of the rectangle */
    public C getPoint2() {
        return value2;
    }

    public void setPoint2(C value2) {
        if (value2 == null) {
            throw new NullPointerException();
        }
        if (!value2.equals(this.value2)) {
            this.value2 = value2;
            fireStateChanged();
        }
    }

    public ShapeStyle getStyle() {
        return style;
    }

    public void setStyle(ShapeStyle style) {
        this.style = style;
    }

    

    //
    //
    // PAINTING
    //
    //

    @Override
    public void paintComponent(VisometryGraphics<C> vg) {
        vg.setShapeStyle(style);
        vg.drawRectangle(value1, value2);
    }

    //
    //
    // DYNAMIC EDITING
    //
    //

    boolean val1Adjusting = true;

    public boolean isClickablyCloseTo(VisometryMouseEvent<C> e) {
        if (e.withinRangeOf(value1, 10)) {
            val1Adjusting = true;
            return true;
        }
        if (e.withinRangeOf(value2, 10)) {
            val1Adjusting = false;
            return true;
        }
        return false;
    }

    @Override
    public void mouseDragged(VisometryMouseEvent<C> e) {
        if (adjusting) {
            if (val1Adjusting) {
                setPoint1(e.getCoordinate());
            } else {
                setPoint2(e.getCoordinate());
            }
        }
    }
}
