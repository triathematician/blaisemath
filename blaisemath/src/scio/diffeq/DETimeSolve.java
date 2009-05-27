/**
 * DESolve.java
 * Created on Nov 25, 2008
 */

package scio.diffeq;

import java.util.Vector;
import scio.coordinate.EuclideanElement;
import scio.coordinate.R1;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.function.Function;
import scio.function.FunctionValueException;

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
public abstract class DETimeSolve<C extends EuclideanElement<C>, D extends EuclideanElement<D>> {
        
    /** Recalculates solution curves using Newton's Method (time-dependent function). */
    public Vector<C> calcNewton(Function<D,C> field, C start, int steps, double stepSize) throws FunctionValueException {
        Vector<C> result=new Vector<C>();
        result.add(start);
        C last;
        for(int i=0;i<steps;i++){
            last=result.lastElement();
            result.add(last.plus(getScaledVector(field,i*stepSize,last,stepSize)));
        }
        return result;
    }
    
    /** Recalculates solution curves using Newton's Method (time-dependent function. */
     public Vector<C> calcNewton(Function<D,C> field, Vector<C> flow, int steps, double stepSize) throws FunctionValueException {
        C last;
        for(int i=0;i<steps;i++){
            last=flow.lastElement();
            flow.add(last.plus(getScaledVector(field,i*stepSize,last,stepSize)));
            flow.remove(0);
        }
        return flow;
    }
     
    /** Returns vector pointing in the direction of the field. This is abstract because the implementation of the field's "get" method will depend on the coordinate system. */
    public abstract C getScaledVector(Function<D,C> field,double time,C point,double size) throws FunctionValueException;
    /** Returns vector pointing in direction of the field, multiplied by a fixed factor that depends only on the underlying vector field. */
    public abstract C getMultipliedVector(Function<D, C> field, double time, C point, double size) throws FunctionValueException;

    
    
    // STATIC SOLVERS
    
    /** This subclass implements the time-dependent solver for a 1D coordinate system, providing solutions
     * to equations of the form x'=f(x,t).
     */
    public static final DETimeSolve<R1,R2> R1T = new DETimeSolve<R1,R2>() {
        @Override
        public R1 getScaledVector(Function<R2, R1> field, double time, R1 point, double size) throws FunctionValueException {
            return field.getValue(new R2(point.x,time)).scaledToLength(size);
        }
        @Override
        public R1 getMultipliedVector(Function<R2, R1> field, double time, R1 point, double size) throws FunctionValueException {
            return field.getValue(new R2(point.x,time)).times(size);
        }        
    };
    
    /** This subclass implements the time-dependent solver in the 2D coordinate system, making it suitable for
     * systems of differential equations of the form x'=f(x,y,t), y'=g(x,y,t).
     */
    public static final DETimeSolve<R2,R3> R2T = new DETimeSolve<R2,R3>() {
        @Override
        public R2 getScaledVector(Function<R3, R2> field, double time, R2 point, double size) throws FunctionValueException {
            return field.getValue(new R3(point.x,point.y,time)).scaledToLength(size);
        }
        @Override
        public R2 getMultipliedVector(Function<R3, R2> field, double time, R2 point, double size) throws FunctionValueException {
            return field.getValue(new R3(point.x,point.y,time)).multipliedBy(size);
        }        
    };
}
