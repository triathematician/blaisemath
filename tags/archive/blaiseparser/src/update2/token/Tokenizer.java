/*
 * Tokenizer.java
 * Created on Nov 25, 2009
 */

package update2.token;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>
 *   This class converts an input String into an array of tokens, along with
 *   a corresponding array of token types.
 * </p>
 * @author Elisha Peterson
 */
public class Tokenizer {

    private static final boolean VERBOSE = false;

    public static void main(String[] args) {
//        String $IDENTIFIER = "([_A-za-z][a-z]*)";
//        Matcher mp = Pattern.compile($IDENTIFIER).matcher("alphabet+b");
//        mp.find();
//        System.out.println(mp.group());
//        System.out.println(mp.start());

        String input = "+-1+alphabet+b(c-d)*_f(5)+-1.033e-5f /** hi */ a ||b c";
        ArrayList<String> tokens = new ArrayList<String>();
        ArrayList<TokenType> types = new ArrayList<TokenType>();
        try {
            tokenize(input, tokens, types);
            System.out.println("Input = " + input);
            System.out.println("Result...");
            for (int i = 0; i < tokens.size(); i++) {
                System.out.println("  " + tokens.get(i) + "\t\t" + types.get(i));
            }
        } catch (ParseException ex) {
            Logger.getLogger(Tokenizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static String $INT = "[0-9]+";
    static String $FRAC = "\\.[0-9]+";
    static String $EXP = "([Ee](\\+|-)?[0-9]+)";
    static String $FLT1 = $INT+"?"+$FRAC+$EXP+"?";
    static String $FLT2 = $INT+"\\."+$EXP+"?";
    static String $FLT3 = $INT+$EXP;
    static String $FLOAT = "("+$FLT1+"|"+$FLT2+"|"+$FLT3+"|"+$INT+"|"+$FRAC+")";

    static String $IDENTIFIER = "([_A-Za-z][_A-Za-z0-9]*)";

//    static String[] parOpen =  { "(", "[", "{", "/**", "\"", "!--" };
//    static String[] parClose = { ")", "]", "}", "*/",  "\"", "-!" };
    static String $PAROPEN = "([\\(\\[\\{]|\\Q/**\\E|\\Q!--\\E)";
    static String $PARCLOSE = "([\\)\\]\\}]|\\Q*/\\E|\\Q-!\\E)";

//    static String[] punctuation = { ",", ";" };
//    static String[] binary = { "==", "!=", ">=", "<=", ">", "<", "=", ":=", "-", "/", "^", "&&", "||" };
//    static String[] multary = { "*", "+" };
//    static String[] unary = { "!", "+", "-" };
    static String $UNARY = "([!\\+\\-])";
    static String $MULTARY = "([\\+\\*,;])";
    static String $BIN1 = "[\\-/\\^><=%]";
    static String $BIN2 = "==|!=|>=|<=|:=|&&|\\Q||\\E";
    static String $BINARY = "(" + $BIN2 + "|" + $BIN1 + ")";

    static Pattern P$ID = Pattern.compile($IDENTIFIER);
    static Pattern P$FL = Pattern.compile($FLOAT);
    static Pattern P$PO = Pattern.compile($PAROPEN);
    static Pattern P$PC = Pattern.compile($PARCLOSE);
    static Pattern P$UN = Pattern.compile($UNARY);
    static Pattern P$MU = Pattern.compile($MULTARY);
    static Pattern P$BI = Pattern.compile($BINARY);
    
    /** 
     * Translates provided input into a String of tokens. Places results at the end of specified arrays.
     *
     * @param input
     * @param tokens
     * @param types
     */
    public static void tokenize(String input, ArrayList<String> tokens, ArrayList<TokenType> types) throws ParseException {
        if (VERBOSE) System.out.println("Tokenizing string " + input);
        input = input.trim(); // remove whitespace
        if (input.length()==0) return;

        // switch here based on whether one permits binary/multary operators to follow the last token or not
        TokenType lastType = types.isEmpty() ? TokenType.PARENTHETICAL_OPEN : types.get(types.size() - 1);
        Matcher match;
        if (lastType.binaryFollow) {
            match = startMatcher(input, P$ID, P$FL, P$PO, P$PC, P$MU, P$BI);
        } else {
            match = startMatcher(input, P$ID, P$FL, P$PO, P$PC, P$UN);
        }

        if (match == null) { 
            throw new ParseException("No matching token for " + input,
                    ParseException.ParseErrorCode.UNRECOGNIZED_SYMBOL);
        }

        Pattern p = match.pattern();
        TokenType addType =
                p==P$ID ? TokenType.IDENTIFIER
                : p==P$FL ? TokenType.NUMBER
                : p==P$PO ? TokenType.PARENTHETICAL_OPEN
                : p==P$PC ? TokenType.PARENTHETICAL_CLOSE
                : p==P$UN ? TokenType.UNARY_OPERATOR
                : p==P$BI ? TokenType.BINARY_OPERATOR
                : p==P$MU ? TokenType.MULTARY_OPERATOR
                : null;

        if (addType == null) {
            throw new ParseException("Unable to retrieve token type from pattern... this should never happen!");
        }

        tokens.add(match.group());
        types.add(addType);
        tokenize( input.substring(match.end()), tokens, types );
    }

    /**
     * Attempts to match the start of the input string against an array of regex patterns.
     * Returns a matcher corresponding to the first pattern that fits the beginning of the input string,
     * or null if nothing is matched
     */
    private static Matcher startMatcher(String input, Pattern... patterns) {
        Matcher m = null;
        for (int i = 0; i < patterns.length; i++) {
            if (VERBOSE) System.out.print("  attempting to match pattern " + patterns[i] + " with input " + input);
            m = patterns[i].matcher(input);
            if (m.find() && m.start()==0) { if (VERBOSE) System.out.println(" ... success!!!"); return m; }
            if (VERBOSE) System.out.println(" ... failed");
        }
        return null;
    }
}
