/**
 * MultivariateRealFunction.java
 * Created on Sep 3, 2009
 */

package scio.function;

import org.apache.commons.math.FunctionEvaluationException;

/**
 * <p>
 *   <code>MultivariateRealFunction</code> is an interface representing a multivariate real function.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface MultivariateVectorialFunction {

    /** Compute the value for the function at the given point. */
    public double[] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException;

}
