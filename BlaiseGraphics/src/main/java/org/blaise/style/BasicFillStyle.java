/*
 * BasicFillStyle.java
 * Created Jan 12, 2011
 */

package org.blaise.style;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Set;

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

    public void draw(Shape s, Graphics2D canvas, Set<VisibilityHint> visibility) {
        if (fill != null) {
            canvas.setColor(StyleUtils.applyHints(fill, visibility));
            canvas.fill(s);
        }
    }

}
