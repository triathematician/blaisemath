/**
 * I2.java
 * Created on Apr 8, 2008
 */

package scio.coordinate;

import java.awt.Point;

/**
 *
 * @author Elisha Peterson
 */
public class I2 extends Point implements Coordinate {
    public I2(int x,int y){super(x,y);}

    public boolean equals(Coordinate c2) {
        return (c2 instanceof Point) && ((Point)c2).x==x && ((Point)c2).y==y;
    }
}
