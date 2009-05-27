/**
 * VectorSpaceElement.java
 * Created on Mar 24, 2008
 */

package scio.coordinate;

import scio.coordinate.*;

/**
 * This class defines what is required for vector spaces.
 * 
 * @author Elisha Peterson
 */
public interface VectorSpaceElement<C> extends Coordinate {
    public C zero();
    public double magnitude();
    public C plus(C p2) throws ArrayIndexOutOfBoundsException;
    public C minus(C p2) throws ArrayIndexOutOfBoundsException;
    public C times(double d);
    public C scaledToLength(double d);
    
    public C translateBy(C p2) throws ArrayIndexOutOfBoundsException;
    public C multiplyBy(double d);
}
