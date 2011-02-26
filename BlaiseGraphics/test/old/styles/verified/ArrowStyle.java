/**
 * ArrowStyle.java
 * Created on Sep 6, 2009
 */
package old.styles.verified;

import old.styles.verified.AbstractPathStyle;
import graphics.renderer.ShapeRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Map;
import static graphics.renderer.StyleUtils.*;

/**
 * <p>
 *   <code>ArrowStyle</code> represents a style that is used to draw arrows
 *   (described by two points). For now the primitive will be displayed as a line segment between the
 *   two points together with an arrow head.
 * </p>
 *
 * @author Elisha Peterson
 */
public class ArrowStyle extends AbstractPathStyle
        implements ShapeRenderer<Point2D.Double[]> {

    private static final Map<String,Class> $KEY_MAP = keyMap(
            $KEY_STROKE, BasicStroke.class,
            $KEY_THICKNESS, Float.class,
            $KEY_STROKE_COLOR, Color.class,
            $KEY_OPACITY, float.class,
            $KEY_HEAD_SHAPE, ArrowShape.class,
            $KEY_ANCHOR_SHAPE, ArrowShape.class,
            $KEY_SIZE, Integer.class
            );

    /** ArrowShape types for the ends of the arrow */
    public enum ArrowShape {
        NONE(false), 
        REGULAR(false),
        DOT(true),
        TRIANGLE(true);
        
        boolean fill;
        ArrowShape(boolean fill) { this.fill = fill; }
    }

    /** ArrowShape of the arrowhead */
    ArrowShape headShape = ArrowShape.REGULAR;
    /** ArrowShape of the anchor */
    ArrowShape anchorShape = ArrowShape.NONE;
    /** Determines shapeSize of arrow's anchor and head shapes */
    int shapeSize = 5;

    /** Construct with default parameters */
    public ArrowStyle() { super(); }
    /** Construct with specified color */
    public ArrowStyle(Color color) { super(color); }
    /** Construct with specified head shape and size */
    public ArrowStyle(Color color, ArrowShape shape, int shapeSize) { super(color); this.headShape = shape; this.shapeSize = shapeSize; }
    /** Construct with specified anchor and head shapes */
    public ArrowStyle(ArrowShape anchorShape, ArrowShape headShape, int shapeSize) { this.anchorShape = anchorShape; this.headShape = headShape; this.shapeSize = shapeSize; }
    /** Construct with specified parameters */
    public ArrowStyle(Color color, float width, ArrowShape anchorShape, ArrowShape headShape, int shapeSize) { super(color, width); this.anchorShape = anchorShape; this.headShape = headShape; this.shapeSize = shapeSize; }

    @Override
    public Object clone() {
        ArrowStyle result = new ArrowStyle(headShape, anchorShape, shapeSize);
        result.stroke = stroke; result.strokeColor = strokeColor;
        return result;
    }
    @Override public String toString() { return "ArrowStyle["+headShape + ", " + anchorShape + "]"; }
    public Class<? extends Point2D.Double[]> getTargetType() { return Point2D.Double[].class; }
    @Override public Map<String, Class> getStyleTypes() { return $KEY_MAP; }

    /** @return arrow shape of anchor */
    public ArrowShape getAnchorShape() { return anchorShape; }
    /** Sets arrow shape of anchor */
    public void setAnchorShape(ArrowShape anchorShape) { this.anchorShape = anchorShape; }

    /** @return arrow shape of head */
    public ArrowShape getHeadShape() { return headShape; }
    /** Sets arrow shape of head */
    public void setHeadShape(ArrowShape shape) { this.headShape = shape; }
    
    /** @return size of arrow head/anchor */
    public int getShapeSize() { return shapeSize; }
    /** Sets size of arrow head/anchor */
    public void setShapeSize(int shapeSize) { this.shapeSize = shapeSize; }
    
    public void draw(Graphics2D canvas, Point2D.Double[] primitive) {
        canvas.setColor(strokeColor);
        canvas.setStroke(stroke);
        canvas.draw(new Line2D.Double(primitive[0], primitive[1]));
        if (headShape != ArrowShape.NONE) {
            java.awt.Shape s = getShape(primitive[1],
                    primitive[1].x - primitive[0].x,
                    primitive[1].y - primitive[0].y,
                    shapeSize, headShape);
            if (headShape.fill) canvas.fill(s); else canvas.draw(s);
        }
        if (anchorShape != ArrowShape.NONE) {
            Shape s = getShape(primitive[0],
                    primitive[0].x - primitive[1].x,
                    primitive[0].y - primitive[1].y,
                    shapeSize, anchorShape);
            if (anchorShape.fill) canvas.fill(s); else canvas.draw(s);
        }
    }

    public void drawArray(Graphics2D canvas, Point2D.Double[][] primitives) {
        for (Point2D.Double[] p : primitives)
            draw(canvas, p);
    }

    public boolean contained(Point2D.Double[] primitive, Graphics2D canvas, Point point) {
        return false;
    }

    public int containedInArray(Point2D.Double[][] primitives, Graphics2D canvas, Point point) {
        for (int i = 0; i < primitives.length; i++)
            if (contained(primitives[i], canvas, point))
                return i;
        return -1;
    }

    //
    // SPECIALIZED DRAW METHODS
    //

    // TODO - transfer this to the shapes class

    java.awt.Shape getShape(Point2D.Double point, double winAngle, double headSize, ArrowShape shape) {
        double headAngle = Math.PI / 6;
        double winAngle = Math.atan2(dy, dx);
        
        switch(shape) {
            case REGULAR:
                GeneralPath path1 = new GeneralPath();
                path1.moveTo((float) point.x, (float) point.y);
                path1.lineTo((float) (point.x + headSize * Math.cos(Math.PI + winAngle + headAngle)),
                        (float) (point.y + headSize * Math.sin(Math.PI + winAngle + headAngle)));
                path1.moveTo((float) point.x, (float) point.y);
                path1.lineTo((float) (point.x + headSize * Math.cos(Math.PI + winAngle - headAngle)),
                        (float) (point.y + headSize * Math.sin(Math.PI + winAngle - headAngle)));
                return path1;
            case TRIANGLE:
                GeneralPath path2 = new GeneralPath();
                path2.moveTo((float) point.x, (float) point.y);
                path2.lineTo((float) (point.x + headSize * Math.cos(Math.PI + winAngle + headAngle)),
                        (float) (point.y + headSize * Math.sin(Math.PI + winAngle + headAngle)));
                path2.lineTo((float) (point.x + headSize * Math.cos(Math.PI + winAngle - headAngle)),
                        (float) (point.y + headSize * Math.sin(Math.PI + winAngle - headAngle)));
                path2.lineTo((float) point.x, (float) point.y);
                return path2;
            case DOT:
                return new Ellipse2D.Double(point.x-headSize/2, point.y-headSize/2, headSize, headSize);
            case NONE:
            default:
                return null;
        }
    }

}
