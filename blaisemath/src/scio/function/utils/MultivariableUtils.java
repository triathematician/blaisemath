/*
 * VectorFieldUtils.java
 * Created on Nov 6, 2009
 */

package scio.function.utils;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.*;

/**
 * Contains utility classes for (approximate) derivatives of multivariable function.
 *
 * @author Elisha Peterson
 */
public class MultivariableUtils {

    /**
     * Returns function that is  differentiable version of that supplied. Uses a basic secant approximation
     * @param dt the distance in time to use for approximation; must be positive
     */
    public static DifferentiableUnivariateRealFunction asDifferentiableFunction(UnivariateRealFunction vf, double dt) {
        if (dt<=0) throw new IllegalArgumentException("Derivative must be positive! (supplied value="+dt+")");
        return new ApproxDiffRealFunction(vf, dt);
    }

    /**
     * Returns function that is differentiable version of that supplied. Uses a basic secant approximation
     * @param dt the distance in time to use for approximation; must be positive
     */
    public static DifferentiableMultivariateRealFunction asDifferentiableFunction(MultivariateRealFunction vf, double dt) {
        if (dt<=0) throw new IllegalArgumentException("Derivative must be positive! (supplied value="+dt+")");
        return new ApproxDiffMultiRealFunction(vf, dt);
    }

    /**
     * Returns function that is differentiable version of that supplied. Uses a basic secant approximation
     * @param dt the distance in time to use for approximation; must be positive
     */
    public static DifferentiableUnivariateVectorialFunction asDifferentiableFunction(UnivariateVectorialFunction vf, double dt) {
        if (dt<=0) throw new IllegalArgumentException("Derivative must be positive! (supplied value="+dt+")");
        return new ApproxDiffVectorFunction(vf, dt);
    }

    /**
     * Returns function that is differentiable version of that supplied. Uses a basic secant approximation
     * @param dt the distance in time to use for approximation; must be positive
     */
    public static DifferentiableMultivariateVectorialFunction asDifferentiableFunction(MultivariateVectorialFunction vf, double dt) {
        if (dt<=0) throw new IllegalArgumentException("Derivative must be positive! (supplied value="+dt+")");
        return new ApproxDiffMultiVectorFunction(vf, dt);
    }

    /**
     * Returns function that is approximate curl of that supplied. Uses a basic secant approximation
     * @param dt the distance in time to use for approximation; must be positive
     */
    public static MultivariateVectorialFunction approximateCurlOf(MultivariateVectorialFunction func, double dt) {
        if (dt<=0) throw new IllegalArgumentException("Derivative must be positive! (supplied value="+dt+")");
        return new ApproxCurlField(func, dt);
    }

    /**
     * Returns function that is approximate div of that supplied. Uses a basic secant approximation
     * @param dt the distance in time to use for approximation; must be positive
     */
    public static MultivariateRealFunction approximateDivOf(MultivariateVectorialFunction func, double dt) {
        if (dt<=0) throw new IllegalArgumentException("Derivative must be positive! (supplied value="+dt+")");
        return new ApproxDivField(func, dt);
    }

    //
    // CLASSES
    //

    /** Differentiable version of a function. */
    static class ApproxDiffRealFunction implements DifferentiableUnivariateRealFunction {
        private UnivariateRealFunction vf;
        private double dt;
        /**
         * Construct this derived function
         * @param vf the original function
         * @param dt distance in time to use for approximation
         */
        public ApproxDiffRealFunction(UnivariateRealFunction vf, double dt) {
            this.vf = vf;
            this.dt = dt;
        }

        public UnivariateRealFunction derivative() {
            return new UnivariateRealFunction() {
                public double value(double x) throws FunctionEvaluationException {
                    return (vf.value(x+dt)-vf.value(x))/dt;
                }
            };
        }

        public double value(double x) throws FunctionEvaluationException {
            return vf.value(x);
        }
    }


    /** Differentiable version of a function. */
    static class ApproxDiffMultiRealFunction implements DifferentiableMultivariateRealFunction {
        private MultivariateRealFunction vf;
        private double dt;
        /**
         * Construct this derived function
         * @param vf the original function
         * @param dt distance in time to use for approximation
         */
        public ApproxDiffMultiRealFunction(MultivariateRealFunction vf, double dt) { 
            this.vf = vf; this.dt = dt;
        }

        public double value(double[] input) throws FunctionEvaluationException {
            return vf.value(input);
        }

