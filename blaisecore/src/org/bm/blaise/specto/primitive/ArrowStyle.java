/**
 * ArrowStyle.java
 * Created on Sep 6, 2009
 */
package org.bm.blaise.specto.primitive;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * <p>
 *   <code>ArrowStyle</code> represents a style that is used to draw arrows
 *   (described by the <code>GraphicArrow</code> primitive). I may alter this
 *   to restrict it to the arrowhead, specified by an anchor point and a direction,
 *   but for now the primitive will be displayed as a line segment between the
 *   two points together with an arrow head.
 * </p>
 *
 * @author Elisha Peterson
 */
public class ArrowStyle extends PrimitiveStyle<GraphicArrow> {

    public enum Shape {
        NONE,
        REGULAR,
        DOT,
        TRIANGLE
    }

    //
    // PROPERTIES
    //

    /** Path style storing color/stroke information */
    PathStyle pathStyle = new PathStyle();
    /** Shape of the arrowhead */
    Shape headShape = Shape.REGULAR;
    /** Shape of the anchor */
    Shape anchorShape = Shape.NONE;
    /** Determines headSize of arrow's head */
    int headSize = 5;

    //
    // CONSTRUCTORS
    //

    public ArrowStyle() {
    }

    public ArrowStyle(Color color) {
        pathStyle = new PathStyle(color);
    }

    public ArrowStyle(PathStyle pathStyle) {
        this.pathStyle = pathStyle;
    }

    public ArrowStyle(Color color, Shape shape, int headSize) {
        pathStyle = new PathStyle(color);
        this.headShape = shape;
        this.headSize = headSize;
    }

    public ArrowStyle(PathStyle pathStyle, Shape shape, int headSize) {
        this.pathStyle = pathStyle;
        this.headShape = shape;
        this.headSize = headSize;
    }

    public ArrowStyle(Shape headShape, Shape anchorShape) {
        this.headShape = headShape;
        this.anchorShape = anchorShape;
    }

    public ArrowStyle(PathStyle pathStyle, Shape headShape, Shape anchorShape, int headSize) {
        this.pathStyle = pathStyle;
        this.headShape = headShape;
        this.anchorShape = anchorShape;
        this.headSize = headSize;
    }

    @Override
    public Object clone() {
        return new ArrowStyle(
                new PathStyle(pathStyle.getColor(), pathStyle.getThickness()),
                headShape, anchorShape, headSize
                );
    }

    //
    // BEANS
    //

    public Color getColor() {
        return pathStyle.getColor();
    }

    public void setColor(Color color) {
        pathStyle.setColor(color);
    }

    public Stroke getStroke() {
        return pathStyle.getStroke();
    }

    public void setStroke(BasicStroke stroke) {
        pathStyle.setStroke(stroke);
    }

    public void setThickness(float width) {
        pathStyle.setThickness(width);
    }

    public float getThickness() {
        return pathStyle.getThickness();
    }

    public Shape getAnchorShape() {
        return anchorShape;
    }

    public void setAnchorShape(Shape anchorShape) {
        this.anchorShape = anchorShape;
    }

    public Shape getHeadShape() {
        return headShape;
    }

    public void setHeadShape(Shape shape) {
        this.headShape = shape;
    }

    public int getHeadSize() {
        return headSize;
    }

    public void setHeadSize(int headSize) {
        this.headSize = headSize;
    }

    //
    // GRAPHICS METHODS
    //
    
    public void draw(Graphics2D canvas, GraphicArrow primitive, boolean selected) {
        canvas.setColor(pathStyle.getColor());
        canvas.setStroke(pathStyle.getStroke());
        canvas.draw(new Line2D.Double(primitive.getAnchor(), primitive.getHead()));
        if (headShape != Shape.NONE) {
            canvas.draw(getShape(primitive.getHead(),
                    primitive.getHead().x - primitive.getAnchor().x,
                    primitive.getHead().y - primitive.getAnchor().y,
                    headSize, headShape));
        }
        if (anchorShape != Shape.NONE) {
            canvas.draw(getShape(primitive.getAnchor(),
                    primitive.getAnchor().x - primitive.getHead().x,
                    primitive.getAnchor().y - primitive.getHead().y,
                    headSize, anchorShape));
        }
    }

    //
    // SPECIALIZED DRAW METHODS
    //

    java.awt.Shape getShape(Point2D point, double dx, double dy, double headSize, Shape shape) {
        double headAngle = Math.PI / 6;
        double winAngle = Math.atan2(dy, dx);
        
        switch(shape) {
            case REGULAR:
                GeneralPath path1 = new GeneralPath();
                path1.moveTo((float) point.getX(), (float) point.getY());
                path1.lineTo((float) (point.getX() + headSize * Math.cos(Math.PI + winAngle + headAngle)),
                        (float) (point.getY() + headSize * Math.sin(Math.PI + winAngle + headAngle)));
                path1.moveTo((float) point.getX(), (float) point.getY());
                path1.lineTo((float) (point.getX() + headSize * Math.cos(Math.PI + winAngle - headAngle)),
                        (float) (point.getY() + headSize * Math.sin(Math.PI + winAngle - headAngle)));
                return path1;
            case TRIANGLE:
                GeneralPath path2 = new GeneralPath();
                path2.moveTo((float) point.getX(), (float) point.getY());
                path2.lineTo((float) (point.getX() + headSize * Math.cos(Math.PI + winAngle + headAngle)),
                        (float) (point.getY() + headSize * Math.sin(Math.PI + winAngle + headAngle)));
                path2.lineTo((float) (point.getX() + headSize * Math.cos(Math.PI + winAngle - headAngle)),
                        (float) (point.getY() + headSize * Math.sin(Math.PI + winAngle - headAngle)));
                path2.lineTo((float) point.getX(), (float) point.getY());
                return path2;
            case DOT:
                return new Ellipse2D.Double(point.getX()-headSize/2, point.getY()-headSize/2, headSize, headSize);
            case NONE:
            default:
                return null;
        }
    }
    
}
