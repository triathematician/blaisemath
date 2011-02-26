/*
 * SpaceAxes.java
 * Created on Oct 22, 2009
 */

package org.bm.blaise.specto.space;

import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.StringStyle;
import org.bm.blaise.specto.visometry.Plottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.Point3D;

/**
 * <p>
 *    This class is responsible for rendering the coordinate axes in 3D space.
 * </p>
 * @author ae3263
 */
public class SpaceAxes extends Plottable<Point3D> {

    StringStyle labStyle = new StringStyle(BlaisePalette.STANDARD.axisLabel());

    String sx = "x";
    String sy = "y";
    String sz = "z";

    AxisType type = AxisType.REGULAR;

    boolean showOrigin = true;
    boolean showAxes = true;
    boolean showXY = false;
    boolean showYZ = false;
    boolean showXZ = false;

    public SpaceAxes() {
    }

    public SpaceAxes(String sx, String sy, String sz) {
        this.sx = sx;
        this.sy = sy;
        this.sz = sz;
    }
    
    @Override
    public String toString() {
        return "3D Axes";
    }

    //
    // BEAN PATTERNS
    //

    public String getLabelX() {
        return sx;
    }

    public void setLabelX(String sx) {
        this.sx = sx;
    }

    public String getLabelY() {
        return sy;
    }

    public void setLabelY(String sy) {
        this.sy = sy;
    }

    public String getLabelZ() {
        return sz;
    }

    public void setLabelZ(String sz) {
        this.sz = sz;
    }

    public boolean isShowAxes() {
        return showAxes;
    }

    public void setShowAxes(boolean showAxes) {
        this.showAxes = showAxes;
    }

    public AxisType getType() {
        return type;
    }

    public void setType(AxisType type) {
        this.type = type;
    }

    public boolean isShowOrigin() {
        return showOrigin;
    }

    public void setShowOrigin(boolean showOrigin) {
        this.showOrigin = showOrigin;
    }

    public boolean isShowXYPlane() {
        return showXY;
    }

    public void setShowXYPlane(boolean showXY) {
        this.showXY = showXY;
    }

    public boolean isShowXZPlane() {
        return showXZ;
    }

    public void setShowXZPlane(boolean showXZ) {
        this.showXZ = showXZ;
    }

    public boolean isShowYZPlane() {
        return showYZ;
    }

    public void setShowYZPlane(boolean showYZ) {
        this.showYZ = showYZ;
    }

    public StringStyle getLabelStyle() {
        return labStyle;
    }

    public void setLabelStyle(StringStyle labStyle) {
        this.labStyle = labStyle;
    }
   
    //
    // PAINT METHODS
    //

    @Override
    public void draw(VisometryGraphics<Point3D> vg) {
        SpaceGraphics sg = (SpaceGraphics) vg;

        if (showOrigin)
            sg.drawPoint( Point3D.ZERO );

        if (showAxes) {
            switch(type) {
                case IJK:
                case POSITIVE:
                    sg.drawArrow( Point3D.ZERO, Point3D.PLUS_I );
                    sg.drawArrow( Point3D.ZERO, Point3D.PLUS_J );
                    sg.drawArrow( Point3D.ZERO, Point3D.PLUS_K );
                    break;
                case BOX:
                case REGULAR:
                    sg.drawArrow( Point3D.MINUS_I, Point3D.PLUS_I );
                    sg.drawArrow( Point3D.MINUS_J, Point3D.PLUS_J );
                    sg.drawArrow( Point3D.MINUS_K, Point3D.PLUS_K );
                default:
            }

            // label axes
            vg.setStringStyle(labStyle);
            vg.drawString(sx, Point3D.PLUS_I, 5, 5);
            vg.drawString(sy, Point3D.PLUS_J, 5, 5);
            vg.drawString(sz, Point3D.PLUS_K, 5, 5);
        }

        if (showXY)
            sg.drawShape(new Point3D[]{new Point3D(.001, .001, 0), new Point3D(.001, .9, 0), new Point3D(.9, .9, 0), new Point3D(.9, .001, 0)});
        if (showXZ)
            sg.drawShape(new Point3D[]{new Point3D(.001, 0, .001), new Point3D(.001, 0, .9), new Point3D(.9, 0, .9), new Point3D(.9, 0, .001)});
        if (showYZ)
            sg.drawShape(new Point3D[]{new Point3D(0, .001, .001), new Point3D(0, .001, .9), new Point3D(0, .9, .9), new Point3D(0, .9, .001)});
    }



    
    /** Determines the type of axes that are displayed. */
    public static enum AxisType {
        IJK("i,j,k"),
        POSITIVE("Positive axes"),
        REGULAR("Regular axes"),
        BOX("Boxed area");

        String name;
        @Override
        public String toString() { return name; }

        AxisType(String name) { this.name = name; }
    }
}
