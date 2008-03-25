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
    public Coordinate zero();
    public Coordinate plus(Coordinate p2);
    public Coordinate minus(Coordinate p2);
    public Coordinate times(double d);
}
