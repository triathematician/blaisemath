/*
 * BasicPathStyle.java
 * Created Jan 12, 2011
 */

package org.blaise.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Set;

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

    public Color getStroke() {
        return color;
    }

    public void setColor(Color stroke) {
        this.color = stroke;
    }

    public BasicPathStyle color(Color stroke) {
        this.color = stroke;
        return this;
    }

    public float getThickness() {
        return thickness;
    }

    public void setWidth(float thickness) {
        this.thickness = thickness;
    }

    public BasicPathStyle width(float thickness) {
        this.thickness = thickness;
        return this;
    }

    public void draw(Shape s, Graphics2D canvas, Set<VisibilityHint> visibility) {
        if(getThickness() <= 0f || getStroke() == null)
            return;

        canvas.setStroke(new BasicStroke(getThickness()));

        canvas.setColor(StyleUtils.applyHints(getStroke(), visibility));
        canvas.draw(s);

        canvas.setStroke(StyleUtils.DEFAULT_STROKE);
    }

}
