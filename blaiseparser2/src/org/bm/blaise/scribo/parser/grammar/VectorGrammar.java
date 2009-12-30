/**
 * BooleanGrammar.java
 * Created on Dec 1, 2009
 */

package org.bm.blaise.scribo.parser.grammar;

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
public class VectorGrammar implements Grammar {

    final static String[] OPS = { " ", ",", ";" };
    final static String[][] PARS = {{"[","]"}};
    final static String[] FUNCS = { "cross", "dot", "identity" };
    final static String[] CONSTANTS = { };

    public String[] operators() { return OPS; }
    public String[][] parentheticals() { return PARS; }
    public String[] functions() { return FUNCS; }
    public String[] constants() { return CONSTANTS; }

    public Map<String, String> synonymMap() { return Collections.EMPTY_MAP; }
    public boolean isCaseSensitive() { return false; }

    public String[] preUnaryOperators() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] postUnaryOperators() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] naryOperators() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] multaryOperators() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
