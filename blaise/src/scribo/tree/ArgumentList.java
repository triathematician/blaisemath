/*
 * ArgumentList.java
 * Created on Oct 27, 2007, 12:11:10 PM
 */

package scribo.tree;

import java.util.HashMap;
import java.util.Vector;

/**
 * This is a somewhat abstract version of a FunctionTreeNode, which really just stores
 * a bunch of FunctionTreeNode's for later use. Intended to pass to a function requiring
 * multiple inputs.
 * @author Elisha
 */
public class ArgumentList extends FunctionTreeNode {
    public ArgumentList(Vector<FunctionTreeNode> arguments){super();addSubNodes(arguments);}
    
    @Override
    public boolean isValidSubNode(){return false;}
    
    @Override
    public Double getValue(HashMap<Variable, Double> table){return null;}

    @Override
    public FunctionTreeNode derivativeTree(Variable v){return null;}

    @Override
    public String toString(){return null;}
}
