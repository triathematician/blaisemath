/*
 * Function.java
 * Created on Sep 27, 2007, 12:31:58 PM
 */

package scio.function;

import java.util.Vector;

/**
 * <p>
 *  Interface for a function with a single input of type <code>C</code> and a single output of type <code>D</code>.
 * </p>
 * 
 * @author Elisha Peterson
 */
public interface Function<C,D> {

    /** Returns the value of the function at the given input. */
    public D getValue(C x) throws FunctionValueException;

    /** Returns an array of values of the function at an array of inputs. */
    public Vector<D> getValue(Vector<C> xx) throws FunctionValueException;
    
}
