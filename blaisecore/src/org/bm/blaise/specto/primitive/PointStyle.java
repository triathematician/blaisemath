/**
 * PointStyle.java
 * Created on Aug 4, 2009
 */
package org.bm.blaise.specto.primitive;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 *   <code>PointStyle</code> draws a point on a 2D graphics canvas.
 *   Supports multiple styles of draw, encoded by the <code>shape</code> property,
 *   in the sub-class <code>PointStyle.PointShape</code>.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PointStyle implements PrimitiveStyle<Point2D> {

    //
    //
    // CONSTANTS
    //
    //

    /** Default point. */
    public static final PointStyle REGULAR = new PointStyle();

    /** Point that shows up as a small gray dot. */
    public static final PointStyle SMALL = new PointStyle(PointShape.SOLID_DOT, null, null, Color.DARK_GRAY, 2);

    //
    //
    // PROPERTIES
    //
    //

    /** Determines shape used to indicate the point. */
    PointShape shape = PointShape.OUTLINE_DOT;

    /** Stroke outline */
    Stroke stroke = DEFAULT_STROKE;

    /** Stroke color */
    Color strokeColor = Color.BLACK;

    /** Fill of object */
    Color fillColor = Color.LIGHT_GRAY;

    /** Radius of the point (in pixels) */
    int radius = 5;

    //
    //
    // CONSTRUCTORS
    //
    //

    /** Construct with defaults. */
    public PointStyle() {
    }

    /** Construct with colors only. */
    public PointStyle(Color strokeColor, Color fillColor) {
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
    }

    /** Construct with specified elements. */
    public PointStyle(PointShape shape, Stroke stroke, Color strokeColor, Color fillColor, int radius) {
        this.shape = shape;
        this.stroke = stroke;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.radius = radius;
    }


    //
    //
    // BEANS
    //
    //
    
    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public PointShape getShape() {
        return shape;
    }

    public void setShape(PointShape shape) {
        this.shape = shape;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }

    public Color getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }



    //
    //
    // GRAPHICS METHODS
    //
    //

    public void draw(Point2D point, Graphics2D canvas) {
        double rad = point instanceof GraphicPoint ? ((GraphicPoint) point).radius : radius;
        Shape s = shape.getShape(point.getX(), point.getY(), Math.abs(rad));
        if (shape.filled) {
            canvas.setColor( rad < 0 ? fillColor.darker() : fillColor );
            canvas.fill(s);
        }
        if (shape.stroked) {
            canvas.setStroke(stroke);
            canvas.setColor( rad < 0 ? strokeColor.darker() : strokeColor );
            canvas.draw(s);
        }
    }

    public void draw(Point2D[] points, Graphics2D canvas) {
        for (Point2D p2d : points) {
            draw(p2d, canvas);
        }
    }


    //
    //
    // SPECIALIZED DRAW METHODS
    //
    //

    /** This <code>enum</code> provides for various formatted point styles. */
    public static enum PointShape {
        /** Dot with fill and outline */
        OUTLINE_DOT(true, true) {
            public Shape getShape(double x, double y, double radius) {
                return new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius);
            }
        },
        SOLID_DOT(false, true) {
            public Shape getShape(double x, double y, double radius) {
                return new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius);
            }
        },
        OPEN_DOT(true, false) {
            public Shape getShape(double x, double y, double radius) {
                return new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius);
            }
        },
        OUTLINE_SQUARE(true, true) {
            public Shape getShape(double x, double y, double radius) {
                return new Rectangle2D.Double(x - radius, y - radius, 2*radius, 2*radius);
            }
        },
        SOLID_SQUARE(false, true) {
            public Shape getShape(double x, double y, double radius) {
                return new Rectangle2D.Double(x - radius, y - radius, 2*radius, 2*radius);
            }
        },
        OPEN_SQUARE (true, false) {
            public Shape getShape(double x, double y, double radius) {
                return new Rectangle2D.Double(x - radius, y - radius, 2*radius, 2*radius);
            }
        },
        PLUS(true, false) {
            public Shape getShape(double x, double y, double radius) {
                GeneralPath gp = new GeneralPath();
                gp.moveTo((float) x, (float) (y-radius));
                gp.lineTo((float) x, (float) (y+radius));
                gp.moveTo((float) (x-radius), (float) y);
                gp.lineTo((float) (x+radius), (float) y);
                return gp;
            }
        },
        CROSS(true, false) {
            public Shape getShape(double x, double y, double radius) {
                GeneralPath gp = new GeneralPath();
                double r2 = 0.7 * radius;
                gp.moveTo((float) (x-r2), (float) (y-r2));
                gp.lineTo((float) (x+r2), (float) (y+r2));
                gp.moveTo((float) (x-r2), (float) (y+r2));
                gp.lineTo((float) (x+r2), (float) (y-r2));
                return gp;
            }
        },
        HAPPY_FACE(true, true) {
            public Shape getShape(double x, double y, double radius) {
                GeneralPath gp = new GeneralPath();
                gp.append(new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius), false);
                gp.append(new Ellipse2D.Double(x - radius/3 - radius/12, y - radius/3, radius/6, radius/6), false);
                gp.append(new Ellipse2D.Double(x + radius/3 - radius/12, y - radius/3, radius/6, radius/6), false);
                gp.append(new Arc2D.Double(x - radius/2, y - radius/2, radius, radius, 200, 140, Arc2D.CHORD), false);
                return gp;
            }
        };
        
        boolean stroked;
        boolean filled;

        /** Constructs the shape, with settings that determine whether or not the object
         * has an outline and fill
         * @param stroked true if outline should be drawn, else false
         * @param filled true if point outline should be filled, else false
         */
        PointShape(boolean stroked, boolean filled) {
            this.stroked = stroked;
            this.filled = filled;
        }

        /** Returns shape at given window location and pixel radius. */
        abstract public Shape getShape(double x, double y, double radius);
    }
}
