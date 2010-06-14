/*
 * PlottableConstants.java
 * Created Mar 31, 2010
 */

package visometry.plottable;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * <p>
 *  Contains flags and constants used for all plottables.
 * </p>
 * @author Elisha Peterson
 */
public class PlottableConstants {

    // no instantiation
    private PlottableConstants() {}

    /** General formatting class for decimals. */
    public static DecimalFormat FLOAT_FORMAT;
    static {
        FLOAT_FORMAT = new DecimalFormat();
        FLOAT_FORMAT.setMaximumFractionDigits(4);
    }

    /** Formatting class for points. */
    public static PointFormat POINT_FORMAT = new PointFormat();

    /** Able to format a point */
    public static class PointFormat {
        DecimalFormat df;
        public PointFormat() { this(FLOAT_FORMAT); }
        public PointFormat(DecimalFormat df) { this.df = df; }
        public String format(Point2D point) { return "(" + df.format(point.getX()) + ", " + df.format(point.getY()) + ")"; }
    }

    /** Formats a coordinate using the formats contained within this class */
    public static String formatCoordinate(Object value) {
        return value instanceof Double
                ? FLOAT_FORMAT.format((Double) value)
                : value instanceof Point2D
                ? POINT_FORMAT.format((Point2D) value)
                : value.toString();
    }
}
