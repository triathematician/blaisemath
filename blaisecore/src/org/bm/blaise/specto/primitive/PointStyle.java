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
    public static final PointStyle SMALL = new PointStyle(PointShape.CIRCLE, null, null, Color.DARK_GRAY, 2);

    //
    //
    // PROPERTIES
    //
    //

    /** Determines shape used to indicate the point. */
    PointShape shape = PointShape.CIRCLE;

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
        if (fillColor != null) {
            canvas.setColor( rad < 0 ? fillColor.darker() : fillColor );
            canvas.fill(s);
        }
        if (stroke != null && strokeColor != null) {
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
        CIRCLE {
            public Shape getShape(double x, double y, double radius) {
                return new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius);
            }
        },
        SQUARE {
            public Shape getShape(double x, double y, double radius) {
                return new Rectangle2D.Double(x - radius/Math.sqrt(2), y - radius/Math.sqrt(2), 2*radius/Math.sqrt(2), 2*radius/Math.sqrt(2));
            }
        },
        DIAMOND {
            public Shape getShape(double x, double y, double radius) {
                GeneralPath gp = new GeneralPath();
                gp.moveTo((float)x, (float)(y-radius));
                gp.lineTo((float)(x-radius), (float)y);
                gp.lineTo((float)x, (float)(y+radius));
                gp.lineTo((float)(x+radius), (float)y);
                gp.closePath();
                return gp;
            }
        },
        TRIANGLE {
            public Shape getShape(double x, double y, double radius) {
                GeneralPath gp = new GeneralPath();
                gp.moveTo((float)x, (float) (y-radius));
                gp.lineTo((float)(x+radius*Math.cos(Math.PI*1.16667)), (float)(y-radius*Math.sin(Math.PI*1.16667)));
                gp.lineTo((float)(x+radius*Math.cos(Math.PI*1.83333)), (float)(y-radius*Math.sin(Math.PI*1.83333)));
                gp.closePath();
                return gp;
            }
        },
        PLUS {
            public Shape getShape(double x, double y, double radius) {
                GeneralPath gp = new GeneralPath();
                gp.moveTo((float) x, (float) (y-radius));
                gp.lineTo((float) x, (float) (y+radius));
                gp.moveTo((float) (x-radius), (float) y);
                gp.lineTo((float) (x+radius), (float) y);
                return gp;
            }
        },
        CROSS {
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
        STAR {
            public Shape getShape(double x, double y, double radius) {
                GeneralPath gp = new GeneralPath();
                gp.moveTo((float)x, (float) (y-radius));
                for (int i = 0; i < 5; i++) {
                    double angle = Math.PI/2 + 2*Math.PI*i/5;
                    gp.lineTo((float)(x+radius*Math.cos(angle)), (float)(y-radius*Math.sin(angle)));
                    angle += Math.PI/5;
                    gp.lineTo((float)(x+radius/Math.sqrt(8)*Math.cos(angle)), (float)(y-radius/Math.sqrt(8)*Math.sin(angle)));
                }
                gp.closePath();
                return gp;
            }
        },
        STAR_7 {
            public Shape getShape(double x, double y, double radius) {
                GeneralPath gp = new GeneralPath();
                gp.moveTo((float)x, (float) (y-radius));
                for (int i = 0; i < 7; i++) {
                    double angle = Math.PI/2 + 2*Math.PI*i/7;
                    gp.lineTo((float)(x+radius*Math.cos(angle)), (float)(y-radius*Math.sin(angle)));
                    angle += Math.PI/7;
                    gp.lineTo((float)(x+radius/2*Math.cos(angle)), (float)(y-radius/2*Math.sin(angle)));
                }
                gp.closePath();
                return gp;
            }
        },
        STAR_11 {
            public Shape getShape(double x, double y, double radius) {
                GeneralPath gp = new GeneralPath();
                gp.moveTo((float)x, (float) (y-radius));
                for (int i = 0; i < 11; i++) {
                    double angle = Math.PI/2 + 2*Math.PI*i/11;
                    gp.lineTo((float)(x+radius*Math.cos(angle)), (float)(y-radius*Math.sin(angle)));
                    angle += Math.PI/11;
                    gp.lineTo((float)(x+radius/1.5*Math.cos(angle)), (float)(y-radius/1.5*Math.sin(angle)));
                }
                gp.closePath();
                return gp;
            }
        },
        HAPPY_FACE {
            public Shape getShape(double x, double y, double radius) {
                GeneralPath gp = new GeneralPath();
                gp.append(new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius), false);
                gp.append(new Ellipse2D.Double(x - radius/3 - radius/12, y - radius/3, radius/6, radius/6), false);
                gp.append(new Ellipse2D.Double(x + radius/3 - radius/12, y - radius/3, radius/6, radius/6), false);
                gp.append(new Arc2D.Double(x - radius/2, y - radius/2, radius, radius, 200, 140, Arc2D.CHORD), false);
                return gp;
            }
        },
        CAR {
            public Shape getShape(double x, double y, double r) {
                GeneralPath gp = new GeneralPath();
                gp.moveTo((float)(x+r), (float)y);
                gp.lineTo((float)(x+.67*r), (float)y);
                gp.append(new Arc2D.Double(x-.33*r, y-.5*r, r, r, 0, 180, Arc2D.OPEN), true); // top
                gp.append(new Arc2D.Double(x-.83*r, y, r, .67*r, 90, 90, Arc2D.OPEN), true); // hood
                gp.append(new Arc2D.Double(x-r, y+.33*r, .33*r, .33*r, 90, 90, Arc2D.OPEN), true); // bumper
                gp.lineTo((float)(x-.7*r), (float)(y+.5*r));
                gp.append(new Arc2D.Double(x-.7*r, y+.3*r, .4*r, .4*r, 180, -180, Arc2D.OPEN), true); // wheel well
                gp.lineTo((float)(x+.3*r), (float)(y+.5*r));
                gp.append(new Arc2D.Double(x+.3*r, y+.3*r, .4*r, .4*r, 180, -180, Arc2D.OPEN), true); // wheel well
                gp.lineTo((float)(x+r), (float)(y+.5*r));
                gp.closePath();

                gp.append(new Arc2D.Double(x-.2*r, y-.4*r, .7*r, .6*r, 90, 90, Arc2D.PIE), false); // windows
                gp.append(new Arc2D.Double(x-.05*r, y-.4*r, .6*r, .6*r, 0, 90, Arc2D.PIE), false); // windows
                
                gp.append(new Ellipse2D.Double(x-.67*r, y+.33*r, .33*r, .33*r), false); // wheels
                gp.append(new Ellipse2D.Double(x+.33*r, y+.33*r, .33*r, .33*r), false);
                return gp;
            }
        };

        /** Returns shape at given window location and pixel radius. */
        abstract public Shape getShape(double x, double y, double radius);
    }
}
