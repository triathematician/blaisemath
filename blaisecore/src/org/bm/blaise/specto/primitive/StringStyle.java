/**
 * StrokeStyle.java
 * Created on Aug 4, 2009
 */
package org.bm.blaise.specto.primitive;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 *   <code>StringStyle</code> draws/colors textual elements.
 *   Initial development mimics standard CSS styling options.
 * </p>
 *
 * @author Elisha Peterson
 */
public class StringStyle extends PrimitiveStyle<GraphicString> {

    //
    // PROPERTIES
    //
    
    /** Color of the text. */
    Color color = Color.BLACK;
    
    /** Font of the text. */
    Font font;

    /** Stores font size. */
    transient Float fontSize = null;

    //
    // CONSTRUCTORS
    //
    
    /** Default constructor. */
    public StringStyle() {
    }

    /** Construct with color only */
    public StringStyle(Color color) {
        this.color = color;
    }

    /** Constructs with provided parameters. */
    public StringStyle(Color color, float size) {
        this.color = color;
        this.fontSize = size;
    }

    /** Constructs with provided parameters. */
    public StringStyle(Color color, Font font) {
        this.color = color;
        this.font = font;
    }

    //
    // UTILITIES
    //

    @Override
    public String toString() {
        return "StringStyle [" + font + "]";
    }

    //
    // BEANS
    //
    
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    //
    // GRAPHICS METHODS
    //

    public void draw(Graphics2D canvas, GraphicString grString, boolean selected) {
        if (fontSize != null && font == null) {
            font = canvas.getFont().deriveFont((float) fontSize);
            canvas.setFont(font);
        } else if (font != null)
            canvas.setFont(font);

        if (grString.getOrientation() == GraphicString.BOTTOM_LEFT) {
            canvas.drawString(grString.string, (float) grString.anchor.x, (float) grString.anchor.y);
        } else {
            FontMetrics fm = canvas.getFontMetrics();
            double width = fm.getStringBounds(grString.string, canvas).getWidth();
            double height = fm.getAscent() - fm.getDescent();
            Point2D.Double shift = new Point2D.Double();

            switch (grString.orientation) {
                case GraphicString.TOP_RIGHT:
                case GraphicString.RIGHT:
                case GraphicString.BOTTOM_RIGHT:
                    shift.x = -width;
                    break;
                case GraphicString.TOP:
                case GraphicString.CENTER:
                case GraphicString.BOTTOM:
                    shift.x = -width / 2;
                    break;
                case GraphicString.TOP_LEFT:
                case GraphicString.LEFT:
                case GraphicString.BOTTOM_LEFT:
                default:
                    // no shift in x direction
                    break;
            }

            switch (grString.orientation) {
                case GraphicString.TOP_LEFT:
                case GraphicString.TOP:
                case GraphicString.TOP_RIGHT:
                    shift.y = height;
                    break;
                case GraphicString.LEFT:
                case GraphicString.CENTER:
                case GraphicString.RIGHT:
                    shift.y = height / 2;
                    break;
                case GraphicString.BOTTOM_LEFT:
                case GraphicString.BOTTOM:
                case GraphicString.BOTTOM_RIGHT:
                default:
                    // no shift in y direction
                    break;
            }

            canvas.drawString(grString.string,
                    (float) (grString.anchor.x + shift.x),
                    (float) (grString.anchor.y + shift.y));
        }
    }
}
