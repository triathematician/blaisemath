/**
 * PlaneDESolution.java
 * Created on Aug 3, 2009
 */
package org.bm.blaise.specto.plane.diffeq;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.IntegratorException;
import scio.diffeq.OneVarODE;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.plane.ComputedPointPath;

/**
 * <p>
 *   <code>PlaneDESolution</code> plots a solution to a first-order differential equation
 *   of the form y'=f(t,y) on the [t,y]-coordinate axes.
 *   The initial conditions are tied to the initial point. Settings for the solution
 *   are stored within the ODE class.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneDESolution extends ComputedPointPath {

    /** ODE responsible for computing the solution. */
    OneVarODE ode;
    /** Used to interpolate the computed solution. */
    ContinuousOutputModel com;
    /** This determines the step size used for solving and interpolating the
     * solution. */
    double step = 0.05;

    /** Constructs a solution curve given the provided differential equation.
     * @param point The initial [t0,y0] point.
     * @param df Derivative of x, in terms of (t,x)
     * @param endTime The final value of t
     */
    public PlaneDESolution( Point2D.Double point, MultivariateRealFunction df, double endTime ) {
        super(point);
        ode = new OneVarODE(df, point.y, point.x, endTime);
    }

    @Override
    public String toString() {
        return "Solution Curve @ " + point.getValueString();
    }

    //
    // BEAN PATTERNS
    //

    /** @return the underlying ODE used for computation */
    public OneVarODE getODE() {
        return ode;
    }

    /** Sets a new ODE for this plottable */
    public void setODE(OneVarODE ode) {
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

    protected GeneralPath getPath(VisometryGraphics<Point2D.Double> vg) {
        ode.setT0(getPoint().x);
        ode.setX0(getPoint().y);
        double[] yFinal = { 0.999 };
        try {
            com = ode.solveODE(yFinal, step);
        } catch (DerivativeException ex) {
            Logger.getLogger(PlaneDESolution.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IntegratorException ex) {
            Logger.getLogger(PlaneDESolution.class.getName()).log(Level.SEVERE, null, ex);
        }
        GeneralPath path = new GeneralPath();
        path.moveTo((float) getPoint().x, (float) getPoint().y);
        int nSteps = (int) (((com.getFinalTime() - com.getInitialTime()) / step) + 1);
        try{
            for (int i=0; i<nSteps; i++) {
                double t = com.getInitialTime() + i * step;
                com.setInterpolatedTime( t );
                path.lineTo((float) t, (float) com.getInterpolatedState()[0]);
            }
        } catch(DerivativeException ex) {
        }
        return path;
    }
}
