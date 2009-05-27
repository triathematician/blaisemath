/**
 * InnerProductSpaceElement.java
 * Created on May 30, 2008
 */

package scio.coordinate;

import scio.coordinate.*;

/**
 * This class defines an inner product, required for inner product spaces.
 * 
 * @author elisha
 */
public interface InnerProductSpaceElement<C> extends Coordinate {
    double dotProduct(C p2) throws ArrayIndexOutOfBoundsException;
}
