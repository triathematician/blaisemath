/**
 * RealGrammar.java
 * Created on Dec 1, 2009
 */

package org.bm.blaise.scribo.parser.real;

import java.util.Map;
import java.util.TreeMap;
import org.bm.blaise.scribo.parser.Grammar;

/**
 * <p>
 *    This class describes the supported language elements
 *    of a real-variable parsing library.
 * </p>
 * @author Elisha Peterson
 */
public class RealGrammar implements Grammar {

    public static RealGrammar INSTANCE = new RealGrammar();

    final static String[] PRE_OPS = { "+", "-" };
    final static String[] POST_OPS = { "!" };
    final static String[] NARY_OPS = { ",", "+", "-", " ", "*", "/", "^", "%" };
    final static String[] MULTARY_OPS = { "+", "*", "," };
    final static String[][] PARS = {{"(",")"}};
    final static String[] FUNCS = {
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
        "max", "min", "average",
        // additional
        "atan2", "pow", "hypot",
        // other
        "sum", "prod", "if" };
    final static String[] CONSTANTS = { "pi", "e" };

    /** Describes alternate expressions of the same underlying function */
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

    public String[] preUnaryOperators() { return PRE_OPS; }
    public String[] postUnaryOperators() { return POST_OPS; }
    public String[] naryOperators() { return NARY_OPS; }
    public String[] multaryOperators() { return MULTARY_OPS; }
    public String[][] parentheticals() { return PARS; }
    public String[] functions() { return FUNCS; }
    public String[] constants() { return CONSTANTS; }

    public Map<String, String> synonyms() { return SYNONYM_MAP; }
    public boolean isCaseSensitive() { return false; }

}
