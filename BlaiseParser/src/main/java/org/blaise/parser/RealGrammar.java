/**
 * RealGrammar.java
 * Created on Dec 1, 2009
 */
package org.blaise.parser;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 *    This class describes the supported language elements
 *    of a real-variable parsing library.
 * </p>
 * @author Elisha Peterson
 */
public class RealGrammar implements Grammar {

    static RealGrammar INSTANCE;
    static TokenParser PARSER;

    /** @return a static instance of a RealGrammar that can be used to construct a parser. */
    public static RealGrammar getInstance() {
        if (INSTANCE == null)
            INSTANCE = new RealGrammar();
        return INSTANCE;
    }

    /** @return a static instance of a parser that uses the RealGrammar. */
    public static TokenParser getParser() {
        if (PARSER == null)
            PARSER = new TokenParser(getInstance());
        return PARSER;
    }

    //
    // STATIC DEFINITIONS
    //
    
    final static String[][] PARS = {{"(", ")"}};

    final static TreeMap<String, Double> CONSTANTS = new TreeMap<String, Double>() {{
        put("pi", Math.PI);
        put("e", Math.E);
    }};

    final static Map<String, Method> PRE_OPS = new TreeMap<String, Method>() {{
        try {
            put("+", RealGrammar.class.getMethod("positive", double.class));
            put("-", RealGrammar.class.getMethod("negative", double.class));
        } catch (Exception ex) {
            Logger.getLogger(RealGrammar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }};
    
    final static Map<String, Method> POST_OPS = new TreeMap<String, Method>() {{
        try {
            put("!", RealGrammar.class.getMethod("factorial", double.class));
        } catch (Exception ex) {
            Logger.getLogger(RealGrammar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }};

    final static Map<String, Method> NARY_OPS = new TreeMap<String, Method>() {{
        try {
            put(",", null);
            put(">", RealGrammar.class.getMethod("greater", double.class, double.class));
            put("+", RealGrammar.class.getMethod("add", double[].class));
            put("-", RealGrammar.class.getMethod("subtract", double.class, double.class));
            put(" ", RealGrammar.class.getMethod("multiply", double[].class));
            put("*", RealGrammar.class.getMethod("multiply", double[].class));
            put("/", RealGrammar.class.getMethod("divide", double.class, double.class));
            put("^", java.lang.Math.class.getMethod("pow", double.class, double.class));
            put("%", RealGrammar.class.getMethod("modulus", double.class, double.class));
        } catch (Exception ex) {
            Logger.getLogger(RealGrammar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }};

    final static String[] MULTARY_OPS = new String[] { ",", "+", " ", "*" };
    final static String[] ORDER_OF_OPS = new String[] { ",", "+", "-", " ", "*", "/", "^", "%" };

    final static Map<String, Method> FUNCTIONS = new TreeMap<String, Method>() {{
        try {
            // java.lang.Math methods
            put("abs", java.lang.Math.class.getMethod("abs", double.class));
            put("acos", java.lang.Math.class.getMethod("acos", double.class));
              put("arccos", java.lang.Math.class.getMethod("acos", double.class));
              put("arccosine", java.lang.Math.class.getMethod("acos", double.class));
            put("asin", java.lang.Math.class.getMethod("asin", double.class));
              put("arcsin", java.lang.Math.class.getMethod("asin", double.class));
              put("arcsine", java.lang.Math.class.getMethod("asin", double.class));
            put("atan", java.lang.Math.class.getMethod("atan", double.class));
              put("arctan", java.lang.Math.class.getMethod("atan", double.class));
              put("arctangent", java.lang.Math.class.getMethod("atan", double.class));
            put("atan2", java.lang.Math.class.getMethod("atan2", double.class, double.class));
            put("cbrt", java.lang.Math.class.getMethod("cbrt", double.class));
            put("ceil", java.lang.Math.class.getMethod("ceil", double.class));
              put("ceiling", java.lang.Math.class.getMethod("ceil", double.class));
            put("cos", java.lang.Math.class.getMethod("cos", double.class));
              put("cosine", java.lang.Math.class.getMethod("cos", double.class));
            put("cosh", java.lang.Math.class.getMethod("cosh", double.class));
            put("exp", java.lang.Math.class.getMethod("exp", double.class));
            put("expm1", java.lang.Math.class.getMethod("expm1", double.class));
            put("floor", java.lang.Math.class.getMethod("floor", double.class));
            put("hypot", java.lang.Math.class.getMethod("hypot", double.class, double.class));
              put("hypotenuse", java.lang.Math.class.getMethod("hypot", double.class, double.class));
//            put("IEEEremainder", java.lang.Math.class.getMethod("IEEEremainder", double.class));
            put("log", java.lang.Math.class.getMethod("log", double.class));
              put("ln", java.lang.Math.class.getMethod("log", double.class));
            put("log10", java.lang.Math.class.getMethod("log10", double.class));
            put("log1p", java.lang.Math.class.getMethod("log1p", double.class));
//            put("max", java.lang.Math.class.getMethod("max", double.class, double.class));
//            put("min", java.lang.Math.class.getMethod("min", double.class, double.class));
            put("pow", java.lang.Math.class.getMethod("pow", double.class, double.class));
            put("random", java.lang.Math.class.getMethod("random"));
              put("rand", java.lang.Math.class.getMethod("random"));
            put("rint", java.lang.Math.class.getMethod("rint", double.class));
              put("round", java.lang.Math.class.getMethod("rint", double.class));
            put("signum", java.lang.Math.class.getMethod("signum", double.class));
              put("sign", java.lang.Math.class.getMethod("signum", double.class));
              put("sgn", java.lang.Math.class.getMethod("signum", double.class));
            put("sin", java.lang.Math.class.getMethod("sin", double.class));
              put("sine", java.lang.Math.class.getMethod("sin", double.class));
            put("sinh", java.lang.Math.class.getMethod("sinh", double.class));
            put("sqrt", java.lang.Math.class.getMethod("sqrt", double.class));
            put("tan", java.lang.Math.class.getMethod("tan", double.class));
              put("tangent", java.lang.Math.class.getMethod("tan", double.class));
            put("tanh", java.lang.Math.class.getMethod("tanh", double.class));
            put("deg", java.lang.Math.class.getMethod("toDegrees", double.class));
            put("rad", java.lang.Math.class.getMethod("toRadians", double.class));
            put("ulp", java.lang.Math.class.getMethod("ulp", double.class));
            
            // additional methods
            put("add", RealGrammar.class.getMethod("add", double[].class));
              put("sum", RealGrammar.class.getMethod("add", double[].class));
              put("summation", RealGrammar.class.getMethod("add", double[].class));
            put("average", RealGrammar.class.getMethod("average", double[].class));
              put("avg", RealGrammar.class.getMethod("average", double[].class));
            put("divide", RealGrammar.class.getMethod("divide", double.class, double.class));
            put("factorial", RealGrammar.class.getMethod("factorial", double.class));
            put("frac", RealGrammar.class.getMethod("frac", double.class));
            put("log2", RealGrammar.class.getMethod("log2", double.class));
            put("max", RealGrammar.class.getMethod("max", double[].class));
              put("maximum", RealGrammar.class.getMethod("max", double[].class));
            put("min", RealGrammar.class.getMethod("min", double[].class));
              put("minimum", RealGrammar.class.getMethod("min", double[].class));
            put("modulus", RealGrammar.class.getMethod("modulus", double.class, double.class));
            put("multiply", RealGrammar.class.getMethod("multiply", double[].class));
              put("prod", RealGrammar.class.getMethod("multiply", double[].class));
              put("product", RealGrammar.class.getMethod("multiply", double[].class));
            put("negative", RealGrammar.class.getMethod("negative", double.class));
            put("randbetween", RealGrammar.class.getMethod("randbetween", double.class, double.class));
            put("subtract", RealGrammar.class.getMethod("subtract", double.class, double.class));

            // TODO - add "sum", "prod", and "if"
        } catch (Exception ex) {
            Logger.getLogger(RealGrammar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }};

    //
    // INTERFACE METHODS
    //

    public boolean isCaseSensitive() { return false; }
    
    public String[][] parentheticals() { return PARS; }
    public String argumentListOpener() { return "("; }
    public String argumentListSeparator() { return ","; }
    public String implicitSpaceOperator() { return "*"; }

    
    public Map<String, Double> constants() { return CONSTANTS; }

    public Map<String, Method> preUnaryOperators() { return PRE_OPS; }
    public Map<String, Method> postUnaryOperators() { return POST_OPS; }
    public Map<String, Method> naryOperators() { return NARY_OPS; }
    public String[] multaryOperators() { return MULTARY_OPS; }
    public String[] orderOfOperations() { return ORDER_OF_OPS; }

    public Map<String, Method> functions() { return FUNCTIONS; }

    //
    // STATIC FUNCTIONS TO USE IN SEMANTIC EVALUATION
    //

    public static double add(double... args) {
        double result = 0.0;
        for (int i = 0; i < args.length; i++)
            result += args[i];
        return result;
    }
    public static double average(double... xs) { 
        return add(xs)/xs.length;
    }
    public static double divide(double arg1, double arg2) { return arg1 / arg2; }
    public static double factorial(double arg) {
        double result = 1;
        while (arg > 1) {
            result *= arg;
            arg--;
        }
        return result;
    }
    public static double frac(double arg) { return arg % 1; }
    public static boolean greater(double arg1, double arg2) { return arg1 > arg2; }
    public static double log2(double arg) { return Math.log(arg)/Math.log(2); }
    public static double max(double... args) {
        if (args.length == 0)
            return Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < args.length; i++)
            if (args[i] > max)
                max = args[i];
        return max;
    }
    public static double min(double... args) {
        if (args.length == 0)
            return -Double.MAX_VALUE;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < args.length; i++)
            if (args[i] < min)
                min = args[i];
        return min;
    }
    public static double modulus(double arg1, double arg2) { return arg1 % arg2; }
    public static double multiply(double... args) {
        double result = 1.0;
        for (int i = 0; i < args.length; i++)
            result *= args[i];
        return result;
    }
    public static double negative(double arg) { return -arg; }
    public static double positive(double arg) { return arg; }
    public static double randbetween(double min, double max) { return Math.random()*(max-min) + min; }
    public static double subtract(double arg1, double arg2) { return arg1 - arg2; }
}
