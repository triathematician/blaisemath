/**
 * FunctionRoot.java
 * Created on Mar 24, 2008
 */

package scribo.tree;

import java.util.TreeMap;
import java.util.Vector;
import deprecated.Function;

/**
 * <p>
 * Represents the root node of some function tree. This is the class that handles interface
 * with all other parts of the program that use the function.
 * </p>
 * @author Elisha Peterson
 */
public interface FunctionRoot<C> {

    /** Returns list of variables. */
    public Vector<String> getVariables();

    /** Returns list of variables as a mapping to their values. */
    public TreeMap<String, C> getParameters();

    /** Returns the number of variables. */
    public int getNumVariables();

    /** Returns the number of parameters. */
    public int getNumParameters();

    /** Returns the function represented by this root class. */
    public Function getFunction();
    
    //public ParameterFunction getParameterFunction();
    
}
