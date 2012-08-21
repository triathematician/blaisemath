/**
 * VectorSpaceElement.java
 * Created on Mar 24, 2008
 */
package org.bm.blaise.scio.coordinate.formal;

/**
 * <p>
 *   This class defines what is required for vector spaces. There are no restrictions
 *   on the type of the underlying type representing the elements.
 * </p>
 *
 * @param <C> the data type of the elements
 * @author Elisha Peterson
 */
public interface VectorSpaceElement<C> {

    /**
     * Returns the zero element in this vector space.
     * @return origin in the vector space
     */
    public C zero();

    /**
     * Computes the magnitude of this vector
     * @return magnitude of the vector
     */
    public double magnitude();

    /**
     * Returns a vector whose magnitude is 1, in the same direction as this vector.
     * @return new vector as described; if magnitude is zero, returns the vector back
     */
    public C normalized();

    /**
     * Computes sum of this vector with supplied vector. DOES NOT CHANGE VECTOR.
     * @param pt vector to add
     * @return new vector that is the sum of this with the supplied vector
     */
    public C plus(C pt);

    /**
     * Computes sum of this vector with supplied vector. DOES NOT CHANGE VECTOR.
     * @param pt vector to add
     * @return new vector that is the sum of this with the supplied vector
     */
    public C minus(C pt);

    /**
     * Translates this vector by the supplied vector. CHANGES THIS VECTOR.
     * @param pt vector to translate by
     * @return copy of this vector
     */
    public C translateBy(C pt);

    /**
     * Computes product of this vector with supplied scalar. DOES NOT CHANGE VECTOR.
     * @param scalar scalar to multiply
     * @return new vector that is the product of this vector with supplied scalar
     */
    public C timesScalar(double scalar);

    /**
     * Computes product of this vector with supplied scalar. CHANGES THIS VECTOR.
     * @param scalar scalar to multiply
     * @return copy of this vector
     */
    public C multiplyBy(double scalar);
}
