// TODO - this should probably be removed in favor of using grammars.

package org.bm.blaise.scribo.parser;

/**
 *
 * @deprecated
 * @author elisha
 */
class Parentheticals {
    /** @deprecated */
    static String[][] $_TOKENS = {
        {"TOP", ""},
        {"(", ")"},
        {"[", "]"},
        {"{", "}"},
        {"/**", "*/"}
    };

    /** Returns true if opening is supported, otherwise false. */
    /** @deprecated */
    static boolean validOpening(String opening) {
        for (int i = 0; i < $_TOKENS.length; i++) {
            if ($_TOKENS[i][0].equals(opening)) return true;
        }
        return false;
    }

    /** Returns closing token for provided opening, or null if opening is not supported. */
    /** @deprecated */
    static String getClose(String opening) {
        for (int i = 0; i < $_TOKENS.length; i++) {
            if ($_TOKENS[i][0].equals(opening)) return $_TOKENS[i][1];
        }
        return null;
    }

    /** Returns true if provided tokens match. */
    /** @deprecated */
    static boolean match(String opening, String closing) {
        for (int i = 0; i < $_TOKENS.length; i++) {
            if ($_TOKENS[i][0].equals(opening) && $_TOKENS[i][1].equals(closing)) return true;
        }
        return false;
    }
}
