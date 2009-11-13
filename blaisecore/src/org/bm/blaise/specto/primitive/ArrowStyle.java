/**
 * ArrowStyle.java
 * Created on Sep 6, 2009
 */
package org.bm.blaise.specto.primitive;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
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
public class ArrowStyle implements PrimitiveStyle<GraphicArrow> {

    //
    //
    // PROPERTIES
    //
    //
    /** Path style storing color/stroke information */
    PathStyle pathStyle = new PathStyle();
    /** Shape of the arrowhead */
    ArrowShape headShape = ArrowShape.REGULAR;
    /** Shape of the anchor */
    ArrowShape anchorShape = ArrowShape.NONE;
    /** Determines headSize of arrow's head */
    int headSize = 5;

    //
    //
    // CONSTRUCTORS
    //
    //
    public ArrowStyle() {
    }

    public ArrowStyle(Color color) {
        pathStyle = new PathStyle(color);
    }

    public ArrowStyle(PathStyle pathStyle) {
        this.pathStyle = pathStyle;
    }

    public ArrowStyle(Color color, ArrowShape shape, int headSize) {
        pathStyle = new PathStyle(color);
        this.headShape = shape;
        this.headSize = headSize;
    }

    public ArrowStyle(PathStyle pathStyle, ArrowShape shape, int headSize) {
        this.pathStyle = pathStyle;
        this.headShape = shape;
        this.headSize = headSize;
    }


    //
    //
    // BEANS
    //
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

    public void setStroke(Stroke stroke) {
        pathStyle.setStroke(stroke);
    }

    public ArrowShape getAnchorShape() {
        return anchorShape;
    }

    public void setAnchorShape(ArrowShape anchorShape) {
        this.anchorShape = anchorShape;
    }

    public ArrowShape getHeadShape() {
        return headShape;
    }

    public void setHeadShape(ArrowShape shape) {
        this.headShape = shape;
    }

    public int getHeadSize() {
        return headSize;
    }

    public void setHeadSize(int headSize) {
        this.headSize = headSize;
    }

    //
    //
    // GRAPHICS METHODS
    //
    //
    public void draw(GraphicArrow primitive, Graphics2D canvas) {
        canvas.setColor(pathStyle.getColor());
        canvas.setStroke(pathStyle.getStroke());
        canvas.draw(new Line2D.Double(primitive.getAnchor(), primitive.getHead()));
        if (headShape != ArrowShape.NONE) {
            canvas.draw(headShape.getShape(primitive.getHead(),
                    primitive.getHead().getX() - primitive.getAnchor().getX(),
                    primitive.getHead().getY() - primitive.getAnchor().getY(),
                    headSize));
        }
        if (anchorShape != ArrowShape.NONE) {
            canvas.draw(anchorShape.getShape(primitive.getAnchor(),
                    primitive.getAnchor().getX() - primitive.getHead().getX(),
                    primitive.getAnchor().getY() - primitive.getHead().getY(),
                    headSize));
        }
    }

    public void draw(GraphicArrow[] primitives, Graphics2D canvas) {
        canvas.setColor(pathStyle.getColor());
        canvas.setStroke(pathStyle.getStroke());
        Line2D.Double arrowLine = new Line2D.Double();
        for (GraphicArrow arr : primitives) {
            arrowLine.setLine(arr.getAnchor(), arr.getHead());
            canvas.draw(arrowLine);
        }
        if (headShape != ArrowShape.NONE) {
            for (GraphicArrow arr : primitives) {
                canvas.draw(headShape.getShape(arr.getHead(),
                        arr.getHead().getX() - arr.getAnchor().getX(),
                        arr.getHead().getY() - arr.getAnchor().getY(),
                        headSize));
            }
        }
        if (anchorShape != ArrowShape.NONE) {
            for (GraphicArrow arr : primitives) {
                canvas.draw(anchorShape.getShape(arr.getAnchor(),
                        arr.getAnchor().getX() - arr.getHead().getX(),
                        arr.getAnchor().getY() - arr.getHead().getY(),
                        headSize));
            }
        }
    }

    //
    //
    // SPECIALIZED DRAW METHODS
    //
    //
    /** This <code>enum</code> provides for various formatted point styles. */
    public enum ArrowShape {

        NONE {
            public Shape getShape(Point2D point, double dx, double dy, double headSize) {
                return null;
            }
        },
        REGULAR {
            public Shape getShape(Point2D point, double dx, double dy, double headSize) {
                double headAngle = Math.PI / 6;
                double winAngle = Math.atan2(dy, dx);
                GeneralPath path = new GeneralPath();
                path.moveTo((float) point.getX(), (float) point.getY());
                path.lineTo((float) (point.getX() + headSize * Math.cos(Math.PI + winAngle + headAngle)),
                        (float) (point.getY() + headSize * Math.sin(Math.PI + winAngle + headAngle)));
                path.moveTo((float) point.getX(), (float) point.getY());
                path.lineTo((float) (point.getX() + headSize * Math.cos(Math.PI + winAngle - headAngle)),
                        (float) (point.getY() + headSize * Math.sin(Math.PI + winAngle - headAngle)));
                return path;
            }
        },
        TRIANGLE {
            public Shape getShape(Point2D point, double dx, double dy, double headSize) {
                double headAngle = Math.PI / 6;
                double winAngle = Math.atan2(dy, dx);
                GeneralPath path = new GeneralPath();
                path.moveTo((float) point.getX(), (float) point.getY());
                path.lineTo((float) (point.getX() + headSize * Math.cos(Math.PI + winAngle + headAngle)),
                        (float) (point.getY() + headSize * Math.sin(Math.PI + winAngle + headAngle)));
                path.lineTo((float) (point.getX() + headSize * Math.cos(Math.PI + winAngle - headAngle)),
                        (float) (point.getY() + headSize * Math.sin(Math.PI + winAngle - headAngle)));
                path.lineTo((float) point.getX(), (float) point.getY());
                return path;
            }
        },
        DOT {
            public Shape getShape(Point2D point, double dx, double dy, double headSize) {
                return new Ellipse2D.Double(point.getX()-headSize/2, point.getY()-headSize/2, headSize, headSize);
            }
        };

        /** 
         * Returns a shape located at a given point, with a specified direction. Note that
         * the shape may be at the anchor point or head of the arrow... the direction specifies
         * where the rest of the arrow is. So different arrow shapes should be used for the beginning
         * and ending of the arrow.
         *
         * @param point the endpoint of the curve/line/etc.
         * @param dx change in x along the line
         * @param dy change in y along the line
         * @param headSize pixel sizing of the arrowhead
         * @return a shape corresponding to the arrow
         */
        abstract public Shape getShape(Point2D point, double dx, double dy, double headSize);
    }

}
