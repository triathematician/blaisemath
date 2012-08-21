/**
 * PlaneDE2Solution.java
 * Created on Aug 9, 2009
 */
package org.bm.blaise.specto.plane.diffeq;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.IntegratorException;
import scio.diffeq.TwoVarODE;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.plane.ComputedPointPath;

/**
 * <p>
 *   <code>PlaneDE2Solution</code> plots a solution to first-order differential equations
 *   of the form x'=f(t,x,y) and y'=g(t,x,y) on the [x,y]-coordinate axes.
 *
 *   The initial conditions for [x,y] are tied to the initial point. Settings for the solution
 *   are stored within the ODE class.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneDE2Solution extends ComputedPointPath {

    //
    // PROPERTIES
    //

    /** ODE responsible for computing the solution. */
    TwoVarODE ode;
    /** Used to interpolate the computed solution. */
    ContinuousOutputModel com;
    /** This determines the step size used for solving and interpolating the
     * solution. */
    double step = 0.05;


    //
    // CONSTRUCTORS
    //

    /**
     * Constructs a solution curve for the plot given the specified set of
     * 2 differential equations.
     * @param point The initial [x0,y0] point.
     * @param dfx Derivative of x, in terms of (t,x,y)
     * @param dfy Derivative of y, in terms of (t,x,y)
     * @param t0 The initial value of t
     * @param tFinal The final value of t
     */
    public PlaneDE2Solution(
            Point2D.Double point,
            MultivariateRealFunction dfx, MultivariateRealFunction dfy,
            double t0, double tFinal) {
        super(point);
        ode = new TwoVarODE(dfx, dfy, point, t0, tFinal);
    }

    //
    // BEAN PATTERNS
    //

    /** @return the underlying ODE used for computation */
    public TwoVarODE getODE() {
        return ode;
    }

    /** Sets a new ODE for this plottable */
    public void setODE(TwoVarODE ode) {
        this.ode = ode;
    }

    /** @return length of time between each sample point in the solution curve. */
    public double getStep() {
        return step;
    }

    /** Sets the time between each sample point in the solution curve. */
    public void setStep(double step) {
        this.step = step;
    }
    

    //
    // COMPUTATION / PAINTING
    //

    @Override
    protected GeneralPath getPath(VisometryGraphics<Point2D.Double> vg) {
        ode.setStartPoint(getPoint());
        double[] yFinal = {0.999, 0.999};
        try {
            com = ode.solveODE(yFinal, step);
        } catch (DerivativeException ex) {
            Logger.getLogger(PlaneDESolution.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IntegratorException ex) {
            Logger.getLogger(PlaneDESolution.class.getName()).log(Level.SEVERE, null, ex);
        }
        int nSteps = (int) (((com.getFinalTime() - com.getInitialTime()) / step) + 1);

        GeneralPath path = new GeneralPath();
        path.moveTo((float) getPoint().x, (float) getPoint().y);
        try {
            for (int i=0; i<nSteps; i++) {
                double t = com.getInitialTime() + i * step;
                com.setInterpolatedTime( t );
                double[] p = com.getInterpolatedState();
                path.lineTo((float) p[0], (float) p[1]);
            }
        } catch(DerivativeException ex) {
        }

        return path;
    }
}
