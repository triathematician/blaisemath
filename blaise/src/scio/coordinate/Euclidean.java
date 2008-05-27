/**
 * Euclidean.java
 * Created on Mar 20, 2008
 */

package scio.coordinate;

import java.util.Vector;

/**
 * This class describes a generic vector of real numbers.
 * 
 * @author Elisha Peterson
 */
public abstract class Euclidean implements Coordinate,MetricSpaceElement,VectorSpaceElement {
    Vector<Double> coord;
    
    public Euclidean(int n){
        coord=new Vector<Double>(n);
        for(int i=0;i<n;i++){coord.add(0.0);}
    }
    
    public Euclidean(Double[] values){
        coord=new Vector<Double>(values.length);
        for(int i=0;i<values.length;i++){coord.add(values[i]);}
    }
    
    
    // BEAN PATTERNS

    public void setElement(int position,double value){coord.set(position, value);}
    public double getElement(int position){return coord.get(position);}
    public int length(){return coord.size();}
    
    @Override
    public String toString() { return coord.toString(); }
    
    
    // TRANSFORMATIONS, RETURNING COPY OF THIS POINT
    
    public Euclidean translateBy(Euclidean p2){return this;}    
    public Euclidean multiplyBy(double d){return this;}
    public double dotProduct(Euclidean p2){return 0;}
    
   
    // REQUIRED TO MAKE THIS A METRIC SPACE
    
    public boolean equals(Coordinate c2) {
        return (c2 instanceof Euclidean && ((Euclidean)c2).coord.equals(coord));
    }
    
    public double coordDistanceSq(double x1,double x2){return (x1-x2)*(x1-x2);}
    
    public double distance(Coordinate p2) {
        if(!(p2 instanceof Euclidean)){return -1;}
        Euclidean e2=(Euclidean)p2;
        int n=Math.min(length(),e2.length());
        double result=0;
        for(int i=0;i<n;i++){
            result+=coordDistanceSq(getElement(i),e2.getElement(i));
        }
        return Math.sqrt(result);
    }
}
