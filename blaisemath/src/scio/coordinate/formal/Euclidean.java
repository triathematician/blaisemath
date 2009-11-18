/**
 * Euclidean.java
 * Created on Mar 20, 2008
 */

package scio.coordinate.formal;

import scio.coordinate.formal.EuclideanElement;
import java.util.Vector;

/**
 * <p>
 *   This class describes a generic vector of real numbers. WARNING: NOT FULLY IMPLEMENTED!
 * </p>
 * 
 * @author Elisha Peterson
 */
public class Euclidean implements EuclideanElement<Euclidean> {
    Vector<Double> coord;
    
    public Euclidean(int n){
        coord=new Vector<Double>(n);
        for(int i=0;i<n;i++){coord.add(0.0);}
    }
    
    public Euclidean(Double[] values){
        coord=new Vector<Double>(values.length);
        for(int i=0;i<values.length;i++){coord.add(values[i]);}
    }
    private Euclidean(Euclidean point) {
        coord=new Vector<Double>(point.getLength());
        for(int i=0;i<point.getLength();i++){coord.add(point.getCoordinate(i));}
    }
    
    
    // BEAN PATTERNS

    public void setCoordinate(int position,double value){coord.set(position, value);}
    public double getCoordinate(int position){return coord.get(position);}
    public int getLength(){return coord.size();}
    
    public void addToCoordinate(int position,double value){coord.set(position, value + coord.get(position));}
    public void multiplyCoordinateBy(int position,double value){coord.set(position, value * coord.get(position));}
    
    @Override
    public String toString() { return coord.toString(); }
    
        
    
    // COORDINATE METHODS

    public Euclidean copy() {
        return new Euclidean(this);
    }
    
    
    // METRIC SPACE METHODS
    
    public double coordDistanceSq(double x1,double x2){return (x1-x2)*(x1-x2);}

    public double distanceTo(Euclidean p2) {
        if(!(p2 instanceof Euclidean)){return -1;}
        Euclidean e2=(Euclidean)p2;
        int n=Math.min(getLength(),e2.getLength());
        double result=0;
        for(int i=0;i<n;i++){
            result+=coordDistanceSq(getCoordinate(i),e2.getCoordinate(i));
        }
        return Math.sqrt(result);
    }

    
    // VECTOR SPACE METHODS
    
    public Euclidean zero() { return new Euclidean(getLength()); }
    public Euclidean plus(Euclidean p2) throws ArrayIndexOutOfBoundsException { return p2.copy().translateBy(this); }
    public Euclidean minus(Euclidean p2) throws ArrayIndexOutOfBoundsException { return p2.copy().multiplyBy(-1.0).translateBy(this); }
    public Euclidean timesScalar(double d) { return copy().multiplyBy(d); }
    
    /** Changes this point and returns it. */
    public Euclidean translateBy(Euclidean p2) throws ArrayIndexOutOfBoundsException {
        EuclideanElement p2e = (EuclideanElement) p2;
        if( p2e.getLength() != getLength() ) { throw new ArrayIndexOutOfBoundsException(); }
        for (int i = 0; i < getLength(); i++) {
            addToCoordinate(i, p2e.getCoordinate(i));
        }
        return this;
    }
    /** Alters this point and returns it. */
    public Euclidean multiplyBy(double d){
        for (int i = 0; i < getLength(); i++) {
            multiplyCoordinateBy(i, d);
        }
        return this;
    }
    /** Scales to a given length. */
    public Euclidean scaledToLength(double d) { return multiplyBy(d/magnitude()); }


    // INNER PRODUCT SPACE METHODS
    
    public double dotProduct(Euclidean p2) throws ArrayIndexOutOfBoundsException {
        EuclideanElement p2e = (EuclideanElement) p2;
        if (p2e.getLength() != getLength()) { throw new ArrayIndexOutOfBoundsException(); }
        double result = 0;
        for (int i = 0; i < getLength(); i++) {
            result += getCoordinate(i) * p2e.getCoordinate(i);
        }
        return result;
    }

    public double magnitude() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Euclidean normalized() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
