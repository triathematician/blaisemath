/**
 * HandleStyle.java
 * Created on Dec 17, 2009
 */

package org.bm.blaise.specto.primitive;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 *    This class can be used to draw a small "handle" at a given point.
 * </p>
 * @author Elisha Peterson
 */
public class HandleStyle extends PrimitiveStyle<Point2D> {

    private static final HandleStyle INSTANCE = new HandleStyle();

    private HandleStyle(){}

    public static HandleStyle getInstance() {
        return INSTANCE;
    }

    public void draw(Graphics2D canvas, Point2D p, boolean selected) {
        canvas.draw(new Rectangle2D.Double(p.getX()-3, p.getY()-3, 6, 6));
    }
}
