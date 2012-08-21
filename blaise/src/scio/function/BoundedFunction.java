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
public interface BoundedFunction<C,D> extends Function<C,D> {
    public D minValue();
    public D maxValue();
}
