/**
 * GraphicString.java
 * Created on Sep 6, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.geom.Point2D;

/**
 * <p>
 *   <code>GraphicString</code> is a graphics primitive that contains a string,
 *   an anchor point, and an orientation.
 * </p>
 *
 * @author Elisha Peterson
 */
public class GraphicString {

    /** Anchor point constants. */
    public static final int LEFT = 0;
    public static final int TOP_LEFT = 1;
    public static final int TOP = 2;
    public static final int TOP_RIGHT = 3;
    public static final int RIGHT = 4;
    public static final int BOTTOM_RIGHT = 5;
    public static final int BOTTOM = 6;
    public static final int BOTTOM_LEFT = 7;
    public static final int CENTER = 8;

    String string;
    Point2D anchor;
    int orientation;

    public GraphicString(String string, Point2D anchor) {
        this.anchor = anchor;
        this.string = string;
    }

    public GraphicString(String string, double x, double y) {
        this.string = string;
        this.anchor = new Point2D.Double(x, y);
    }

    public GraphicString(String string, Point2D anchor, int orientation) {
        this.anchor = anchor;
        this.string = string;
        this.orientation = orientation;
    }

    public GraphicString(String string, double x, double y, int orientation) {
        this.string = string;
        this.anchor = new Point2D.Double(x, y);
        this.orientation = orientation;
    }

    //
    // BEANS
    //

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public Point2D getAnchor() {
        return anchor;
    }

    public void setAnchor(Point2D newAnchor) {
        this.anchor = newAnchor;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
