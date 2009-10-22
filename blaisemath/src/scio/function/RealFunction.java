/**
 * RealFunction.java
 * Created on Jul 29, 2009
 */

package scio.function;

import org.apache.commons.math.analysis.UnivariateRealFunction;

/**
 * <p>
 *   <code>RealFunction</code> ensures that a class supports the apache commons version of a function
 *   as well as the hard-coded version
 * </p>
 *
 * @author Elisha Peterson
 */
@Deprecated
public interface RealFunction extends Function<Double, Double>, UnivariateRealFunction {
}
