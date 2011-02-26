/*
 * DimensionHandleStyle.java
 * Created Jun 14, 2010
 */

package old.styles;

import old.styles.verified.StringStyle;
import old.styles.verified.ShapeStyle;
import old.styles.verified.AbstractPrimitiveStyle;
import old.styles.verified.GraphicString;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import old.styles.verified.ArrowStyle.ArrowShape;

/**
 * Displays a "handle" useful for marking dimensions. Consists of an integer "offset"
 * at which the arrow is drawn, two bars marking the boundaries, and possibly a label.
 *
 * @author elisha
 */
public class DimensionHandleStyle extends AbstractPrimitiveStyle<Point2D.Double[]> {

    /** For drawing the arrow */
    ArrowStyle arrStyle = new ArrowStyle(ArrowShape.REGULAR, ArrowShape.REGULAR, 8);
    /** For drawing the label */
    StringStyle labelStyle = new StringStyle();
    /** For drawing the bars */
    ShapeStyle barStyle = new ShapeStyle(Color.BLACK, Color.BLACK);
    /** Determines the offset for the arrow */
    int arrOffset = 14;
    /** Determines the height of the bars */
    int barHeight = 20;
    /** Label marking the line */
    String label = "x = 5";

    /** Default constructor */
    public DimensionHandleStyle() {
    }

    public Class<? extends Point2D.Double[]> getTargetType() {
        return Point2D.Double[].class;
    }

    public void draw(Graphics2D canvas, Point2D.Double[] primitive) {
        // setup orthonormal basis
        Point2D.Double dir1 = new Point2D.Double(primitive[1].x-primitive[0].x, primitive[1].y-primitive[0].y);
        double magn = dir1.distance(0, 0);
        dir1.x /= magn; dir1.y /= magn;
        Point2D.Double dir2 = new Point2D.Double(dir1.y, -dir1.x);

        barStyle.drawArray(canvas, getBars(primitive));
        arrStyle.draw(canvas, new Point2D.Double[]{
            new Point2D.Double(primitive[0].x + dir1.x + arrOffset * dir2.x, primitive[0].y + dir1.y + arrOffset * dir2.y),
            new Point2D.Double(primitive[1].x - dir1.x + arrOffset * dir2.x, primitive[1].y - dir1.y + arrOffset * dir2.y)
        });
        Point2D.Double midPt = new Point2D.Double(
                .5*(primitive[0].x+primitive[1].x) + (arrOffset+5)*dir2.x,
                .5*(primitive[0].y+primitive[1].y) + (arrOffset+5)*dir2.y);
        labelStyle.draw(canvas, new GraphicString<Point2D.Double>(midPt, label));
    }

    int idx = -1;

    /** @return index of last found primitive bar... need a better way to do this */
    public int getLastContainedIndex() { return idx; }

    public boolean contained(Point2D.Double[] primitive, Graphics2D canvas, Point point) {
        Shape[] bars = getBars(primitive);
        idx = bars[0].contains(point) ? 0 : bars[1].contains(point) ? 1 : -1;
        return idx != -1;
    }

    /** Computes bar shapes marking the boundaries of the dimension */
    private Shape[] getBars(Point2D.Double[] pts) {
        // setup orthonormal basis
        Point2D.Double dir1 = new Point2D.Double(pts[1].x-pts[0].x, pts[1].y-pts[0].y);
        double magn = dir1.distance(0, 0);
        dir1.x /= magn; dir1.y /= magn;
        Point2D.Double dir2 = new Point2D.Double(dir1.y, -dir1.x);

        GeneralPath p1 = new GeneralPath();
        p1.moveTo((float)(pts[0].x + dir1.x), (float)(pts[0].y + dir1.y));
        p1.lineTo((float)(pts[0].x + dir1.x + barHeight * dir2.x), (float)(pts[0].y + dir1.y + barHeight * dir2.y));
        p1.lineTo((float)(pts[0].x - dir1.x + barHeight * dir2.x), (float)(pts[0].y - dir1.y + barHeight * dir2.y));
        p1.lineTo((float)(pts[0].x - dir1.x), (float)(pts[0].y - dir1.y));
        p1.closePath();
        
        GeneralPath p2 = new GeneralPath();
        p2.moveTo((float)(pts[1].x + dir1.x), (float)(pts[1].y + dir1.y));
        p2.lineTo((float)(pts[1].x + dir1.x + barHeight * dir2.x), (float)(pts[1].y + dir1.y + barHeight * dir2.y));
        p2.lineTo((float)(pts[1].x - dir1.x + barHeight * dir2.x), (float)(pts[1].y - dir1.y + barHeight * dir2.y));
        p2.lineTo((float)(pts[1].x - dir1.x), (float)(pts[1].y - dir1.y));
        p2.closePath();

        return new Shape[] {p1, p2};
    }

}
