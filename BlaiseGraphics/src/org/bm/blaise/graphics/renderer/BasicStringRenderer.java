/**
 * BasicStringRenderer.java
 * Created Jan 22, 2011 absed on earlier <code>StringStyle.java</code>
 */
package org.bm.blaise.graphics.renderer;

import org.bm.blaise.graphics.GraphicVisibility;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 *   BasicStringRenderer draws/colors textual elements.
 * </p>
 *
 * @author Elisha Peterson
 */
public class BasicStringRenderer implements StringRenderer {
    
    /** Color of the text. */
    Color color = Color.BLACK;    
    /** Font of the text. */
    Font font;
    /** Stores font size. */
    transient Float fontSize = null;

    /** Offset from main anchor point */
    Point offset = new Point();
    /** Stores the anchor for the text block. */
    Anchor anchor = Anchor.Southwest;


// <editor-fold defaultstate="collapsed" desc="CONSTRUCTORS & INITIALIZERS">

    /** Default constructor. */
    public BasicStringRenderer() { }
    /** Construct with color, size, anchor */
    public BasicStringRenderer(Color color, float size, Anchor anchor) { setColor(color); setFontSize(size); setAnchor(anchor); }
    /** Construct with provided parameters */
    public BasicStringRenderer(Color color, Font font, Anchor anchor) { setAnchor(anchor); setColor(color); setFont(font); }

    @Override
    public String toString() { 
        return "BasicStringRenderer [" + font + "]";
    }

    /** Sets color & returns pointer to object */
    public BasicStringRenderer color(Color color) { this.color = color; return this; }
    /** Sets font & returns pointer to object */
    public BasicStringRenderer font(Font font) { this.font = font; return this; }
    /** Sets font size & returns pointer to object */
    public BasicStringRenderer fontSize(float size) { this.fontSize = size; return this; }
    /** Sets offset & returns pointer to object */
    public BasicStringRenderer offset(Point off) { this.offset = off; return this; }
    /** Sets offset & returns pointer to object */
    public BasicStringRenderer offset(int x, int y) { this.offset = new Point(x,y); return this; }
    /** Sets anchor & returns pointer to object */
    public BasicStringRenderer anchor(Anchor anchor) { this.anchor = anchor; return this; }

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
    public float getFontSize() { return font == null ? fontSize : font.getSize2D(); }
    /** @param size new font */
    public void setFontSize(float size) { fontSize = size; if (font != null) font = font.deriveFont(size); }

    /** @return offset for the string */
    public Point getOffset() { return offset; }
    /** Sets offset for the string */
    public void setOffset(Point off) { this.offset = off; }
    /** @return location of anchor point of string relative to provided coordinate */
    public Anchor getAnchor() { return anchor; }
    /** @param newValue new location of anchor point of string relative to provided coordinate */
    public void setAnchor(Anchor newValue) { anchor = newValue; }

// </editor-fold>

    
    public void draw(Point2D point, String string, Graphics2D canvas, GraphicVisibility visibility) {
        if (string == null || string.length() == 0)
            return;
        Rectangle2D bounds = bounds(point, string, canvas);
//        canvas.setColor(new Color(250, 250, 250, 100));
//        canvas.fill(bounds);
        canvas.setColor(color);
        canvas.drawString(string, (float) bounds.getX(), (float) (bounds.getY()+bounds.getHeight()));
    }

    public Rectangle2D bounds(Point2D point, String string, Graphics2D canvas) {
        if (fontSize != null && font == null) {
            font = canvas.getFont().deriveFont((float) fontSize);
            canvas.setFont(font);
        } else if (font != null)
            canvas.setFont(font);

        FontMetrics fm = canvas.getFontMetrics();
        double width = fm.getStringBounds(string, canvas).getWidth();
        double height = fm.getAscent() - fm.getDescent();

        if (anchor == Anchor.Southwest)
            return new Rectangle2D.Double(point.getX() + offset.x, point.getY() + offset.y-height, width, height);

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

        return new Rectangle2D.Double(point.getX() + offset.x + shift.x, point.getY() + offset.y + shift.y-height, width, height);
    }
}
