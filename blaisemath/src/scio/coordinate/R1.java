/*
 * R1.java
 * Created on May 30, 2008
 */

package scio.coordinate;

/**
 * <p>
 * R1 is an implementation of the 1D Euclidean space (the reals!)
 * </p>
 * @author Elisha Peterson
 */
public class R1 implements EuclideanElement<R1> {
    public double x=0; 
    
    public R1() { }
    public R1(double d) { x=d; }
    
    public Double getValue() { return x; }
    public void setValue(double d) { x=d; }

    public double distanceTo(R1 p2) { return Math.abs(p2.x-x); }
    public double dotProduct(R1 p2) throws ArrayIndexOutOfBoundsException { return x*p2.x; }
    
    public boolean equals(Coordinate c2) { return (c2 instanceof R1) && ((R1)c2).x == x; }
    public Object copy() { return new R1(x); }

    public R1 zero() { return new R1(); }

    public double magnitude() { return Math.abs(x); }
    
    public R1 translateBy(R1 p2) throws ArrayIndexOutOfBoundsException { x+= p2.x; return this; }
    public R1 multiplyBy(double d) { x*= d; return this; }

    public R1 plus(R1 p2) throws ArrayIndexOutOfBoundsException { return new R1(x+p2.x); }
    public R1 minus(R1 p2) throws ArrayIndexOutOfBoundsException { return new R1(x-p2.x); }
    public R1 times(double d) { return new R1(x*d); }
    public R1 scaledToLength(double d) { return new R1(x/Math.abs(x)*d); }
    
    public int getLength() { return 1; }
    public void addToElement(int position, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public double getElement(int position) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public void multiplyElement(int position, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public void setElement(int position, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
