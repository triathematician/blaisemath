/**
 * PlaneTriangle.java
 * Created on Nov 30, 2009
 */

package visometry.plane;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import visometry.plottable.PlottableConstants;
import visometry.plottable.VShape;

/**
 * <p>
 *    This class displays a 2d triangle on a 2d plot.
 * </p>
 * @author Elisha Peterson
 */
public class PlaneTriangle extends VShape<Point2D.Double> {

    /** Construct with a default triangle */
    public PlaneTriangle() {
        this(0,0,1,2,2,1);
    }

    /** Construct with 3 coordinates provided with explicit x and y values. */
    PlaneTriangle(double v1x, double v1y, double v2x, double v2y, double v3x, double v3y) {
        this(new Point2D.Double[]{new Point2D.Double(v1x,v1y), new Point2D.Double(v2x,v2y), new Point2D.Double(v3x,v3y)});
    }

    /** Constrct with specified values. */
    public PlaneTriangle(Point2D.Double[] values) {
        super(values);
    }

    @Override
    public void setPoint(Point2D.Double[] values) {
        if (values == null || values.length != 3)
            throw new IllegalArgumentException("Triangle requires exactly 3 endpoints!");
        super.setPoint(values);
    }

    @Override
    public String toString() {
        DecimalFormat f = PlottableConstants.FLOAT_FORMAT;
        Point2D.Double[] v = getPoint();
        return "Triangle[ (" + f.format(v[0].x) + ", " + v[0].y + ") ; ("
                 + f.format(v[1].x) + ", " + v[1].y + ") ; ("
                 + f.format(v[2].x) + ", " + v[2].y + ") ]";
    }


}
