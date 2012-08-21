/**
 * OneVarODE.java
 * Created on Jul 29, 2009
 */
package org.bm.blaise.scio.diffeq;

import java.io.Serializable;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.ode.ContinuousOutputModel;
import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math.ode.FirstOrderIntegrator;
import org.apache.commons.math.ode.IntegratorException;

/**
 * <p>
 *   <code>OneVarODE</code> represents a single variable diff-eq problem, of the form<br>
 *  <ul>
 *      <li>x(t0) = x0</li>
 *      <li>dx/dt = f(t,x)</li>
 *  </ul>
 *
 *  Uses apache math commons utilities to solve.
 * </p>
 *
 * @author Elisha Peterson
 */
public class OneVarODE implements FirstOrderDifferentialEquations, Serializable {


    //
    //
    // PARAMETERS
    //
    //
    
    /** The function dx/dt=f(t,x). */
    MultivariateRealFunction function;

    /** Initial position */
    double x0;
    /** Initial time */
    double t0;
    /** Final time */
    double tf;

    /** Stores technique for solving. */
    ODESolver algorithm = ODESolver.RUNGE_KUTTA;

    //
    //
    // CONSTRUCTORS
    //
    //
    /** Standard constructor. */
    public OneVarODE(MultivariateRealFunction function, double t0, double x0, double tFinal) {
        this.function = function;
        this.t0 = t0;
        this.x0 = x0;
        this.tf = tFinal;
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
    public MultivariateRealFunction getFunction() {
        return function;
    }

    /**
     * Sets dx/dt function
     * @param function dx/dt function
     */
    public void setFunction(MultivariateRealFunction function) {
        this.function = function;
    }

    /**
     * Returns initial position
     * @return initial position
     */
    public double getX0() {
        return x0;
    }

    /**
     * Sets initial position
     * @param initial position
     */
    public void setX0(double value) {
        this.x0 = value;
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
    public void setT0(double value) {
        this.t0 = value;
    }

    /**
     * Returns current tf
     * @return current tf
     */
    public double getTFinal() {
        return tf;
    }

    /**
     * Sets current tf
     * @param current tf
     */
    public void setTFinal(double tFinal) {
        this.tf = tFinal;
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
        return 1;
    }

    public void computeDerivatives(double t, double[] y, double[] yDot) throws DerivativeException {
        try {
            yDot[0] = function.value(new double[]{t, y[0]});
        //System.out.println("yDot="+yDot[0]+" for input "+new Point2D.Double(t, y[0]));
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
    public ContinuousOutputModel solveODE(double[] xFinal, double step) throws DerivativeException, IntegratorException {
        FirstOrderIntegrator i = algorithm.getIntegrator(step);
        com = new ContinuousOutputModel();
        i.addStepHandler(com);
        i.integrate(this, t0, new double[]{x0}, tf, xFinal);
        return com;
    }
}
