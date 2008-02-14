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
public class R1 implements Coordinate {
    
    // PROPERTIES
    
    Double value;

    // CONSTRUCTORS
    public R1(){setValue(0.0);}
    public R1(double v){setValue(v);}
    
    // GETTERS & SETTERS
    
    public Double getValue(){return value;}
    public void setValue(Double value){this.value=value;}
    
    // METHODS
    
    @Override
    public boolean equals(Coordinate c2) {
        return (c2 instanceof R1)&&(getValue().equals(((R1)c2).getValue()));
    }

}
