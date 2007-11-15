/*
 * FunctionTreeNodeOneChild.java
 * Created on Sep 20, 2007, 8:52:28 AM
 */

package scribo.tree;

import java.util.HashMap;
import scribo.parser.*;

/**
 * This class represents a basic input/output function. It requires just one subnode, called
 * the <i>argument</i>, which can be easily accessed. This allows many methods required by
 * FunctionTreeNode to be automatically generated.<br><br>
 * 
 * Such nodes have four owned settings: a string which represents the function, a string
 * representing the derivative class of the function, a string representing the function's
 * inverse, and the class type of the inverse. These allow the function's derivative to be
 * computed easily, and the function to be automatically simplified. Sub-classes may augment
 * these automatic methods for increases in performance.<br><br>
 * 
 * @author Elisha
 */
public abstract class FunctionTreeFunctionNode extends FunctionTreeNode {

    private Class<? extends FunctionTreeFunctionNode> ifc;
    protected String fn;
    protected String ifn;
    protected String dfn;

    public FunctionTreeFunctionNode(){initFunctionType();}
    public FunctionTreeFunctionNode(FunctionTreeNode argument){initFunctionType();addSubNode(argument);}
    
    public abstract void initFunctionType();
    /** Sets the names of the function, its derivative, and its inverse, as used in parsing.
     * @param fn    Name of the function, as used in parsing.
     * @param dfn   Name of the function's derivative.
     * @param ifn   Name of the function's inverse.
     * @param ifc   Class type of the function's inverse.
     */
    public void setFunctionNames(String fn,String dfn,String ifn,Class<? extends FunctionTreeFunctionNode> ifc){this.fn=fn;this.dfn=dfn;this.ifn=ifn;this.ifc=ifc;}
    
    public boolean isValidSubNode(){return numSubNodes() == 1 && argumentNode() != null;}
    
    public String toString(){return fn+"("+argumentString()+")";}    
    public void setArgumentNode(FunctionTreeNode argument){if(argument!=null&&argument.isValidSubNode()){getSubNodes().set(0,argument);}}
    public FunctionTreeNode argumentNode(){return getSubNode(0);}
    public Double argumentValue(HashMap<Variable, Double> table){return argumentNode().getValue(table);}
    public String argumentString(){return argumentNode().toString();}
    public FunctionTreeNode argumentDerivative(Variable v){return argumentNode().derivativeTree(v);}
    public FunctionTreeNode argumentSimplified(){return argumentNode().simplified();}
    public String getFunctionName(){return fn;}
    public String getInverseFunctionName(){return ifn;}
    public String getDerivativeFunctionName(){return dfn;}
    public boolean inverseOf(FunctionTreeNode fb){return fb.getClass().equals(ifc);}

    @Override
    public FunctionTreeNode derivativeTree(Variable v){
        try{
            return new Operation.Multiply(FunctionTreeFactory.getFunction(dfn,argumentNode()),argumentDerivative(v));
        }catch(FunctionSyntaxException e){
            return null;
        }
    }
    @Override
    public FunctionTreeNode simplified(){
        if(isNumber()){return new Constant(getValue()).simplified();}
        FunctionTreeNode argResult=argumentSimplified();
        // if the argument is an inverse function, cancel the two functions and go deeper
        if (argResult instanceof FunctionTreeFunctionNode && this.inverseOf(argResult)){
            return ((FunctionTreeFunctionNode) argResult).argumentNode();
        }
        // default is to return a function of this type with the simplified argument
        try{
            return FunctionTreeFactory.getFunction(fn,argResult);
        }catch(FunctionSyntaxException e){
            return null;
        }
    }
}
