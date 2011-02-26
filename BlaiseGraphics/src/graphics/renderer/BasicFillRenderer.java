/*
 * FillRenderer.java
 * Created Jan 12, 2011
 */

package graphics.renderer;

import graphics.GraphicVisibility;
import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Fills a shape using stored color and opacity.
 * @author Elisha
 */
public class BasicFillRenderer implements ShapeRenderer {

    Color fill;

    public BasicFillRenderer(Color fill) {
        this.fill = fill;
    }

    public Color getFill() {
        return fill;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public void draw(Shape s, Graphics2D canvas, GraphicVisibility visibility) {
        if (fill != null) {
            canvas.setColor(visibility == GraphicVisibility.Highlight ? StyleUtils.lighterThan(fill) : fill);
            canvas.fill(s);
        }
    }

    public void drawAll(Iterable<Shape> primitives, Graphics2D canvas, GraphicVisibility visibility) {
        if (fill != null) {
            canvas.setColor(visibility == GraphicVisibility.Highlight ? StyleUtils.lighterThan(fill) : fill);
            for (Shape s : primitives)
                canvas.fill(s);
        }
    }

}
