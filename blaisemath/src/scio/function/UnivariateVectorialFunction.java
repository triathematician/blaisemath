/**
 * UnivariateVectorialFunction.java
 * Created on Sep 3, 2009
 */

package scio.function;

import org.apache.commons.math.FunctionEvaluationException;

/**
 * <p>
 *   <code>UnivariateVectorialFunction</code> is an interface representing a univariate vectorial function,
 *   ie. a function with one input and multiple outputs.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface UnivariateVectorialFunction {

    /** Compute the value for the function. */
    public double[] value(double x) throws FunctionEvaluationException;

}
