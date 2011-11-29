/**
 * DifferenceEquation1.java
 * Created on Oct 17, 2009
 */

package org.bm.blaise.scio.diffeq;

import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * <p>
 *   <code>OneVarODE</code> represents a single variable iterated equation, of the form<br>
 *  <ul>
 *      <li>x_0 = x0</li>
 *      <li>x_n = f(x_{n-1})</li>
 *  </ul>
 * </p>
 *
 * @author Elisha Peterson
 */
public class DifferenceEquation1 {

    //
    //
    // PARAMETERS
    //
    //

    /** Initial position */
    double x0;
    /** Number of time increments */
    int tf;

    /** The function for x. */
    UnivariateRealFunction function;

    //
    //
    // CONSTRUCTORS
    //
    //

    /** Standard constructor. */
    public DifferenceEquation1(UnivariateRealFunction function, double x0, int tf) {
        this.function = function;
        this.x0 = x0;
        this.tf = tf;
    }


    //
    //
    // BEAN PATTERNS
    //
    //

    /**
     * Returns current function.
     * @return current function
     */
    public UnivariateRealFunction getFunction() {
        return function;
    }

    /**
     * Sets function
     * @param function function
     */
    public void setFunction(UnivariateRealFunction function) {
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
     * Returns current tf
     * @return current tf
     */
    public int getTFinal() {
        return tf;
    }

    /**
     * Sets current tf
     * @param current tf
     */
    public void setTFinal(int tFinal) {
        this.tf = tFinal;
    }

    //
    //
    // SOLUTION METHODS
    //
    //

    /** Stores values */
    transient double[] lastResult;

    /** Computes the result. */
    public double[] computeResult() throws FunctionEvaluationException {
        lastResult = new double[tf + 1];
        lastResult[0] = x0;
        for (int i = 1; i < lastResult.length; i++)
            lastResult[i] = function.value(lastResult[i-1]);
        return lastResult;
    }

    /** Returns result as a path of points (t, x_t). */
    public Point2D.Double[] getIndexedPath() {
        if (lastResult == null) {
            try {
                computeResult();
            } catch (FunctionEvaluationException ex) {
                lastResult = new double[1];
                lastResult[0] = x0;
            }
        }
        Point2D.Double[] result = new Point2D.Double[lastResult.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Point2D.Double(i, lastResult[i]);
        }
        return result;
    }
    
    /** Returns result as a path of successive points (x_{t-1}, x_t). */
    public Point2D.Double[] getSuccPath() {
        if (lastResult == null) {
            try {
                computeResult();
            } catch (FunctionEvaluationException ex) {
                lastResult = new double[1];
                lastResult[0] = x0;
            }
        }
        Point2D.Double[] result = new Point2D.Double[lastResult.length - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Point2D.Double(lastResult[i], lastResult[i + 1]);
        }
        return result;
    }

    /** Returns result as a path of successive points (x_{t-1}, x_t)->(x_t,x_t)->... */
    public Point2D.Double[] getCobwebPath() {
        if (lastResult == null) {
            try {
                computeResult();
            } catch (FunctionEvaluationException ex) {
                lastResult = new double[1];
                lastResult[0] = x0;
            }
        }
        Point2D.Double[] result = new Point2D.Double[2 * lastResult.length - 1];
        result[0] = new Point2D.Double(lastResult[0], 0);
        for (int i = 0; i < lastResult.length - 1; i++) {
            result[2*i + 1] = new Point2D.Double(lastResult[i], lastResult[i + 1]);
            result[2*i + 2] = new Point2D.Double(lastResult[i + 1], lastResult[i + 1]);
        }
        return result;
    }
}
