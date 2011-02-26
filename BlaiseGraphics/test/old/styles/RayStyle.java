/*
 * RayStyle.java
 * Created Apr 12, 2010
 */

package old.styles;

import old.styles.verified.ArrowStyle;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Draws a ray from first primitive point through second primitive point, terminating at the boundary of the window.
 * Uses the method contained in <code>LineStyle</code> to compute the window boundary intersection.
 * 
 * @author Elisha Peterson
 */
public class RayStyle extends ArrowStyle {

    /** Construct with default parameters */
    public RayStyle() { super(); }
    /** Construct with specified color */
    public RayStyle(Color color) { super(color); }
    /** Construct with specified head shape and size */
    public RayStyle(Color color, ArrowShape shape, int shapeSize) { super(color, shape, shapeSize); }
    /** Construct with specified head and anchor shapes */
    public RayStyle(ArrowShape headShape, ArrowShape anchorShape, int shapeSize) { super(headShape, anchorShape, shapeSize); }

    @Override
    public void draw(Graphics2D canvas, Point2D.Double[] ray) {
        super.draw(canvas, new Point2D.Double[]{ ray[0], LineStyle.boundaryHit(ray[0], ray[1], canvas.getClipBounds()) });
    }

    @Override
    public void drawArray(Graphics2D canvas, Point2D.Double[][] rays) {
        Rectangle2D bounds = canvas.getClipBounds();
        for (Point2D.Double[] ray : rays)
            super.draw(canvas, new Point2D.Double[]{ ray[0], LineStyle.boundaryHit(ray[0], ray[1], bounds) });
    }

}
