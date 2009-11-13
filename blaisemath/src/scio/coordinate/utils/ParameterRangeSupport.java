/**
 * ParameterRangeSupport.java
 * Created on Sep 3, 2009
 */

package scio.coordinate.utils;

/**
 * <p>
 *   <code>ParameterRangeSupport</code> stores a range of values as an interval between a minimum coord and a maximum coord.
 *   This is the default implementation of <code>ParameterRange</code>
 * </p>
 *
 * @param C the coordinate type for the range
 * @author Elisha Peterson
 */
public class ParameterRangeSupport<C> implements ParameterRange<C> {

    C min;
    C max;
    boolean minInclusive;
    boolean maxInclusive;

    /** Sets the range to be between the min value and the max value.
     * @param min minimum value
     * @param minInclusive true if min is included in the range
     * @param max maximum value
     * @param maxInclusive true if max is included in the range
     */
    public ParameterRangeSupport(C min, boolean minInclusive, C max, boolean maxInclusive) {
        this.min = min;
        this.minInclusive = minInclusive;
        this.max = max;
        this.maxInclusive = maxInclusive;
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

    
}
