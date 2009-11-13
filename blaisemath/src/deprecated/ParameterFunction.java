/*
 * ParameterFunction.java
 * Created on Sep 27, 2007, 12:31:58 PM
 */
package deprecated;

import java.util.List;
import org.apache.commons.math.FunctionEvaluationException;

/**
 * <p>
 *  Interface for a function with a single input of type <code>C</code> and a single output of type <code>E</code>.
 *  Also requires a <i>parameter</i> of type <code>D</code> to evaluate.
 * </p>
 *
 * @author Elisha Peterson
 */
@Deprecated
public interface ParameterFunction<C, D, E> {

    /** Returns the value of the function at the given input. */
    public E getValue(C x, D p) throws FunctionEvaluationException;

    /** Returns an array of values of the function at an array of inputs and a single parameter. */
    public List<E> getValue(List<C> xx, D p) throws FunctionEvaluationException;
}
