/*
 * BasicPointRenderer.java
 * Created Jan 22, 2011
 */

package org.bm.blaise.graphics.renderer;

import java.awt.Color;

/**
 * Draws a point on the graphics canvas.
 * @author Elisha
 */
public class BasicPointRenderer extends AbstractPointRenderer {

    /** Style of the point */
    ShapeProvider shaper = ShapeLibrary.CIRCLE;
    /** Radius of the displayed point */
    float radius = 4;
    /** Delegate renderer used to draw the point */
    BasicShapeRenderer rend = new BasicShapeRenderer(Color.lightGray, Color.darkGray);

// <editor-fold defaultstate="collapsed" desc="Constructors & Initializers">
    public BasicPointRenderer() { }

    /** Sets radius & returns pointer to object */
    public BasicPointRenderer radius(float radius) { this.radius = radius; return this; }
    /** Sets stroke color & returns pointer to object */
    public BasicPointRenderer stroke(Color c) { rend.stroke = c; return this; }
    /** Sets fill color & returns pointer to object */
    public BasicPointRenderer fill(Color c) { rend.fill = c; return this; }
    /** Sets shape & returns pointer to object */
    public BasicPointRenderer shape(ShapeProvider s) { this.shaper = s; return this; }
    /** Sets thickness & returns pointer to object */
    public BasicPointRenderer thickness(float thick) { rend.thickness = thick; return this; }

// </editor-fold>

    @Override
    public BasicPointRenderer clone() {
        return new BasicPointRenderer().fill(rend.fill).stroke(rend.stroke).thickness(rend.thickness).radius(radius).shape(shaper);
    }

// <editor-fold defaultstate="collapsed" desc="Property Patterns">

    public BasicShapeRenderer getShapeRenderer() { return rend; }

    public ShapeLibrary getShape() { return (ShapeLibrary) shaper; }
    public void setShape(ShapeLibrary shaper) { this.shaper = shaper; }

    public float getRadius() { return radius; }
    public final void setRadius(float r) { radius = Math.max(r, 1); }

    public Color getFill() { return rend.getFill(); }
    public void setFill(Color fill) { rend.setFill(fill); }

    public Color getStroke() { return rend.getStroke(); }
    public void setStroke(Color stroke) { rend.setStroke(stroke); }

    public float getThickness() { return rend.getThickness(); }
    public void setThickness(float thickness) { rend.setThickness(thickness); }
// </editor-fold>

}
