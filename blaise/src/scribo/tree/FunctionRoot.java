/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * FunctionRoot.java
 * Created on Mar 24, 2008
 */

package scribo.tree;

import java.util.Set;
import scio.function.Function;
import scio.function.ParameterFunction;

/**
 *
 * @author Elisha Peterson
 */
public interface FunctionRoot {
    public Set<String> getVariables();
    public Set<String> getParameters();
    public int getNumVariables();
    public int getNumParameters();
    public Function getFunction();
    public ParameterFunction getParameterFunction();
}
