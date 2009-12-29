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
public class HandleStyle implements PrimitiveStyle<Point2D> {

    public static final HandleStyle INSTANCE = new HandleStyle();

    private HandleStyle(){}

    public void draw(Point2D p, Graphics2D canvas) {
        canvas.draw(new Rectangle2D.Double(p.getX()-3, p.getY()-3, 6, 6));
    }

    public void draw(Point2D[] primitives, Graphics2D canvas) {
        for (Point2D p : primitives) {
            draw(p, canvas);
        }
    }

}
