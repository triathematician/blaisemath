/**
 * StrokeStyle.java
 * Created on Aug 4, 2009
 */
package org.bm.blaise.specto.primitive;

import org.bm.blaise.specto.primitive.GraphicString;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * <p>
 *   <code>StringStyle</code> draws/colors textual elements.
 *   Initial development mimics standard CSS styling options.
 * </p>
 *
 * @author Elisha Peterson
 */
public class StringStyle implements PrimitiveStyle<GraphicString> {

    //
    //
    // PROPERTIES
    //
    //
    
    /** Color of the text. */
    Color color = Color.BLACK;
    
    /** Font of the text. */
    Font font;

    /** Stores font size. */
    transient Float fontSize = null;

    //
    //
    // CONSTRUCTORS
    //
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
    //
    // BEANS
    //
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
    //
    // GRAPHICS METHODS
    //
    //

    public void draw(GraphicString grString, Graphics2D canvas) {
        if (fontSize != null && font == null) {
            font = canvas.getFont().deriveFont((float) fontSize);
            canvas.setFont(font);
        } else if (font != null) {
            canvas.setFont(font);
        }
        canvas.setColor(color);
        canvas.drawString(grString.getString(), (float) grString.getAnchor().getX(), (float) grString.getAnchor().getY());
    }

    public void draw(GraphicString[] grStrings, Graphics2D canvas) {
        for (GraphicString gs : grStrings) {
            draw(gs, canvas);
        }
    }
}
