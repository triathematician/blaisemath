/**
 * BasicStringGraphic.java
 * Created Jan 22, 2011
 */
package org.blaise.graphics;

import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import org.blaise.style.StringStyle;
import org.blaise.util.PointBean;

/**
 * Draws a string at a point.
 *
 * @author Elisha
 */
public class BasicStringGraphic extends GraphicSupport
        implements PointBean<Point2D> {

    /** Basepoint for the string */
    protected Point2D point;
    /** The object that will be drawn. */
    protected String text;

    /** The associated style (may be null). */
    protected StringStyle style;

    //
    // CONSTRUCTORS
    //

    /** Construct with no style (will use the default) */
    public BasicStringGraphic(Point2D point, String s) {
        this(point, s, null);
    }

    /**
     * Construct with given primitive and style.
     * @param point location of string
     * @param s string to draw
     * @param style draws the string
     */
    public BasicStringGraphic(Point2D point, String s, StringStyle style) {
        this.point = point;
        this.text = s;
        this.style = style;
        PointBeanDragger dragger = new PointBeanDragger(this);
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
    }

    @Override
    public String toString() {
        return "String";
    }


    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    public Point2D getPoint() {
        return point;
    }

    public void setPoint(Point2D p) {
        if (!point.equals(p)) {
            point = new Point2D.Double(p.getX(), p.getY());
            fireGraphicChanged();
        }
    }

    /**
     * Return the string to be drawn.
     * @return the string
     */
    public String getString() {
        return text;
    }

    /**
     * Set the string to be drawn.
     * @param s the string
     */
    public void setString(String s) {
        if (text == null ? s != null : !text.equals(s)) {
            text = s;
            fireGraphicChanged();
        }
    }

    /**
     * Return the style used to draw.
     * @return style
     */
    public StringStyle getStyle() {
        return style;
    }

    /**
     * Sets the style used to draw.
     * @param style the style
     */
    public void setStyle(StringStyle style) {
        if (this.style != style) {
            this.style = style;
            fireGraphicChanged();
        }
    }

    //</editor-fold>


    //
    // GRAPHIC METHODS
    //

    public boolean contains(Point p) {
        if (text == null || text.length() == 0 || lastGr == null)
            return false;
        return drawStyle().bounds(point, text, lastGr).contains(p);
    }

    public boolean intersects(Rectangle box) {
        if (text == null || text.length() == 0 || lastGr == null)
            return false;
        return drawStyle().bounds(point, text, lastGr).intersects(box);
    }


    //
    // DRAW METHODS
    //

    /** Return the actual style used for drawing */
    private StringStyle drawStyle() {
        return style == null ? parent.getStyleProvider().getStringStyle(this) : style;
    }

    /** Need this to be able to compute string bounds for the contains() method. */
    private transient Graphics2D lastGr = null;

    public void draw(Graphics2D canvas) {
        if (text == null || text.length() == 0) {
            return;
        }
        lastGr = canvas;
        drawStyle().draw(point, text, canvas, visibility);
    }
}
