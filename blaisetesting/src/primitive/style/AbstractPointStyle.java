/*
 * AbstractPointStyle.java
 * Created May 13, 2010
 */

package primitive.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 *   <code>AbstractPointStyle</code> contains the basic stylizing features for drawing a point
 *   on a plane as a small circle, or within a small radius. Handles parameters for the radius,
 *   color/stroke/fill, and the type of visual mark displayed.
 *   Intended to be used as a superclass for any styles that draw points on the screen.
 * </p>
 * @see PointStyle
 * @author Elisha Peterson
 */
public abstract class AbstractPointStyle {

    /** This <code>enum</code> provides for various formatted point styles. */
    public static enum PointShape { CIRCLE, SQUARE, DIAMOND, TRIANGLE, PLUS, CROSS, STAR, STAR_7, STAR_11, HAPPY_FACE, HOUSE, TRIANGLE_SIDE, ARROWHEAD, TEARDROP, CAR };

    /** Determines shape used to indicate the point. */
    PointShape shape = PointShape.CIRCLE;
    /** Stroke outline */
    BasicStroke stroke = PrimitiveStyle.DEFAULT_STROKE;
    /** Stroke color */
    Color strokeColor = Color.BLACK;
    /** Fill of object */
    Color fillColor = Color.LIGHT_GRAY;
    /** Radius of the point (in pixels) */
    int radius = 5;

    /** Construct with defaults. */
    public AbstractPointStyle() { }
    /** Construct with colors only. */
    public AbstractPointStyle(Color strokeColor, Color fillColor) { this.strokeColor = strokeColor; this.fillColor = fillColor; }
    /** Construct with specified elements. */
    public AbstractPointStyle(PointShape shape, int radius) { this.shape = shape; this.radius = radius; }
    /** Construct with specified elements. */
    public AbstractPointStyle(PointShape shape, BasicStroke stroke, Color strokeColor, Color fillColor, int radius) {
        this.shape = shape;
        this.stroke = stroke;
        this.strokeColor = strokeColor;
        this.fillColor = fillColor;
        this.radius = radius;
    }

    /** @return shape of the point */
    public PointShape getShape() { return shape; }
    /** @param shape new shape of the point */
    public void setShape(PointShape shape) { this.shape = shape; }
    /** @return radius of displayed point */
    public int getRadius() { return radius; }
    /** @param radius new point radius */
    public void setRadius(int radius) { this.radius = radius; }
    /** @return fill color */
    public Color getFillColor() { return fillColor; }
    /** @param fillColor the fill color */
    public void setFillColor(Color fillColor) { this.fillColor = fillColor; }
    /** @return stroke for the outline of the point */
    public BasicStroke getStroke() { return stroke; }
    /** @param stroke new stroke for the outline of the point */
    public void setStroke(BasicStroke stroke) { this.stroke = stroke; }
    /** @return color of the outline stroke */
    public Color getStrokeColor() { return strokeColor; }
    /** @param strokeColor new color of the outline stroke */
    public void setStrokeColor(Color strokeColor) { this.strokeColor = strokeColor; }
    /** @return thickness of the outline stroke */
    public float getThickness() { return stroke.getLineWidth(); }
    /** @param width new thickness of the outline stroke */
    public void setThickness(float width) {
        stroke = new BasicStroke(width, stroke.getEndCap(), stroke.getLineJoin(), stroke.getMiterLimit(), stroke.getDashArray(), stroke.getDashPhase());
    }

