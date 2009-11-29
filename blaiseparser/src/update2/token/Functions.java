/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package update2.token;

import java.util.List;

/**
 *
 * @author elisha
 */
public class Functions {

    static String[] functions = {
        "sin", "cos", "tan",
        "asin", "acos", "atan", "arcsin", "arccos", "arctan",
        "sinh", "cosh",
        "exp", "log", "log2", "log10", "pow", "sqrt",
        "abs", "floor", "ceil", "round",
        "sum", "prod", "product", "avg", "max", "min",
        "rand",
        "if"
    };
    static String[] constants = {
        "pi", "e", "i"
    };

    private static boolean isFunction(String token) {
        for (int i = 0; i < functions.length; i++) {
            if (functions[i].equalsIgnoreCase(token)) return true;
        }
        return false;
    }

    /** Converts all identifiers that represent functions listed here to function types. */
    public static void convertFunctionIdentifiers(List<String> tokens, List<TokenType> types) {
        for (int i = 0; i < tokens.size(); i++) {
            if ( types.get(i)==TokenType.IDENTIFIER && isFunction(tokens.get(i)) ) {
                types.set(i, TokenType.FUNCTION);
            }
        }
    }

}
