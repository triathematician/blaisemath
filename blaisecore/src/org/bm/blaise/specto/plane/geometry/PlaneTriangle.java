/**
 * PlaneTriangle.java
 * Created on Nov 30, 2009
 */

package org.bm.blaise.specto.plane.geometry;

import java.awt.geom.Point2D;
import org.bm.blaise.specto.plottable.VPolygon;

/**
 * <p>
 *    This class displays a 2d triangle on a 2d plot.
 * </p>
 * @author Elisha Peterson
 */
public class PlaneTriangle extends VPolygon<Point2D.Double> {

    public PlaneTriangle() {
        this(new Point2D.Double[]{new Point2D.Double(0,0), new Point2D.Double(1,1), new Point2D.Double(1,2)});
    }

    public PlaneTriangle(Point2D.Double[] values) {
        super(values);
    }

    @Override
    public void setValues(Point2D.Double[] values) {
        if (values == null || values.length != 3)
            throw new IllegalArgumentException("Triangle requires exactly 3 endpoints!");
        super.setValues(values);
    }

    @Override
    public String toString() {
        return "Triangle ("
                + formatter.format(values[0].getX()) + ", " + formatter.format(values[0].getY()) + "), ("
                + formatter.format(values[1].getX()) + ", " + formatter.format(values[1].getY()) + "), ("
                + formatter.format(values[2].getX()) + ", " + formatter.format(values[2].getY())
                + ")";
    }
}
