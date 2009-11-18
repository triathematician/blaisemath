/**
 * MaxMinDomain.java
 * Created on Sep 3, 2009
 */

package scio.coordinate;

/**
 * <p>
 *   <code>MaxMinDomain</code> is a domain that can be completely specified by
 *   a maximum value and a minimum value
 * </p>
 *
 * @param <C> the underlying type of the values
 * @author Elisha Peterson
 */
public interface MaxMinDomain<C> extends Domain<C> {

    /** @return maximum value in the domain */
    C getMax();

    /** Sets max value of domain */
    void setMax(C max);

    /** @return minimum value of the domain */
    C getMin();

    /** Sets min value of domain */
    void setMin(C min);

    /** @return true if max is included in the domain */
    boolean isMaxInclusive();

    /** Sets whether max is included in domain */
    void setMaxInclusive(boolean maxInclusive);

    /** @return true if the min is included in the domain */
    boolean isMinInclusive();

    /** Sets whether min is included in domain */
    void setMinInclusive(boolean minInclusive);
}
