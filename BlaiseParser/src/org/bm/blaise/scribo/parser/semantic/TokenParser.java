/*
 * Parser.java
 * Created Nov 2009
 */

package org.bm.blaise.scribo.parser.semantic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bm.blaise.scribo.parser.Grammar;
import org.bm.blaise.scribo.parser.GrammarParser;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.scribo.parser.SemanticNode;

/**
 * <p>
 *   This class converts an input String into a tree of tokens with associated
 *   token types. The procedure is to compile regular expression patterns based
 *   upon an underlying grammar, then to use these to parse the expressions into tokens,
 *   and finally to place the tokens into a tree format.
 * </p>
 * <p>
 *   The primary method of interest is <code>parseTree</code>, which handles the conversion
 *   of a string input into a token tree (returning the top node of that tree).
 *   This method depnds heavily on the private method <code>tokenize</code>, which converts a
 *   string input expression into lists of tokens and corresponding types.
 *   This class will lump all identifiers into a single type, so it is the responsibility
 *   of the <i>parser</i> to separate identifiers into multiple types (variables,
 *   constants, and functions).
 * </p>
 * <p>
 *   Here are some hints on formats supported by the tokenizer:
 *   <ul>
 *      <li>Identifiers <b>MUST</b> consist of letters, numbers, or underscores, and the first
 *          letter in an expression <b>MUST</b> be a letter.</li>
 *      <li>Numbers may be in any valid exponential format, e.g. 1.234 or 1.234e-5</li>
 *      <li>Parenthetical expressions are dynamic and depend solely on the parser.
 *          Exceptions are generated if the parenthetical expressions do not close properly.</li>
 *      <li>Operator expressions are dynamic and also depend solely on the parser.</li>
 *   </ul>
 * </p>
 *
 * @author elisha
 */
public class TokenParser implements GrammarParser {

    /** Debugging constant */
    static final boolean VERBOSE = false;

    /** The grammar used to parse expressions. */
    Grammar grammar;
    /** Tokenizer built from the grammar. */
    Tokenizer tokenizer;
    /** Class responsible for constructing the semantic tree from the token node. */
    SemanticTreeBuilder builder;

    /** Set up a parser with a specified grammar. */
    public TokenParser(Grammar grammar) {
        setGrammar(grammar);
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
        tokenizer = new Tokenizer(grammar);
        builder = new SemanticTreeBuilder(grammar);
    }

    /**
     * Converts a string expression into a token tree.
     * @param expression the string input to the parser
     * @return a <code>SemanticNode</code> which is the top node in the tree
     * @throws ParseException if the parser fails in some way to parse the input
     */
    public SemanticNode parseTree(String expression) throws ParseException {
        return builder.buildTree(tokenizeTree(expression));
    }

    /**
     * Converts a string expression into a token tree.
     * @param expression the string input to the parser
     * @return a <code>SemanticNode</code> which is the top node in the tree
     * @throws ParseException if the parser fails in some way to parse the input
     */
    TokenNode tokenizeTree(String expression) throws ParseException {
        ArrayList<String> tokens = new ArrayList<String>();
        ArrayList<TokenType> types = new ArrayList<TokenType>();
        tokenizer.tokenize(expression, tokens, types);
        convertFunctionTypes(tokens, types, grammar);
        if (VERBOSE) {
            System.out.println("------------------------------------------------------------");
            System.out.println("Token Table:");
            System.out.println("============");
            for (int i = 0; i < tokens.size(); i++) { System.out.println("  " + tokens.get(i) + "\t\t" + types.get(i)); }
            System.out.println("------------------------------------------------------------");
        }
        TokenNode topNode = new TokenNode.GroupNode(null, TokenType.PARENTHETICAL_OPEN, "TOP", "END");
        TokenNode curNode = topNode;
        for (int i = 0; i < types.size(); i++) {
            curNode = addToTree(curNode, tokens.get(i), types.get(i));
            if (VERBOSE) System.out.println("=" + topNode.toString());
        }
        if (curNode != topNode) {
            throw new ParseException("Expected more input!", ParseException.ParseErrorCode.ENDED_EARLY);
        }
        return topNode;
    }

