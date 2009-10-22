/**
 * DifferentiableMultivariateRealFunction.java
 * Created on Sep 25, 2009
 */

package scio.function;

/**
 * <p>
 *   Extension of <code>MultivariateRealFunction</code> representing a differentiable multivariate real function.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface DifferentiableMultivariateRealFunction extends MultivariateRealFunction {

    /** Returns the gradient function. */
    abstract public MultivariateVectorialFunction gradient();

    /** Returns the partial derivative of the function with respect to a point coordinate. */
    abstract public MultivariateRealFunction partialDerivative(int k);

}
