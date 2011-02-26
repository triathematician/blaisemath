/*
 * BasicShapeRenderer.java
 * Created Jan 9, 2011 (based on much earlier code)
 */

package graphics.renderer;

import graphics.GraphicVisibility;
import java.awt.AlphaComposite;
import java.awt.Shape;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Draws a point on the screen.
 * @author Elisha
 */
public class BasicShapeRenderer implements ShapeRenderer {

    Color fill;
    Color stroke;
    float thickness = 1f;

    public BasicShapeRenderer(Color fill, Color stroke) {
        this.fill = fill;
        this.stroke = stroke;
    }

    public BasicShapeRenderer(Color fill, Color stroke, float thickness) {
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

    public void draw(Shape s, Graphics2D canvas, GraphicVisibility visibility) {
        if (thickness != 1f)
            canvas.setStroke(new BasicStroke(thickness));

        if (fill != null) {
            canvas.setColor(visibility == GraphicVisibility.Highlight ? StyleUtils.lighterThan(fill) : fill);
            canvas.fill(s);
        }
        if (stroke != null && stroke != null) {
            canvas.setColor(visibility == GraphicVisibility.Highlight ? StyleUtils.lighterThan(stroke) : stroke);
            canvas.draw(s);
        }

        if (thickness != 1f)
            canvas.setStroke(StyleUtils.DEFAULT_STROKE);
    }

    public void drawAll(Iterable<Shape> primitives, Graphics2D canvas, GraphicVisibility visibility) {
        if (thickness != 1f)
            canvas.setStroke(new BasicStroke(thickness));

        for (Shape s : primitives) {
            if (fill != null) {
                canvas.setColor(visibility == GraphicVisibility.Highlight ? StyleUtils.lighterThan(fill) : fill);
                canvas.fill(s);
            }
            if (stroke != null) {
                canvas.setColor(visibility == GraphicVisibility.Highlight ? StyleUtils.lighterThan(stroke) : stroke);
                canvas.draw(s);
            }
        }

        if (thickness != 1f)
            canvas.setStroke(StyleUtils.DEFAULT_STROKE);
    }

}
