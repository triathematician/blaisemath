/**
 * StringStyle.java
 * Created Sep 2009
 */
package old.styles.verified;

import old.styles.verified.AbstractPrimitiveStyle;
import old.other.StyleMap;
import old.styles.verified.GraphicString;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import graphics.renderer.Anchor;

/**
 * <p>
 *   <code>StringStyle</code> draws/colors textual elements.
 *   Initial development mimics standard CSS styling options.
 * </p>
 *
 * @author Elisha Peterson
 */
public class StringStyle extends AbstractPrimitiveStyle<GraphicString<Point2D.Double>> {
    
    /** Color of the text. */
    Color color = Color.BLACK;    
    /** Font of the text. */
    Font font;
    /** Stores font size. */
    transient Float fontSize = null;
    /** Stores the anchor. */
    Anchor anchor = Anchor.Southwest;
    
    /** Default constructor. */
    public StringStyle() { }
    /** Construct with anchor only */
    public StringStyle(Anchor anchor) { setAnchor(anchor); }
    /** Construct with color only */
    public StringStyle(Color color) { setColor(color); }
    /** Constructs with provided parameters. */
    public StringStyle(Color color, float size) { setColor(color); setFontSize(size); }
    /** Constructs with provided parameters. */
    public StringStyle(Color color, Font font) { setColor(color); setFont(font); }
    /** Construct with color, size, anchor */
    public StringStyle(Color color, float size, Anchor anchor) { setColor(color); setFontSize(size); setAnchor(anchor); }
    /** Construct with provided parameters */
    public StringStyle(Color color, Font font, Anchor anchor) { setAnchor(anchor); setColor(color); setFont(font); }

    @Override public String toString() { return "StringStyle [" + font + "]"; }
    public Class getTargetType() { return GraphicString.class; }

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
    /** @return location of anchor point of string relative to provided coordinate */
    public Anchor getAnchor() { return anchor; }
    /** @param newValue new location of anchor point of string relative to provided coordinate */
    public void setAnchor(Anchor newValue) { anchor = newValue; }

    public void draw(Graphics2D canvas, GraphicString<Point2D.Double> gs, StyleMap customizer) {
        canvas.setColor(color);
        Rectangle2D.Double bounds = bounds(canvas, gs);
        canvas.drawString(gs.string, (float) (bounds.x + gs.offset.x), (float) (bounds.y + gs.offset.y));
    }

    public boolean contained(Point point, GraphicString<Point2D.Double> primitive, Graphics2D canvas, StyleMap customizer) {
        return bounds(canvas, primitive).contains(point);
    }

    /** @return boundaries of the string for the current settings */
    Rectangle2D.Double bounds(Graphics2D canvas, GraphicString<Point2D.Double> gs) {
        if (fontSize != null && font == null) {
            font = canvas.getFont().deriveFont((float) fontSize);
            canvas.setFont(font);
        } else if (font != null)
            canvas.setFont(font);

        FontMetrics fm = canvas.getFontMetrics();
        double width = fm.getStringBounds(gs.string, canvas).getWidth();
        double height = fm.getAscent() - fm.getDescent();

        if (anchor == Anchor.Southwest)
            return new Rectangle2D.Double(gs.anchor.getX(), gs.anchor.getY(), width, height);

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

        return new Rectangle2D.Double(gs.anchor.getX() + shift.x, gs.anchor.getY() + shift.y, width, height);
    }
}
