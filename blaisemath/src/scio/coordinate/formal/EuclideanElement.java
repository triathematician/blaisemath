/**
 * EuclideanElement.java
 * Created on May 30, 2008
 */

package scio.coordinate.formal;

import scio.coordinate.*;

/**
 * <p>
 *   Necessary methods for defining an element of Euclidean space (ordered n-vector of doubles). There are no restrictions
 *   on the type of the underlying type representing the elements.
 * </p>
 *
 * @param <C> the data type of the elements
 * @author elisha
 */
public interface EuclideanElement<C> extends InnerProductSpaceElement<C>, MetricSpaceElement<C>, VectorSpaceElement<C> {

    /**
     * Returns length of this element
     * @return length
     */
    public int getLength();

    /**
     * Returns coordinate of this element, by position.
     * @param position the coordinate position
     * @return the value of that coordinate
     */
    public double getCoordinate(int position);

    /**
     * Sets the value of a coordinate.
     * @param position the coordinate position
     * @param value the new value of the coordinate
     */
    public void setCoordinate(int position, double value);

    /**
     * Adds to the value of a coordinate.
     * @param position the coordinate position
     * @param value the value to add to the coordinate
     */
    public void addToCoordinate(int position, double value);

    /**
     * Multiplies a coordinate by a value.
     * @param position the coordinate position
     * @param value the value to multiply the coordinate by
     */
    public void multiplyCoordinateBy(int position, double value);
}
