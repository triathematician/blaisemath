/**
 * PlaneRandomWalk.java
 * Created on Oct 17, 2009
 */
package org.bm.blaise.specto.plane.random;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.plottable.VComputedPointPath;
import scio.random.RandomWalk2D;

/**
 * <p>
 *   <code>PlaneRandomWalk</code> computes a random walk from a dynamic starting location.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneRandomWalk extends VComputedPointPath<Point2D.Double> {

    //
    //
    // PROPERTIES
    //
    //

    /** Responsible for computing the walk. */
    RandomWalk2D walk;

    //
    //
    // CONSTRUCTORS
    //
    //

    /**
     * Default constructor.
     */
    public PlaneRandomWalk(Point2D.Double point) {
        super(point);
        walk = new RandomWalk2D();
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    /** @return the underlying walk used for computation */
    public RandomWalk2D getWalk() {
        return walk;
    }

    /** Sets a new walk for this plottable */
    public void setWalk(RandomWalk2D walk) {
        this.walk = walk;
    }


    //
    //
    // COMPUTATION / PAINTING
    //
    //

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        needsComputation = true;
    }

    protected void recompute(VisometryGraphics<Point2D.Double> vg) {
        if (path == null) {
            path = new GeneralPath();
        } else {
            path.reset();
        }
        path.moveTo((float) value.x, (float) value.y);
        walk.setStartPoint(value);
        Point2D.Double[] pw = walk.computeWalk();
        for (int i=0; i < pw.length; i++) {
            path.lineTo((float) pw[i].x, (float) pw[i].y);
        }
        needsComputation = false;
    }

    final NumberFormat formatter = new DecimalFormat("#0.00");

    @Override
    public String getValueString() {
        return "(" + formatter.format(value.x) + ", " + formatter.format(value.y) + ")";
    }
}
