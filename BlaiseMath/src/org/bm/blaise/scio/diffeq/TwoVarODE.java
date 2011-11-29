/**
 * TwoVarODE.java
 * Created on Jul 29, 2009
 */

package org.bm.blaise.scio.diffeq;

import java.awt.geom.Point2D;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;

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

    /** Stores technique for solving. */
    ODESolver algorithm = ODESolver.RUNGE_KUTTA;

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

    /**
     * Returns current dx/dt function.
     * @return current dx/dt function
     */
    public MultivariateRealFunction getF1() {
        return f1;
    }

    /**
     * Sets dx/dt function
     * @param function dx/dt function
     */
    public void setF1(MultivariateRealFunction f1) {
        this.f1 = f1;
    }

    public MultivariateRealFunction getF2() {
        return f2;
    }

    public void setF2(MultivariateRealFunction f2) {
        this.f2 = f2;
    }

    /**
     * Returns initial position
     * @return initial position
     */
    public Point2D.Double getStartPoint() {
        return xy0;
    }

    /**
     * Sets initial position
     * @param initial position
     */
    public void setStartPoint(Point2D.Double xy0) {
        this.xy0 = xy0;
    }

    /**
     * Returns initial time
     * @return initial time
     */
    public double getT0() {
        return t0;
    }

    /**
     * Sets initial time
     * @param initial time
     */
    public void setT0(double t0) {
        this.t0 = t0;
    }

    /**
     * Returns current tf
     * @return current tf
     */
    public double getTFinal() {
        return tFinal;
    }

    /**
     * Sets current tf
     * @param current tf
     */
    public void setTFinal(double tFinal) {
        this.tFinal = tFinal;
    }

    /**
     * Returns current solver algorithm
     * @return current algorithm
     */
    public ODESolver getSolver() {
        return algorithm;
    }

    /**
     * Sets current solver algorithm
     * @param current algorithm
     */
    public void setSolver(ODESolver algorithm) {
        this.algorithm = algorithm;
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
    // SOLUTION METHODS
    //
    //

    /** Stores values */
    transient ContinuousOutputModel com;

    /**
     * Solves differential equation specified by this class, and saves the output in a
     * <code>ContinuousOutputModel</code>
     *
     * @param xFinal pass this in to store the final value of x at tf
     * @param step the step size to use for the integrator
     * @return a <code>ContinuousOutputModel</code>, which may be used to interpolate the solution curve
     * @throws org.apache.commons.math.ode.DerivativeException
     * @throws org.apache.commons.math.ode.IntegratorException
     */
    public ContinuousOutputModel solveODE(double[] yFinal, double tStep) throws DerivativeException, IntegratorException {
        FirstOrderIntegrator i = algorithm.getIntegrator(tStep);
        com = new ContinuousOutputModel();
        i.addStepHandler(com);
        i.integrate(this, t0, new double[]{xy0.x, xy0.y}, tFinal, yFinal);
        return com;
    }

}
