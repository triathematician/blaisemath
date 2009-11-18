/*
 * RandomPointGenerator.java
 * Created on Nov 4, 2009
 */

package scio.coordinate;

/**
 * <p>
 *  Generates random points in specified coordinate system.
 * </p>
 *
 * @param <C> the coordinate type of the plottable
 * 
 * @author Elisha Peterson
 */
public interface RandomCoordinateGenerator<C> {

    /**
     * Return a random point.
     * @return a randomly chosen point
     */
    public C randomValue();

}
