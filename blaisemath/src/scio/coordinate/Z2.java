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
@Deprecated
public class Z2 extends Point {

    @Deprecated
    public Z2() {this(0,0);}
    @Deprecated
    public Z2(int x,int y){super(x,y);}

    @Deprecated
    public boolean equals(Z2 c2) {
        return (c2 instanceof Point) && ((Point)c2).x==x && ((Point)c2).y==y;
    }
    @Deprecated
    public Z2 copy() {
        return new Z2(x,y); 
    }

    @Deprecated
    @Override
    public String toString(){
        return x+","+y;
    }
}
