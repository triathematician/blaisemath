/*
 * PlanePathPlottable.java
 * Created Apr 8, 2010
 */

package visometry.space;

import java.awt.Color;
import primitive.style.PathStyleShape;
import scio.coordinate.Point3D;
import visometry.VPrimitiveEntry;
import visometry.plottable.Plottable;

/**
 * This simplifies implementation of plottables that display a single (general path),
 * by providing a default entry and default style patterns.
 *
 * @author Elisha Peterson
 */
public abstract class SpacePathPlottable extends Plottable<Point3D> {

    /** Entry containing the path and style */
    VPrimitiveEntry entry;

    /** Stores path underlying graph in local coordinates */
    transient Point3D[] path;

    /** Constructs the plottable and the corresponding primitive entry. */
    public SpacePathPlottable() {
        addPrimitive(entry = new VPrimitiveEntry(path, new PathStyleShape( new Color(64, 0, 0) )));
    }


    //
    // STYLE METHODS
    //

    /** @return current style of stroke for this plottable */
    public PathStyleShape getStyle() { return (PathStyleShape) entry.style; }
    /** Set current style of stroke for this plottable */
    public void setStyle(PathStyleShape newValue) { if (entry.style != newValue) { entry.style = newValue; firePlottableStyleChanged(); } }
}
