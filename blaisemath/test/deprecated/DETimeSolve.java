/**
 * DESolve.java
 * Created on Nov 25, 2008
 */
package deprecated;

import scio.function.Function;
import java.util.Vector;
import scio.coordinate.formal.EuclideanElement;

/**
 * <p>
 * This class contains methods used for solving time-dependent differential equations.
 * In particular, it contains implementations of Newton's algorithm and the Runge-Kutta method.
 * </p>
 * <p>
 * The parameter C is the default coordinate for the solver; the parameter D is the coordinate that includes
 * a time component.
 * </p>
 * @author Elisha Peterson
 */
@Deprecated
public abstract class DETimeSolve<C extends EuclideanElement<C>, D extends EuclideanElement<D>> {

    /** Recalculates solution curves using Newton's Method (time-dependent function). */
    @Deprecated
    public Vector<C> calcNewton(Function<D, C> field, C start, int steps, double stepSize) throws org.apache.commons.math.MathException {
        Vector<C> result = new Vector<C>();
        result.add(start);
        C last;
        for (int i = 0; i < steps; i++) {
            last = result.lastElement();
            result.add(last.plus(getScaledVector(field, i * stepSize, last, stepSize)));
        }
        return result;
    }

    /** Recalculates solution curves using Newton's Method (time-dependent function. */
    @Deprecated
    public Vector<C> calcNewton(Function<D, C> field, Vector<C> flow, int steps, double stepSize) throws org.apache.commons.math.MathException {
        C last;
        for (int i = 0; i < steps; i++) {
            last = flow.lastElement();
            flow.add(last.plus(getScaledVector(field, i * stepSize, last, stepSize)));
            flow.remove(0);
        }
        return flow;
    }

    /** Returns vector pointing in the direction of the field. This is abstract because the implementation of the field's "get" method will depend on the coordinate system. */
    @Deprecated
    public abstract C getScaledVector(Function<D, C> field, double time, C point, double size) throws org.apache.commons.math.MathException;

    /** Returns vector pointing in direction of the field, multiplied by a fixed factor that depends only on the underlying vector field. */
    @Deprecated
    public abstract C getMultipliedVector(Function<D, C> field, double time, C point, double size) throws org.apache.commons.math.MathException;
//    // STATIC SOLVERS
//
//    /** This subclass implements the time-dependent solver for a 1D coordinate system, providing solutions
//     * to equations of the form x'=f(x,t).
//     */
//    public static final DETimeSolve<R1,EuclideanPoint2D> R1T = new DETimeSolve<R1,EuclideanPoint2D>() {
//        @Override
//        public R1 getScaledVector(Function<EuclideanPoint2D, R1> field, double time, R1 point, double size) throws FunctionValueException {
//            return field.getValue(new EuclideanPoint2D(point.x,time)).scaledToLength(size);
//        }
//        @Override
//        public R1 getMultipliedVector(Function<EuclideanPoint2D, R1> field, double time, R1 point, double size) throws FunctionValueException {
//            return field.getValue(new EuclideanPoint2D(point.x,time)).timesScalar(size);
//        }
//    };
//
//    /** This subclass implements the time-dependent solver in the 2D coordinate system, making it suitable for
//     * systems of differential equations of the form x'=f(x,y,t), y'=g(x,y,t).
//     */
//    public static final DETimeSolve<EuclideanPoint2D,EuclideanPoint3D> R2T = new DETimeSolve<EuclideanPoint2D,EuclideanPoint3D>() {
//        @Override
//        public EuclideanPoint2D getScaledVector(Function<EuclideanPoint3D, EuclideanPoint2D> field, double time, EuclideanPoint2D point, double size) throws FunctionValueException {
//            return field.getValue(new EuclideanPoint3D(point.x,point.y,time)).scaledToLength(size);
//        }
//        @Override
//        public EuclideanPoint2D getMultipliedVector(Function<EuclideanPoint3D, EuclideanPoint2D> field, double time, EuclideanPoint2D point, double size) throws FunctionValueException {
//            return field.getValue(new EuclideanPoint3D(point.x,point.y,time)).multipliedBy(size);
//        }
//    };
}
