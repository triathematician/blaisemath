/*
 * Differentiator.java
 * Created on Oct 8, 2007, 3:17:28 PM
 */

package scribo.tree;

import java.util.HashMap;
import java.util.Vector;
import scribo.parser.FunctionSyntaxException;

/**
 * This class represents a differentiation operator contained on a tree.<br><br>
 * 
 * @author Elisha
 */
public class Differentiator extends FunctionTreeFunctionNode {
    FunctionTreeNode dArg;
    Variable x;
    int n;
    
    public Differentiator(){this(Constant.ZERO,new Variable("x"),1);}
    public Differentiator(FunctionTreeNode argument,String s){this(argument,new Variable(s),1);}
    public Differentiator(FunctionTreeNode argument,Variable x){this(argument,x,1);}
    public Differentiator(FunctionTreeNode argument,Variable x,int n){super(argument);this.x=x;setDegree(n);}
    public void initFunctionType(){setFunctionNames("D",null,null,null);}
    
    @Override
    public String toString(){return getFunctionName()+(n==1?"":"^"+n)+x.toString()+"("+argumentString()+")";}
    
    public void setDegree(int n){if(n>0){this.n=n;}}
    public void initDArg(){
        if(dArg==null){
            dArg=argumentNode();
            for(int i=0;i<n;i++){dArg=dArg.derivativeTree(x);}
        }
    }
    public Double getValue(HashMap<Variable, Double> table){initDArg();return dArg.getValue(table);}
    
    @Override
    public FunctionTreeNode derivativeTree(Variable v){return new Differentiator(argumentNode(),x,n+1);}
    @Override
    public FunctionTreeNode simplified(){initDArg();return dArg.simplified();}
    
    /** Static method generating a derivative given a vector of elements. First element should be a regular argument;
     * remaining elements should be variables.
     * @param arguments
     * @return derivative of elements
     * @throws parser.FunctionSyntaxException
     * @throws java.lang.ArrayIndexOutOfBoundsException
     */    
    public static Differentiator getDiff(Vector<FunctionTreeNode> arguments) throws FunctionSyntaxException,ArrayIndexOutOfBoundsException{
        if(!(arguments.get(1) instanceof Variable)){
            throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_TYPE);
        }
        if(arguments.size()==2){
            return new Differentiator(arguments.get(0),(Variable)arguments.get(1));
        }
        throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_NUMBER);                
    }
}
