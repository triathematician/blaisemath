/**
 * BasicStringStyle.java
 * Created Jan 22, 2011 absed on earlier <code>StringStyle.java</code>
 */
package org.blaise.style;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Set;

/**
 * <p>
 *   BasicStringStyle draws/colors textual elements.
 * </p>
 *
 * @author Elisha Peterson
 */
public class BasicStringStyle implements StringStyle {
    
    /** Color of the text. */
    Color color = Color.BLACK;    
    /** Font of the text. */
    Font font = new Font("Dialog", Font.PLAIN, 12);
    /** Stores font size. */
    transient Float fontSize = 12f;

    /** Offset from main anchor point */
    Point2D offset = new Point2D.Double();
    /** Stores the anchor for the text block. */
    Anchor anchor = Anchor.Southwest;


// <editor-fold defaultstate="collapsed" desc="CONSTRUCTORS & INITIALIZERS">

    /** Default constructor. */
    public BasicStringStyle() { }
    /** Construct with color, size, anchor */
    public BasicStringStyle(Color color, float size, Anchor anchor) { setColor(color); setFontSize(size); setAnchor(anchor); }
    /** Construct with provided parameters */
    public BasicStringStyle(Color color, Font font, Anchor anchor) { setAnchor(anchor); setColor(color); setFont(font); }

    @Override
    public String toString() { 
        return "BasicStringStyle [" + font + "]";
    }

    /** Sets color & returns pointer to object */
    public BasicStringStyle color(Color color) { this.color = color; return this; }
    /** Sets font & returns pointer to object */
    public BasicStringStyle font(Font font) { this.font = font; return this; }
    /** Sets font size & returns pointer to object */
    public BasicStringStyle fontSize(float size) { setFontSize(size); return this; }
    /** Sets offset & returns pointer to object */
    public BasicStringStyle offset(Point2D off) { this.offset = off; return this; }
    /** Sets offset & returns pointer to object */
    public BasicStringStyle offset(double x, double y) { this.offset = new Point2D.Double(x,y); return this; }
    /** 
     * Sets offset & returns pointer to object 
     * @param r how far from center to offset (angle is generated from anchor)
     */
    public BasicStringStyle offset(double r) { 
        if (anchor == Anchor.Center)
            offset = new Point2D.Double();
        else
            offset(r*Math.cos(anchor.getAngle()),r*Math.sin(anchor.getAngle())); 
        return this; 
    }
    /** Sets anchor & returns pointer to object */
    public BasicStringStyle anchor(Anchor anchor) { this.anchor = anchor; return this; }

// </editor-fold>


// <editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">

    /** @return color of string */
    public Color getColor() { return color; }
    /** @param color new string color */
    public void setColor(Color color) { this.color = color; }
    /** @return current font */
    public Font getFont() { return font; }
    /** @param font new font */
    public void setFont(Font font) { this.font = font; }
    /** @return current font size */
    public float getFontSize() { return font != null ? font.getSize2D() : fontSize == null ? 10f : fontSize; }
    /** @param size new font */
    public void setFontSize(float size) { fontSize = size; if (font != null) font = font.deriveFont(size); }

    /** @return offset for the string */
    public Point2D getOffset() { return offset; }
    /** Sets offset for the string */
    public void setOffset(Point2D off) { this.offset = off; }
    /** @return location of anchor point of string relative to provided coordinate */
    public Anchor getAnchor() { return anchor; }
    /** @param newValue new location of anchor point of string relative to provided coordinate */
    public void setAnchor(Anchor newValue) { anchor = newValue; }

// </editor-fold>

    
    public void draw(Point2D point, String string, Graphics2D canvas, Set<VisibilityHint> visibility) {
        if (string == null || string.length() == 0)
            return;
        if (fontSize != null && font == null) {
            font = canvas.getFont().deriveFont((float) fontSize);
            canvas.setFont(font);
        } else if (font != null)
            canvas.setFont(font);
        Rectangle2D bounds = bounds(point, string, canvas);
        canvas.setColor(StyleUtils.applyHints(color, visibility));
        canvas.drawString(string, (float) bounds.getX(), (float) (bounds.getY()+bounds.getHeight()));
    }

    public Rectangle2D bounds(Point2D point, String string, Graphics2D canvas) {
        FontMetrics fm = canvas.getFontMetrics();
        double width = fm.getStringBounds(string, canvas).getWidth();
        double height = fm.getAscent() - fm.getDescent();

        if (anchor == Anchor.Southwest)
            return new Rectangle2D.Double(point.getX() + offset.getX(), point.getY() + offset.getY()-height, width, height);

        Point2D.Double shift = new Point2D.Double();

        switch (anchor) {
            case Northeast:
            case East:
            case Southeast:
                shift.x = -width;
                break;
            case North:
            case Center:
            case South:
                shift.x = -width / 2;
                break;
        }
        
        switch (anchor) {
            case Northwest:
            case North:
            case Northeast:
                shift.y = height;
                break;
            case West:
            case Center:
            case East:
                shift.y = height / 2;
                break;
        }

        return new Rectangle2D.Double(point.getX() + offset.getX() + shift.x, point.getY() + offset.getY() + shift.y-height, width, height);
    }
}
