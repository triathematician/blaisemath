package com.googlecode.blaisemath.svg.render;

import com.googlecode.blaisemath.graphics.svg.SvgPathCoder;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.xml.*;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * Write SVG shape representation. SVG element type depends on input shape type.
 * @author Elisha Peterson
 */
public class SvgShapeRenderer extends SvgRenderer<Shape> {

    @Override
    public void render(Shape shape, AttributeSet style, SvgTreeBuilder canvas) {
        SvgElement res = svgElement(shape);
        res.id = StyleWriter.id(style);
        res.style = StyleWriter.toString(style);
        canvas.add(res);
    }

    public static SvgElement svgElement(Shape shape) {
        if (shape == null) {
            throw new SvgRenderException("Null shape");
        } else if (shape instanceof Ellipse2D) {
            return createCircleOrEllipse((Ellipse2D) shape);
        } else if (shape instanceof Rectangle2D) {
            return createRect((Rectangle2D) shape);
        } else if (shape instanceof RoundRectangle2D) {
            return createRect((RoundRectangle2D) shape);
        } else if (shape instanceof Line2D) {
            return createLine((Line2D) shape);
        } else {
            return createPath(shape);
        }
    }

    /** Creates ellipse/circle elements */
    private static SvgElement createCircleOrEllipse(Ellipse2D r) {
        if (r.getWidth() == r.getHeight()) {
            return SvgCircle.create(r.getCenterX(), r.getCenterY(), r.getWidth()/2);
        } else {
            return SvgEllipse.create(r.getCenterX(), r.getCenterY(), r.getWidth()/2f, r.getHeight()/2);
        }
    }

    /** Creates rect element */
    private static SvgElement createRect(Rectangle2D r) {
        return SvgRect.create(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /** Creates rect element */
    private static SvgElement createRect(RoundRectangle2D rr) {
        return SvgRect.create(rr.getX(), rr.getY(), rr.getWidth(), rr.getHeight(), rr.getArcWidth(), rr.getArcHeight());
    }

    /** Creates line element */
    private static SvgLine createLine(Line2D r) {
        return SvgLine.create(r.getX1(), r.getY1(), r.getX2(), r.getY2());
    }

    /** Creates path element */
    private static SvgPath createPath(Shape shape) throws SvgRenderException {
        // TODO - may want to consider polygon, polyline as potential outputs in some cases?
        return SvgPath.create(new SvgPathCoder().encodeShapePath(shape));
    }

}
