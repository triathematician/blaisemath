// TODO - this file may be deleted when it is convenient

package org.bm.blaise.scribo.parser;

import java.util.TreeMap;

/**
 *
 * @deprecated
 * @author elisha
 */
class Functions {

    /** Describes alternate expressions of the same underlying function 
     * @deprecated
     */
    static TreeMap<String,String> SYNONYM_MAP = new TreeMap<String,String>(){
        {
            put("sine", "sin");
            put("cosine", "cos");
            put("tangent", "tan");

            put("arcsin", "asin");
            put("arcsine", "asin");
            put("arccos", "acos");
            put("arccosine", "acos");
            put("arctan", "atan");
            put("arctangent", "atan");
            
            put("squareroot", "sqrt");
            put("cuberoot", "cbrt");
            put("hypotenuse", "hypot");
            
            put("absolutevalue", "abs");
            put("sign", "signum");
            put("sgn", "signum");
            put("round", "rint");
            put("ceiling", "ceil");
            
            put("ln", "log");
            
            put("rand", "random");

            put("maximum", "max");
            put("minimum", "min");
            put("summation", "sum");
            put("avg", "average");

            put("product", "prod");
        };
    };

    /** @deprecated */
    static String[] FUNC_NAMES = {
        // basic functions
        "sqrt", "cbrt",
        // non-continuous functions
        "signum", "abs", "floor", "ceil", "rint", "frac",
        // trig functions
        "sin", "cos", "tan", "asin", "acos", "atan",
        // exponential functions
        "exp", "log", "log10", "log2",
        // hyperbolic functions
        "sinh", "cosh", "tanh",
        // random
        "random",
        // statistical
        "max", "min", "average", "sum",
        // additional
        "atan2",
        "pow",
        "hypot",
        // other
        "prod", "if"
    };
    
    /** @deprecated */
    static String[] constants = {
        "pi", "e", "i"
    };

}