    /** 
     * Adds specified token to tree, and updates tree's position;
     * Returns the new current node.
     */
    TokenNode addToTree(TokenNode curNode, String token, TokenType type) throws ParseException {
        if (VERBOSE) System.out.println("Adding token " + token + " to tree" + " at current node " + curNode.name);
        if ( ! curNode.canAdd(type) )
            throw new ParseException("Cannot add a token of type " + type + " to node " + curNode + "!",
                    ParseException.ParseErrorCode.INVALID_OPERATOR_POSITION);

        // check for implicit multiplication
        if (curNode instanceof TokenNode.GroupNode && curNode.children.size() > 0 &&
                (type==TokenType.NUMBER || type==TokenType.IDENTIFIER || type==TokenType.FUNCTION
                || type==TokenType.PARENTHETICAL_OPEN || type==TokenType.PRE_UNARY_OPERATOR )
                ) {
           curNode = addToTree(curNode, grammar.implicitSpaceOperator(), TokenType.MULTARY_OPERATOR);
        }

        TokenNode newNode;

        switch (type) {
            case FUNCTION:
                // add a function node to the tree, which is the new current node
                curNode = curNode.addNode(new TokenNode.FunctionNode(curNode, type, token));
                break;
            case PRE_UNARY_OPERATOR:
                // add a unary operator to the tree, which is the new current node
                curNode = curNode.addNode(new TokenNode.OperatorNode(curNode, type, token, Integer.MAX_VALUE));
                break;
            case POST_UNARY_OPERATOR:
                // add a post-unary operator to the tree, which acts on the last added node
                TokenNode last = curNode.getLastDescendant();
                TokenNode parentLast = last.parent;
                parentLast.removeNode(last);
                newNode = new TokenNode.OperatorNode(parentLast, type, token, Integer.MAX_VALUE);
                parentLast.addNode(newNode);
                newNode.addNode(last);
                curNode = newNode.groupParent();
                break;
            case PARENTHETICAL_OPEN:
                // add a new group node to the tree, which is the new current node
                curNode = curNode.addNode(new TokenNode.GroupNode(curNode, type, token, closingToken(token, grammar)));
                break;

            case NUMBER:
            case IDENTIFIER:
                // add a basic node to the tree, and return to the last open group
                newNode = new TokenNode.BasicNode(curNode, type, token);
                curNode.addNode(newNode);
                curNode = newNode.groupParent();
                break;

            case PARENTHETICAL_CLOSE:
                // close out the current node, and return to the upper level
                if ( curNode instanceof TokenNode.GroupNode && ((TokenNode.GroupNode)curNode).closesWith(token) ) {
                    curNode = curNode.groupParent();
                } else {
                    throw new ParseException(
                            "Cannot close the node " + curNode + " with the token " + token,
                            ParseException.ParseErrorCode.PARENTHETICAL_ERROR
                            );
                }
                break;
            case BINARY_OPERATOR:
            case MULTARY_OPERATOR:
                // add operator to the tree at appropriate depth, and set current node to be that operator
                int depth = operatorDepth(token, grammar);
                TokenNode depthNode = curNode.findChildAtMaximumDepth(depth);
                if (VERBOSE) System.out.println("  found child " + depthNode.name + " @ max depth " + depth + " from node " + curNode.name);
                if (depthNode.name.equals(token) && depthNode.type == type && type == TokenType.MULTARY_OPERATOR) {
                    curNode = depthNode;
                } else {
                    TokenNode parentNode = depthNode.parent;
                    parentNode.removeNode(depthNode);
                    newNode = new TokenNode.OperatorNode(parentNode, type, token, depth);
                    parentNode.addNode(newNode);
                    newNode.addNode(depthNode);
                    curNode = newNode;
                }
                break;
        }
        return curNode;
    }

    /**
     * This class is responsible for converting a string expression into a list of tokens.
     * It uses a grammar to determine the types of the tokens.
     */
    static class Tokenizer {
        /** The underlying grammar. */
        Grammar grammar;

        /** Construct with a given grammar. */
        public Tokenizer(Grammar grammar) {
            this.grammar = grammar;
            buildPatterns();
        }

        //
        // STATIC & DYNAMIC PATTERNS
        //

        // numeric patterns
        static String $INT = "[0-9]+";
        static String $FRAC = "\\.[0-9]+";
        static String $EXP = "([Ee](\\+|-)?[0-9]+)";
        static String $FLT1 = $INT+"?"+$FRAC+$EXP+"?";
        static String $FLT2 = $INT+"\\."+$EXP+"?";
        static String $FLT3 = $INT+$EXP;
        static String $FLOAT = "("+$FLT1+"|"+$FLT2+"|"+$FLT3+"|"+$INT+"|"+$FRAC+")";
        static Pattern P$FL = Pattern.compile($FLOAT);

