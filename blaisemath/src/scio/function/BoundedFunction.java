/*
 * BoundedFunction.java
 * Created on Sep 27, 2007, 12:31:58 PM
 */

package scio.function;

/**
 * <p>
 *  Interface for a function with a single input of type <code>C</code> and a single output of type <code>D</code>.
 *  Also able to describe the maximum and minimum values achieved by the function.
 * </p>
 *
 * @author Elisha Peterson
 */
@Deprecated
public interface BoundedFunction<C,D> extends Function<C,D> {

    /** Returns the function's minimum value. */
    @Deprecated
    public D minValue();
    
    /** Returns the function's maximum value. */
    @Deprecated
    public D maxValue();
    
}
