/**
 * Vector2D.java
 * Created on Sep 22, 2008
 */

package specto.euclidean2;

import javax.swing.event.ChangeEvent;
import scio.coordinate.R2;

/**
 * Represents a two-dimensional vector with arbitrary endpoints. Implemented using Segment2D,
 * but with a corresponding 
 * @author Elisha Peterson
 */
public class Vector2D extends Segment2D {
    
    public Vector2D(){
        super();
        this.style.setValue(LINE_VECTOR);
    }
    
    public void setVector(R2 vec) {
        setPoint2(getPoint1().plus(vec));
    }
    
    public R2 getVector() {
        return getPoint2().minus(getPoint1());
    }

    /** Forces vector to be a unit vector. */
    public static class Unit extends Vector2D {
        boolean editing=false;
        public Unit() {
            super();
        }
        
        @Override public void setPoint1(R2 p1) {
            super.setPoint1(p1);
            setPoint2(getPoint2());
        }
        @Override public void setPoint2(R2 p2) {
            setVector(p2.minus(getPoint1()));
        }
        @Override public void setVector(R2 vec) {
            point2.setPoint(getPoint1().plus(vec.normalized()));
        }
    }
}
