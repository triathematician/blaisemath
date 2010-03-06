/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package update2.token;

/**
 *
 * @author elisha
 */
public class Parentheticals {
    static String[][] $_TOKENS = {
        {"TOP", ""},
        {"(", ")"},
        {"[", "]"},
        {"{", "}"},
        {"/**", "*/"}
    };

    /** Returns true if opening is supported, otherwise false. */
    public static boolean validOpening(String opening) {
        for (int i = 0; i < $_TOKENS.length; i++) {
            if ($_TOKENS[i][0].equals(opening)) return true;
        }
        return false;
    }

    /** Returns closing token for provided opening, or null if opening is not supported. */
    public static String getClose(String opening) {
        for (int i = 0; i < $_TOKENS.length; i++) {
            if ($_TOKENS[i][0].equals(opening)) return $_TOKENS[i][1];
        }
        return null;
    }

    /** Returns true if provided tokens match. */
    static boolean match(String opening, String closing) {
        for (int i = 0; i < $_TOKENS.length; i++) {
            if ($_TOKENS[i][0].equals(opening) && $_TOKENS[i][1].equals(closing)) return true;
        }
        return false;
    }
}
