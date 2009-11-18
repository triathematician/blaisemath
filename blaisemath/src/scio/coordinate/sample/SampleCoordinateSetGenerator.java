/*
 * SampleCoordinateSetGenerator.java
 * Created on Nov 4, 2009
 */

package scio.coordinate.sample;

import java.util.List;

/**
 * <p>
 *  Returns a collection of points of specified type. This should be used, e.g. to provide
 *  several points in a grid, or a list of sample values for a Riemann sum.
 * </p>
 *
 * @param <C> the coordinate type of the output
 * 
 * @author Elisha Peterson
 */
public interface SampleCoordinateSetGenerator<C> {

    /**
     * Return a list of sampled points. Any options as to what this looks
     * like should be controlled by implementing classes.
     *
     * @return an array of sampled points of the given coordinate type
     */
    public List<C> getSamples();

    /**
     * Returns difference between adjacent samples, if relevant to the generator.
     * E.g. in a grid of sampled points, would be the difference between the x
     * and y values.
     *
     * @return object describing the difference between successive samples
     */
    public C getSampleDiff();
}
