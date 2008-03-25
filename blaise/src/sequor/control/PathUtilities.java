/**
 * PathUtilities.java
 * Created on Mar 25, 2008
 */

package sequor.control;

import java.util.Vector;
import scio.coordinate.R2;
import scio.function.Function;
import scio.function.FunctionValueException;

/**
 * This class contains a library of functions which can be used to determine such things as how far
 * along a path one is, etc.
 * 
 * @author Elisha Peterson
 */
public class PathUtilities {
    public Vector<R2> path;
    
    /** Return length of i'th segment. i must be between 1 and the size of the path. */
    public double getLength(int i){
        try{
            return path.get(i).distance(path.get(i-1));
        }catch(Exception e){
            return 0;
        }
    }
    
    /** Returns the total length of the path. */
    public double getTotalLength(){
        double total=0;
        for(int i=1;i<path.size();i++){total+=getLength(i);}
        return total;
    }
    
    /** Returns a point at a given percentage of length along the path. */
    public R2 getPercentPoint(double percent,double totLength){
        double total=0;
        int i=1;
        while(total+getLength(i)<percent*totLength){
            total+=getLength(i);
            i++;
        }
        double percentSegment=(percent*totLength-total)/getLength(i);
        return getPercentPoint(path.get(i-1),path.get(i),percentSegment);
    }
    
    /** Returns point at given percentage of length between two other points. */
    public R2 getPercentPoint(R2 p1,R2 p2,double percent){
        return p1.plus(p2.minus(p1).times(percent));
    }
    
    /** Returns function mapping [0,1] to this path. */
    public class PathFunction implements Function<Double,R2> {
        public R2 getValue(Double x) throws FunctionValueException {
            x=Math.min(Math.max(x,0),1);
            return getPercentPoint(x,getTotalLength());
        }
        public Vector<R2> getValue(Vector<Double> xx) throws FunctionValueException {throw new UnsupportedOperationException("Not supported yet.");}
        public R2 minValue() {throw new UnsupportedOperationException("Not supported yet.");}
        public R2 maxValue() {throw new UnsupportedOperationException("Not supported yet.");}            
    }
}
