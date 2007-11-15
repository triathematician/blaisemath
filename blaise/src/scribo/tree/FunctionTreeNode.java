/*
 * FunctionTreeNode.java
 * Created on Sep 18, 2007, 5:03:20 PM
 */

// TODO Add representative string (non-dependent on successors)!!!
// TODO (HARD) Add integration node
// TODO (HARD) Add advanced equality testing
// TODO (HARD) Add integer-based simplification


package scribo.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * This class is the overarching generic class used to construct a function tree. Much of the functionality is coded here.
 * The important methods to override are "toString", "getValue", and "derivativeTree". Most of the rest is automatic. This means
 * that anonymous inner classes may be used.
 * @author Elisha
 */
public abstract class FunctionTreeNode {
    /** Depth of this node (used for drawing parentheticals). */
    protected int depth=0;
    
    /** The subnodes associated to this node */
    private Vector<FunctionTreeNode> children; 
    /** Basic initializer */
    public FunctionTreeNode(){children=new Vector<FunctionTreeNode>();}
    
    
    // HANDLING SUBNODES
    
    /** Whether the node may be added to a tree. */
    public abstract boolean isValidSubNode();
    /** Adds a subnode, provided it is non-null and valid */
    public FunctionTreeNode addSubNode(FunctionTreeNode child){if(child!=null&&child.isValidSubNode()){children.add(child);}return this;}
    /** Adds multiple subnodes */
    public FunctionTreeNode addSubNodes(Collection<? extends FunctionTreeNode> kids){for(FunctionTreeNode c:kids)addSubNode(c);return this;}
    /** Returns number of subnodes */
    public int numSubNodes(){return (children==null)?-1:children.size();}
    /** Returns all subnodes */
    public Vector<FunctionTreeNode> getSubNodes(){return children;}
    /** Returns a specific subnode */
    public FunctionTreeNode getSubNode(int i){return children.get(i);}
    
    
    // EVALUATING THE TREE
    
    /** Checks equality with a numeric value. */
    public boolean equals(double d){
        return isNumber()&&getValue().equals(d);
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof FunctionTreeNode){
            return equals((FunctionTreeNode)o);
        }
        return false;
    }
    /** Checks equality with another functionNode (string only!!) */
    public boolean equals(FunctionTreeNode ftn){return toString().equals(ftn.toString());}
    
    /** Determines whether the tree is numeric or not. */
    public boolean isNumber(){
        if(children==null){return true;}
        for(FunctionTreeNode c:children){
            if(!c.isNumber()){return false;}
        }
        return true;
    }
    /** Returns the numeric value of the tree (provided there are no variables in it). */
    public Double getValue(){
        if(isNumber()){return getValue("novariable",0.0);}
        return null;
    }
    /** Returns the value of the tree given a set of variable/value matches. */
    public abstract Double getValue(HashMap<Variable,Double> table);       
    /** Returns the value of the tree assuming there is a single variable. */
    public Double getValue(Variable v,Double value){
        HashMap<Variable,Double> table=new HashMap<Variable,Double>();
        table.put(v,value);
        return getValue(table);
    }    
    /** Translates string into a variable. */
    public Double getValue(String s,Double value){return getValue(new Variable(s),value);}    
    
    

    // OPERATIONS ON THE TREE
    
    /** Returns a simplified version of this tree. */
    public FunctionTreeNode simplified(){
        return isNumber()?new Constant(getValue()).simplified():this;
    }
    /** Returns a recursive simplification */
    public FunctionTreeNode fullSimplified(){
        FunctionTreeNode result=simplified();
        FunctionTreeNode newResult=result.simplified();
        while(!newResult.toString().equals(result.toString())){
            result=newResult;
            newResult=result.simplified();
        }
        return newResult;
    }
    /** Returns the derivative of this tree, with respect to the given variable. */
    public abstract FunctionTreeNode derivativeTree(Variable v);
    /** Returns the derivative using the given string for the variable. */
    public FunctionTreeNode derivativeTree(String s){
        return derivativeTree(new Variable(s));
    }
    
    
    // DISPLAY AND INFORMATIVE METHODS
    
    /** String output of the function... descendants MUST override! */
    @Override
    public abstract String toString();
    /** Prints string based on another FunctionTreeNode. Used to nest parentheses. */
    public String toString(FunctionTreeNode parent){
        if(parent.depth>=depth){return toString();}
        else{return "("+toString()+")";}
    }
    /** Alternate output displaying the node name first. In particular, the basic operations should override this. */
    public String toOpString(){return toString();}
    
    /** Returns list of variables used in the tree. */
    public Vector<Variable> getVariables(){
        Vector<Variable> result=new Vector<Variable>();
        for(FunctionTreeNode ftn:children){result.addAll(ftn.getVariables());}
        return result;
    }
    
    /** Returns a default variable for the tree. */
    public Variable getDefaultVariable(){
        Vector<Variable> vars=getVariables();
        if(vars.size()==1){return vars.get(0);}
        return null;
    }
    
    /** Returns MutableTreeNode corresponding to this ndoe in the tree. */
    public MutableTreeNode getTreeNode(){
        DefaultMutableTreeNode result=new DefaultMutableTreeNode(this);
        for(FunctionTreeNode ftn:children){result.add(ftn.getTreeNode());}
        return result;
    }
}
