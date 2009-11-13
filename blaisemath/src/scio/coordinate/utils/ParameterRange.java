/**
 * ParameterRange.java
 * Created on Sep 3, 2009
 */

package scio.coordinate.utils;

/**
 * <p>
 *   <code>ParameterRange</code> is an interface describing a range of values for a single variable type.
 * </p>
 *
 * @param <C> the underlying type of the values
 * @author Elisha Peterson
 */
public interface ParameterRange<C> {

    /** @return maximum value in the range */
    C getMax();

    /** @return minimum value of the range */
    C getMin();

    /** @return true if max is included in the range */
    boolean isMaxInclusive();

    /** @return true if the min is included in the range */
    boolean isMinInclusive();

    void setMax(C max);

    void setMaxInclusive(boolean maxInclusive);

    void setMin(C min);

    void setMinInclusive(boolean minInclusive);
}
