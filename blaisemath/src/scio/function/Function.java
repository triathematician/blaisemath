/*
 * Function.java
 * Created on Sep 27, 2007, 12:31:58 PM
 */

package scio.function;

import java.util.Vector;

/**
 * Basic interface for a particular function. Requires an input and an output.<br><br>
 * 
 * @author ae3263
 */
public interface Function<C,D> {
    public D getValue(C x) throws FunctionValueException;
    public Vector<D> getValue(Vector<C> xx) throws FunctionValueException;
//    public D minValue();
//    public D maxValue();
}
