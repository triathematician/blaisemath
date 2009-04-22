/**
 * Vector2D.java
 * Created on Sep 22, 2008
 */

package specto.euclidean3;

import scio.coordinate.R3;

/**
 * Represents a two-dimensional vector with arbitrary endpoints. Implemented using Segment2D,
 * but with a corresponding 
 * @author Elisha Peterson
 */
public class Vector3D extends Segment3D {
    
    public Vector3D(){
        super();
        this.style.setValue(LINE_VECTOR);
    }
    
    public void setVector(R3 vec) {
        setPoint2(getPoint1().plus(vec));
    }
    
    public R3 getVector() {
        return getPoint2().minus(getPoint1());
    }

    /** Forces vector to be a unit vector. */
    public static class Unit extends Vector3D {
        boolean editing=false;
        public Unit() {
            super();
        }
        
        @Override public void setPoint1(R3 p1) {
            super.setPoint1(p1);
            setPoint2(getPoint2());
        }
        @Override public void setPoint2(R3 p2) {
            setVector(p2.minus(getPoint1()));
        }
        @Override public void setVector(R3 vec) {
            point2.setPoint(getPoint1().plus(vec.normalized()));
        }
    }
}
