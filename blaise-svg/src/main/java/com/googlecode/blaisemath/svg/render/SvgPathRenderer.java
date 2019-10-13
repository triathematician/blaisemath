package com.googlecode.blaisemath.svg.render;

import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.xml.SvgElement;

import java.awt.*;

/**
 * Write SVG path representation.
 * @author Elisha Peterson
 */
public class SvgPathRenderer extends SvgRenderer<Shape> {

    @Override
    public void render(Shape shape, AttributeSet style, SvgTreeBuilder canvas) {
        if (Styles.hasStroke(style)) {
            SvgElement res = SvgShapeRenderer.svgElement(shape);
            res.id = StyleWriter.id(style);
            res.style = StyleWriter.toString(style);
            res.addStyle(Styles.FILL, null);
            canvas.add(res);
        }
    }

}
