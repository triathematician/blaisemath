/*
 * Function.java
 * Created on Sep 27, 2007, 12:31:58 PM
 */

package scio.function;

import java.util.Vector;

/**
 * Basic interface for a particular function. Requires an input with parameters and an output.<br><br>
 * 
 * @author ae3263
 */
public interface ParameterFunction<C,D,E> {
    public E getValue(C x,D p) throws FunctionValueException;
    public Vector<E> getValue(Vector<C> xx,Vector<D> pp) throws FunctionValueException;
    public E minValue();
    public E maxValue();
//    public int getNumVariables();
//    public int getNumParameters();
//    public int getNumOutputs();
}
