/**
 * MetricSpaceElement.java
 * Created on Mar 24, 2008
 */

package org.bm.blaise.scio.coordinate.formal;

/**
 * <p>
 *   This interface defines what is required for metric spaces. There are no restrictions
 *   on the type of the underlying type representing the elements.
 * </p>
 *
 * @param <C> data type for the metric space elements
 * @author Elisha Peterson
 */
public interface MetricSpaceElement<C> {

    /**
     * Computes distance between this element and another element.
     * @param pt second metric space element
     * @return distance between this element and that supplied
     */
    public double distanceTo(C pt);
}
