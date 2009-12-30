/**
 * BooleanGrammar.java
 * Created on Dec 1, 2009
 */

package org.bm.blaise.scribo.parser.grammars;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bm.blaise.scribo.parser.Grammar;

/**
 * <p>
 *    This class describes the supported language elements
 *    of a boolean parsing library.
 * </p>
 * @author Elisha Peterson
 */
public class BooleanGrammar implements Grammar {

    public static final BooleanGrammar INSTANCE = new BooleanGrammar();

    //
    // STATIC DEFINITIONS
    //

    final static String[][] PARS = {{"(", ")"}};

    final static TreeMap<String, Boolean> CONSTANTS = new TreeMap<String, Boolean>() {{
        put("true", true);
        put("false", false);
    }};

    final static Map<String, Method> PRE_OPS = new TreeMap<String, Method>() {{
        try {
            put("!", BooleanGrammar.class.getMethod("not", boolean.class));
              put("not", BooleanGrammar.class.getMethod("not", boolean.class));
        } catch (Exception ex) {
            Logger.getLogger(BooleanGrammar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }};

    final static Map<String, Method> POST_OPS = new TreeMap<String, Method>() {{
        try {
        } catch (Exception ex) {
            Logger.getLogger(BooleanGrammar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }};

    final static Map<String, Method> NARY_OPS = new TreeMap<String, Method>() {{
        try {
            put(",", null);
            put("||", BooleanGrammar.class.getMethod("or", boolean.class, boolean.class));
              put("or", BooleanGrammar.class.getMethod("or", boolean.class, boolean.class));
            put("&&", BooleanGrammar.class.getMethod("and", boolean.class, boolean.class));
              put("and", BooleanGrammar.class.getMethod("and", boolean.class, boolean.class));
            put("!=", BooleanGrammar.class.getMethod("xor", boolean.class, boolean.class));
              put("xor", BooleanGrammar.class.getMethod("xor", boolean.class, boolean.class));
            put("==", BooleanGrammar.class.getMethod("equals", boolean.class, boolean.class));
              put("equals", BooleanGrammar.class.getMethod("equals", boolean.class, boolean.class));
        } catch (Exception ex) {
            Logger.getLogger(BooleanGrammar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }};

    final static String[] MULTARY_OPS = new String[] { };//"||", "or", "&&", "and", "," };
    final static String[] ORDER_OF_OPS = new String[] { "==", "equals", "!=", "||", "or", "&&", "and", "xor" };

    final static Map<String, Method> FUNCTIONS = new TreeMap<String, Method>() {{
        try {
              put("and", BooleanGrammar.class.getMethod("and", boolean.class, boolean.class));
        } catch (Exception ex) {
            Logger.getLogger(BooleanGrammar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }};

    //
    // INTERFACE METHODS
    //

    public boolean isCaseSensitive() { return false; }

    public String[][] parentheticals() { return PARS; }
    public String argumentListOpener() { return "("; }
    public String argumentListSeparator() { return ","; }
    public String implicitSpaceOperator() { return "&&"; }

    public Map<String, Boolean> constants() { return CONSTANTS; }

    public Map<String, Method> preUnaryOperators() { return PRE_OPS; }
    public Map<String, Method> postUnaryOperators() { return POST_OPS; }
    public Map<String, Method> naryOperators() { return NARY_OPS; }
    public String[] multaryOperators() { return MULTARY_OPS; }
    public String[] orderOfOperations() { return ORDER_OF_OPS; }

    public Map<String, Method> functions() { return FUNCTIONS; }


    //
    // STATIC FUNCTIONS TO USE IN SEMANTIC EVALUATION
    //
    
    public static boolean not(boolean x) { return !x; }
    public static boolean and(boolean x, boolean y) { return x && y; }
    public static boolean or(boolean x, boolean y) { return x || y; }
    public static boolean xor(boolean x, boolean y) { return (x && !y) || (!x && y); }
    public static boolean equals(boolean x, boolean y) { return x == y; }
}
