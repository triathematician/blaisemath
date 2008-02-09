/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scio.function;

import java.util.Vector;

/**
 * A specific type of function which takes in a list of values and outputs a single value.
 * @author Elisha
 */
public interface DoubleFunction extends Function<Vector<Double>,Double> {
    /** Should return the number of input variables. */
    public int getNumInputs();
    /** Used ONLY WHEN the number of inputs is one. */
    public Double getValue(Double x);
}
