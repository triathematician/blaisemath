/**
 * PlaneEllipse.java
 * Created on Nov 30, 2009
 */

package org.bm.blaise.specto.plane.geometry;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;
import org.bm.blaise.specto.plottable.VPointSet;
import org.bm.blaise.specto.primitive.HandleStyle;
import org.bm.blaise.specto.primitive.ShapeStyle;
import org.bm.blaise.specto.visometry.VisometryGraphics;

/**
 * <p>
 *    This class draws an ellipse with specified center and major radii.
 * </p>
 * @author Elisha Peterson
 */
public class PlaneEllipse extends VPointSet<Point2D.Double> {

    /** Whether points should be shown */
    boolean pointsVisible = false;

    /** Controls the outline and fill */
    ShapeStyle shapeStyle = new ShapeStyle(Color.BLACK, Color.GRAY);
    

    public PlaneEllipse() {
        super(new Point2D.Double[]{new Point2D.Double(0,0), new Point2D.Double(1,0), new Point2D.Double(0,1)});
    }

    //
    // OBJECT UTILTIES
    //

    @Override
    public String toString() {
        return "PlaneEllipse " + Arrays.toString(values);
    }

    //
    // VALUE PATTERNS
    //

    @Override
    public void setValue(int index, Point2D.Double value) {
        if (index == 0) {
            double dx = value.x - values[0].x;
            double dy = value.y - values[0].y;
            super.setValue(index, value);
            values[1].x += dx;
            values[2].x += dx;
            values[1].y += dy;
            values[2].y += dy;
        } else if (index == 1) {
            super.setValue(index, value);
            values[1].y = values[0].y;
        } else if (index == 2) {
            super.setValue(index, value);
            values[2].x = values[0].x;
        } else {
            throw new IllegalArgumentException("Cannot change the coordinate of index " + index + " on a PlaneEllipse.");
        }
    }

    //
    // STYLE PATTERNS
    //

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

    //
    // PAINT METHODS
    //

    public Point2D.Double center() {
        return values[0];
    }

    public Point2D.Double corner1() {
        return new Point2D.Double(values[1].x, values[2].y);
    }

    public Point2D.Double corner2() {
        return new Point2D.Double(2*values[0].x-values[1].x, 2*values[0].y-values[2].y);
    }

    @Override
    public void paintComponent(VisometryGraphics<Point2D.Double> vg) {
        vg.drawEllipse(corner1(), corner2(), shapeStyle);
        if (pointsVisible) {
            vg.setPointStyle(pointStyle);
            for (Point2D.Double v : values)
                vg.drawPoint(v, HandleStyle.getInstance());
        }
        if (labelsVisible)
            for (int i = 0; i < values.length; i++)
                vg.drawString(getValueString(i), values[i], 5, -5, labelStyle);
    }
}
