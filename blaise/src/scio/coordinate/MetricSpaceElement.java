/**
 * MetricSpaceElement.java
 * Created on Mar 24, 2008
 */

package scio.coordinate;

/**
 * This interface defines what is required for metric spaces... a distanceTo function.
 * 
 * @author Elisha Peterson
 */
public interface MetricSpaceElement extends Coordinate {
    public double distanceTo(Coordinate p2);
}
