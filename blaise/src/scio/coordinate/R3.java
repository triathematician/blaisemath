/*
 * R3.java
 * Created on Sep 14, 2007, 8:15:59 AM
 */

package scio.coordinate;

import java.text.NumberFormat;

/**
 * This class handles three-dimensional coordinates in space.
 * <br><br>
 * @author Elisha Peterson
 */
public class R3 implements EuclideanElement {
    
    public final static R3 ZERO = new R3();
    public final static R3 I = new R3(1,0,0);
    public final static R3 J = new R3(0,1,0);
    public final static R3 K = new R3(0,0,1);
    
    
    public double x,y,z;
    
    /** Default constructor. */
    public R3(){x=0; y=0; z=0;}
    
    /** Construct with coordinates. */
    public R3(double x, double y, double z){
        this();
        setTo(x, y, z);
    }
    

    // GETTERS & SETTERS
    
    public double getX(){ return x; }
    public double getY(){ return y; }
    public double getZ(){ return z; }

    
    public void setX(double x){ this.x=x; }
    public void setY(double y){ this.y=y; }
    public void setZ(double z){ this.z=z; }
    
    public void setTo(double x, double y, double z){ setX(x); setY(y); setZ(z); }
        
    
    // ADJUSTMENTS TO THIS VECTOR
    
    public double magnitudeSq() { return getX()*getX()+getY()*getY()+getZ()*getZ(); }
    public double magnitude() { return Math.sqrt(magnitudeSq()); }    
    public R3 normalized() { return times(1/magnitude()); }
    public R3 scaledToLength(double length){ double m = length/magnitude(); return new R3(getX()*m, getY()*m, getZ()*m); }
    public R3 multipliedBy(double length){ return new R3(getX()*length, getY()*length, getZ()*length); }
    
    // OPERATIONS WITH MULTIPLE VECTORS
    
    public R3 times(double d) { return new R3(d*x, d*y, d*z); }
    public R3 plus(R3 pt) { return new R3(x+pt.x, y+pt.y, z+pt.z); }
    public R3 plus(double bx,double by,double bz) { return new R3(x+bx,y+by,z+bz); }
    public R3 minus(R3 pt) { return new R3(x-pt.x, y-pt.y, z-pt.z); }
    public R3 minus(double bx,double by,double bz) { return new R3(x-bx,y-by,z-bz); }
    
      
    // METHODS FOR VECTORS IN R3
    
    /** Projects to plane, returning the first two coordinates only */
    public R2 projectXY() { return new R2(x,y); }

    /** Returns the dot product of two vectors. */
    public double dot(R3 pt2) { return x*pt2.x+y*pt2.y+z*pt2.z; }
    
    /** Returns the cross product of two 3-vectors */
    public R3 cross(R3 p2) {
        return new R3 (y*p2.z-z*p2.y, z*p2.x-x*p2.z, x*p2.y-y*p2.x);
    }

    /** Returns the (scalar) triple product of three 3-vectors */
    public static double tripleProduct(R3 p1, R3 p2, R3 p3) {
        return p1.cross(p2).dot(p3);
    }
    
    
    // STRING HANDLERS

    final static NumberFormat nf=NumberFormat.getInstance();
    
    @Override
    public String toString(){ return "("+nf.format(x)+", "+nf.format(y)+", "+nf.format(z)+")"; }
    

    public void addToElement(int position, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getElement(int position) {
        return position==0 ? x : (position==1 ? y : (position==2 ? z : 0));
    }

    public int getLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void multiplyElement(int position, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setElement(int position, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double dotProduct(InnerProductSpaceElement p2) throws ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean equals(Coordinate c2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Coordinate copy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double distanceTo(Coordinate p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public VectorSpaceElement zero() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public VectorSpaceElement plus(VectorSpaceElement p2) throws ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public VectorSpaceElement minus(VectorSpaceElement p2) throws ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public VectorSpaceElement translateBy(VectorSpaceElement p2) throws ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public VectorSpaceElement multiplyBy(double d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
