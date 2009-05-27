/*
 * FunctionTreeFactory.java
 * Created on Sep 21, 2007, 9:01:36 AM
 */

package scribo.parser;

import scribo.tree.*;
import scio.function.FunctionValueException;
import java.util.Vector;
import scio.function.Function;

/**
 * <p>
 * Contains several static methods for generating functions based on input.
 * Converts strings, etc. to <code>FunctionTree</code> elements.
 * </p>
 * 
 * @author Elisha Peterson
 */
public abstract class FunctionTreeFactory {

    /** Generates function node with specified argument, based upon the given name. If no node is known with the specified
     * name, treats the node as multiplication.
     * @param name string representing the function
     * @param argument argument of the function
     * @return a tree node with the function applied to the argument
     * @throws scribo.parser.FunctionSyntaxException if there is a problem with the arguments' syntax
     */
    public static FunctionTreeNode getFunction(String name, FunctionTreeNode argument) throws FunctionSyntaxException{
        if(argument==null||name==null){return null;}
        if(argument instanceof ArgumentList){return getFunction(name,((ArgumentList)argument).arguments());}
        name=name.toLowerCase();
        if      (name.equals("abs")){return new Piecewise.Abs(argument);
        }else if(name.equals("step")){return new Piecewise.Step(argument);
        }else if(name.equals("stepp")){return new Piecewise.Stepp(argument);
        }else if(name.equals("cos")){return new Trig.Cos(argument);
        }else if(name.equals("sin")){return new Trig.Sin(argument);            
        }else if(name.equals("tan")){return new Trig.Tan(argument);
        }else if(name.equals("csc")){return new Trig.Csc(argument);            
        }else if(name.equals("sec")){return new Trig.Sec(argument);            
        }else if(name.equals("cot")){return new Trig.Cot(argument);
        }else if(name.equals("sqr")){return new Exponential.Sqr(argument);
        }else if(name.equals("sqrt")){return new Exponential.Sqrt(argument);
        }else if(name.equals("cubert")){return new Exponential.Cubert(argument);
        }else if(name.equals("exp")){return new Exponential.Exp(argument);
        }else if(name.equals("log")){return new Exponential.Log(argument);
        }else if(name.equals("ln")){return new Exponential.Log(argument);
        }else if(name.equals("log2")){return new Exponential.Log2(argument);
        }else if(name.equals("logtwo")){return new Exponential.Log2(argument);
        }else if(name.equals("log10")){return new Exponential.Log10(argument);
        }else if(name.equals("logten")){return new Exponential.Log10(argument);
        }else if(name.equals("sinh")){return new Exponential.Sinh(argument);            
        }else if(name.equals("cosh")){return new Exponential.Cosh(argument);
        }else if(name.equals("dx")){return new Differentiator(argument,"x");
        }else if(name.equals("dy")){return new Differentiator(argument,"y");
        }else if(name.equals("dz")){return new Differentiator(argument,"z");
        }
        return new Operation.Multiply(new Variable(name),argument);        
    }
    
    /** Returns functions with specified name and multiple arguments
     * @param name string representing the function (should take multiple arguments)
     * @param arguments list of arguments of the function
     * @return a tree node representing the function with given name applied to the given arguments
     * @throws scribo.parser.FunctionSyntaxException if there is a problem with the arguemnts' syntax
     */
    public static FunctionTreeNode getFunction(String name, Vector<FunctionTreeNode> arguments) throws FunctionSyntaxException{
        if(arguments==null||arguments.size()==0||name==null){return null;}
        if(arguments.size()==1){return getFunction(name,arguments.get(0));}
        name=name.toLowerCase();
        try{
            if      (name.equals("sum")){return Series.getSum(arguments);
            }else if(name.equals("prod")){return Series.getProd(arguments);
            }else if(name.equals("if")){            
            }else if(name.equals("d") || name.equals("diff")){return Differentiator.getDiff(arguments);
            }
        }catch(ArrayIndexOutOfBoundsException e){
            throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_NUMBER);                            
        }
        throw new FunctionSyntaxException(FunctionSyntaxException.UNKNOWN_FUNCTION);                
    }
    
    /** Converts an input String to a function tree.
     * @param fnString a string representing the input, which should represent a function of some sort
     * @return the compiled tree ready for action
     * @throws scribo.parser.FunctionSyntaxException
     */
    public static FunctionRoot getFunction(String fnString) throws FunctionSyntaxException{
        FunctionTreeNode result=Parser.parseExpression(fnString);
        if(result instanceof ArgumentList){return new VectorFunctionRoot(result);}
        return new FunctionTreeRoot(result);
    }
    
    /** Converts an input String to a <code>Function</code> class with a single input and a single output
     * @param fnString a string representing the input, which should represent a function of one variable
     * @param var the variable corresponding to the function's single input
     * @return the function, as a class with <code>getValue</code> methods
     * @throws scribo.parser.FunctionSyntaxException
     */
    public static Function<Double,Double> getFunctionObject(final String fnString, final Variable var) throws FunctionSyntaxException{
        return new Function<Double,Double>(){
            final FunctionTreeNode compiled=(FunctionTreeNode) getFunction(fnString);
            public Double getValue(Double x) throws FunctionValueException{return compiled.getValue(var,x);}
            public Vector<Double> getValue(Vector<Double> xs) throws FunctionValueException{return compiled.getValue(var,xs);}
        };
    }
    
    /** Converts an input string representing a constant, number, or variable, into the corresponding node for the tree.
     * @param name the string representing the variable or constant
     * @return a tree node of leaf type
     */
    public static FunctionTreeLeaf getLeaf(String name){
        if      (name.equals("pi")){return Constant.PI;
        }else if(name.equals("e")){return Constant.E;
        }else if(name.equals("phi")){return Constant.PHI;
        }else{
            try{
                return new Constant(Double.parseDouble(name));
            }catch(NumberFormatException n){
                return new Variable(name);
            }
        }
    }
}
