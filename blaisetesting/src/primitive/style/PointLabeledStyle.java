/*
 * PointLabeledStyle.java
 * Created Apr 12, 2010
 */

package primitive.style;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import primitive.GraphicString;

/**
 * <p>
 *      This style class handles both <code>Point2D.Double</code> primitives
 *      and <code>GraphicString</code> primitives. If a <code>GraphicString<Point2D.Double></code>,
 *      the location of the anchor point is used for the point and the string.
 *      If the primitive is not a <code>GraphicSTring</code>, it just displays the point.
 * </p>
 *
 * @see GraphicString
 * @author Elisha Peterson
 */
public class PointLabeledStyle extends PointStyle {

    /** Whether label is visible */
    boolean labelVisible = true;
    /** The string style */
    StringStyle sStyle = new StringStyle();

    /** Construct with defaults. */
    public PointLabeledStyle() { sStyle.anchor = StringStyle.ANCHOR_N; }
    /** Construct with default point style, specified anchor location */
    public PointLabeledStyle(int anchor) { super(); sStyle.anchor = anchor; }
    /** Construct with colors only. */
    public PointLabeledStyle(Color strokeColor, Color fillColor, int anchor) { super(strokeColor, fillColor); sStyle.anchor = anchor; }
    /** Construct with specified elements. */
    public PointLabeledStyle(PointShape shape, int radius, int anchor) { super(shape, radius); sStyle.anchor = anchor; }
    /** Construct with specified elements. */
    public PointLabeledStyle(PointShape shape, BasicStroke stroke, Color strokeColor, Color fillColor, int radius, int anchor) { super(shape, stroke, strokeColor, fillColor, radius); sStyle.anchor = anchor; }

    /** @return color of string */
    public Color getLabelColor() { return sStyle.getColor(); }
    /** @param color new string color */
    public void setLabelColor(Color color) { sStyle.setColor(color); }
    /** @return current font */
    public Font getLabelFont() { return sStyle.getFont(); }
    /** @param font new font */
    public void setLabelFont(Font font) { sStyle.setFont(font); }
    /** @return location of anchor point of string relative to provided coordinate */
    public int getLabelAnchor() { return sStyle.getAnchor(); }
    /** @param newValue new location of anchor point of string relative to provided coordinate */
    public void setLabelAnchor(int newValue) { sStyle.setAnchor(newValue); }
    /** @return true if label is visible, else false */
    public boolean isLabelVisible() { return labelVisible; }
    /** Sets label visibility */
    public void setLabelVisible(boolean visible) { this.labelVisible = visible; }


    @Override
    public void draw(Graphics2D canvas, Point2D.Double point) {
        if (point instanceof GraphicString) {
            GraphicString gs = (GraphicString) point;
            if (gs.anchor instanceof Point2D.Double)
                super.draw(canvas, (Point2D.Double) gs.anchor);
            else
                super.draw(canvas, point);
            if (labelVisible) {
                setOffset(gs, sStyle.anchor, 3+radius);
                sStyle.draw(canvas, gs);
            }
        } else {
            super.draw(canvas, point);
        }
    }

    /**
     * Updates the offset parameter of a graphic string to be at specified radius.
     * Uses the anchor points specified within the graphic string
     * @param gs a graphic string
     * @param anchor anchor location for string
     * @param radius radius to offset the string
     */
    static void setOffset(GraphicString gs, int anchor, double radius) {
        if (anchor == StringStyle.ANCHOR_CENTER) {
            gs.offset = new Point2D.Double();
            return;
        }
        double angle = 0;
        switch(anchor) {
            case StringStyle.ANCHOR_NE: angle = .75*Math.PI; break;
            case StringStyle.ANCHOR_N: angle = .5*Math.PI; break;
            case StringStyle.ANCHOR_NW: angle = .25*Math.PI; break;
            case StringStyle.ANCHOR_W: angle = 0; break;
            case StringStyle.ANCHOR_SW: angle = 1.75*Math.PI; break;
            case StringStyle.ANCHOR_S: angle = 1.5*Math.PI; break;
            case StringStyle.ANCHOR_SE: angle = 1.25*Math.PI; break;
            case StringStyle.ANCHOR_E: angle = Math.PI; break;
        }
        gs.offset = new Point2D.Double(radius*Math.cos(angle), radius*Math.sin(angle));
    }

}
