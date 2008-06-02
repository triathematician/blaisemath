/*
 * R1.java
 * Created on May 30, 2008
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.coordinate;

/**
 * <p>
 * R1 is ...
 * </p>
 * @author Elisha Peterson
 */
public class R1 extends Euclidean {
    public R1() { super(1); }
    public R1(double d) { super(1); setValue(d); }
    public Double getValue() { return getElement(0); }
    public void setValue(Double d) { setElement(0, d); }
}
