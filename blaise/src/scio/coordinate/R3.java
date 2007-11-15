/*
 * R3.java
 * Created on Sep 14, 2007, 8:15:59 AM
 */

package scio.coordinate;

import scio.coordinate.Coordinate;

/**
 * This class handles three-dimensional coordinates in space.
 * <br><br>
 * @author Elisha Peterson
 */
public class R3 implements Coordinate {

// PROPERTIES
    
    public double x,y,z;


// CONSTANTS


// CONSTRUCTORS

    /** Default constructor */
    public R3(){}

    public boolean equals(Coordinate c2){return (x==((R3)c2).x)&&(y==((R3)c2).y)&&(z==((R3)c2).z);}


// BEAN PATTERNS: GETTERS & SETTERS


// METHODS:

}
