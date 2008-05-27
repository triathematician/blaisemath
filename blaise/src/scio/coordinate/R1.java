/*
 * R1.java
 * Created on Feb 11, 2008
 */

package scio.coordinate;

/**
 * <p>
 * R1 is ...
 * </p>
 * @author Elisha Peterson
 */
public class R1 extends Euclidean {

    // CONSTRUCTORS
    public R1(){super(1);setValue(0.0);}
    public R1(double v){super(1);setValue(v);}
    
    // GETTERS & SETTERS
    
    public Double getValue(){return coord.get(0);}
    public void setValue(Double value){setElement(0,value);}

    // VECTOR SPACE METHODS
    
    public VectorSpaceElement zero() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public VectorSpaceElement plus(VectorSpaceElement p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public VectorSpaceElement minus(VectorSpaceElement p2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public VectorSpaceElement times(double d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
