/*
 * Coordinate.java
 * Created on Sep 14, 2007, 7:45:27 AM
 */

package scio.coordinate;

/**
 * This interface describes methods required by coordinate systems for visual geometries,
 * whether the underlying window or some other geometry. Visometries and Transformations
 * compute changes between these systems. If I can't come up with anything useful to put
 * here, classes which use "Coordinate" should be changed to use "Vector" instead.
 * <br><br>
 * @author ae3263
 */
@Deprecated
public interface Coordinate<C> {
    @Deprecated
    public boolean equals(Coordinate c2);
    @Deprecated
    public C copy();
}
