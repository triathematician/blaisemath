/**
 * TwoVarODE.java
 * Created on Jul 29, 2009
 */

package scio.diffeq;

import java.awt.geom.Point2D;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.ode.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;
import scio.function.MultivariateRealFunction;

/**
 * <p>
 *   <code>TwoVarODE</code> represents a two-variable diff-eq problem, of the form<br>
 *      <code>x(t0) = x0;  dx/dt = f1(t,x,y);  y(t0) = y0;  dy/dt = f2(t,x,y);</code><br>
 *   Uses a math-commons integrator.
 * </p>
 *
 * @author Elisha Peterson
 */
public class TwoVarODE implements FirstOrderDifferentialEquations {

    //
    //
    // PARAMETERS
    //
    //

    /** Function for x-derivative. */
    MultivariateRealFunction f1;

    /** Function for y-derivative. */
    MultivariateRealFunction f2;

    /** Initial (x,y) */
    Point2D.Double xy0;

    /** Initial t */
    double t0;

    /** Final t */
    double tFinal;

    /** Stores values */
    transient ContinuousOutputModel com;

    //
    //
    // CONSTRUCTORS
    //
    //

    public TwoVarODE(MultivariateRealFunction f1, MultivariateRealFunction f2, Point2D.Double xy0, double t0, double tFinal) {
        this.f1 = f1;
        this.f2 = f2;
        this.xy0 = xy0;
        this.t0 = t0;
        this.tFinal = tFinal;
    }

    //
    //
    // BEAN PATTERNS
    //
    //


    public MultivariateRealFunction getF1() {
        return f1;
    }

    public void setF1(MultivariateRealFunction f1) {
        this.f1 = f1;
    }

    public MultivariateRealFunction getF2() {
        return f2;
    }

    public void setF2(MultivariateRealFunction f2) {
        this.f2 = f2;
    }

    public double getT0() {
        return t0;
    }

    public void setT0(double t0) {
        this.t0 = t0;
    }

    public double getTFinal() {
        return tFinal;
    }

    public void setTFinal(double tFinal) {
        this.tFinal = tFinal;
    }

    public Point2D.Double getStartPoint() {
        return xy0;
    }

    public void setStartPoint(Point2D.Double xy0) {
        this.xy0 = xy0;
    }

    //
    //
    // FirstOrderDifferentialEquations INTERFACE
    //
    //

    public int getDimension() {
        return 2;
    }

    public void computeDerivatives(double t, double[] y, double[] yDot) throws DerivativeException {
        try {
            yDot[0] = f1.value(new double[]{t, y[0], y[1]});
            yDot[1] = f2.value(new double[]{t, y[0], y[1]});
        } catch (FunctionEvaluationException ex) {
            throw new DerivativeException(ex);
        }
    }

    //
    //
    // Helpful methods
    //
    //

    public ContinuousOutputModel solveODE(double[] yFinal, double tStep) throws DerivativeException, IntegratorException {
        FirstOrderIntegrator i = new ClassicalRungeKuttaIntegrator(tStep);
        com = new ContinuousOutputModel();
        i.setStepHandler(com);
        i.integrate(this, t0, new double[]{xy0.x, xy0.y}, tFinal, yFinal);
        return com;
    }

}
