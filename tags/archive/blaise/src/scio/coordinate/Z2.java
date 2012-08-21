/**
 * Z2.java
 * Created on Apr 8, 2008
 */

package scio.coordinate;

import java.awt.Point;

/**
 * This class represents a point with integer coordinates.
 * @author Elisha Peterson
 */
public class Z2 extends Point implements Coordinate {

    public Z2() {this(0,0);}
    public Z2(int x,int y){super(x,y);}

    public boolean equals(Coordinate c2) {
        return (c2 instanceof Point) && ((Point)c2).x==x && ((Point)c2).y==y;
    }
    public Coordinate copy() {
        return new Z2(x,y); 
    }
    
    @Override
    public String toString(){
        return x+","+y;
    }
}
