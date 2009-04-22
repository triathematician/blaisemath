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
import scio.function.BoundedFunction;
import scio.function.Function;
import scio.function.FunctionValueException;

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
public class DESolve<C extends EuclideanElement<C>> {
        
    /** Re-calculates the solution curves, using Newton's Method.
     * @param steps     The number of iterations.
     * @param stepSize  The size of path added at each step.
     */
    public Vector<C> calcNewton(Function<C,C> field, C start, int steps, double stepSize) throws FunctionValueException{
        Vector<C> result=new Vector<C>();
        result.add(start);
        C last;
        for(int i=0;i<steps;i++){
            last=result.lastElement();
            result.add(last.plus(field.getValue(last).scaledToLength(stepSize)));
        }
        return result;
    }   
    
    /** Re-calculates the solution curves, using Newton's Method. Instead of using a starting
     * point, uses a starting vector; removes "steps" number of points from the beginning, and
     * adds the same number onto the end of the vector.
     * @param steps     The number of iterations.
     * @param stepSize  The size of path added at each step.
     */
    public Vector<C> calcNewton(Function<C,C> field,Vector<C> flow,int steps,double stepSize) throws FunctionValueException{
        C last;
        for(int i=0;i<steps;i++){
            last=flow.lastElement();
            flow.add(last.plus(getScaledVector(field,last,stepSize)));
            flow.remove(0);
        }
        return flow;
    }
    
    /** Re-calculates solution curves using Runge-Kutta 4th order.
     * @param steps the number of iteration
     * @param stepSize the change in t for each iteration
     */
    public Vector<C> calcRungeKutta4(Function<C,C> field,C start,int steps,double stepSize) throws FunctionValueException{
        Vector<C> result=new Vector<C>();
        result.add(start);
        C k1,k2,k3,k4;
        C last;
        for(int i=0;i<steps;i++){
            last=result.lastElement();
            k1=getScaledVector(field,last,stepSize);
            k2=getScaledVector(field,last.plus(k1.times(0.5)),stepSize);
            k3=getScaledVector(field,last.plus(k2.times(0.5)),stepSize);
            k4=getScaledVector(field,last.plus(k3),stepSize);
            result.add(last.plus(k1.times(1/6.0)).plus(k2.times(1/3.0)).plus(k3.times(1/3.0)).plus(k4.times(1/6.0)));
            
        }
        return result;
    }
    
    /** Returns vector pointing in the direction of the field. */
    public C getScaledVector(Function<C,C> field,C point,double size) throws FunctionValueException{
        return field.getValue(point).scaledToLength(size);
    }
    
    /** Returns vector pointing in direction of the field, multiplied by a fixed factor that depends only on the underlying vector field. */
    public C getMultipliedVector(BoundedFunction<C,C> field,C point,double scaleFactor) throws FunctionValueException{
        return field.getValue(point).times(scaleFactor);
    }
    
    
    // STATIC SOLVERS
    
    public static final DESolve<R1> R1S = new DESolve<R1>();
    public static final DESolve<R2> R2S = new DESolve<R2>();
    public static final DESolve<R3> R3S = new DESolve<R3>();
}
