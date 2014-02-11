/**
 * TextStyleBasic.java
 * Created Jan 22, 2011 based on earlier <code>TextStyle.java</code>
 */
package org.blaise.style;

import static com.google.common.base.Preconditions.*;
import com.google.common.base.Strings;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import javax.annotation.Nullable;

/**
 * <p>
 *   Renders a string using a chosen color and font (or the current canvas font and
 *   a specific font size). Also provides an anchor determining text alignment
 *   and an offset determining the text anchor's offset from a given location.
 * </p>
 *
 * @author Elisha Peterson
 */
public class TextStyleBasic implements TextStyle {

    /** Color of the text. */
    protected Color fill = Color.BLACK;
    /** Font of the text. */
    protected @Nullable Font font = new Font("Dialog", Font.PLAIN, 12);
    /** Font size (may apply if font is null) */
    protected float fontSize = font.getSize2D();

    /** Offset from main anchor point */
    protected Point2D offset = new Point2D.Double();
    /** Stores the anchor for the text block. */
    protected Anchor textAnchor = Anchor.SOUTHWEST;

    /** Default constructor. */
    public TextStyleBasic() { }
    
    @Override
    public String toString() {
        return String.format("BasicStringStyle[fill=%s, font=%s, fontSize=%.1f, offset=(%.1f,%.1f), textAnchor=%s]", 
                fill, font, fontSize, offset.getX(), offset.getY(), textAnchor);
    }
    
    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERNS">

    /** Sets color & returns pointer to object */
    public TextStyleBasic fill(Color color) {
        setFill(color);
        return this; 
    }
    
    /** Sets font & returns pointer to object */
    public TextStyleBasic font(@Nullable Font font) {
        setFont(font);
        return this;
    }
    
    /** Sets font size & returns pointer to object */
    public TextStyleBasic fontSize(float size) { 
        setFontSize(size);
        return this;
    }
    
    /** Sets offset & returns pointer to object */
    public TextStyleBasic offset(Point2D off) { 
        setOffset(off);
        return this; 
    }
    
    /** Sets offset & returns pointer to object */
    public TextStyleBasic offset(double x, double y) { 
        setOffset(new Point2D.Double(x,y));
        return this;
    }
    
    /**
     * Sets offset & returns pointer to object
     * @param r how far from center to offset (angle is generated from anchor)
     */
    public TextStyleBasic offset(double r) {
        setOffset(textAnchor.getOffset(r));
        return this;
    }
    
    /** 
     * Sets anchor & returns pointer to object.
     * @param anchor the anchor
     */
    public TextStyleBasic textAnchor(Anchor anchor) { 
        setTextAnchor(anchor);
        return this; 
    }
    
    //</editor-fold>


    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //

    /**
     * Get string color
     * @return color of string 
     */
    public Color getFill() {
        return fill;
    }
    
    /**
     * Set string color
     * @param color new string color 
     */
    public final void setFill(Color color) {
        this.fill = checkNotNull(color);
    }
    
    /**
     * Get font.
     * @return current font 
     */
    public @Nullable Font getFont() { 
        return font;
    }
    
    /**
     * Set font.
     * @param font new font 
     */
    public final void setFont(@Nullable Font font) { 
        this.font = font; 
        this.fontSize = font == null ? 11f : font.getSize2D();
    }
    
    /** 
     * Get current font size
     * @return current font size 
     */
    public float getFontSize() { 
        return font != null ? font.getSize2D() : fontSize; 
    }
    /**
     * Update current font size
     * @param size new font 
     */
    public final void setFontSize(float size) {
        fontSize = size; 
        if (font != null) {
            font = font.deriveFont(size);
        } 
    }

    /**
     * Get offset from anchor point for drawing string.
     * @return offset for the string 
     */
    public Point2D getOffset() { 
        return offset; 
    }
    
    /**
     * Sets offset for the string 
     * @param off offset
     */
    public void setOffset(Point2D off) {
        this.offset = checkNotNull(off);
    }
    
    /** 
     * Get anchor point for string
     * @return location of anchor point of string relative to provided coordinate
     */
    public Anchor getTextAnchor() {
        return textAnchor;
    }
    
    /**
     * Set anchor for string
     * @param newValue new location of anchor point of string relative to provided coordinate
     */
    public final void setTextAnchor(Anchor newValue) {
        textAnchor = checkNotNull(newValue); 
    }

    // </editor-fold>


    public void draw(Point2D point, @Nullable String string, Graphics2D canvas, VisibilityHintSet visibility) {
        if (Strings.isNullOrEmpty(string)) {
            return;
        }
        if (font == null) {
            font = canvas.getFont().deriveFont((float) fontSize);
            canvas.setFont(font);
        } else if (font != null) {
            canvas.setFont(font);
        }
        Rectangle2D bounds = bounds(point, string, canvas);
        canvas.setColor(ColorUtils.applyHints(fill, visibility));
        canvas.drawString(string, (float) bounds.getX(), (float) (bounds.getY()+bounds.getHeight()));
    }

    public Rectangle2D bounds(Point2D point, @Nullable String string, Graphics2D canvas) {
        if (Strings.isNullOrEmpty(string)) {
            return null;
        }
        FontMetrics fm = canvas.getFontMetrics();
        double width = fm.getStringBounds(string, canvas).getWidth();
        double height = fm.getAscent() - fm.getDescent();

        if (textAnchor == Anchor.SOUTHWEST) {
            return new Rectangle2D.Double(point.getX() + offset.getX(), point.getY() + offset.getY()-height, width, height);
        }

        Point2D.Double shift = new Point2D.Double();

        switch (textAnchor) {
            case NORTHEAST:
            case EAST:
            case SOUTHEAST:
                shift.x = -width;
                break;
            case NORTH:
            case CENTER:
            case SOUTH:
                shift.x = -width / 2;
                break;
        }

        switch (textAnchor) {
            case NORTHWEST:
            case NORTH:
            case NORTHEAST:
                shift.y = height;
                break;
            case WEST:
            case CENTER:
            case EAST:
                shift.y = height / 2;
                break;
        }

        return new Rectangle2D.Double(point.getX() + offset.getX() + shift.x, point.getY() + offset.getY() + shift.y-height, width, height);
    }
}
