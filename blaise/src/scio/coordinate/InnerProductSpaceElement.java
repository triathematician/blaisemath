/**
 * InnerProductSpaceElement.java
 * Created on May 30, 2008
 */

package scio.coordinate;

/**
 * This class defines an inner product, required for inner product spaces.
 * 
 * @author elisha
 */
public interface InnerProductSpaceElement extends Coordinate {
    double dotProduct(InnerProductSpaceElement p2) throws ArrayIndexOutOfBoundsException;
}
