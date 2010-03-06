/*
 * Operator.java
 * Created on Feb 22, 2008
 */

package scribo.tree;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * An operator is something that can transform several input trees into a single output tree. Examples are the basic
 * differentiation, summations and series, etc.  Note that technically operations like + and * are also operators, but
 * this class concerns only the case in which there is a single argument representing a function, and the other arguments
 * represent parameters that alter the function in some way.
 * 
 * @author Elisha Peterson
 */
public abstract class Operator extends FunctionTreeNode {
    /** The primary argument (input) of the operator. */
    FunctionTreeNode argument;
    /** List of variables which may be set by the operator. */
    TreeMap<String,Double> unknowns;
    
    /** Default constructor with argument. */
    public Operator(FunctionTreeNode ftn){
        super();
        unknowns=new TreeMap<String,Double>();
        argument=ftn;
    }
    
    /** Adds a parameter to the list. */
    public void addParameter(String s,Double d){unknowns.put(s,d);}
    /** Resets list of parameters. */
    public void resetParameters(){unknowns.clear();}

    /** Eliminate the local list of parameters before passing upward. */
    @Override
    public TreeSet<String> getUnknowns(){
        TreeSet<String> result=argument.getUnknowns();
        for(FunctionTreeNode ftn:children){result.addAll(ftn.getUnknowns());}
        result.removeAll(unknowns.keySet());
        return result;
    }
    
    public FunctionTreeNode getArgument(){return argument;}
    public String argumentString(){return argument.toString();}
    public FunctionTreeNode argumentDerivative(Variable v){return argument.derivativeTree(v);}
    public FunctionTreeNode getFirstChild(){return getSubNode(0);}
    public FunctionTreeNode getSecondChild(){return getSubNode(1);}
    public FunctionTreeNode getThirdChild(){return getSubNode(2);}
    public FunctionTreeNode getFourthChild(){return getSubNode(3);}
}
