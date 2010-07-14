/**
 * StrokeStyle.java
 * Created on Aug 4, 2009
 */
package primitive.style;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import primitive.GraphicString;

/**
 * <p>
 *   <code>StringStyle</code> draws/colors textual elements.
 *   Initial development mimics standard CSS styling options.
 * </p>
 *
 * @author Elisha Peterson
 */
public class StringStyle extends AbstractPrimitiveStyle<GraphicString<Point2D.Double>> {

    /** Anchor point constants. */
    public static final int ANCHOR_SW = 0;
    public static final int ANCHOR_W = 1;
    public static final int ANCHOR_NW = 2;
    public static final int ANCHOR_N = 3;
    public static final int ANCHOR_NE = 4;
    public static final int ANCHOR_E = 5;
    public static final int ANCHOR_SE = 6;
    public static final int ANCHOR_S = 7;
    public static final int ANCHOR_CENTER = 8;
    
    /** Color of the text. */
    Color color = Color.BLACK;    
    /** Font of the text. */
    Font font;
    /** Stores font size. */
    transient Float fontSize = null;
    /** Stores the anchor. */
    int anchor = ANCHOR_SW;
    
    /** Default constructor. */
    public StringStyle() { }
    /** Construct with anchor only */
    public StringStyle(int orientation) { setAnchor(orientation); }
    /** Construct with color only */
    public StringStyle(Color color) { setColor(color); }
    /** Constructs with provided parameters. */
    public StringStyle(Color color, float size) { this.color = color; this.fontSize = size; }
    /** Constructs with provided parameters. */
    public StringStyle(Color color, Font font) { this.color = color; this.font = font; }
    /** Construct with provided parameters */
    public StringStyle(Color color, Font font, int anchor) { setAnchor(anchor); setColor(color); setFont(font); }

    @Override
    public String toString() {
        return "StringStyle [" + font + "]";
    }

    public Class getTargetType() {
        return GraphicString.class;
    }

    /** @return color of string */
    public Color getColor() { return color; }
    /** @param color new string color */
    public void setColor(Color color) { this.color = color; }
    /** @return current font */
    public Font getFont() { return font; }
    /** @param font new font */
    public void setFont(Font font) { this.font = font; }
    /** @return location of anchor point of string relative to provided coordinate */
    public int getAnchor() { return anchor; }
    /** @param newValue new location of anchor point of string relative to provided coordinate */
    public void setAnchor(int newValue) { if (newValue >= 0 && newValue <= 8) anchor = newValue; }

    public void draw(Graphics2D canvas, GraphicString<Point2D.Double> gs) {
        canvas.setColor(color);
        Rectangle2D.Double bounds = bounds(canvas, gs);
        canvas.drawString(gs.string, (float) (bounds.x + gs.offset.x), (float) (bounds.y + gs.offset.y));
    }

    public boolean contained(GraphicString<Point2D.Double> primitive, Graphics2D canvas, Point point) {
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

        if (anchor == ANCHOR_SW)
            return new Rectangle2D.Double(gs.anchor.getX(), gs.anchor.getY(), width, height);

        Point2D.Double shift = new Point2D.Double();

        switch (anchor) {
            case ANCHOR_NE: case ANCHOR_E: case ANCHOR_SE:
                shift.x = -width;
                break;
            case ANCHOR_N: case ANCHOR_CENTER: case ANCHOR_S:
                shift.x = -width / 2;
                break;
        }
        
        switch (anchor) {
            case ANCHOR_NW: case ANCHOR_N: case ANCHOR_NE:
                shift.y = height;
                break;
            case ANCHOR_W: case ANCHOR_CENTER: case ANCHOR_E:
                shift.y = height / 2;
                break;
        }

        return new Rectangle2D.Double(gs.anchor.getX() + shift.x, gs.anchor.getY() + shift.y, width, height);
    }
}
