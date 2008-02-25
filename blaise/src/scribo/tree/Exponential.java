/*
 * ExponentialFunctionNode.java
 * Created on Sep 29, 2007, 10:07:06 AM
 */

// TODO Add power, root, and arbitrary base logs.

package scribo.tree;

import scio.function.FunctionValueException;

/**
 * Represents exp(x) and associated function types.<br><br>
 * @author Elisha
 */
public class Exponential {
    public static class Exp extends FunctionTreeFunctionNode {
        public Exp(FunctionTreeNode argument){super(argument);}
        public Double getValue(Double x){return Math.exp(x);}
        public void initFunctionType(){setFunctionNames("exp","exp","log",Log.class);}
    }
    
    public static class Log extends FunctionTreeFunctionNode {
        public Log(FunctionTreeNode argument){super(argument);}
        public Double getValue(Double x){return Math.log(x);}
        public void initFunctionType(){setFunctionNames("ln",null,"exp",Exp.class);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return Operation.divideNode(argumentDerivative(v),argumentNode());}
    }
    
    public static class Log2 extends FunctionTreeFunctionNode {
        final static double log2=Math.log(2);
        public Log2(FunctionTreeNode argument){super(argument);}
        public Double getValue(Double x){return Math.log(x)/log2;}
        public void initFunctionType(){setFunctionNames("log2",null,null,null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return Operation.divideNode(1.0/log2,argumentDerivative(v),v);}
        @Override
        public FunctionTreeNode simplified(){
            try{return new Constant(getValue()).simplified();}catch(FunctionValueException e){}
            FunctionTreeNode argResult=argumentSimplified();
            if(argResult instanceof Operation.Power && ((Operation.Power)argResult).basePart().equals(2.0)){return ((Operation.Power)argResult).powerPart();}
            return new Log2(argResult);
        }    
    }
    
    public static class Log10 extends FunctionTreeFunctionNode {
        final static double log10=Math.log(10);
        public Log10(FunctionTreeNode argument){super(argument);}
        public Double getValue(Double x){return Math.log(x)/log10;}
        public void initFunctionType(){setFunctionNames("log10",null,null,null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return Operation.divideNode(1.0/log10,argumentDerivative(v),v);}
        @Override
        public FunctionTreeNode simplified(){
            try{return new Constant(getValue()).simplified();}catch(FunctionValueException e){}
            FunctionTreeNode argResult=argumentSimplified();
            if(argResult instanceof Operation.Power && ((Operation.Power)argResult).basePart().equals(10.0)){return ((Operation.Power)argResult).powerPart();}
            return new Log10(argResult);
        }    
    }
    
    public static class Cosh extends FunctionTreeFunctionNode {
        public Cosh(FunctionTreeNode argument){super(argument);}
        public Double getValue(Double x){return Math.cosh(x);}
        public void initFunctionType(){setFunctionNames("cosh","sinh",null,null);}
    }
    
    public static class Sinh extends FunctionTreeFunctionNode {
        public Sinh(FunctionTreeNode argument){super(argument);}
        public Double getValue(Double x){return Math.sinh(x);}
        public void initFunctionType(){setFunctionNames("sinh","cosh",null,null);}
    }
}
