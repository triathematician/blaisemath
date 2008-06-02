/**
 * VectorSpaceElement.java
 * Created on Mar 24, 2008
 */

package scio.coordinate;

/**
 * This class defines what is required for vector spaces.
 * 
 * @author Elisha Peterson
 */
public interface VectorSpaceElement extends Coordinate {
    public VectorSpaceElement zero();
    public VectorSpaceElement plus(VectorSpaceElement p2) throws ArrayIndexOutOfBoundsException;
    public VectorSpaceElement minus(VectorSpaceElement p2) throws ArrayIndexOutOfBoundsException;
    public VectorSpaceElement times(double d);
    
    public VectorSpaceElement translateBy(VectorSpaceElement p2) throws ArrayIndexOutOfBoundsException;
    public VectorSpaceElement multiplyBy(double d);
}
