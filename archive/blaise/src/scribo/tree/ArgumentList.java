/*
 * ArgumentList.java
 * Created on Oct 27, 2007, 12:11:10 PM
 */

package scribo.tree;

import java.util.TreeMap;
import java.util.Vector;
import scio.function.FunctionValueException;

/**
 * This is a somewhat abstract version of a FunctionTreeNode, which really just stores
 * a bunch of FunctionTreeNode's for later use. Intended to pass to a function requiring
 * multiple inputs.
 * 
 * @author Elisha Peterson
 */
public class ArgumentList extends FunctionTreeNode {
    public ArgumentList(){super();}
    public ArgumentList(Vector<FunctionTreeNode> arguments){super();addSubNodes(arguments);}    
    public Vector<FunctionTreeNode> arguments(){return children;}
    @Override
    public boolean isValidSubNode(){return false;}    
    @Override
    public FunctionTreeNode derivativeTree(Variable v){return null;}
    @Override
    public String toString(){return null;}
    @Override
    public Double getValue(TreeMap<String, Double> table) throws FunctionValueException {throw new FunctionValueException();}
    @Override
    public Double getValue(String s, Double d) throws FunctionValueException {throw new FunctionValueException();}
    @Override
    public Vector<Double> getValue(String s, Vector<Double> d) throws FunctionValueException {throw new FunctionValueException();}
}
