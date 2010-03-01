/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.sequor.fk;

import java.util.List;

/**
 * Stores a table of functions that can be accessed by clients.
 * 
 * @author ae3263
 */
public interface FunctionKernel {

    /** Returns function of given name, or null if there is no such function. */
    public Object getFunction(String name);

    /** Return list of functions with specified numbers of inputs and outputs. */
    public List<Object> getFunctionsOfType(int nInputs, int nOutputs);

    /** Sets function of given name to be specified function. */
    public void setFunction(String name, Object function);
}
