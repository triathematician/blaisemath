/**
 * PlaneGrid.java
 * Created on Aug 3, 2009
 */

package org.bm.blaise.specto.plane;

import java.awt.geom.Point2D;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.visometry.Plottable;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.utils.NiceRangeGenerator;

/**
 * <p>
 *   <code>PlaneGrid</code> is a grid on a plot.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneGrid extends Plottable<Point2D.Double> implements VisometryChangeListener {

    /** Default approximate spacing between the grid. */
    private static final int PIXEL_SPACING = 80;

    /** Style for the grid elements. */
    PathStyle style = new PathStyle( BlaisePalette.STANDARD.grid() );

    /** Construct a default grid. */
    public PlaneGrid() {
    }

    /**
     * <b>FACTORY METHOD</b>
     * @return instance of the class with specified parameters.
     */
    public static PlaneGrid instance() {
        return new PlaneGrid();
    }

    public PathStyle getStrokeStyle() {
        return style;
    }

    public void setStrokeStyle(PathStyle style) {
        this.style = style;
    }

    // PAINT METHODS

    transient double[] xValues;
    transient double[] yValues;

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        if (canvas instanceof PlaneGraphics) {
            PlaneGraphics pg = ((PlaneGraphics) canvas);
            Point2D.Double min = pg.getMinCoord();
            Point2D.Double max = pg.getMaxCoord();
            xValues = NiceRangeGenerator.STANDARD.niceRange(min.x, max.x, pg.getIdealHStepForPixelSpacing(PIXEL_SPACING));
            yValues = NiceRangeGenerator.STANDARD.niceRange(min.y, max.y, pg.getIdealVStepForPixelSpacing(PIXEL_SPACING));
        }
    }

    @Override
    public void draw(VisometryGraphics<Point2D.Double> canvas) {
        PlaneGraphics pg = (PlaneGraphics) canvas;
        canvas.setPathStyle(style);
        
        if (xValues == null)
            return;
        for (Double x : xValues)
            pg.drawVerticalLine(x);
        
        if (yValues == null)
            return;
        for (Double y : yValues)
            pg.drawHorizontalLine(y);
    }

    @Override public String toString() { return "PlaneGrid"; }
}
