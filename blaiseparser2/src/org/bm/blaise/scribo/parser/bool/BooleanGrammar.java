/**
 * BooleanGrammar.java
 * Created on Dec 1, 2009
 */

package org.bm.blaise.scribo.parser.bool;

import java.util.Collections;
import java.util.Map;
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
    // STATIC MAPS
    //
    
    final static String[][] PARS = {{"(",")"}};

    final static String[] PRE_OPS = { "!", "not" };
    final static String[] POST_OPS = {};
    final static String[] NARY_OPS = { ",", "||", "or", "&&", "and", "xor" };
    final static String[] MULTARY_OPS = { "||", "or", "&&", "and", "," };
    final static String[] FUNCS = { };
    final static String[] CONSTANTS = { "true", "false" };

    //
    // INTERFACE METHODS
    //

    public boolean isCaseSensitive() { return false; }

    public String[][] parentheticals() { return PARS; }
    public String argumentListOpener() { return "("; }
    public String argumentListSeparator() { return ","; }

    public Map<String, ? extends Object> constants() { return CONSTANTS; }



    //
    // STATIC FUNCTIONS TO USE IN SEMANTIC EVALUATION
    //
    
    public static boolean not(boolean x) { return !x; }
    public static boolean and(boolean x, boolean y) { return x && y; }
    public static boolean or(boolean x, boolean y) { return x || y; }
    public static boolean xor(boolean x, boolean y) { return (x && !y) || (!x && y); }
}
