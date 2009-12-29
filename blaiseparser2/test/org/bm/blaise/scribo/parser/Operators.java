// TODO - this should probably be removed in favor of using grammars.

package org.bm.blaise.scribo.parser;

import java.util.TreeMap;

/**
 *
 * @deprecated
 * @author elisha
 */
class Operators {

    /** Describes alternate expressions of the same underlying operator */
    /** @deprecated */
    static TreeMap<String,String> SYNONYM_MAP = new TreeMap<String,String>(){
        {
            put("plus", "+");
            put("minus", "-");
            put("times", "*");
            put("dividedby", "/");
            put("tothepower", "^");
            put("**", "^");
            put("mod", "%");
            put("modulo", "%");
            put("factorial", "!");
            put("and", "&&");
            put("or", "||");
            put("not", "!");
            put("equals", "==");
            put("greaterthan", ">");
            put("lessthan", "<");            
        };
    };

    /** 
     * Allowed tokens for operators, in order from lowest precedence to highest,
     * i.e. the further down the list, the further down the tree, and the first to evaluate
     */
    /** @deprecated */
    static String[] $_TOKENS = {
      ";", ",",
      "=", ":=",
      "==", "!=", ">=", "<=", ">", "<",
      "+", "-", " ", "*", "/", "^", "%",
      "||", "&&", "!"
    };

    /** 
     * Returns relative depth of the provided token, or -1 if the
     * operator has not been defined.
     */
    /** @deprecated */
    static int depthOf(String token) {
        for (int i = 0; i < $_TOKENS.length; i++) {
            if ($_TOKENS[i].equals(token)) return i;
        }
        return -1;
    }

}
