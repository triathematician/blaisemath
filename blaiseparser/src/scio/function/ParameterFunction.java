/*
 * ParameterFunction.java
 * Created on Sep 27, 2007, 12:31:58 PM
 */

package scio.function;

import java.util.Vector;

/**
 * <p>
 *  Interface for a function with a single input of type <code>C</code> and a single output of type <code>E</code>.
 *  Also requires a <i>parameter</i> of type <code>D</code> to evaluate.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface ParameterFunction<C,D,E> {

    /** Returns the value of the function at the given input. */
    public E getValue(C x,D p) throws FunctionValueException;

    /** Returns an array of values of the function at an array of inputs and a single parameter. */
    public Vector<E> getValue(Vector<C> xx,D p) throws FunctionValueException;
    
}
