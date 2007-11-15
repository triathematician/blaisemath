/*
 * EvaluationNode.java
 * 
 * Created on Sep 18, 2007, 5:04:19 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scribo.tree;

/**
 *
 * @author Elisha
 */
public abstract class FunctionTreeLeaf extends FunctionTreeNode {
    public FunctionTreeLeaf(){}  
    @Override
    public FunctionTreeNode addSubNode(FunctionTreeNode child){throw new UnsupportedOperationException("Leaves may not have children!");}
    public boolean isValidSubNode(){return numSubNodes()==0;}
    @Override
    public FunctionTreeNode simplified(){return this;}
}
