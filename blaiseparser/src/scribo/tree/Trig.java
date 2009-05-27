/*
 * ExponentialFunctionNode.java
 * Created on Sep 29, 2007, 10:07:06 AM
 */

package scribo.tree;

/**
 * This class contains trig functions which can be added to the parser tree.
 * 
 * @author Elisha Peterson
 */
public class Trig {
    public static class Cos extends FunctionTreeFunctionNode {
        public Cos(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("cos","-sin","acos",null);}    
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return new Operation.Multiply(-1,new Sin(argumentNode()),argumentDerivative(v));}
//        @Override
        public Double getValue(Double x){return Math.cos(x);}
    }
    
    public static class Sin extends FunctionTreeFunctionNode {
        public Sin(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("sin","cos","asin",null);}
//        @Override
        public Double getValue(Double x){return Math.sin(x);}
    }
    
    public static class Tan extends FunctionTreeFunctionNode {
        public Tan(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("tan",null,"atan",null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return new Operation.Multiply(new Operation.Power(new Sec(argumentNode()),new Constant(2.0)),argumentDerivative(v));}
//        @Override
        public Double getValue(Double x){return Math.tan(x);}
    }
    
    public static class Sec extends FunctionTreeFunctionNode {
        public Sec(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("sec",null,"asec",null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return new Operation.Multiply(new Sec(argumentNode()),new Tan(argumentNode()),argumentDerivative(v));}
//        @Override
        public Double getValue(Double x){return 1/Math.cos(x);}
    }
    
    public static class Csc extends FunctionTreeFunctionNode {
        public Csc(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("csc",null,"acsc",null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return new Operation.Multiply(-1,new Cot(argumentNode()),new Csc(argumentNode()),argumentDerivative(v));}
//        @Override
        public Double getValue(Double x){return 1/Math.sin(x);}    
    }    
    
    public static class Cot extends FunctionTreeFunctionNode {
        public Cot(FunctionTreeNode argument){super(argument);}
        @Override
        public void initFunctionType(){setFunctionNames("cot",null,"acot",null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return new Operation.Multiply(-1,new Operation.Power(new Csc(argumentNode()),2),argumentDerivative(v));}
//        @Override
        public Double getValue(Double x){return 1/Math.tan(x);}    
    }
}