        // generic identifier pattern
        static String $IDENTIFIER = "([_A-Za-z][_A-Za-z0-9]*)";
        static Pattern P$IDENTIFIER = Pattern.compile($IDENTIFIER);

        /** parenthetical patterns */
        Pattern P$PAR_OPEN, P$PAR_CLOSE;
        /** unary patterns */
        Pattern P$PRE_UNARY, P$POST_UNARY;
        /** nary patterns */
        Pattern P$NARY, P$MULTARY;

        /** Uses the currently defined grammar to construct regex patterns. */
        private void buildPatterns() {
            P$PAR_OPEN = buildRegEx(grammar.parentheticals(), 0);
            P$PAR_CLOSE = buildRegEx(grammar.parentheticals(), 1);
            P$PRE_UNARY = buildRegEx(grammar.preUnaryOperators().keySet());
            P$POST_UNARY = buildRegEx(grammar.postUnaryOperators().keySet());
            P$NARY = buildRegEx(grammar.naryOperators().keySet());
            P$MULTARY = buildRegEx(grammar.multaryOperators());
        }

        //
        // TOKENIZING METHODS
        //

        /**
         * Translates provided input into a String of tokens. Places results at the end of specified arrays.
         *
         * @param input
         * @param tokens
         * @param types
         */
        public void tokenize(String input, ArrayList<String> tokens, ArrayList<TokenType> types) throws ParseException {
            if (VERBOSE) System.out.println("Tokenizing string " + input);
            input = input.trim(); // remove whitespace
            if (input.length()==0) return;

            // switch here based on whether one permits binary/multary operators to follow the last token or not
            TokenType lastType = types.isEmpty() ? TokenType.PARENTHETICAL_OPEN : types.get(types.size() - 1);
            Matcher match;
            if (lastType.naryFollow) {
                match = startMatcher(input, P$FL, P$PAR_OPEN, P$PAR_CLOSE, P$MULTARY, P$NARY, P$POST_UNARY, P$IDENTIFIER);
            } else {
                match = startMatcher(input, P$FL, P$PAR_OPEN, P$PAR_CLOSE, P$PRE_UNARY, P$IDENTIFIER);
            }

            if (match == null) {
                throw new ParseException("No matching token for " + input,
                        ParseException.ParseErrorCode.UNRECOGNIZED_SYMBOL);
            }

            Pattern p = match.pattern();
            TokenType addType =
                    p==P$IDENTIFIER ? TokenType.IDENTIFIER
                    : p==P$FL ? TokenType.NUMBER
                    : p==P$PAR_OPEN ? TokenType.PARENTHETICAL_OPEN
                    : p==P$PAR_CLOSE ? TokenType.PARENTHETICAL_CLOSE
                    : p==P$PRE_UNARY ? TokenType.PRE_UNARY_OPERATOR
                    : p==P$POST_UNARY ? TokenType.POST_UNARY_OPERATOR
                    : p==P$MULTARY ? TokenType.MULTARY_OPERATOR
                    : p==P$NARY ? TokenType.BINARY_OPERATOR
                    : null;

            if (addType == null) {
                throw new ParseException("Unable to retrieve token type from pattern... this should never happen!");
            }

            tokens.add(match.group());
            types.add(addType);
            tokenize( input.substring(match.end()), tokens, types );
        }
    }


    //
    // UTILITY METHODS
    //

