/*
 * R2.java
 * Created on Sep 14, 2007, 8:10:55 AM
 * 
 * Implements PPoint class as a coordinate system. May work to do this in PPoint instead.
 */

package specto.coordinate;

import java.awt.geom.Point2D;
import specto.Coordinate;

/**
 * This class
 * <br><br>
 * @author Elisha Peterson
 */
public class R2 extends Point2D.Double implements Coordinate {

// PROPERTIES


// CONSTANTS


// CONSTRUCTORS

    /** Default constructor */
    public R2(double x,double y){super(x,y);}

    public boolean equals(Coordinate c2){return (x==((R2)c2).x)&&(y==((R2)c2).y);}


// BEAN PATTERNS: GETTERS & SETTERS


// METHODS:

}
