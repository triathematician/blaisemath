/**
 * MaxMinDomainSupport.java
 * Created on Sep 3, 2009
 */

package scio.coordinate;

/**
 * <p>
 *   <code>MaxMinDomainSupport</code> is the default implementation of <code>MaxMinDomain</code>.
 *   If <code>C</code> is a <code>Comparable</code>, uses the comparison to determine whether another
 *   object is in the domain; otherwise, assumes nothing but the max and min are in the domain.
 * </p>
 *
 * @param C the coordinate type for the range
 * @author Elisha Peterson
 */
public class MaxMinDomainSupport<C> implements MaxMinDomain<C> {

    protected C min;
    protected C max;
    protected boolean minInclusive = true;
    protected boolean maxInclusive = true;

    /**
     * Sets the range to be between the min value and the max value.
     * @param domain another domain to copy
     */
    public MaxMinDomainSupport(MaxMinDomain<C> domain) {
        this(domain.getMin(), domain.isMinInclusive(), domain.getMax(), domain.isMaxInclusive());
    }

    /**
     * Sets the range to be between the min value and the max value.
     * @param min minimum value
     * @param max maximum value
     */
    public MaxMinDomainSupport(C min, C max) {
        setMin(min);
        setMax(max);
    }

    /**
     * Sets the range to be between the min value and the max value.
     * @param min minimum value
     * @param minInclusive true if min is included in the range
     * @param max maximum value
     * @param maxInclusive true if max is included in the range
     */
    public MaxMinDomainSupport(C min, boolean minInclusive, C max, boolean maxInclusive) {
        setMin(min);
        setMax(max);
        setMinInclusive(minInclusive);
        setMaxInclusive(maxInclusive);
    }

    public C getMax() {
        return max;
    }

    public void setMax(C max) {
        this.max = max;
    }

    public boolean isMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(boolean maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public C getMin() {
        return min;
    }

    public void setMin(C min) {
        this.min = min;
    }

    public boolean isMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(boolean minInclusive) {
        this.minInclusive = minInclusive;
    }

    public boolean contains(C coord) {
        if (coord == max) {
            return maxInclusive;
        } else if (coord == min) {
            return minInclusive;
        } else if (coord instanceof Comparable) {
            Comparable c = (Comparable) coord;
            return (c.compareTo(min) > 0) && (c.compareTo(max) < 0);
        }
        return false;
    }

    
}
