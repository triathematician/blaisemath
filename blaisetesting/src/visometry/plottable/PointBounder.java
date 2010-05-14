/*
 * PointBounder.java
 * Created Apr 30, 2010
 */

package visometry.plottable;

/**
 * Used to enforce a bounding system on a point, so that the point cannot be moved outside of
 * some restricted region.
 * @param <C> the type of coordinate being used
 *
 * @author Elisha Peterson
 */
public interface PointBounder<C> {

    /**
     * Finds and returns the closest value within the bounds of the class.
     * @param coord starting coordinate
     * @return coordinate within the class's bounds that is closest to the provided coordinate
     */
    public C getConstrainedValue(C coord);

}
