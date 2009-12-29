/**
 * PlaneRandomWalk.java
 * Created on Oct 17, 2009
 */
package org.bm.blaise.specto.plane.diffeq;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.apache.commons.math.FunctionEvaluationException;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.plottable.VComputedPointPath;
import scio.diffeq.DifferenceEquation1;

/**
 * <p>
 *   <code>PlaneRandomWalk</code> computes a random walk from a dynamic starting location.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneCobwebFunction extends VComputedPointPath<Point2D.Double> {

    //
    //
    // PROPERTIES
    //
    //

    /** Responsible for computing the walk. */
    DifferenceEquation1 de;

    //
    //
    // CONSTRUCTORS
    //
    //

    /**
     * Default constructor.
     */
    public PlaneCobwebFunction(Point2D.Double point, DifferenceEquation1 de) {
        super(point);
        this.de = de;
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    /** @return the underlying walk used for computation */
    public DifferenceEquation1 getEquation() {
        return de;
    }

    /** Sets a new walk for this plottable */
    public void setEquation(DifferenceEquation1 de) {
        this.de = de;
    }


    //
    //
    // COMPUTATION / PAINTING
    //
    //

    protected void recompute(VisometryGraphics<Point2D.Double> vg) {
        if (path == null) {
            path = new GeneralPath();
        } else {
            path.reset();
        }
        path.moveTo((float) value.x, (float) value.y);
        de.setX0(value.x);
        try {
            de.computeResult();
        } catch (FunctionEvaluationException ex) {
        }
        Point2D.Double[] pw = de.getCobwebPath();
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
