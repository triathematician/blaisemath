/**
 * DerivativeUtils.java
 * Created on Mar 11, 2008
 */
package org.bm.blaise.scio.function;

import java.awt.geom.Point2D;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.bm.blaise.scio.coordinate.Point3D;

/**
 * <p>
 *    Contains utility methods for approximating the derivative of a function.
 * </p>
 *
 * @author Elisha Peterson
 */
public class DerivativeUtils {

    /** No instantiation. */
    private DerivativeUtils() {
    }

    /**
     * Returns function providing the derivative of the input function.
     */
    public static UnivariateRealFunction getDerivativeOf(final UnivariateRealFunction function) {
        return new UnivariateRealFunction() {
            public double value(double x) throws FunctionEvaluationException {
                try {
                    return approximateDerivative(function, x, 1E-6);
                } catch (ConvergenceException ex) {
                    throw new FunctionEvaluationException(x);
                }
            }
        };
    }

    /**
     * Computes an approximate derivative.
     * @param function input function mapping a number to another number
     * @param input the input value
     * @param tolerance accuracy required
     * @return approximation of the derivative
     * @throws org.apache.commons.math.FunctionEvaluationException if the function fails to evaluate for some reason
     */
    public static double approximateDerivative(UnivariateRealFunction function, double input, double tolerance) throws FunctionEvaluationException, ConvergenceException {
        double step1 = 0.1;
        double value = function.value(input);
        double result = Double.MAX_VALUE;
        double leftResult = (function.value(input + step1) - value) / step1;
        while (Math.abs(leftResult - result) > tolerance) {
            result = leftResult;
            step1 = step1 / 10.;
            leftResult = (function.value(input + step1) - value) / step1;
        }

        step1 = 0.1;
        result = Double.MAX_VALUE;
        double rightResult = (function.value(input + step1) - value) / step1;
        while (Math.abs(rightResult - result) > tolerance) {
            result = rightResult;
            step1 = step1 / 10.;
            rightResult = (function.value(input + step1) - value) / step1;
        }

        if (Math.abs(leftResult - rightResult) < tolerance) {
            return leftResult;
        }
        throw new ConvergenceException();
    }

    /**
     * Computes an approximate derivative.
     * @param function input function mapping a number to a point
     * @param input the input value
     * @param tolerance accuracy required
     * @return approximation of the derivative, as a point
     * @throws org.apache.commons.math.FunctionEvaluationException if the function fails to evaluate for some reason
     */
    @Deprecated
    public static Point2D.Double approximateDerivative(Function<Double, Point2D.Double> function, double input, double tolerance) throws FunctionEvaluationException, ConvergenceException {
        double step1 = 0.1;
        Point2D.Double value = function.getValue(input);
        Point2D.Double result = new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
        Point2D.Double leftResult = scaledDiff(function.getValue(input + step1), value, step1);
        while (result.distance(leftResult) > tolerance) {
            result = leftResult;
            step1 = step1 / 10.;
            leftResult = scaledDiff(function.getValue(input + step1), value, step1);
        }

        step1 = 0.1;
        result = new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
        Point2D.Double rightResult = scaledDiff(function.getValue(input + step1), value, step1);
        while (result.distance(rightResult) > tolerance) {
            result = rightResult;
            step1 = step1 / 10.;
            rightResult = scaledDiff(function.getValue(input + step1), value, step1);
        }

        if (leftResult.distance(rightResult) < tolerance) {
            return leftResult;
        }
        throw new ConvergenceException();
    }

    /** subtract and divide points */
    private static Point2D.Double scaledDiff(Point2D.Double p1, Point2D.Double p2, double divider) {
        return new Point2D.Double((p1.x - p2.x) / divider, (p1.y - p2.y) / divider);
    }
    /** subtract and divide points */
    private static Point3D scaledDiff(Point3D p1, Point3D p2, double divider) {
        return new Point3D((p1.x - p2.x) / divider, (p1.y - p2.y) / divider, (p1.z - p2.z) / divider);
    }

    /**
     * Computes an approximate derivative.
     * @param function input function mapping a number to a point
     * @param input the input value
     * @param tolerance accuracy required
     * @return approximation of the derivative, as a point
     * @throws org.apache.commons.math.FunctionEvaluationException if the function fails to evaluate for some reason
     */
    @Deprecated
    public static Point3D approximateDerivative3D(Function<Double, Point3D> function, double input, double tolerance) throws FunctionEvaluationException, ConvergenceException {
        double step1 = 0.01;
        Point3D value = function.getValue(input);
        Point3D result = new Point3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Point3D leftResult = scaledDiff(function.getValue(input + step1), value, step1);
        while (result.distance(leftResult) > tolerance / 2) {
            result = leftResult;
            step1 = step1 / 10.;
            leftResult = scaledDiff(function.getValue(input + step1), value, step1);
        }

        step1 = 0.01;
        result = new Point3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Point3D rightResult = scaledDiff(function.getValue(input + step1), value, step1);
        while (result.distance(rightResult) > tolerance / 2) {
            result = rightResult;
            step1 = step1 / 10.;
            rightResult = scaledDiff(function.getValue(input + step1), value, step1);
        }

        if (leftResult.distance(rightResult) < tolerance) {
            return leftResult;
        }
        throw new ConvergenceException();
    }

    /**
     * Computes an approximate second derivative.
     * @param function input function mapping a number to a point
     * @param input the input value
     * @param tolerance accuracy required
     * @return approximation of the derivative, as a point
     * @throws org.apache.commons.math.FunctionEvaluationException if the function fails to evaluate for some reason
     */
    @Deprecated
    public static Point2D.Double approximateDerivativeTwo(Function<Double, Point2D.Double> function, double input, double tolerance) throws FunctionEvaluationException, ConvergenceException {
        double step1 = 0.01;
        Point2D.Double value = DerivativeUtils.approximateDerivative(function, input, tolerance);
        Point2D.Double result = new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
        Point2D.Double leftResult = scaledDiff(DerivativeUtils.approximateDerivative(function, input + step1, tolerance), value, step1);
        while (result.distance(leftResult) > tolerance / 2) {
            result = leftResult;
            step1 = step1 / 10.;
            leftResult = scaledDiff(DerivativeUtils.approximateDerivative(function, input + step1, tolerance), value, step1);
        }

        step1 = 0.01;
        result = new Point2D.Double(Double.MAX_VALUE, Double.MAX_VALUE);
        Point2D.Double rightResult = scaledDiff(DerivativeUtils.approximateDerivative(function, input + step1, tolerance), value, step1);
        while (result.distance(rightResult) > tolerance / 2) {
            result = rightResult;
            step1 = step1 / 10.;
            rightResult = scaledDiff(DerivativeUtils.approximateDerivative(function, input + step1, tolerance), value, step1);
        }

        if (leftResult.distance(rightResult) < tolerance) {
            return leftResult;
        }
        throw new ConvergenceException();
    }
}