        public MultivariateRealFunction partialDerivative(final int k) {
            return new MultivariateRealFunction() {
                public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                    double result1 = vf.value(point);
                    point[k] += dt;
                    double result2 = vf.value(point);
                    return (result2-result1)/dt;
                }
            };
        }

        public MultivariateVectorialFunction gradient() {
            return new MultivariateVectorialFunction() {
                public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                    double[] result = new double[point.length];
                    for (int i = 0; i < result.length; i++) {
                        result[i] = vf.value(point);
                        point[i] += dt;
                        result[i] = (vf.value(point)-result[i])/dt;
                        point[i] -= dt;
                    }
                    return result;
                }
            };
        }
    }

    /** Differentiable version of a function. */
    static class ApproxDiffVectorFunction implements DifferentiableUnivariateVectorialFunction {
        UnivariateVectorialFunction vf;
        double dt;
        /**
         * Construct this derived field
         * @param vf the original vector field
         * @param dt distance in time to use for approximation
         */
        public ApproxDiffVectorFunction(UnivariateVectorialFunction vf, double dt) {
            this.vf = vf;
            this.dt = dt;
        }
        /** Value returned is the part of the first field in the direction of the second field. */
        public double[] value(double t) throws FunctionEvaluationException, IllegalArgumentException {
            return vf.value(t);
        }

        public UnivariateVectorialFunction derivative() {
            return new UnivariateVectorialFunction() {
                public double[] value(double t) throws FunctionEvaluationException {
                    double[] result = vf.value(t);
                    double[] result2 = vf.value(t+dt);
                    for (int i = 0; i < result.length; i++) { result[i] = (result2[i]-result[i])/dt; }
                    return result;
                }
            };
        }
    }

    /** Differentiable version of a function. */
    static class ApproxDiffMultiVectorFunction implements DifferentiableMultivariateVectorialFunction {
        MultivariateVectorialFunction vf;
        double dt;
        /**
         * Construct this derived field
         * @param vf the original vector field
         * @param dt distance in time to use for approximation
         */
        public ApproxDiffMultiVectorFunction(MultivariateVectorialFunction vf, double dt) {
            this.vf = vf;
            this.dt = dt;
        }
        /** Value returned is the part of the first field in the direction of the second field. */
        public double[] value(double[] input) throws FunctionEvaluationException, IllegalArgumentException {
            return vf.value(input);
        }

        public MultivariateMatrixFunction jacobian() {
            return new MultivariateMatrixFunction() {
                public double[][] value(double[] input) throws FunctionEvaluationException, IllegalArgumentException {
                    double[] result1 = vf.value(input);
                    double[][] result = new double[input.length][result1.length];
                    for (int i = 0; i < result1.length; i++) {
                        input[i] += dt;
                        result[i] = vf.value(input);
                        input[i] -= dt;
                        for (int j = 0; j < result[i].length; j++) {
                            result[i][j] = (result[i][j]-result1[j])/dt;
                        }
                    }
                    return result;
                }
            };
        }
    }

    /** Approximate curl of input field. */
    static class ApproxCurlField implements MultivariateVectorialFunction {
        DifferentiableMultivariateVectorialFunction vf;
        double dt;
        /**
         * Construct this derived field
         * @param vf the original vector field
         * @param dt distance in time to use for approximation
         */
        public ApproxCurlField(MultivariateVectorialFunction vf, double dt) {
            this.vf = asDifferentiableFunction(vf, dt);
            this.dt = dt;
        }
        /** Value returned is the part of the first field in the direction of the second field. */
        public double[] value(double[] input) throws FunctionEvaluationException, IllegalArgumentException {
            double[][] jac = vf.jacobian().value(input);
            return new double[] {
                jac[1][2]-jac[2][1],
                jac[2][0]-jac[0][2],
                jac[0][1]-jac[1][0]
            };
        }
    }

    /** Approximate curl of input field. */
    static class ApproxDivField implements MultivariateRealFunction {
        DifferentiableMultivariateVectorialFunction vf;
        double dt;
        /**
         * Construct this derived field
         * @param vf the original vector field
         * @param dt distance in time to use for approximation
         */
        public ApproxDivField(MultivariateVectorialFunction vf, double dt) {
            this.vf = asDifferentiableFunction(vf, dt);
            this.dt = dt;
        }
        /** Value returned is the part of the first field in the direction of the second field. */
        public double value(double[] input) throws FunctionEvaluationException, IllegalArgumentException {
            double[][] jac = vf.jacobian().value(input);
            double result = 0;
            for (int i = 0; i < jac.length; i++) {
                result += jac[i][i];
            }
            return result;
        }
    }
}
