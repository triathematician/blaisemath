/**
 * GraphicString.java
 * Created on Sep 6, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.geom.Point2D;

/**
 * <p>
 *   <code>GraphicString</code> is a graphics primitive that contains a string
 *   and an anchor point.
 * </p>
 *
 * @author Elisha Peterson
 */
public class GraphicString {

    Point2D anchor;
    String string;

    public GraphicString(Point2D anchor, String string) {
        this.anchor = anchor;
        this.string = string;
    }

    public GraphicString(double x, double y, String string) {
        this.string = string;
        this.anchor = new Point2D.Double(x, y);
    }

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
}
