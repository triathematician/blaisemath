/**
 * BasicStringGraphic.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.graphics;

import java.awt.event.ActionEvent;
import org.bm.blaise.style.StringStyle;
import org.bm.util.PointBean;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

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
        actions.add(new AbstractAction("Edit..."){
            public void actionPerformed(ActionEvent e) {
                String nue = JOptionPane.showInputDialog("Enter string:", text);
                if (nue != null)
                    setString(nue);
            }
        });
        addMouseListener(new GMouseDragger(this));
    }

    @Override
    public String toString() {
        return "String";
    }

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
            fireAppearanceChanged();
        }
    }

    //
    // DRAW METHODS
    //
    
    /** Return the actual style used for drawing */
    private StringStyle drawStyle() {
        return style == null ? parent.getStyleProvider().getStringStyle() : style;
    }

    /** Need this to be able to compute string bounds for the contains() method. */
    private transient Graphics2D lastGr = null;

    public void draw(Graphics2D canvas) {
        if (text == null || text.length() == 0)
            return;
        lastGr = canvas;
        drawStyle().draw(point, text, canvas, visibility);
    }

    public boolean contains(Point p) {
        if (text == null || text.length() == 0 || lastGr == null)
            return false;
        return drawStyle().bounds(point, text, lastGr).contains(p);
    }
}
