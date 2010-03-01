/**
 * PlaneBezierCurve.java
 * Created on Dec 17, 2009
 */

package org.bm.blaise.specto.plane.geometry;

import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import org.bm.blaise.specto.plane.PlaneGraphics;
import org.bm.blaise.specto.plottable.VPointSet;
import org.bm.blaise.specto.primitive.ArrowStyle;
import org.bm.blaise.specto.primitive.ArrowStyle.ArrowShape;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *    This class displays a Bezier curve.
 * </p>
 * @author Elisha Peterson
 */
public class PlaneBezierCurve extends VPointSet<Point2D.Double> {

    Point2D.Double[][] beziers;
    
    PathStyle curveStyle = new PathStyle(BlaisePalette.STANDARD.func1());

    ArrowStyle controlStyle = new ArrowStyle(BlaisePalette.STANDARD.grid(), ArrowShape.DOT, 4);
    boolean drawControls = true;

    public PlaneBezierCurve(Point2D.Double... values) {
        super(values);
    }
    
    //
    // CONTROLS
    //

    public boolean isDrawControls() {
        return drawControls;
    }

    public void setDrawControls(boolean drawControls) {
        this.drawControls = drawControls;
    }

    public ArrowStyle getControlStyle() {
        return controlStyle;
    }

    public void setControlStyle(ArrowStyle controlStyle) {
        this.controlStyle = controlStyle;
    }

    public PathStyle getCurveStyle() {
        return curveStyle;
    }

    public void setCurveStyle(PathStyle curveStyle) {
        this.curveStyle = curveStyle;
    }

    

    //
    // COMPOSITIONAL
    //

    @Override
    public void addValue(Point2D.Double value) {
        super.addValue(value);
        int n = values.length;
        Point2D.Double[][] newBezier = new Point2D.Double[n-1][2];
        for (int i = 0; i < n-2; i++) {
            newBezier[i] = beziers[i];
        }
        newBezier[n-2][0] = new Point2D.Double((values[n-2].x+values[n-1].x)/2,(values[n-2].y+values[n-1].y)/2);
        newBezier[n-2][1] = null;
        beziers = newBezier;
    }

    @Override
    public void removeValue(int index) {
        if (index>=0 && index < values.length) {
            Point2D.Double[] newValues = new Point2D.Double[values.length-1];
            Point2D.Double[][] newBezier = new Point2D.Double[beziers.length-1][2];
            for (int i = 0; i < index; i++) {
                newValues[i] = values[i];
                newBezier[i] = beziers[i];
            }
            for (int i = index+1; i < values.length; i++) {
                newValues[i-1] = values[i];
            }
            for (int i = index+1; i < beziers.length; i++) {
                newBezier[i-1] = beziers[i];
            }
            values = newValues;
            beziers = newBezier;
            fireStateChanged();
        }
    }

    @Override
    public void setValues(Point2D.Double[] values) {
        super.setValues(values);
        beziers = new Point2D.Double[values.length-1][2];
        for (int i = 0; i < values.length-1; i++) {
            beziers[i][0] = new Point2D.Double((values[i].x+values[i+1].x)/2,(values[i].y+values[i+1].y)/2);
            beziers[i][1] = null;
        }
    }

    @Override
    public void setValue(int index, Point2D.Double value) {
        if (index >= 0 && index < values.length) {
            Point2D.Double oldValue = values[index];
            values[index] = value;
            if (index >= 1 && beziers[index-1][1] != null) {
                beziers[index-1][1].x += value.x - oldValue.x;
                beziers[index-1][1].y += value.y - oldValue.y;
            }
            if (index < values.length-1 && beziers[index][0] != null) {
                beziers[index][0].x += value.x - oldValue.x;
                beziers[index][0].y += value.y - oldValue.y;
            }
            fireStateChanged();
        }
    }


    private void setBezierValue(int index, int i, Point2D.Double coord) {
        beziers[index][i] = coord;
        fireStateChanged();
    }

    //
    // PAINT METHODS
    //

    @Override
    public void paintComponent(VisometryGraphics<Point2D.Double> vg) {
        super.paintComponent(vg);
        if (drawControls && values.length > 1) {
            vg.setArrowStyle(controlStyle);
            for (int i = 0; i < beziers.length; i++) {
                if (beziers[i][0] == null)
                    continue;
                else if (beziers[i][1]==null) {
                    vg.drawArrow(values[i], beziers[i][0]);
                    vg.drawArrow(values[i+1], beziers[i][0]);
                } else {
                    vg.drawArrow(values[i], beziers[i][0]);
                    vg.drawArrow(values[i+1], beziers[i][1]);
                }
            }
        }
        if (values.length > 1) {
            vg.setPathStyle(curveStyle);
            GeneralPath gp = new GeneralPath();
            gp.moveTo((float) values[0].x, (float) values[0].y);
            for (int i = 1; i < values.length; i++) {
                if (beziers[i-1][0]==null)
                    gp.lineTo((float) values[i].x, (float) values[i].y);
                else if (beziers[i-1][1]==null)
                    gp.quadTo((float) beziers[i-1][0].x, (float) beziers[i-1][0].y, (float) values[i].x, (float) values[i].y);
                else
                    gp.curveTo((float) beziers[i-1][0].x, (float) beziers[i-1][0].y, (float) beziers[i-1][1].x, (float) beziers[i-1][1].y, (float) values[i].x, (float) values[i].y);
            }
            ((PlaneGraphics)vg).drawPath(gp);
        }
    }
    //
    // EVENT HANDLING
    //

    transient int selectionType = 0;

    @Override
    public boolean isClickablyCloseTo(VisometryMouseEvent<Double> e) {
        boolean closePt = super.isClickablyCloseTo(e);
        if (closePt) {
            selectionType = 0;
            return true;
        } else {
            for (int i = 0; i < beziers.length; i++) {
                if (beziers[i][0] != null && e.withinRangeOf(beziers[i][0], 10)) {
                    selectedIndex = i;
                    selectionType = 1;
                    return true;
                } else if (beziers[i][1] != null && e.withinRangeOf(beziers[i][1], 10)) {
                    selectedIndex = i;
                    selectionType = 2;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void mouseDragged(VisometryMouseEvent<Double> e) {
        if (adjusting && editable && selectedIndex != -1) {
            if (selectionType == 0)
                setValue(selectedIndex, e.getCoordinate());
            else
                setBezierValue(selectedIndex, selectionType-1, e.getCoordinate());
        }
    }

    @Override
    public void mouseClicked(VisometryMouseEvent<Double> e) {
        if (editable && selectedIndex != -1) {
            String mode = MouseEvent.getModifiersExText(e.getModifiersEx());
            if (mode.equals("Ctrl") && selectionType == 0) {
                removeValue(selectedIndex);
            } else if (mode.equals("Alt") && selectionType != 0) {
                setBezierValue(selectedIndex, 1, selectionType == 1 ? beziers[selectedIndex][0] : null);
            } else if (mode.equals("Alt") && selectionType == 0) {
                // TODO - make this force the two beziers at the point to be equal/opposite directions ?
            }
        }
    }



    

}
