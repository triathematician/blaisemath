/**
 * VPointSet.java
 * Created on Jul 30, 2009
 */

package org.bm.blaise.specto.plottable;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import org.bm.blaise.specto.primitive.PointStyle;
import org.bm.blaise.specto.primitive.StringStyle;
import org.bm.blaise.specto.visometry.AbstractDynamicPlottable;
import org.bm.blaise.specto.visometry.CoordinateHandler;
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
public class VPointSet<C> extends AbstractDynamicPlottable<C> implements CoordinateHandler<C> {

    //
    // PROPERTIES
    //

    /** Values */
    protected C[] values;

    /** Style option */
    protected PointStyle pointStyle = new PointStyle();

    /** Style for the coordinate label. */
    protected StringStyle labelStyle;
    /** Whether the label si shown. */
    protected boolean labelsVisible = false;

    //
    // CONSTRUCTORS
    //

    /**
     * Construct with sequence of points
     * @param values the points
     */
    public VPointSet(C... values) {
        setValues(values);
    }

    @Override
    public String toString() {
        return "VPointSet " + Arrays.toString(values);
    }

    //
    // VALUE PATTERNS
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

    /** Adds an additional value to the list of values. */
    public void addValue(C value) {
        C[] newValues = (C[]) java.lang.reflect.Array.newInstance(values.getClass().getComponentType(), values.length + 1);
        System.arraycopy(values, 0, newValues, 0, values.length);
        newValues[values.length] = value;
        values = newValues;
        fireStateChanged();
    }

    /** Removes a value from the lsit of values. */
    public void removeValue(int index) {
        if (index>=0 && index < values.length) {
            C[] newValues = (C[]) Array.newInstance(values.getClass().getComponentType(), values.length - 1);
            if (index >= 1)
                System.arraycopy(values, 0, newValues, 0, index);
            if (index <= values.length-2)
                System.arraycopy(values, index+1, newValues, index, values.length-index-1);
            values = newValues;
            fireStateChanged();
        }
    }

    /** Removes all values. */
    public void removeAllValues() {
        setValues((C[]) Array.newInstance(values.getClass().getComponentType(), 0));
    }

    //
    // STYLE GETTERS & SETTERS
    //

    /** @return current style for the path */
    public PointStyle getPointStyle() {
        return pointStyle;
    }

    /** Sets the style for the path
     * @param style new style
     */
    public void setPointStyle(PointStyle style) {
        if (style != pointStyle) {
            this.pointStyle = style;
            fireStateChanged();
        }
    }

    public StringStyle getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(StringStyle labelStyle) {
        if (this.labelStyle != labelStyle) {
            this.labelStyle = labelStyle;
            fireStateChanged();
        }
    }

    public boolean isLabelsVisible() {
        return labelsVisible;
    }

    public void setLabelsVisible(boolean labelVisible) {
        if (this.labelsVisible != labelVisible) {
            this.labelsVisible = labelVisible;
            fireStateChanged();
        }
    }

    //
    // PAINTING
    //

    @Override
    public void paintComponent(VisometryGraphics<C> vg) {
        vg.drawPoints(values, pointStyle);
        if (labelsVisible) {
            for (int i = 0; i < values.length; i++)
                vg.drawString(getValueString(i), values[i], 5, -5, labelStyle);
        }
    }

    final protected NumberFormat formatter = new DecimalFormat("#0.000");

    /** Hook for subclasses to provide custom formatting of displayed coordinates of point. */
    public String getValueString(int i) {
        if (values[i] instanceof Point2D) {
            Point2D p2d = ((Point2D)values[i]);
            return "(" + formatter.format(p2d.getX()) + ", " + formatter.format(p2d.getY()) + ")";
        }
        return values[i].toString();
    }

    //
    //
    // DYNAMIC EDITING
    //
    //

    /** Adds a new point at the specified coordinate. */
    public boolean handleCoordinate(C coord) {
        if (editable) {
            addValue(coord);
            return true;
        }
        return false;
    }

    protected transient int selectedIndex = -1;

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
        selectedIndex = -1;
        return false;
    }

    //
    //
    // HANDLING MOUSE EVENTS
    //
    //
    
    @Override
    public void mouseDragged(VisometryMouseEvent<C> e) {
        super.mouseDragged(e);
        if (adjusting && editable && selectedIndex != -1)
            setValue(selectedIndex, e.getCoordinate());
    }

    @Override
    public void mouseClicked(VisometryMouseEvent<C> e) {
        if (editable) {
            String mode = MouseEvent.getModifiersExText(e.getModifiersEx());
            if (mode.equals("Ctrl") && selectedIndex != -1) {
                removeValue(selectedIndex);
            }
        }
    }


}
