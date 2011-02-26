/**
 * PlaneRandomWalk.java
 * Created on Oct 17, 2009
 */
package org.bm.blaise.specto.plane.diffeq;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import org.apache.commons.math.FunctionEvaluationException;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.plane.ComputedPointPath;
import scio.diffeq.DifferenceEquation1;

/**
 * <p>
 *   <code>PlaneRandomWalk</code> computes a random walk from a dynamic starting location.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneCobwebFunction extends ComputedPointPath {

    /** Responsible for computing the walk. */
    DifferenceEquation1 de;

    //
    // CONSTRUCTORS
    //

    /**
     * Default constructor.
     */
    public PlaneCobwebFunction(Point2D.Double point, DifferenceEquation1 de) {
        super(point);
        this.de = de;
    }

    //
    // BEAN PATTERNS
    //

    /** @return the underlying walk used for computation */
    public DifferenceEquation1 getEquation() {
        return de;
    }

    /** Sets a new walk for this plottable */
    public void setEquation(DifferenceEquation1 de) {
        if (this.de != de) {
            this.de = de;
            needsComputation = true;
            fireStateChanged();
        }
    }


    //
    // COMPUTATION / PAINTING
    //

    protected GeneralPath getPath(VisometryGraphics<Point2D.Double> vg) {
        GeneralPath path = new GeneralPath();
        path.moveTo((float) getPoint().x, (float) getPoint().y);
        de.setX0(getPoint().x);
        try {
            de.computeResult();
            Point2D.Double[] pw = de.getCobwebPath();
            for (int i=0; i < pw.length; i++)
                path.lineTo((float) pw[i].x, (float) pw[i].y);
        } catch (FunctionEvaluationException ex) {
        }
        return path;
    }
}
