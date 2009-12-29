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

    final static String[] PRE_OPS = { "!", "not" };
    final static String[] POST_OPS = {};
    final static String[] NARY_OPS = { ",", "||", "or", "&&", "and", "xor" };
    final static String[] MULTARY_OPS = { "||", "or", "&&", "and", "," };
    final static String[][] PARS = {{"(",")"}};
    final static String[] FUNCS = { };
    final static String[] CONSTANTS = { "true", "false" };

    public String[] preUnaryOperators() { return PRE_OPS; }
    public String[] postUnaryOperators() { return POST_OPS; }
    public String[] naryOperators() { return NARY_OPS; }
    public String[] multaryOperators() { return MULTARY_OPS; }
    public String[][] parentheticals() { return PARS; }
    public String[] functions() { return FUNCS; }
    public String[] constants() { return CONSTANTS; }
    
    public Map<String, String> synonyms() { return Collections.EMPTY_MAP; }
    public boolean isCaseSensitive() { return false; }


}
