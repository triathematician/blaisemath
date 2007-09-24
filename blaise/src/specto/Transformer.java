/*
 * Transformer.java
 * Created on Sep 14, 2007, 7:46:26 AM
 */

package specto;

/**
 * This interface is more than meets the eye. Okay, well it handles transformations between
 * two underlying geometries, geometry "from" and geometry "to".
 * <br><br>
 * @author ae3263
 */
public interface Transformer {
    public Coordinate transform(Coordinate c);
    public Coordinate inverseTransform(Coordinate c);
    public Transformer getInverseTransform();
}
