/**
 * BasicPointEntry.java
 * Created Jan 22, 2011
 */
package org.bm.blaise.graphics;

import org.bm.blaise.graphics.renderer.GraphicRendererProvider;
import org.bm.blaise.graphics.renderer.StringRenderer;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author Elisha
 */
public class BasicStringEntry extends AbstractGraphicEntry
        implements GraphicMouseListener.PointBean {

    /** Basepoint for the string */
    Point2D point;
    /** The object that will be drawn. */
    String text;
    /** The associated renderer (may be null). */
    StringRenderer renderer;

    //
    // CONSTRUCTORS
    //

    /** Construct with no renderer (will use the default) */
    public BasicStringEntry(Point2D point, String s) { this(point, s, null); }

    /** Construct with given primitive and renderer. */
    public BasicStringEntry(Point2D point, String s, StringRenderer renderer) {
        this.point = point;
        this.text = s;
        this.renderer = renderer;
    }

    //
    // PROPERTY PATTERNS
    //

    public Point2D getPoint() { return point; }
    public void setPoint(Point2D p) { 
        if (!point.equals(p)) {
            point = new Point2D.Double(p.getX(), p.getY());
            fireStateChanged();
        }
    }

    public String getString() { return text; }
    public void setString(String s) {
        if (text == null ? s != null : !text.equals(s)) {
            text = s;
            fireStateChanged();
        }
    }

    public StringRenderer getRenderer() { return renderer; }
    public void setRenderer(StringRenderer renderer) {
        if (this.renderer != renderer) {
            this.renderer = renderer;
            fireStateChanged();
        }
    }

    //
    // DRAW METHODS
    //

    transient Graphics2D lastGr = null;

    /** Draws the primitive on the specified graphics canvas, using current style.
     * @param canvas graphics canvas
     * @param rend the default renderer to use if the shape entry has none
     */
    public void draw(Graphics2D canvas, GraphicRendererProvider rend) {
        if (text == null || text.length() == 0)
            return;
        lastGr = canvas;
        if (renderer == null)
            rend.getStringRenderer().draw(point, text, canvas, visibility);
        else
            renderer.draw(point, text, canvas, visibility);
    }

    /**
     * Checks to see if the provided window point is covered by the primitive, when drawn in this style.
     * @param point the window point
     */
    public boolean contains(Point p, GraphicRendererProvider factory) {
        if (text == null || text.length() == 0)
            return false;
        return lastGr == null ? false
                : renderer == null ? factory.getStringRenderer().bounds(point, text, lastGr).contains(p)
                : renderer.bounds(point, text, lastGr).contains(p);
    }
}
