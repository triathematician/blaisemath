/*
 * ExponentialFunctionNode.java
 * 
 * Created on Sep 29, 2007, 10:07:06 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scribo.tree;

import java.util.HashMap;

/**
 *
 * @author Elisha
 */
public class Trig {
    public static class Cos extends FunctionTreeFunctionNode {
        public Cos(FunctionTreeNode argument){super(argument);}
        public Double getValue(HashMap<Variable,Double> table){return Math.cos(argumentValue(table));}
        public void initFunctionType(){setFunctionNames("cos","-sin","acos",null);}    
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return new Operation.Multiply(-1,new Sin(argumentNode()),argumentDerivative(v));}
    }
    
    public static class Sin extends FunctionTreeFunctionNode {
        public Sin(FunctionTreeNode argument){super(argument);}
        public Double getValue(HashMap<Variable,Double> table){return Math.sin(argumentValue(table));}
        public void initFunctionType(){setFunctionNames("sin","cos","asin",null);}
    }
    
    public static class Tan extends FunctionTreeFunctionNode {
        public Tan(FunctionTreeNode argument){super(argument);}
        public Double getValue(HashMap<Variable,Double> table){return Math.tan(argumentValue(table));}
        public void initFunctionType(){setFunctionNames("tan",null,"atan",null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return new Operation.Multiply(new Operation.Power(new Sec(argumentNode()),new Constant(2.0)),argumentDerivative(v));}
    }
    
    public static class Sec extends FunctionTreeFunctionNode {
        public Sec(FunctionTreeNode argument){super(argument);}
        public Double getValue(HashMap<Variable,Double> table){return 1/Math.cos(argumentValue(table));}
        public void initFunctionType(){setFunctionNames("sec",null,"asec",null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return new Operation.Multiply(new Sec(argumentNode()),new Tan(argumentNode()),argumentDerivative(v));}
    }
    
    public static class Csc extends FunctionTreeFunctionNode {
        public Csc(FunctionTreeNode argument){super(argument);}
        public Double getValue(HashMap<Variable,Double> table){return 1/Math.sin(argumentValue(table));}
        public void initFunctionType(){setFunctionNames("csc",null,"acsc",null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return new Operation.Multiply(-1,new Cot(argumentNode()),new Csc(argumentNode()),argumentDerivative(v));}
    }
    
    public static class Cot extends FunctionTreeFunctionNode {
        public Cot(FunctionTreeNode argument){super(argument);}
        public Double getValue(HashMap<Variable,Double> table){return 1/Math.tan(argumentValue(table));}
        public void initFunctionType(){setFunctionNames("cot",null,"acot",null);}
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return new Operation.Multiply(-1,new Operation.Power(new Csc(argumentNode()),2),argumentDerivative(v));}
    }
}
