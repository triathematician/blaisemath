/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * VectorSpaceElement.java
 * Created on Mar 24, 2008
 */

package scio.coordinate;

/**
 *
 * @author Elisha Peterson
 */
public interface VectorSpaceElement extends Coordinate {
    public VectorSpaceElement zero();
    public VectorSpaceElement plus(VectorSpaceElement p2);
    public VectorSpaceElement minus(VectorSpaceElement p2);
    public VectorSpaceElement times(double d);
}
