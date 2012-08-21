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
    public static final int BOTTOM_LEFT = 0;
    public static final int LEFT = 1;
    public static final int TOP_LEFT = 2;
    public static final int TOP = 3;
    public static final int TOP_RIGHT = 4;
    public static final int RIGHT = 5;
    public static final int BOTTOM_RIGHT = 6;
    public static final int BOTTOM = 7;
    public static final int CENTER = 8;

    String string;
    Point2D.Double anchor = new Point2D.Double();
    int orientation = BOTTOM_LEFT;

    public GraphicString(String string, Point2D anchor) {
        this(string, anchor, BOTTOM_LEFT);
    }

    public GraphicString(String string, double x, double y) {
        this(string, new Point2D.Double(x, y), BOTTOM_LEFT);
    }

    public GraphicString(String string, double x, double y, int orientation) {
        this(string, new Point2D.Double(x, y), orientation);
    }
    
    public GraphicString(String string, Point2D anchor, int orientation) {
        setAnchor(anchor);
        setString(string);
        setOrientation(orientation);
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
        this.anchor.setLocation(newAnchor);
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }
}
