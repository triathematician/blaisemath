/**
 * PlanePolarGrid.java
 * Created on Aug 6, 2009
 */
package org.bm.blaise.specto.plane;

import java.awt.geom.Point2D;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.primitive.ShapeStyle;
import org.bm.utils.NiceRangeGenerator;

/**
 * <p>
 *   <code>PlanePolarGrid</code> shows a polar-coordinate grid.
 * </p>
 *
 * TODO - options for angle spacing of grid
 *
 * @author Elisha Peterson
 */
public class PlanePolarGrid extends AbstractPlottable<Point2D.Double> {

    PathStyle style = new PathStyle(BlaisePalette.STANDARD.grid());

    public PlanePolarGrid() {
    }

    public PathStyle getStrokeStyle() {
        return style;
    }

    public void setStrokeStyle(PathStyle style) {
        this.style = style;
    }
    
    private static final int PIXEL_SPACING = 80;
    double THETA_STEPS = 24;

    @Override
    public void paintComponent(VisometryGraphics<Point2D.Double> vg) {
        PlaneGraphics pg = (PlaneGraphics) vg;
        pg.setPathStyle(style);
        pg.setShapeStyle(new ShapeStyle(style));

        double minR = minRadius(vg.getMinCoord(), vg.getMaxCoord());
        double maxR = maxRadius(vg.getMinCoord(), vg.getMaxCoord());

        double[] rValues = NiceRangeGenerator.STANDARD.niceRange(minR, maxR, pg.getIdealStepForPixelSpacing(PIXEL_SPACING));

        for (Double r : rValues)
            pg.drawCircle(0.0, 0.0, r);

       for (double theta = 0; theta < 2 * Math.PI; theta += 2 * Math.PI / THETA_STEPS)
            pg.drawSegment(minR * Math.cos(theta), minR * Math.sin(theta), maxR * Math.cos(theta), maxR * Math.sin(theta));
    }

    /** Compute minimum distance from origin in window specified by given points. */
    private static double minRadius(Point2D.Double pt1, Point2D.Double pt2) {
        if (pt1.x * pt2.x <= 0 && pt1.y * pt2.y <= 0) { // origin is inside box: min is 0
            return 0;
        } else if (pt1.x * pt2.x <= 0) { // origin is within x bounds
            return Math.min(Math.abs(pt1.y), Math.abs(pt2.y));
        } else if (pt1.y * pt2.y <= 0) { // origin is within y bounds
            return Math.min(Math.abs(pt1.x), Math.abs(pt2.x));
        }
        // default to minimum radius at a boundary point
        double a1 = pt1.distanceSq(0, 0);
        double a2 = pt2.distanceSq(0, 0);
        double a3 = Point2D.Double.distanceSq(pt1.x, pt2.y, 0, 0);
        double a4 = Point2D.Double.distanceSq(pt2.x, pt1.y, 0, 0);
        return Math.sqrt( Math.min(Math.min(Math.min(a1, a2), a3), a4) );
    }

    /** Compute maximum distance from origin in window specified by given points. */
    private static double maxRadius(Point2D.Double pt1, Point2D.Double pt2) {
        double a1 = pt1.distanceSq(0, 0);
        double a2 = pt2.distanceSq(0, 0);
        double a3 = Point2D.Double.distanceSq(pt1.x, pt2.y, 0, 0);
        double a4 = Point2D.Double.distanceSq(pt2.x, pt1.y, 0, 0);
        return Math.sqrt( Math.max(Math.max(Math.max(a1, a2), a3), a4) );
    }

    @Override
    public String toString() {
        return "Polar Grid";
    }
}
