/*
 * FunctionUtils.java
 * Created on Dec 31, 2009
 */

package org.bm.blaise.scio.function.utils;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;

/**
 * <p>
 *   Some static utilities to generate new functions from old ones.
 * </p>
 * @author Elisha Peterson
 */
public class FunctionUtils {

    //
    // COMPOSITIONS
    //

    /** Returns function representing the composition f1 (of) f2, i.e. evaluates f2 first. */
    public static MultivariateVectorialFunction compositionOf(final MultivariateVectorialFunction f1, final MultivariateVectorialFunction f2) {
        return new MultivariateVectorialFunction(){
            public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                return f1.value(f2.value(point));
            }
        };
    }
    /** Returns function representing the composition f1 (of) f2, i.e. evaluates f2 first. */
    public static MultivariateVectorialFunction compositionOf(final UnivariateVectorialFunction f1, final MultivariateRealFunction f2) {
        return new MultivariateVectorialFunction(){
            public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                return f1.value(f2.value(point));
            }
        };
    }

    /** Returns function representing the composition f1 (of) f2, i.e. evaluates f2 first. */
    public static UnivariateRealFunction compositionOf(final MultivariateRealFunction f1, final UnivariateVectorialFunction f2) {
        return new UnivariateRealFunction(){
            public double value(double point) throws FunctionEvaluationException, IllegalArgumentException {
                return f1.value(f2.value(point));
            }
        };
    }
    /** Returns function representing the composition f1 (of) f2, i.e. evaluates f2 first. */
    public static UnivariateRealFunction compositionOf(final UnivariateRealFunction f1, final UnivariateRealFunction f2) {
        return new UnivariateRealFunction(){
            public double value(double point) throws FunctionEvaluationException, IllegalArgumentException {
                return f1.value(f2.value(point));
            }
        };
    }

    /** Returns function representing the composition f1 (of) f2, i.e. evaluates f2 first. */
    public static UnivariateVectorialFunction compositionOf(final MultivariateVectorialFunction f1, final UnivariateVectorialFunction f2) {
        return new UnivariateVectorialFunction(){
            public double[] value(double point) throws FunctionEvaluationException, IllegalArgumentException {
                return f1.value(f2.value(point));
            }
        };
    }

    // TODO - add remaining 4 vases

    //
    // PROJECTIONS
    //

    /** Projects function onto the first n coordinates. */
    public static MultivariateVectorialFunction projectionOf(final MultivariateVectorialFunction f, final int n) {
        return new MultivariateVectorialFunction() {
            public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                double[] fp = f.value(point);
                for (int i = n; i < fp.length; i++) {
                    fp[i] = 0;
                }
                return fp;
            }
        };
    }
    /** Projects function onto the first n coordinates; or extends to n coordinates. */
    public static UnivariateVectorialFunction projectionOf(final UnivariateVectorialFunction f, final int n) {
        return new UnivariateVectorialFunction() {
            public double[] value(double point) throws FunctionEvaluationException, IllegalArgumentException {
                double[] fp = f.value(point);
                if (n < fp.length) {
                    for (int i = n; i < fp.length; i++) {
                        fp[i] = 0;
                    }
                    return fp;
                } else if (n > fp.length) {
                    double[] result = new double[n];
                    System.arraycopy(fp, 0, result, 0, fp.length);
                    for (int i = fp.length; i < n; i++) {
                        result[i] = 0;
                    }
                    return result;
                }
                return fp;
            }
        };
    }

    //
    // GRAPHS
    //

    /** Graph of a real function. */
    public static UnivariateVectorialFunction graphOf(final UnivariateRealFunction f) {
        return new UnivariateVectorialFunction(){
            public double[] value(double x) throws FunctionEvaluationException {
                return new double[] { x, f.value(x) };
            }
        };
    }
    /** Graph of a multivariate function. */
    public static MultivariateVectorialFunction graphOf(final MultivariateRealFunction f) {
        return new MultivariateVectorialFunction(){
            public double[] value(double[] x) throws FunctionEvaluationException {
                double[] result = new double[x.length + 1];
                System.arraycopy(x, 0, result, 0, x.length);
                result[x.length] = f.value(x);
                return result;
            }
        };
    }
    /** Graph of a multivariate function. */
    public static MultivariateVectorialFunction graphOf(final MultivariateVectorialFunction f) {
        return new MultivariateVectorialFunction(){
            public double[] value(double[] x) throws FunctionEvaluationException {
                double[] fx = f.value(x);
                double[] result = new double[x.length + fx.length];
                System.arraycopy(x, 0, result, 0, x.length);
                System.arraycopy(fx, x.length, result, x.length, fx.length);
                return result;
            }
        };
    }


}
