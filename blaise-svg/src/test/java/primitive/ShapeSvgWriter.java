package primitive;

import com.googlecode.blaisemath.graphics.svg.SvgPathCoder;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.svg.render.StyleWriter;
import com.googlecode.blaisemath.svg.render.SvgRenderException;
import com.googlecode.blaisemath.svg.xml.*;

import java.awt.*;
import java.awt.geom.*;

/**
 * Converts general shape objects to their corresponding SVG elements. Supports {@code Ellipse2D, Line2D, Rectangle2D, RoundRectangle2D}
 * mapping to custom SVG elements, and other shapes to SVG path elements.
 * @author Elisha Peterson
 */
public class ShapeSvgWriter implements PrimitiveSvgWriter<Shape, SvgElement> {

    @Override
    public SvgElement write(String id, Shape shape, AttributeSet style) throws SvgRenderException {
        SvgElement res;
        if (shape == null) {
            throw new SvgRenderException("Invalid shape (null)");
        } else if (shape instanceof Ellipse2D) {
            res = createCircleOrEllipse((Ellipse2D) shape);
        } else if (shape instanceof Rectangle2D) {
            res = createRect((Rectangle2D) shape);
        } else if (shape instanceof RoundRectangle2D) {
            res = createRect((RoundRectangle2D) shape);
        } else if (shape instanceof Line2D) {
            res = createLine((Line2D) shape);
        } else {
            res = createPath(shape);
        }
        res.id = id;
        res.style = StyleWriter.toString(style);
        return res;
    }

    /** Creates ellipse/circle elements */
    private SvgElement createCircleOrEllipse(Ellipse2D r) {
        if (r.getWidth() == r.getHeight()) {
            return SvgCircle.create(r.getCenterX(), r.getCenterY(), r.getWidth()/2);
        } else {
            return SvgEllipse.create(r.getCenterX(), r.getCenterY(), r.getWidth()/2f, r.getHeight()/2);
        }
    }

    /** Creates rect element */
    private SvgElement createRect(Rectangle2D r) {
        return SvgRect.create(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    /** Creates rect element */
    private SvgElement createRect(RoundRectangle2D rr) {
        return SvgRect.create(rr.getX(), rr.getY(), rr.getWidth(), rr.getHeight(), rr.getArcWidth(), rr.getArcHeight());
    }

    /** Creates line element */
    private SvgLine createLine(Line2D r) {
        return SvgLine.create(r.getX1(), r.getY1(), r.getX2(), r.getY2());
    }

    /** Creates path element */
    private SvgPath createPath(Shape shape) throws SvgRenderException {
        // TODO - may want to consider polygon, polyline as potential outputs in some cases?
        return SvgPath.create(new SvgPathCoder().encodeShapePath(shape));
    }

}
