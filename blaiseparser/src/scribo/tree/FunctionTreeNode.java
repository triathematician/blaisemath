/*
 * FunctionTreeNode.java
 * Created on Sep 18, 2007, 5:03:20 PM
 */


package scribo.tree;

import java.util.Collection;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import scio.function.FunctionValueException;

/**
 * The <b>FunctionTreeNode</b> has support for basic functionality of a node in a <i>function tree</i>
 * <p>
 * The most import methods to override are <i>toString</i>, <i>getValue</i>, and <i>derivativeTree</i>. Much of the rest
 * of the code required for nodes is already implemented in this class.
 * </p>
 * <p>
 * There are several <i>getValue</i> methods which may be used in different cases. In the most generic case, a table assigning
 * potential inputs to variables is passed to the function.
 * </p>
 * @author Elisha Peterson
 */
public abstract class FunctionTreeNode {
    
    
    // VARIABLES
    
    /** Depth of this node (used for drawing parentheticals). */
    protected int depth=0;
    
    /** The subnodes associated to this node */
    protected Vector<FunctionTreeNode> children; 
    
    /** String which can be used to represent the type of node this is */
    protected String nodeName=null;
    
    
    // INITIALIZERS
    
    /** Basic initializer */
    public FunctionTreeNode(){children=new Vector<FunctionTreeNode>();}
    
    
    // METHODS FOR HANDLING SUBNODES
    
    /** Whether the node may be added to a tree. */
    public abstract boolean isValidSubNode();

    /** Adds a subnode, provided it is non-null and valid */
    public FunctionTreeNode addSubNode(FunctionTreeNode child){
        if(child!=null && child.isValidSubNode()) {
            children.add(child);
        }
        return this;
    }
    /** Adds multiple subnodes */
    public FunctionTreeNode addSubNodes(Collection<? extends FunctionTreeNode> kids){
        for(FunctionTreeNode c:kids) {
            addSubNode(c);
        }
        return this;
    }
    /** Returns ith subnode */
    public FunctionTreeNode getSubNode(int i){return children.get(i);}
    
    /** Returns number of subnodes */
    public int numSubNodes(){return (children==null)?-1:children.size();}
    
    
    // METHODS FOR CHECKING EQUALITY
    
    /** Checks equality with another tree */
    @Override
    public boolean equals(Object o){
        if(o instanceof FunctionTreeNode){
            return equals((FunctionTreeNode)o);
        }
        return false;
    }
    
    /** Checks equality with another functionNode (string only!!) */
    public boolean equals(FunctionTreeNode ftn){
        return toString().equals(ftn.toString());
    }
    
    /** Determines whether the tree is numeric or not. */
    public boolean isNumber(){
        if(children==null){return true;}
        for(FunctionTreeNode c:children){
            if(!c.isNumber()){return false;}
        }
        return true;
    }
    
    /** Checks equality with a numeric value. */
    public boolean equals(double d){
        try {
            return getValue()==d;
        } catch (FunctionValueException ex) {
            return false;
        }
    }
    
   
    // OPERATIONS ON THE TREE
    
    /** Returns a simplified version of this tree.
     * Many subclasses will override this method.
     * By default, just tries to return a constant.
     * @return simplified tree
     */
    public FunctionTreeNode simplified(){
        try{
            return new Constant(getValue());
        }catch(FunctionValueException e){
            return this;
        }
    }
    
    /** Returns a recursive simplification */
    public FunctionTreeNode fullSimplified(){
        FunctionTreeNode result = simplified();
        FunctionTreeNode newResult = result.simplified();
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
    
    
    // METHODS TO ASCERTAIN INFO REGARDING PARAMETERS AND VARIABLES
    
    /** Returns list of variables used in the tree. */
    public TreeSet<String> getUnknowns(){
        TreeSet<String> result=new TreeSet<String>();
        for(FunctionTreeNode ftn:children){result.addAll(ftn.getUnknowns());}
        return result;
    }
    
    /** Returns a default variable for the tree. */
    public String getDefaultVariable(){
        TreeSet<String> vars=getUnknowns();
        if(vars.isEmpty()){return null;}
        return vars.first();
    }
    
    
    // METHOD TO EXPORT FUNCTIONTREE TO GUI'ABLE TREE
    
    /** Returns MutableTreeNode corresponding to this node in the tree. */
    public MutableTreeNode getTreeNode(){ return new MutableFunctionTreeNode(this); }

    /** Represents a node in a function tree. */
    public static class MutableFunctionTreeNode extends DefaultMutableTreeNode {
        public MutableFunctionTreeNode(FunctionTreeNode ftn) {
            super(ftn);
            for(FunctionTreeNode child : ftn.children){ add(child.getTreeNode()); }
        }
        @Override
        public String toString() {
            FunctionTreeNode ftn = (FunctionTreeNode) userObject;
            String s = ftn.nodeName;
            if (ftn instanceof FunctionTreeRoot) {
                return ((FunctionTreeRoot)ftn).toString() + (((FunctionTreeRoot)ftn).parameters == null ? ""
                        : " , " + ((FunctionTreeRoot)ftn).parameters.toString());
            }
            else if (ftn instanceof Operation) {
                return s + " " + ((Operation)ftn).coefficient.toString();
            } else if (ftn instanceof Series) {
                return s + " " + ((Series)ftn).getIndex() + "="
                        + ((Series)ftn).getMinNode().toString() + " to " + ((Series)ftn).getMaxNode().toString()
                        + " step " + ((Series)ftn).getStepNode().toString();
            }
            return (s==null) ? userObject.toString() : s;
        }
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
    


    
    
    // ABSTRACT METHODS FOR RETURNING VALUE OF A NODE

    public Double getValue() throws FunctionValueException {
        return getValue("novariablegiven",0.0);
    }
   
    /** Returns the value of the tree given a table of variable/value matches. This is one of the
     * primary functions which needs to be overridden by subclasses.
     * 
     * @param table
     *      a table mapping variables to doubles.
     * @return
     *      a single value representing the output of the function
     * @throws scribo.parser.FunctionValueException
     *      if there are variables whose value cannot be assigned
     */
    public abstract Double getValue(TreeMap<String, Double> table) throws FunctionValueException;
    
    /** Returns value of tree given a single variable assignment. This could by default call the above
     * method, but to ensure speed that is not done.
     * 
     * @param s
     *      the string representing a variable
     * @param d
     *      the double value associated with the variable
     * @return
     *      a single value representing the output of the function
     * @throws scribo.parser.FunctionValueException
     *      if there are variables whose value cannot be assigned
     */
    public abstract Double getValue(String s,Double d) throws FunctionValueException;

    public Double getValue(Variable v, Double d) throws FunctionValueException {
        return getValue(v.nodeName, d);
    }

    /** Returns value of tree given a list of values for a single input.
     * @param s the string representing a variable
     * @param d the list of double values associated with the variable
     * @return a list of double values representing the output of the function
     * @throws scribo.parser.FunctionValueException if there are variables whose value cannot be assigned
     */
    public abstract Vector<Double> getValue(String s, Vector<Double> d) throws FunctionValueException;
    public Vector<Double> getValue(Variable v, Vector<Double> d) throws FunctionValueException{
        return getValue(v.nodeName, d);
    }
}