    /**
     * @param shape enum describing type of shape
     * @param x x coordinate of center of drawn shape
     * @param y y coordinate of center of drawn shape
     * @param radius radius of drawn shape
     * @return shape of given parameters for given shape type
     */
    public static Shape getShape(PointShape shape, double x, double y, double radius) {
        switch(shape) {
            case CIRCLE:
                return new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius);
            case SQUARE:
                return new Rectangle2D.Double(x - radius/Math.sqrt(2), y - radius/Math.sqrt(2), 2*radius/Math.sqrt(2), 2*radius/Math.sqrt(2));
            case DIAMOND:
                GeneralPath gp = new GeneralPath();
                gp.moveTo((float)x, (float)(y-radius));
                gp.lineTo((float)(x-radius), (float)y);
                gp.lineTo((float)x, (float)(y+radius));
                gp.lineTo((float)(x+radius), (float)y);
                gp.closePath();
                return gp;
            case TRIANGLE:
                GeneralPath gp2 = new GeneralPath();
                gp2.moveTo((float)x, (float) (y-radius));
                gp2.lineTo((float)(x+radius*Math.cos(Math.PI*1.16667)), (float)(y-radius*Math.sin(Math.PI*1.16667)));
                gp2.lineTo((float)(x+radius*Math.cos(Math.PI*1.83333)), (float)(y-radius*Math.sin(Math.PI*1.83333)));
                gp2.closePath();
                return gp2;
            case PLUS:
                GeneralPath gp3 = new GeneralPath();
                gp3.moveTo((float) x, (float) (y-radius));
                gp3.lineTo((float) x, (float) (y+radius));
                gp3.moveTo((float) (x-radius), (float) y);
                gp3.lineTo((float) (x+radius), (float) y);
                return gp3;
            case CROSS:
                GeneralPath gp4 = new GeneralPath();
                double r2 = 0.7 * radius;
                gp4.moveTo((float) (x-r2), (float) (y-r2));
                gp4.lineTo((float) (x+r2), (float) (y+r2));
                gp4.moveTo((float) (x-r2), (float) (y+r2));
                gp4.lineTo((float) (x+r2), (float) (y-r2));
                return gp4;
            case STAR:
                GeneralPath gp5 = new GeneralPath();
                gp5.moveTo((float)x, (float) (y-radius));
                for (int i = 0; i < 5; i++) {
                    double angle = Math.PI/2 + 2*Math.PI*i/5;
                    gp5.lineTo((float)(x+radius*Math.cos(angle)), (float)(y-radius*Math.sin(angle)));
                    angle += Math.PI/5;
                    gp5.lineTo((float)(x+radius/Math.sqrt(8)*Math.cos(angle)), (float)(y-radius/Math.sqrt(8)*Math.sin(angle)));
                }
                gp5.closePath();
                return gp5;
            case STAR_7:
                GeneralPath gp6 = new GeneralPath();
                gp6.moveTo((float)x, (float) (y-radius));
                for (int i = 0; i < 7; i++) {
                    double angle = Math.PI/2 + 2*Math.PI*i/7;
                    gp6.lineTo((float)(x+radius*Math.cos(angle)), (float)(y-radius*Math.sin(angle)));
                    angle += Math.PI/7;
                    gp6.lineTo((float)(x+radius/2*Math.cos(angle)), (float)(y-radius/2*Math.sin(angle)));
                }
                gp6.closePath();
                return gp6;
            case STAR_11:
                GeneralPath gp7 = new GeneralPath();
                gp7.moveTo((float)x, (float) (y-radius));
                for (int i = 0; i < 11; i++) {
                    double angle = Math.PI/2 + 2*Math.PI*i/11;
                    gp7.lineTo((float)(x+radius*Math.cos(angle)), (float)(y-radius*Math.sin(angle)));
                    angle += Math.PI/11;
                    gp7.lineTo((float)(x+radius/1.5*Math.cos(angle)), (float)(y-radius/1.5*Math.sin(angle)));
                }
                gp7.closePath();
                return gp7;
            case HAPPY_FACE:
                GeneralPath gp8 = new GeneralPath();
                gp8.append(new Ellipse2D.Double(x - radius, y - radius, 2*radius, 2*radius), false);
                gp8.append(new Ellipse2D.Double(x - radius/3 - radius/12, y - radius/3, radius/6, radius/6), false);
                gp8.append(new Ellipse2D.Double(x + radius/3 - radius/12, y - radius/3, radius/6, radius/6), false);
                gp8.append(new Arc2D.Double(x - radius/2, y - radius/2, radius, radius, 200, 140, Arc2D.CHORD), false);
                return gp8;
            case HOUSE:
                GeneralPath gp13 = new GeneralPath();
                gp13.moveTo(-.9f, -.9f);
                gp13.lineTo(.9f, -.9f); gp13.lineTo(.9f, .5f); gp13.lineTo(1f, .5f);
                gp13.lineTo(.75f, .625f); gp13.lineTo(.75f, 1f); gp13.lineTo(.5f, 1f); gp13.lineTo(.5f,.75f);
                gp13.lineTo(0f, 1f); gp13.lineTo(-1f, .5f); gp13.lineTo(-.9f, .5f); gp13.lineTo(-.9f, -.9f);
                gp13.closePath();
                gp13.transform(new AffineTransform(radius, 0, 0, -radius, x, y));
                return gp13;
            case TRIANGLE_SIDE:
                GeneralPath gp9 = new GeneralPath();
                gp9.moveTo((float)(x+radius), (float) y);
                gp9.lineTo((float)(x+radius*Math.cos(Math.PI*0.6667)), (float)(y-radius*Math.sin(Math.PI*0.6667)));
                gp9.lineTo((float)(x+radius*Math.cos(Math.PI*1.3333)), (float)(y-radius*Math.sin(Math.PI*1.3333)));
                gp9.closePath();
                return gp9;
            case ARROWHEAD:
                GeneralPath gp10 = new GeneralPath();
                gp10.moveTo((float)(x+radius), (float) y);
                gp10.lineTo((float)(x-radius), (float)(y+radius));
                gp10.lineTo((float)(x-.5*radius), (float) y);
                gp10.lineTo((float)(x-radius), (float)(y-radius));
                gp10.closePath();
                return gp10;
            case TEARDROP:
                GeneralPath gp11 = new GeneralPath();
                gp11.moveTo(-.25f, -.5f);
                gp11.curveTo(-1f, -.5f, -1f, .5f, -.25f, .5f);
                gp11.curveTo(.5f, .5f, .5f, 0, 1f, 0);
                gp11.curveTo(.5f, 0, .5f, -.5f, -.2f, -.5f);
                gp11.closePath();
                gp11.transform(new AffineTransform(radius, 0, 0, radius, x, y));
                return gp11;
            case CAR:
                GeneralPath gp12 = new GeneralPath();
                gp12.moveTo(1f, 0);
                gp12.lineTo(.67f, 0);
                gp12.append(new Arc2D.Double(-.33f, -.5f,   1f,   1f, 0, 180, Arc2D.OPEN), true); // top
                gp12.append(new Arc2D.Double(-.83f,   0f,   1f, .67f, 90, 90, Arc2D.OPEN), true); // hood
                gp12.append(new Arc2D.Double(-1f,   .33f, .33f, .33f, 90, 90, Arc2D.OPEN), true); // bumper
                gp12.lineTo(-.7f, .5f);
                gp12.append(new Arc2D.Double(-.7f, .3f, .4f, .4f, 180, -180, Arc2D.OPEN), true); // wheel well
                gp12.lineTo(.3f, .5f);
                gp12.append(new Arc2D.Double(.3f, .3f, .4f, .4f, 180, -180, Arc2D.OPEN), true); // wheel well
                gp12.lineTo(1f, .5f);
                gp12.closePath();

                gp12.append(new Arc2D.Double(-.2f,  -.4f, .7f, .6f, 90, 90, Arc2D.PIE), false); // windows
                gp12.append(new Arc2D.Double(-.05f, -.4f, .6f, .6f, 0, 90, Arc2D.PIE), false); // windows

                gp12.append(new Ellipse2D.Double(-.67f, .33f, .33f, .33f), false); // wheels
                gp12.append(new Ellipse2D.Double( .33f, .33f, .33f, .33f), false);
                gp12.transform(new AffineTransform(-radius, 0, 0, radius, x, y));
                return gp12;
            default:
                return null;
        }
    } // STATIC METHOD getShape
}
