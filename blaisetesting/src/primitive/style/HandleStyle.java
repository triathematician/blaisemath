/**
 * HandleStyle.java
 * Created on Dec 17, 2009
 */

package primitive.style;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * <p>
 *    This class can be used to draw a small "handle" at a given point.
 * </p>
 * @author Elisha Peterson
 */
public class HandleStyle extends AbstractPrimitiveStyle<Point2D.Double> {

    private static final HandleStyle INSTANCE = new HandleStyle();

    /** Constructs with default parameters */
    public HandleStyle(){}

    public static HandleStyle getInstance() { return INSTANCE; }

    public Class<? extends Point2D.Double> getTargetType() {
        return Point2D.Double.class;
    }

    public void draw(Graphics2D canvas, Point2D.Double p) {
        canvas.setColor(Color.BLACK);
        canvas.draw(new Rectangle2D.Double(p.getX()-3, p.getY()-3, 6, 6));
    }

    public boolean contained(Point2D.Double primitive, Graphics2D canvas, Point point) {
        return Math.abs(primitive.x - point.x) <= 3 && Math.abs(primitive.y - point.y) <= 3;
    }
}
