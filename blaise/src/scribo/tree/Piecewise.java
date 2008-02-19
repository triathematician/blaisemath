
/*
 * PiecewiseFunctionNode.java
 * 
 * Created on Sep 28, 2007, 3:52:01 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scribo.tree;

import java.util.TreeMap;

/**
 * Implements a piecewise function. Argument is additive sum of restricted
 * domain functions, defined on intervals [a,b], [a,b), (a,b], or (a,b), with
 * possibly a or b=infinity. Each interval has an associated function.
 * Assumes a value of zero if not contained in an interval.
 * @author Elisha
 */
public class Piecewise extends FunctionTreeFunctionNode {     
    public Piecewise(Domain a,Domain b){super(new Operation.Add(a,b));}
    
    public void initFunctionType(){setFunctionNames(null,null,null,null);}
    public Double getValue(TreeMap<Variable, Double> table){return argumentValue(table);}
        
    /** Function defined on a restricted domain. Supports a single input variable only. */
    public static class Domain extends FunctionTreeFunctionNode{
        double lower,upper;
        boolean lowerSharp;
        boolean upperSharp;
        public Domain(FunctionTreeNode argument){super(argument);initDomain();}
        public Domain(FunctionTreeNode argument,double l,double u,boolean ls,boolean us){super(argument);initDomain(l,u,ls,us);}
        
        public void initDomain(){initDomain(Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,false,false);}
        public void initDomain(double l,double u,boolean ls,boolean us){lower=l;upper=u;lowerSharp=ls;upperSharp=us;}
        public void initFunctionType(){setFunctionNames(null,null,null,null);}

        @Override
        public Double getValue(Variable v,Double value){
            if(value<lower||value>upper){return null;}
            if(value==lower&&!lowerSharp){return null;}
            if(value==upper&&!upperSharp){return null;}
            return argumentNode().getValue(v,value);
        }
        
        public Double getValue(TreeMap<Variable, Double> table){
            Variable first=(Variable)(table.keySet().toArray()[0]);
            return getValue(first,table.get(first));
        }        
        
        @Override
        public FunctionTreeNode derivativeTree(Variable v){
            return new Domain(argumentDerivative(v),lower,upper,lowerSharp,upperSharp);
        }
    } // class Piecewise.Domain
    
    
    
    
    /** Step function */
    public static class Step extends FunctionTreeFunctionNode{
        public Step(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("step",null,null,null);}
        @Override
        public Double getValue(TreeMap<Variable,Double>table){return argumentValue(table)>=0.0?1.0:0.0;}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return Constant.ZERO;}
    } // class Piecewise.Step
    
    /** Step function */
    public static class Stepp extends FunctionTreeFunctionNode{
        public Stepp(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("stepp",null,null,null);}
        @Override
        public Double getValue(TreeMap<Variable,Double>table){return argumentValue(table)>=0.0?1.0:-1.0;}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return Constant.ZERO;}
    } // class Piecewise.Stepp
   
    /** Absolute value function */
    public static class Abs extends FunctionTreeFunctionNode{
        public Abs(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("abs","stepp",null,null);}
        @Override
        public Double getValue(TreeMap<Variable,Double> table) {return Math.abs(argumentValue(table));}
    } // class Piecewise.Abs
}
