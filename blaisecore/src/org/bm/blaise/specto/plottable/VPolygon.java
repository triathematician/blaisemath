/**
 * VPolygon.java
 * Created on Nov 30, 2009
 */

package org.bm.blaise.specto.plottable;

import java.awt.Color;
import java.util.Arrays;
import org.bm.blaise.specto.primitive.ShapeStyle;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *    This class is a dynamically adjustable polygon.
 * </p>
 * @param <C> the underlying system of coordinates
 * 
 * @author Elisha Peterson
 */
public class VPolygon<C> extends VPointSet<C> {

    /** Whether points should be shown */
    protected boolean pointsVisible = false;
    /** Controls the outline and fill */
    protected ShapeStyle shapeStyle = new ShapeStyle(Color.BLACK, Color.GRAY);

    public VPolygon(C... values) {
        super(values);
        setLabelsVisible(false);
    }

    public ShapeStyle getShapeStyle() {
        return shapeStyle;
    }

    public void setShapeStyle(ShapeStyle shapeStyle) {
        this.shapeStyle = shapeStyle;
    }

    public boolean isPointsVisible() {
        return pointsVisible;
    }

    public void setPointsVisible(boolean pointsVisible) {
        this.pointsVisible = pointsVisible;
        fireStateChanged();
    }

    @Override
    public String toString() {
        return "VPolygon " + Arrays.toString(values);
    }

    @Override
    public void paintComponent(VisometryGraphics<C> vg) {
        vg.setShapeStyle(shapeStyle);
        vg.drawClosedPath(values);
        if (pointsVisible) {
            vg.setPointStyle(pointStyle);
            vg.drawPoints(values);
        }
        if (labelsVisible) {
            if (labelStyle != null) {
                vg.setStringStyle(labelStyle);
            }
            for (int i = 0; i < values.length; i++) {
                vg.drawString(getValueString(i), values[i], 5, -5);
            }
        }
    }
}
