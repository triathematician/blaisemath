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
 * by providing a default entry and default style patterns. Implementing classes
 * should update the <code>path</code> property within the <code>recompute</code> method.
 *
 * @author Elisha Peterson
 */
public abstract class PlanePathPlottable extends Plottable<Point2D.Double> {

    /** Entry containing the path and style */
    protected VPrimitiveEntry entry;

    /** Stores path underlying graph in local coordinates */
    protected transient GeneralPath path;

    /** Constructs the plottable and the corresponding primitive entry. */
    public PlanePathPlottable() {
        addPrimitive(entry = new VPrimitiveEntry(path, new PathStyle( new Color(64, 0, 0) )));
    }

    /** @return current style of stroke for this plottable */
    public PathStyle getStyle() { return (PathStyle) entry.style; }
    /** Set current style of stroke for this plottable */
    public void setStyle(PathStyle newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }
}
