/**
 * InnerProductSpaceElement.java
 * Created on May 30, 2008
 */

package scio.coordinate.formal;

/**
 * <p>
 *   This class defines an inner product, required for inner product spaces. There are no restrictions
 *   on the type of the underlying type representing the elements.
 * </p>
 *
 * @param <C> the data type of the elements
 * @author elisha
 */
public interface InnerProductSpaceElement<C> {

    /**
     * Computes dot product with another element.
     * @param pt the other element of the space
     * @return dot product with supplied element
     */
    public double dotProduct(C pt);
}