    /**
     * Determines whether specified token is a function token. Uses the grammar to
     * look up the names of functions and their synonyms. If the grammar is case-insensitive,
     * so is this method.
     * @param idToken a string identifier that may represent a function
     * @param grammar the grammar to check against
     * @return <code>true</code> if the grammar recognizes the token as a function
     */
    static boolean isFunctionToken(String idToken, Grammar grammar) {
        if (grammar.isCaseSensitive()) {
            return grammar.functions().keySet().contains(idToken);
        } else {
            for (String s : grammar.functions().keySet()) {
                if (s.equalsIgnoreCase(idToken)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 
     * Looks through a list of tokens with associated types, and converts any identifiers
     * that are recognized by the grammar as functions to be of type <code>TokenType.FUNCTION</code>.
     * The change takes place in the list of types.
     * @param funcTokens a list of strings representing the tokens to check
     * @param tokenTypes a list of token types, of the same length as the <code>tokens</code> parameter,
     *      representing the types associated with each token; this argument will generally be modified
     * @param grammar the grammar to check tokens against
     */
     static void convertFunctionTypes(List<String> funcTokens, List<TokenType> tokenTypes, Grammar grammar) {
        for (int i = 0; i < funcTokens.size(); i++) {
            if ( tokenTypes.get(i) == TokenType.IDENTIFIER && isFunctionToken(funcTokens.get(i), grammar) ) {
                tokenTypes.set(i, TokenType.FUNCTION);
            }
        }
    }

    /**
     * Returns the string that closes the provided parenthetical opening token.
     * @param openToken a parenthetical opening token
     * @param grammar the grammar to check tokens against
     * @return the string token that closes the provided opening token, or <code>null</code>
     *      if there is no such token
     */
    static String closingToken(String openToken, Grammar grammar) {
        String[][] par = grammar.parentheticals();
        for (int i = 0; i < par.length; i++) {
            if (sensitiveEquals(openToken, par[i][0], grammar.isCaseSensitive()))
                return par[i][1];
        }
        return null;
    }

    /**
     * Computes the depth of the operator token in the provided grammar.
     * @param opToken the operator token
     * @param grammar the grammar to check tokens against
     * @return an integer representing the index of the token in the grammar's
     *      <i>n-ary operators</i>, or -1 if the grammar does not contain the provided
     *      token as an n-ary operator.
     */
    static int operatorDepth(String opToken, Grammar grammar) {
        String[] ops = grammar.orderOfOperations();
        for (int i = 0; i < ops.length; i++) {
            if (sensitiveEquals(opToken, ops[i], grammar.isCaseSensitive()))
                return i;
        }
        return -1;
    }

    /**
     * Tests for equality of strings with or without case sensitivity.
     * @param string1 the first string
     * @param string2 the second string
     * @param caseSensitive <code>true</code> if the checking is case sensitive, otherwise false
     * @return <code>true</code> if the strings are equal under supplied sensitivity, or if both are null
     */
    static boolean sensitiveEquals(String string1, String string2, boolean caseSensitive) {
        if (string1 == null)
            return string2 == null;
        return caseSensitive ? string1.equals(string2) : string1.equalsIgnoreCase(string2);
    }

    /** Constructs a regex that checks for any string in the provided array. */
    static Pattern buildRegEx(Collection<String> arr) {
        if (arr.size() == 0)
            return null;
        String result = "(";
        int i = 0;
        for (String s : arr) {
            if (i != 0) {
                result += "|";
            }
            result += "\\Q" + s + "\\E";
            i++;
        }
        result += ")";
        return Pattern.compile(result);
    }

    /** Constructs a regex that checks for any string in the provided array. */
    static Pattern buildRegEx(String[] arr) {
        if (arr.length == 0)
            return null;
        String result = "(\\Q" + arr[0] + "\\E";
        for (int i = 1; i < arr.length; i++) {
            result += "|\\Q" + arr[i] + "\\E";
        }
        result += ")";
        return Pattern.compile(result);
    }

    /** Constructs a regex that checks for any string in the provided array. */
    static Pattern buildRegEx(String[][] arr, int index) {
        if (arr.length == 0)
            return null;
        String result = "(\\Q" + arr[0][index] + "\\E";
        for (int i = 1; i < arr.length; i++) {
            result += "|\\Q" + arr[i][index] + "\\E";
        }
        result += ")";
        return Pattern.compile(result);
    }

    /**
     * Attempts to match the start of the input string against an array of regex patterns.
     * Returns a matcher corresponding to the first pattern that fits the beginning of the input string,
     * or null if nothing is matched
     */
    static Matcher startMatcher(String input, Pattern... patterns) {
        Matcher m = null;
        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i] == null) continue;
            if (VERBOSE) System.out.print("  attempting to match pattern " + patterns[i] + " with input " + input);
            m = patterns[i].matcher(input);
            if (m.find() && m.start()==0) { if (VERBOSE) System.out.println(" ... success!!!"); return m; }
            if (VERBOSE) System.out.println(" ... failed");
        }
        return null;
    }
    
}
