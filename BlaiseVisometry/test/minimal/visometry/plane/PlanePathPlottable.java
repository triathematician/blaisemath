/*
 * PlanePathPlottable.java
 * Created Apr 8, 2010
 */

package minimal.visometry.plane;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import primitive.style.temp.PathStyleShape;
import visometry.graphics.VGraphicEntry;
import visometry.plottable.AbstractPlottable;

/**
 * This simplifies implementation of plottables that display a single (general path),
 * by providing a default entry and default style patterns. Implementing classes
 * should update the <code>path</code> property within the <code>recompute</code> method.
 *
 * @author Elisha Peterson
 */
public abstract class PlanePathPlottable extends AbstractPlottable<Point2D.Double> {

    /** Entry containing the path and style */
    protected VGraphicEntry entry;

    /** Stores path underlying graph in local coordinates */
    protected transient GeneralPath path;

    /** Constructs the plottable and the corresponding primitive entry. */
    public PlanePathPlottable() {
        addPrimitive(entry = new VGraphicEntry(path, new PathStyleShape( new Color(64, 0, 0) )));
    }

    /** @return current style of stroke for this plottable */
    public PathStyleShape getStyle() { return (PathStyleShape) entry.renderer; }
    /** Set current style of stroke for this plottable */
    public void setStyle(PathStyleShape newValue) { if (entry.renderer != newValue) { entry.renderer = newValue; firePlottableStyleChanged(); } }
}
