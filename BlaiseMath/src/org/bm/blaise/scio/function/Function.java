/*
 * Function.java
 * Created on Sep 27, 2007, 12:31:58 PM
 */

package org.bm.blaise.scio.function;

import java.util.List;

/**
 * <p>
 *  Interface for a function with a single input of type <code>C</code> and a single output of type <code>D</code>.
 * </p>
 * 
 * @author Elisha Peterson
 */
@Deprecated
public interface Function<C,D> {

    /** Returns the value of the function at the given input. */
    public D getValue(C x) throws org.apache.commons.math.FunctionEvaluationException;

    /** Returns an array of values of the function at an array of inputs. */
    @Deprecated
    public List<D> getValue(List<C> xx) throws org.apache.commons.math.FunctionEvaluationException;
    
}
