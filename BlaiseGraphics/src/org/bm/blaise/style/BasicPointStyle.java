/*
 * BasicPointStyle.java
 * Created Jan 22, 2011
 */

package org.bm.blaise.style;

import org.bm.blaise.shape.ShapeProvider;
import org.bm.blaise.shape.ShapeLibrary;
import java.awt.Color;

/**
 * Draws a point on the graphics canvas.
 * @author Elisha
 */
public class BasicPointStyle extends PointStyleSupport {

    /** Style of the point */
    ShapeProvider shaper = new ShapeLibrary.CircleShape();
    /** Radius of the displayed point */
    float radius = 4;
    /** Delegate style used to draw the point */
    BasicShapeStyle shapeStyle = new BasicShapeStyle(Color.lightGray, Color.darkGray);


    // <editor-fold defaultstate="collapsed" desc="Constructors & Initializers">

    /** Construct instance with default settings */
    public BasicPointStyle() { }

    @Override
    public String toString() {
        return String.format("BasicPointStyle[fill=%s, stroke=%s, stroke_width=%.1f, radius=%.1f, shape=%s]", shapeStyle.fill, shapeStyle.stroke, shapeStyle.thickness, radius, shaper);
    }

    @Override
    public BasicPointStyle clone() {
        return new BasicPointStyle().fill(shapeStyle.fill).stroke(shapeStyle.stroke).thickness(shapeStyle.thickness).radius(radius).shape(shaper);
    }

    /** Sets radius & returns pointer to object */
    public BasicPointStyle radius(float radius) {
        this.radius = radius;
        return this;
    }

    /** Sets stroke color & returns pointer to object */
    public BasicPointStyle stroke(Color c) {
        shapeStyle.stroke = c;
        return this;
    }

    /** Sets fill color & returns pointer to object */
    public BasicPointStyle fill(Color c) {
        shapeStyle.fill = c;
        return this;
    }

    /** Sets shape & returns pointer to object */
    public BasicPointStyle shape(ShapeProvider s) {
        this.shaper = s;
        return this;
    }

    /** Sets thickness & returns pointer to object */
    public BasicPointStyle thickness(float thick) {
        shapeStyle.thickness = thick;
        return this;
    }

    // </editor-fold>


    // <editor-fold defaultstate="collapsed" desc="Property Patterns">

    protected BasicShapeStyle getShapeStyle() {
        return shapeStyle;
    }
    protected void setShapeStyle(BasicShapeStyle r) {
        this.shapeStyle = r;
    }

    public ShapeProvider getShape() {
        return shaper;
    }

    public void setShape(ShapeProvider shaper) {
        this.shaper = shaper;
    }

    public float getRadius() {
        return radius;
    }

    public final void setRadius(float r) {
        radius = Math.max(r, 1);
    }

    public Color getFill() {
        return shapeStyle.getFill();
    }
    public void setFill(Color fill) {
        shapeStyle.setFill(fill);
    }

    public Color getStroke() {
        return shapeStyle.getStroke();
    }

    public void setStroke(Color stroke) {
        shapeStyle.setStroke(stroke);
    }

    public float getThickness() {
        return shapeStyle.getThickness();
    }

    public void setThickness(float thickness) {
        shapeStyle.setThickness(thickness);
    }
    // </editor-fold>

}
