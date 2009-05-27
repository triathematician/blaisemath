/*
 * FunctionTreeNodeOneChild.java
 * Created on Sep 20, 2007, 8:52:28 AM
 */

package scribo.tree;

import scio.function.FunctionValueException;
import java.util.TreeMap;
import java.util.Vector;
import scio.function.Function;
import scribo.parser.*;

/**
 * This class represents a basic input/output function. It requires just one subnode, called
 * the <i>argument</i>, which can be easily accessed. This allows many methods required by
 * <b>FunctionTreeNode</b> to be automatically generated.
 * 
 * <p>
 * Such nodes have four owned settings: a string which represents the function, a string
 * representing the derivative class of the function, a string representing the function's
 * inverse, and the class type of the inverse. These allow the function's derivative to be
 * computed easily, and the function to be automatically simplified. Sub-classes may augment
 * these automatic methods for increases in performance.
 * </p>
 * @author Elisha Peterson
 */
public abstract class FunctionTreeFunctionNode extends FunctionTreeNode implements Function<Double,Double> {

    private Class<? extends FunctionTreeFunctionNode> ifc;
    protected String ifn;
    protected String dfn;

    public FunctionTreeFunctionNode(){
        initFunctionType();
    }
    
    public FunctionTreeFunctionNode(FunctionTreeNode argument){
        initFunctionType();
        addSubNode(argument);
    }

    /** This method is used by sub-classes to initialize the function, normally using a call to "setFunctionNames". Although
     * it could possibly be implemented as doing nothing, we prefer to force the user to override this for subclasses.
     */
    public abstract void initFunctionType();

    /** Sets the names of the function, its derivative, and its inverse, as used in parsing.
     * @param fn    Name of the function, as used in parsing.
     * @param dfn   Name of the function's derivative.
     * @param ifn   Name of the function's inverse.
     * @param ifc   Class type of the function's inverse.
     */
    public void setFunctionNames(String fn, String dfn, String ifn, Class<? extends FunctionTreeFunctionNode> ifc){
        this.nodeName=fn;
        this.dfn=dfn;
        this.ifn=ifn;
        this.ifc=ifc;
    }
    @Override
    public boolean isValidSubNode(){return numSubNodes() == 1 && argumentNode() != null;}
    @Override
    public String toString(){return nodeName+"("+argumentString()+")";}    
    public void setArgumentNode(FunctionTreeNode argument){if(argument!=null&&argument.isValidSubNode()){children.set(0,argument);}}
    public FunctionTreeNode argumentNode(){return children.firstElement();}
    public String argumentString(){return argumentNode().toString();}
    public FunctionTreeNode argumentDerivative(Variable v){return argumentNode().derivativeTree(v);}
    public FunctionTreeNode argumentSimplified(){return argumentNode().simplified();}
    public String getFunctionName(){return nodeName;}
    public String getInverseFunctionName(){return ifn;}
    public String getDerivativeFunctionName(){return dfn;}
    public boolean inverseOf(FunctionTreeNode fb){return fb.getClass().equals(ifc);}

    /** Returns derivative of this function. This may be done automatically by the chain rule if the appropriate derivative class is known. */
    @Override
    public FunctionTreeNode derivativeTree(Variable v){
        try{
            return new Operation.Multiply(FunctionTreeFactory.getFunction(dfn, argumentNode()), argumentDerivative(v));
        } catch (FunctionSyntaxException e) {
            System.out.println("Unknown derivative!");
            return null;
        }
    }

    /** Returns a simplifed version of this function. Checks to see if it encloses the inverse function. */
    @Override
    public FunctionTreeNode simplified(){
        try{
            return new Constant(getValue()).simplified();
        } catch (FunctionValueException e) {}
        FunctionTreeNode argResult=argumentSimplified();
        // if the argument is an inverse function, cancel the two functions and go deeper
        if (argResult instanceof FunctionTreeFunctionNode && this.inverseOf(argResult)){
            return ((FunctionTreeFunctionNode) argResult).argumentNode();
        }
        // default is to return a function of this type with the simplified argument
        try{
            return FunctionTreeFactory.getFunction(nodeName,argResult);
        }catch(FunctionSyntaxException e){
            return null;
        }
    }
    
    
    // ARGUMENT VALUE METHODS
    
    public Double argumentValue(String s, Double d)
            throws FunctionValueException {
        return argumentNode().getValue(s, d);
    }
    
    public Double argumentValue(TreeMap<String, Double> table)
            throws FunctionValueException{
        return argumentNode().getValue(table);
    }

    public Vector<Double> argumentValue(String s, Vector<Double> d)
            throws FunctionValueException {
        return argumentNode().getValue(s, d);
    }
    

    // VALUE METHODS
    
    @Override
    public Double getValue(String s, Double d) 
            throws FunctionValueException {
        return getValue(argumentValue(s, d));
    }

    @Override
    public Double getValue(TreeMap<String, Double> table)
            throws FunctionValueException {
        return getValue(argumentValue(table));
    }

    @Override
    public Vector<Double> getValue(String s, Vector<Double> d)
            throws FunctionValueException {
        return getValue(argumentValue(s, d));
    }

    public Vector<Double> getValue(Vector<Double> x)
            throws FunctionValueException {
        Vector<Double> result = new Vector<Double>(x.size());
        for(Double v:x){ result.add(getValue(v)); }
        return result;
    }
}
