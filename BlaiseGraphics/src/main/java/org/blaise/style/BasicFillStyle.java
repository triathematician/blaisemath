/*
 * BasicFillStyle.java
 * Created Jan 12, 2011
 */

package org.blaise.style;

import java.awt.Shape;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Fills a shape using a specified color (no outline).
 *
 * @author Elisha
 */
public class BasicFillStyle implements ShapeStyle {

    Color fill;

    public BasicFillStyle(Color fill) {
        this.fill = fill;
    }

    public Color getFill() {
        return fill;
    }

    public void setFill(Color fill) {
        this.fill = fill;
    }

    public void draw(Shape s, Graphics2D canvas, VisibilityHint visibility) {
        if (fill != null) {
            canvas.setColor(
                    visibility == VisibilityHint.Highlight ? StyleUtils.lighterThan(fill)
                    : visibility == VisibilityHint.Obscure ? StyleUtils.blanderThan(fill)
                    : fill);
            canvas.fill(s);
        }
    }

}
