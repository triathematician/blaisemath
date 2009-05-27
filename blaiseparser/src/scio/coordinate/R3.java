/*
 * R3.java
 * Created on Sep 14, 2007, 8:15:59 AM
 */

package scio.coordinate;

import scio.coordinate.*;
import java.text.NumberFormat;

/**
 * This class handles three-dimensional coordinates in space.
 * <br><br>
 * @author Elisha Peterson
 */
public class R3 implements EuclideanElement<R3> {
    
    
    // STATIC VECTORS
    
    public final static R3 ZERO = new R3();
    public final static R3 I = new R3(1,0,0);
    public final static R3 J = new R3(0,1,0);
    public final static R3 K = new R3(0,0,1);
    
    
    // PROPERTIES
    
    public double x,y,z;
    
    
    // CONSTRUCTORS
    
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
        
    
    // COMPARISONS
    
    public boolean equals(Coordinate c2) {
        if (!(c2 instanceof R3)) return false;
        R3 c2r = (R3)c2;
        return x==c2r.x && y==c2r.y && z==c2r.z;
    }
    
    // OPERATIONS WHICH CHANGE THE POINT
    
    public R3 translateBy(R3 b){x+=b.x; y+=b.y; z+=b.z; return this;}
    public R3 translateBy(double x,double y,double z){this.x+=x; this.y+=y; this.z+=z; return this;}    
    public R3 multiplyBy(double d) {this.x*=d; this.y*=d; this.z*=d; return this;}
    
    
    // OPERATIONS WHICH DO NOT CHANGE THE POINT
    
    public double magnitudeSq() { return x*x+y*y+z*z; }
    public double magnitude() { return Math.sqrt(magnitudeSq()); }    
    public R3 normalized() { return times(1/magnitude()); }
    public R3 scaledToLength(double length){ double m = length/magnitude(); return new R3(getX()*m, getY()*m, getZ()*m); }
    public R3 multipliedBy(double length){ return new R3(getX()*length, getY()*length, getZ()*length); }
    
    
    // OPERATIONS WITH MULTIPLE VECTORS
    
    public double distanceTo(R3 pt) { return minus(pt).magnitude(); }
    public R3 times(double d) { return new R3(d*x, d*y, d*z); }
    public R3 plus(R3 pt) { return new R3(x+pt.x, y+pt.y, z+pt.z); }
    public R3 plus(double bx,double by,double bz) { return new R3(x+bx,y+by,z+bz); }
    public R3 minus(R3 pt) { return new R3(x-pt.x, y-pt.y, z-pt.z); }
    public R3 minus(double bx,double by,double bz) { return new R3(x-bx,y-by,z-bz); }
    
      
    // METHODS FOR VECTORS IN R3
    
    /** Projects to plane, returning the first two coordinates only */
    public R2 projectXY() { return new R2(x,y); }

    /** Returns the dotProduct product of two vectors. */
    public double dotProduct(R3 pt2) { return x*pt2.x+y*pt2.y+z*pt2.z; }
    /** Returns the crossProduct product of two 3-vectors */
    public R3 crossProduct(R3 p2) { return new R3 (y*p2.z-z*p2.y, z*p2.x-x*p2.z, x*p2.y-y*p2.x); }
    /** Returns the (scalar) triple product of three 3-vectors */
    public static double tripleProduct(R3 p1, R3 p2, R3 p3) { return p1.crossProduct(p2).dotProduct(p3); }
    
    
    // METHODS FOR EuclideanElement INTERFACE

    public void addToElement(int position, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getElement(int position) {
        return position==0 ? x : (position==1 ? y : (position==2 ? z : 0));
    }

    public int getLength() { return 3; }
    public void multiplyElement(int position, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public void setElement(int position, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public Coordinate copy() { return new R3(x,y,z); }    
    public R3 zero() { return ZERO; }
    
    
    // STRING HANDLERS

    final static NumberFormat nf=NumberFormat.getInstance();
    
    @Override
    public String toString(){ return "("+nf.format(x)+", "+nf.format(y)+", "+nf.format(z)+")"; }
    
}
