/*
 * BasicShapeStyle.java
 * Created Jan 9, 2011 (based on much earlier code)
 */

package org.blaise.style;

import java.awt.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Draws a point on the screen.
 * @author Elisha
 */
public class BasicShapeStyle implements ShapeStyle {

    Color fill;
    Color stroke;
    float thickness = 1f;

    public BasicShapeStyle(Color fill, Color stroke) {
        this.fill = fill;
        this.stroke = stroke;
    }

    public BasicShapeStyle(Color fill, Color stroke, float thickness) {
        this.fill = fill;
        this.stroke = stroke;
        this.thickness = thickness;
    }

    public Color getFill() {
        return fill;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public Color getStroke() {
        return stroke;
    }

    public void setStroke(Color stroke) {
        this.stroke = stroke;
    }

    public float getThickness() {
        return thickness;
    }

    public void setThickness(float thickness) {
        this.thickness = thickness;
    }

    public void draw(Shape s, Graphics2D canvas, VisibilityHint visibility) {
        if (thickness <= 0)
            return;

        canvas.setStroke(new BasicStroke(thickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, 5.0f, null, 0.0f));

        if (fill != null) {
            canvas.setColor(
                    visibility == VisibilityHint.Highlight ? StyleUtils.lighterThan(fill)
                    : visibility == VisibilityHint.Obscure ? StyleUtils.blanderThan(fill)
                    : fill);
            canvas.fill(s);
        }
        if (stroke != null) {
            canvas.setColor(
                    visibility == VisibilityHint.Highlight ? StyleUtils.lighterThan(stroke)
                    : visibility == VisibilityHint.Obscure ? StyleUtils.blanderThan(stroke)
                    : stroke);
            canvas.draw(s);
        }

        canvas.setStroke(StyleUtils.DEFAULT_STROKE);
    }

}
