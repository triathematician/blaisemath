/**
 * BasicStringRenderer.java
 * Created Jan 22, 2011 absed on earlier <code>StringStyle.java</code>
 */
package graphics.renderer;

import graphics.GraphicVisibility;
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
    
    /** Default constructor. */
    public BasicStringRenderer() { }
    /** Construct with color, size, anchor */
    public BasicStringRenderer(Color color, float size, Anchor anchor) { setColor(color); setFontSize(size); setAnchor(anchor); }
    /** Construct with provided parameters */
    public BasicStringRenderer(Color color, Font font, Anchor anchor) { setAnchor(anchor); setColor(color); setFont(font); }

    @Override public String toString() { return "BasicStringRenderer [" + font + "]"; }

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

    public void draw(Point2D point, String string, Graphics2D canvas, GraphicVisibility visibility) {
        canvas.setColor(color);
        Rectangle2D bounds = bounds(point, string, canvas);
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
