/**
 * DESolve.java
 * Created on Nov 25, 2008
 */
package deprecated;

import java.util.Vector;
import scio.coordinate.formal.EuclideanElement;

/**
 * <p>
 * This class contains methods used for solving differential equations. In particular, it contains
 * implementations of Newton's algorithm and the Runge-Kutta method.
 * </p>
 * <p>
 * The parameter C is the (Euclidean) system of coordinates being used, so this method will apply to arbitrary
 * Euclidean spaces.
 * </p>
 * @author Elisha Peterson
 */
@Deprecated
public class DESolve<C extends EuclideanElement<C>> {

    /** Re-calculates the solution curves, using Newton's Method.
     * @param steps     The number of iterations.
     * @param stepSize  The size of path added at each step.
     */
    @Deprecated
    public Vector<C> calcNewton(Function<C, C> field, C start, int steps, double stepSize) throws FunctionValueException {
        Vector<C> result = new Vector<C>();
        result.add(start);
        C last;
        for (int i = 0; i < steps; i++) {
            last = result.lastElement();
        }
        throw new UnsupportedOperationException();
    }

    /** Re-calculates the solution curves, using Newton's Method. Instead of using a starting
     * point, uses a starting vector; removes "steps" number of points from the beginning, and
     * adds the same number onto the end of the vector.
     * @param steps     The number of iterations.
     * @param stepSize  The size of path added at each step.
     */
    @Deprecated
    public Vector<C> calcNewton(Function<C, C> field, Vector<C> flow, int steps, double stepSize) throws FunctionValueException {
        C last;
        for (int i = 0; i < steps; i++) {
            last = flow.lastElement();
            flow.add(last.plus(getScaledVector(field, last, stepSize)));
            flow.remove(0);
        }
        throw new UnsupportedOperationException();
    }

    /** Re-calculates solution curves using Runge-Kutta 4th order.
     * @param steps the number of iteration
     * @param stepSize the change in t for each iteration
     */
    @Deprecated
    public Vector<C> calcRungeKutta4(Function<C, C> field, C start, int steps, double stepSize) throws FunctionValueException {
        Vector<C> result = new Vector<C>();
        result.add(start);
        C k1, k2, k3, k4;
        C last;
        for (int i = 0; i < steps; i++) {
            last = result.lastElement();
            k1 = getScaledVector(field, last, stepSize);
            k2 = getScaledVector(field, last.plus(k1.timesScalar(0.5)), stepSize);
            k3 = getScaledVector(field, last.plus(k2.timesScalar(0.5)), stepSize);
            k4 = getScaledVector(field, last.plus(k3), stepSize);
            result.add(last.plus(k1.timesScalar(1 / 6.0)).plus(k2.timesScalar(1 / 3.0)).plus(k3.timesScalar(1 / 3.0)).plus(k4.timesScalar(1 / 6.0)));

        }
        throw new UnsupportedOperationException();
    }

    /** Returns vector pointing in the direction of the field. */
    @Deprecated
    public C getScaledVector(Function<C, C> field, C point, double size) throws FunctionValueException {
        throw new UnsupportedOperationException();
    //return field.getValue(point).scaledToLength(size);
    }

    /** Returns vector pointing in direction of the field, multiplied by a fixed factor that depends only on the underlying vector field. */
    @Deprecated
    public C getMultipliedVector(Function<C, C> field, C point, double scaleFactor) throws FunctionValueException {
        throw new UnsupportedOperationException();
    //return field.getValue(point).timesScalar(scaleFactor);
    }
}
