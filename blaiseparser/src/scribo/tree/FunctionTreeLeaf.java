/*
 * EvaluationNode.java
 * Created on Sep 18, 2007, 5:04:19 PM
 */

package scribo.tree;

/**
 * Superclass for leaves in the function tree, including Variables and Constants.
 * 
 * @author Elisha
 */
public abstract class FunctionTreeLeaf extends FunctionTreeNode {    
    public FunctionTreeLeaf(){}  
    @Override
    public FunctionTreeNode addSubNode(FunctionTreeNode child){throw new UnsupportedOperationException("Leaves may not have children!");}
    @Override
    public boolean isValidSubNode(){return numSubNodes()==0;}
    @Override
    public FunctionTreeNode simplified(){return this;}
}
