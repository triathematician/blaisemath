/*
 * FunctionTreeFactory.java
 * Created on Sep 21, 2007, 9:01:36 AM
 */

package scribo.parser;

import scribo.tree.*;
import scio.function.FunctionValueException;
import scribo.parser.FunctionSyntaxException;
import java.util.Vector;
import scribo.parser.*;
import scio.function.Function;

/**
 * Contains several static methods for generating functions based on input.
 * Converts strings, etc. to FunctionTree elements.<br><br>
 * 
 * @author Elisha Peterson
 */;
public class FunctionTreeFactory {
    /** Returns functions with single arguments. */
    public static FunctionTreeNode getFunction(String name,FunctionTreeNode argument) throws FunctionSyntaxException{
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
        }
        return new Operation.Multiply(new Variable(name),argument);        
    }
    
    /** Returns functions with multiple arguments. */
    public static FunctionTreeNode getFunction(String name,Vector<FunctionTreeNode> arguments) throws FunctionSyntaxException{
        if(arguments==null||arguments.size()==0||name==null){return null;}
        if(arguments.size()==1){return getFunction(name,arguments.get(0));}
        name=name.toLowerCase();
        try{
            if      (name.equals("sum")){return Series.getSum(arguments);
            }else if(name.equals("prod")){return Series.getProd(arguments);
            }else if(name.equals("if")){            
            }else if(name.equals("d")){return Differentiator.getDiff(arguments);                
            }
        }catch(ArrayIndexOutOfBoundsException e){
            throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_NUMBER);                            
        }
        throw new FunctionSyntaxException(FunctionSyntaxException.UNKNOWN_FUNCTION);                
    }
    
    /** Returns parsed tree for the function */
    public static FunctionRoot getFunction(String s) throws FunctionSyntaxException{
        FunctionTreeNode result=Parser.parseExpression(s);
        if(result instanceof ArgumentList){return new VectorFunctionRoot(result);}
        return new FunctionTreeRoot(result);
    }
    
    /** Returns a function object corresponding to the given string and variable. */
    public static Function<Double,Double> getFunctionObject(final String s,final Variable v) throws FunctionSyntaxException{
        return new Function<Double,Double>(){
            final FunctionTreeNode compiled=(FunctionTreeNode) getFunction(s);
            @Override
            public Double getValue(Double x) throws FunctionValueException{return compiled.getValue(v,x);}
            public Vector<Double> getValue(Vector<Double> xs) throws FunctionValueException{return compiled.getValue(v,xs);}
            @Override
            public Double minValue(){throw new UnsupportedOperationException("Not supported yet.");}
            @Override
            public Double maxValue(){throw new UnsupportedOperationException("Not supported yet.");}
        };
    }
    
    /** Returns leaf corresponding to a given string. This is a constant, number, or variable. */
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
