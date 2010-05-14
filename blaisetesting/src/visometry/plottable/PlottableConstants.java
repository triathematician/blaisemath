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
public interface PlottableConstants {
    /** General formatting class for decimals. */
    public static DecimalFormat floatFormat = new DecimalFormat("#0.00");

    /** Formatting class for points. */
    public static PointFormat pointFormat = new PointFormat();

    /** Able to format a point */
    public static class PointFormat {
        DecimalFormat df;
        public PointFormat() { this(floatFormat); }
        public PointFormat(DecimalFormat df) { this.df = df; }
        public String format(Point2D point) { return "(" + df.format(point.getX()) + ", " + df.format(point.getY()) + ")"; }
    }
}
