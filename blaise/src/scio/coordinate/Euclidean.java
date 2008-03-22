/**
 * Euclidean.java
 * Created on Mar 20, 2008
 */

package scio.coordinate;

import java.util.Vector;

/**
 *
 * @author Elisha Peterson
 */
public class Euclidean implements Coordinate {
    Vector<Double> coord;
    
    public Euclidean(int n){
        coord=new Vector<Double>(n);
        for(int i=0;i<n;i++){coord.add(0.0);}
    }
    
    public Euclidean(Double[] values){
        coord=new Vector<Double>(values.length);
        for(int i=0;i<values.length;i++){coord.add(values[i]);}
    }

    public void setElement(int position,double value){coord.set(position, value);}
    public double getElement(int position){return coord.get(position);}
    
    public boolean equals(Coordinate c2) {
        return (c2 instanceof Euclidean && ((Euclidean)c2).coord.equals(coord));
    }
    
    
    // TRANSFORMATIONS, RETURNING COPY OF THIS POINT
    
    public Euclidean translateBy(Euclidean p2){return this;}    
    public Euclidean multiplyBy(double d){return this;}
    public double dotProduct(Euclidean p2){return 0;}
    
    
    // TRANSFORMATIONS RETURNING A NEW POINT
    
    public Euclidean plus(Euclidean p2){return new Euclidean(coord.size());}
    public Euclidean minus(Euclidean p2){return new Euclidean(coord.size());}
    public Euclidean times(double d){return new Euclidean(coord.size());}    
}
