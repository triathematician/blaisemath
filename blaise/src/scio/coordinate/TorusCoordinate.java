/**
 * TorusCoordinate.java
 * Created on Mar 24, 2008
 */

package scio.coordinate;

/**
 * Implements coordinates which "wrap" around as on a circle or torus. Requires redefining distance.
 * The coordinates are stored as real numbers, with the option to cut them off cyclically.
 * 
 * @author Elisha Peterson
 */
public class TorusCoordinate extends Euclidean {
    double max;
    
    public TorusCoordinate(int n){this(n,1.0);}
    public TorusCoordinate(int n,double max){super(n);this.max=max;}
    public TorusCoordinate(Double[] values){this(values,1.0);}
    public TorusCoordinate(Double[] values,double max){super(values);this.max=max;}

    /** Forces all coordinates to occur between 0 and max. */
    public void modulate(){
        
    }
    
    /** Finds the distance between a single coordinate. This is the smallest distance out of
     * d(x1,x2), d(x1+max,x2), and d(x1-max,x2), after x1 and x2 have been restricted to [0,max].
     */
    @Override
    public double coordDistanceSq(double x1,double x2){
        return Math.min(Math.abs(x1%max-x2%max),Math.min(Math.abs(x1%max+max-x2%max),Math.abs(x1%max-max-x2%max)));
    }
}
