/*
 * PlanePathPlottable.java
 * Created Apr 8, 2010
 */

package visometry.plane;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import primitive.style.PathStyle;
import visometry.VPrimitiveEntry;
import visometry.plottable.Plottable;

/**
 * This simplifies implementation of plottables that display a single (general path),
 * by providing a default entry and default style patterns.
 *
 * @author Elisha Peterson
 */
public abstract class PlanePathPlottable extends Plottable<Point2D.Double> {

    /** Entry containing the path and style */
    VPrimitiveEntry entry;

    /** Stores path underlying graph in local coordinates */
    transient GeneralPath path;

    /** Constructs the plottable and the corresponding primitive entry. */
    public PlanePathPlottable() {
        addPrimitive(entry = new VPrimitiveEntry(path, new PathStyle( new Color(64, 0, 0) )));
    }


    //
    // STYLE METHODS
    //

    /** @return current style of stroke for this plottable */
    public PathStyle getStyle() { return (PathStyle) entry.style; }
    /** Set current style of stroke for this plottable */
    public void setStyle(PathStyle newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }
}
