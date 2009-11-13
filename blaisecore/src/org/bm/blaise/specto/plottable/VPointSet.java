/**
 * VPointSet.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.specto.plottable;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.bm.blaise.specto.primitive.PointStyle;
import org.bm.blaise.specto.primitive.StringStyle;
import org.bm.blaise.specto.visometry.AbstractDynamicPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *   <code>VPointSet</code> is a set of points in the visometry. Each point is
 *   displayed as a separate entity. The individual points may be moved, if the
 *   <code>editable</code> property is set to <code>TRUE</code>.
 * </p>
 *
 * @author Elisha Peterson
 */
public class VPointSet<C> extends AbstractDynamicPlottable<C> {

    //
    //
    // PROPERTIES
    //
    //

    /** Values */
    protected C[] values;

    /** Style option */
    protected PointStyle style = new PointStyle();

    /** Whether the label si shown. */
    protected boolean labelVisible = true;

    /** Style for the coordinate label. */
    protected StringStyle labelStyle;

    //
    //
    // CONSTRUCTORS
    //
    //

    /**
     * Construct with sequence of points
     * @param values the points
     */
    public VPointSet(C[] values) {
        setValues(values);
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    /** @return entire list of values, as an array */
    public C[] getValues() {
        return values;
    }

    /** @return value at specified index */
    public C getValue(int index) {
        return values[index];
    }

    /** Replaces entire list of values with those supplied */
    public void setValues(C[] values) {
        this.values = values;
        fireStateChanged();
    }

    /** Replaces value at specified index with that supplied. */
    public void setValue(int index, C value) {
        values[index] = value;
        fireStateChanged();
    }

    /** @return current style for the path */
    public PointStyle getStyle() {
        return style;
    }

    /** Sets the style for the path
     * @param style new style
     */
    public void setStyle(PointStyle style) {
        this.style = style;
    }

    public StringStyle getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(StringStyle labelStyle) {
        this.labelStyle = labelStyle;
    }

    public boolean isLabelVisible() {
        return labelVisible;
    }

    public void setLabelVisible(boolean labelVisible) {
        this.labelVisible = labelVisible;
    }


    //
    //
    // PAINTING
    //
    //
    final protected NumberFormat formatter = new DecimalFormat("#0.000");

    /** Hook for subclasses to provide custom formatting of displayed coordinates of point. */
    public String getValueString(int i) {
        if (values[i] instanceof Point2D) {
            Point2D p2d = ((Point2D)values[i]);
            return "(" + formatter.format(p2d.getX()) + ", " + formatter.format(p2d.getY()) + ")";

        }
        return values[i].toString();
    }

    @Override
    public void paintComponent(VisometryGraphics<C> vg) {
        vg.setPointStyle(style);
        vg.drawPoints(values);
        if (labelVisible) {
            if (labelStyle != null) {
                vg.setStringStyle(labelStyle);
            }
            for (int i = 0; i < values.length; i++) {
                vg.drawString(getValueString(i), values[i], 5, -5);
            }
        }
    }

    //
    //
    // DYNAMIC EDITING
    //
    //

    private transient int selectedIndex = -1;

    /** @return index of selected coordinate */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /** @return value underlying selected coordinate */
    public C getSelectedValue() {
        return selectedIndex >= 0 ? values[selectedIndex] : null;
    }

    public boolean isClickablyCloseTo(VisometryMouseEvent<C> e) {
        for (int i = 0; i < values.length; i++) {
            if (e.withinRangeOf(values[i], 10)) {
                selectedIndex = i;
                return true;
            }
        }
        return false;
    }

    //
    //
    // HANDLING MOUSE EVENTS
    //
    //
    
    @Override
    public void mouseDragged(VisometryMouseEvent<C> e) {
        if (adjusting && selectedIndex != -1) {
            setValue(selectedIndex, e.getCoordinate());
        }
    }
}
