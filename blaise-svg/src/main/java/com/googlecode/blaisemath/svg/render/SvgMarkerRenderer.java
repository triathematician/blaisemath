package com.googlecode.blaisemath.svg.render;

import com.googlecode.blaisemath.graphics.swing.render.MarkerRenderer;
import com.googlecode.blaisemath.style.AttributeSet;

import java.awt.geom.Point2D;

/**
 * Write SVG marker representation.
 * @author Elisha Peterson
 */
public class SvgMarkerRenderer extends SvgRenderer<Point2D> {

    @Override
    public void render(Point2D primitive, AttributeSet style, SvgTreeBuilder canvas) {
        MarkerRenderer m = new MarkerRenderer();
        new SvgShapeRenderer().render(m.getShape(primitive, style), style, canvas);
    }

}
