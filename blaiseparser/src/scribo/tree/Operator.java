/*
 * Operator.java
 * Created on Feb 22, 2008
 */

package scribo.tree;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * <p>
 * An operator is something that can transform several input trees into a single output tree. Examples are the basic
 * differentiation, summations and series, etc.  Note that technically operations like + and * are also operators, but
 * this class concerns only the case in which there is a single argument representing a function, and the other arguments
 * represent parameters that alter the function in some way.
 * </p>
 * <p>
 * Some operators (e.g. differentiation or summation) will introduce "local" variables, which are determined by other arguments.
 * For this reason, operator's have the capacity for an additional layer of unknowns that are passed into the argument.
 * </p>
 *
 * @author Elisha Peterson
 */
public abstract class Operator extends FunctionTreeNode {
    
    /** List of variables which may be set by the operator. */
    TreeMap<String,Double> unknowns;

    /** Default constructor */
    public Operator(){
        super();
        unknowns=new TreeMap<String,Double>();
        addSubNode(Constant.ZERO);
    }

    /** Default constructor with argument. */
    public Operator(FunctionTreeNode ftn){
        unknowns=new TreeMap<String,Double>();
        addSubNode(ftn);
    }
    
    /** Adds an argument to the list, or assigns its value. */
    public void addLocalVar(String s,Double d){
        unknowns.put(s,d);
    }

    /** Resets list of parameters. */
    public void resetLocalVar(){
        unknowns.clear();
    }

    /** Eliminate the local list of parameters before passing upward. */
    @Override
    public TreeSet<String> getUnknowns(){
        TreeSet<String> result = super.getUnknowns();
        result.removeAll(unknowns.keySet());
        return result;
    }
    
    public FunctionTreeNode getArgument(){return getSubNode(0);}
    public String argumentString(){return getArgument().toString();}
    public FunctionTreeNode argumentDerivative(Variable v){return getArgument().derivativeTree(v);}
    public FunctionTreeNode getFirstChild(){return getSubNode(1);}
    public FunctionTreeNode getSecondChild(){return getSubNode(2);}
    public FunctionTreeNode getThirdChild(){return getSubNode(3);}
    public FunctionTreeNode getFourthChild(){return getSubNode(4);}
}
