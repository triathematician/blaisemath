/*
 * R3.java
 * Created on Sep 14, 2007, 8:15:59 AM
 */

package scio.coordinate;

/**
 * This class handles three-dimensional coordinates in space.
 * <br><br>
 * @author Elisha Peterson
 */
public class R3 extends Euclidean {
    
    /** Default constructor. */
    public R3(){super(3);}
    
    /** Construct with coordinates. */
    public R3(double x, double y, double z){
        this();
        setTo(x, y, z);
    }
    

    // GETTERS & SETTERS
    
    public double getX(){ return getElement(0); }
    public double getY(){ return getElement(1); }
    public double getZ(){ return getElement(2); }
    
    public void setX(double x){ setElement(0,x); }
    public void setY(double y){ setElement(1,y); }
    public void setZ(double z){ setElement(2,z); }
    
    public void setTo(double x, double y, double z){ setX(x); setY(y); setZ(z); }
    
    
    // VECTOR SPACE METHODS    

    public VectorSpaceElement zero() { return new R3(0,0,0); }
    public VectorSpaceElement plus(VectorSpaceElement p2) {
        R3 p2r3 = (R3) p2;
        return new R3( getX() + p2r3.getX(), getY() + p2r3.getY(), getZ() + p2r3.getZ() );
    }
    public VectorSpaceElement minus(VectorSpaceElement p2) {
        R3 p2r3 = (R3) p2;
        return new R3( getX() - p2r3.getX(), getY() - p2r3.getY(), getZ() - p2r3.getZ() );
    }
    public VectorSpaceElement times(double d) {
        return new R3( d * getX(), d * getY(), d * getZ() );
    }
    
    
    // METHODS FOR VECTORS IN R3
    
    /** Projects to plane, returning the first two coordinates only */
    public R2 projectXY() { return new R2(getX(), getY()); }

    /** Returns the cross product of two 3-vectors */
    public R3 cross(R3 p2) {
        return new R3 (getY()*p2.getZ()-getZ()*p2.getY(), getZ()*p2.getX()-getX()*p2.getZ(), getX()*p2.getY()-getY()*p2.getX());
    }

    /** Returns the (scalar) triple product of three 3-vectors */
    public static double tripleProduct(R3 p1, R3 p2, R3 p3) {
        return p1.cross(p2).dotProduct(p3);
    }
}
