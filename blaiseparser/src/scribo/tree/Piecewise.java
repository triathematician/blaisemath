
/*
 * PiecewiseFunctionNode.java
 * Created on Sep 28, 2007, 3:52:01 PM
 */

package scribo.tree;

import java.util.TreeMap;
import java.util.Vector;
import scio.function.FunctionValueException;

/**
 * Implements a piecewise function. Argument is additive sum of restricted
 * domain functions, defined on intervals [a,b], [a,b), (a,b], or (a,b), with
 * possibly a or b=infinity. Each interval has an associated function.
 * Assumes a value of zero if not contained in an interval.
 * 
 * @author Elisha Peterson
 */
public class Piecewise extends FunctionTreeFunctionNode {     
    // TODO this is not implemented properly
    
    public Piecewise(Domain a,Domain b){super(new Operation.Add(a,b));}
    
    @Override
    public void initFunctionType(){setFunctionNames(null,null,null,null);}
    @Override
    public Double getValue(TreeMap<String, Double> table) throws FunctionValueException {return argumentValue(table);}
    @Override
    public Double getValue(String s, Double d) throws FunctionValueException {return argumentValue(s,d);}
    @Override
    public Vector<Double> getValue(String s, Vector<Double> d) throws FunctionValueException {return argumentValue(s,d);}
    public Double getValue(Double x) throws FunctionValueException {
        throw new UnsupportedOperationException("Not supported yet.");
    }    
    
        
    /** Function which restricts the domain of another function. Returns 0 if the input is outside the domain;
     * otherwise returns the value of the argument.
     */
    public static class Domain extends FunctionTreeFunctionNode {
        // TODO this has not been implemented properly!!
        double lower,upper;
        boolean lowerSharp;
        boolean upperSharp;
        public Domain(FunctionTreeNode argument){super(argument);initDomain();}
        public Domain(FunctionTreeNode argument,double l,double u,boolean ls,boolean us){super(argument);initDomain(l,u,ls,us);}
        
        public void initDomain(){initDomain(Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,false,false);}
        public void initDomain(double l,double u,boolean ls,boolean us){lower=l;upper=u;lowerSharp=ls;upperSharp=us;}
        @Override
        public void initFunctionType(){setFunctionNames(null,null,null,null);}

        @Override
        public FunctionTreeNode derivativeTree(Variable v){
            return new Domain(argumentDerivative(v),lower,upper,lowerSharp,upperSharp);
        }

        // VALUE METHODS

        public Double getValue(Double x) throws FunctionValueException {
            if(x<lower||x>upper){return 0.0;}
            if(x==lower&&!lowerSharp){return 0.0;}
            if(x==upper&&!upperSharp){return 0.0;}
            return x;
        } 
    } // class Piecewise.Domain
    
    
    /** Step function */
    public static class Step extends FunctionTreeFunctionNode{
        public Step(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("step",null,null,null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return Constant.ZERO;}
        public Double getValue(Double x) throws FunctionValueException {return x>=0?1.0:0.0;}
    } // class Piecewise.Step
    

    /** Step function */
    public static class Stepp extends FunctionTreeFunctionNode{
        public Stepp(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("stepp",null,null,null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return Constant.ZERO;}
        public Double getValue(Double x) throws FunctionValueException {return x>=0?1.0:-1.0;}
    } // class Piecewise.Stepp
   
    
    /** Absolute value function */
    public static class Abs extends FunctionTreeFunctionNode{
        public Abs(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("abs","stepp",null,null);}
        public Double getValue(Double x) throws FunctionValueException {return Math.abs(x);}
    } // class Piecewise.Abs
}
