/*
 * StrokeRenderer.java
 * Created Jan 12, 2011
 */

package org.bm.blaise.graphics.renderer;

import org.bm.blaise.graphics.GraphicVisibility;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Draws a point on the screen.
 * @author Elisha
 */
public class BasicStrokeRenderer implements ShapeRenderer {

    Color stroke;
    float thickness = 1f;

    public BasicStrokeRenderer(Color stroke) {
        this.stroke = stroke;
    }

    public BasicStrokeRenderer(Color stroke, float thickness) {
        this.stroke = stroke;
        this.thickness = thickness;
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

    public void draw(Shape s, Graphics2D canvas, GraphicVisibility visibility) {
        if(thickness <= 0f && stroke != null)
            return;

        canvas.setStroke(new BasicStroke(thickness));

        canvas.setColor(visibility == GraphicVisibility.Highlight  ? StyleUtils.lighterThan(stroke) : stroke);
        canvas.draw(s);

        canvas.setStroke(StyleUtils.DEFAULT_STROKE);
    }

    public void drawAll(Iterable<Shape> primitives, Graphics2D canvas, GraphicVisibility visibility) {
        if(thickness <= 0f && stroke != null)
            return;

        canvas.setStroke(new BasicStroke(thickness));

        canvas.setColor(visibility == GraphicVisibility.Highlight  ? StyleUtils.lighterThan(stroke) : stroke);
        for (Shape s : primitives)
            canvas.draw(s);

        canvas.setStroke(StyleUtils.DEFAULT_STROKE);
    }

}
