/*
 * BasicPathStyle.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.style;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Draws a path with arbitrary color and width.
 * 
 * @author Elisha
 */
public class BasicPathStyle implements PathStyle {

    Color color = Color.black;
    float thickness = 1f;
    
    public BasicPathStyle() {
    }

    public BasicPathStyle(Color stroke) {
        this.color = stroke;
    }

    public BasicPathStyle(Color stroke, float thickness) {
        this.color = stroke;
        this.thickness = thickness;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color stroke) {
        this.color = stroke;
    }
    
    public BasicPathStyle color(Color stroke) {
        this.color = stroke;
        return this;
    }

    public float getWidth() {
        return thickness;
    }

    public void setWidth(float thickness) {
        this.thickness = thickness;
    }

    public BasicPathStyle width(float thickness) {
        this.thickness = thickness;
        return this;
    }

    public void draw(Shape s, Graphics2D canvas, VisibilityKey visibility) {
        if(getWidth() <= 0f || getColor() == null)
            return;

        canvas.setStroke(new BasicStroke(getWidth()));

        canvas.setColor(visibility == VisibilityKey.Highlight  ? StyleUtils.lighterThan(getColor()) : getColor());
        canvas.draw(s);

        canvas.setStroke(StyleUtils.DEFAULT_STROKE);
    }

    public void drawAll(Iterable<Shape> primitives, Graphics2D canvas, VisibilityKey visibility) {
        if(getWidth() <= 0f && getColor() != null)
            return;

        canvas.setStroke(new BasicStroke(getWidth()));

        canvas.setColor(visibility == VisibilityKey.Highlight  ? StyleUtils.lighterThan(getColor()) : getColor());
        for (Shape s : primitives)
            canvas.draw(s);

        canvas.setStroke(StyleUtils.DEFAULT_STROKE);
    }

}
