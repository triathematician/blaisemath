/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.parser;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ae3263
 */
public class TestGrammar implements Grammar {
    public boolean isCaseSensitive() { return false; }
    public String argumentListOpener() { return "("; }
    public String argumentListSeparator() { return ")"; }
    public String implicitSpaceOperator() { return "*"; }
    public String[][] parentheticals() { return new String[][] { {"(",")"}, {"/**", "*/"}, {"[", "]"} }; }
    public Map<String, ? extends Object> constants() { return Collections.EMPTY_MAP; }
    public Map<String, Method> preUnaryOperators() { return asNullMap("+", "-", "!"); }
    public Map<String, Method> postUnaryOperators() { return asNullMap("!"); }
    public Map<String, Method> naryOperators() { return asNullMap(",", "+", "-", " ", "*", "/", "^", "%", "&&", "||"); }
    public String[] multaryOperators() { return new String[] { "+", "*", "&&", "||", "," }; }
    public String[] orderOfOperations() { return new String[] {",", "+", "-", " ", "*", "/", "^", "%", "&&", "||"}; }
    public Map<String, Method> functions() { return asNullMap("sin"); }

    static Map<String, Method> asNullMap(String... arr) {
        Map<String, Method> result = new HashMap<String, Method>();
        for (int i = 0; i < arr.length; i++) {
            result.put(arr[i], null);
        }
        return result;
    }
}
