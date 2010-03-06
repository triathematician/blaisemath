/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package update2.token;

/**
 *
 * @author elisha
 */
public class Operators {

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
    public static int getDepth(String token) {
        for (int i = 0; i < $_TOKENS.length; i++) {
            if ($_TOKENS[i].equals(token)) return i;
        }
        return -1;
    }

}
