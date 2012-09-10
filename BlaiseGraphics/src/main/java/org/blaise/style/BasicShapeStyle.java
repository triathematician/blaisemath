/*
 * BasicShapeStyle.java
 * Created Jan 9, 2011 (based on much earlier code)
 */

package org.blaise.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Set;

/**
 * Draws a shape.
 *
 * @author Elisha
 */
public class BasicShapeStyle implements PathStyle {

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

    public void draw(Shape s, Graphics2D canvas, Set<VisibilityHint> visibility) {
        if (fill != null) {
            canvas.setColor(StyleUtils.applyHints(fill, visibility));
            canvas.fill(s);
        }
        if (stroke != null && thickness >= 0) {
            canvas.setColor(StyleUtils.applyHints(stroke, visibility));
            canvas.setStroke(visibility != null
                    && (visibility.contains(VisibilityHint.Highlight) || visibility.contains(VisibilityHint.Selected))
                    ? new BasicStroke(thickness+1f) : new BasicStroke(thickness));
            canvas.draw(s);
        }

        canvas.setStroke(StyleUtils.DEFAULT_STROKE);
    }

}
