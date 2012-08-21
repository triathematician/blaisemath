/**
 * VComputedPath.java
 * Created on Sep 17, 2009
 */

package org.bm.blaise.specto.plane;

import java.awt.Point;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import org.bm.blaise.specto.primitive.BlaisePalette;
import org.bm.blaise.specto.primitive.PathStyle;
import org.bm.blaise.specto.visometry.DynamicPlottable;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryChangeListener;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;

/**
 * <p>
 *   <code>VComputedPath</code> displays a path that may be recomputed when the visometry
 *   or other elements change
 * </p>
 *
 * @param <C> coordinate type of visometry
 * @author Elisha Peterson
 */
public abstract class ComputedPath extends DynamicPlottable<Point2D.Double>
        implements VisometryChangeListener {

    //
    // PROPERTIES
    //
    
    /** Style of stroke */
    protected PathStyle strokeStyle = new PathStyle(BlaisePalette.STANDARD.func1());


    //
    // STYLE PATTERNS
    //

    public PathStyle getStrokeStyle() {
        return strokeStyle;
    }

    public void setStrokeStyle(PathStyle style) {
        this.strokeStyle = style;
    }

    //
    // DRAW METHODS
    //
    
    /** Determines whether path needs to be recomputed */
    transient protected boolean needsComputation = true;
    /** Stores the path computed and displayed (in WINDOW coordinates) */
    transient GeneralPath path;

    /** This method is called to recompute the path; should return the path in LOCAL coordinates. */
    abstract protected GeneralPath getPath(VisometryGraphics<Point2D.Double> vg);

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        path = ((PlaneGraphics)canvas).toWindow(getPath(canvas));
        needsComputation = false;
    }


    @Override
    public void draw(VisometryGraphics<Point2D.Double> vg) {
        if (needsComputation || path == null)
            visometryChanged(null, vg);
        strokeStyle.draw(vg.getScreenGraphics(), path, selected);
    }

    //
    // MOUSE METHODS
    //

    /**
     * Checks to see if the mouse point is close to this path. Uses the built in intersection
     * checking of the <code>GeneralPath</code>
     * @param e the mouse visometry event
     * @return <code>true</code> if the mouse pointer is within a few pixels of the path
     */
    public boolean isClickablyCloseTo(VisometryMouseEvent<Point2D.Double> e) {
        Point p = e.getWindowPoint();
        if (path == null)
            return false;
        return path.intersects(p.x - 2, p.y - 2, 5, 5);
    }
}
