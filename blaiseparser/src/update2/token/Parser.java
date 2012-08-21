/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package update2.token;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elisha
 */
public class Parser {
    static final boolean VERBOSE = false;

    public static void main(String[] args) {

        // TREE TEST CASES

        tryWith("[a, b, c, d]",     "TOP[[[,[a, b, c, d]]]");
        tryWith("a+b-c",            "TOP[+[a, -[b, c]]]");
        tryWith("(a-b)+c",          "TOP[+[([-[a, b]], c]]");
        tryWith("a+b*c/d-f^g+h",    "TOP[+[a, -[*[b, /[c, d]], ^[f, g]], h]]");
        tryWith("a+b*(c+d-2)-2+f",  "TOP[+[a, -[*[b, ([+[c, -[d, 2]]]], 2], f]]");
        tryWith("[a b*c]",          "TOP[[[*[a, b, c]]]");
        tryWith("sin(x)-5",         "TOP[-[sin[([x]], 5]]");
        tryWith("a+b*c*d*(e*g^3+-f)(2-3)[5 1,2,7]",
                "TOP[+[a, *[b, c, d, ([+[*[e, ^[g, 3]], -[f]]], ([-[2, 3]], [[,[*[5, 1], 2, 7]]]]]");
        tryWith("+-1+alphabet+b(c-d)*_f(5)+-1.033e-5f /** hi */ a ||b c", 
                "TOP[+[-[1], alphabet, *[b, ([-[c, d]], _f, ([5]], -[*[1.033e-5, f, /**[hi], ||[a, b], c]]]]");

        // 
    }

    public static void tryWith(String input, String expected) {
        try {
            TokenNode tree = parseTree(input);
            System.out.println("in: " + input + "\n" + (tree.toString().equals(expected) ? "okay" : "FAILS!!"));
            System.out.println("              out:      " + tree);
            System.out.println("              expected: " + expected);
        } catch (ParseException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static TokenNode parseTree(String expression) throws ParseException {
        ArrayList<String> tokens = new ArrayList<String>();
        ArrayList<TokenType> types = new ArrayList<TokenType>();
        Tokenizer.tokenize(expression, tokens, types);
        Functions.convertFunctionIdentifiers(tokens, types);
        if (VERBOSE) {
            System.out.println("------------------------------------------------------------");
            System.out.println("Token Table:");
            System.out.println("============");
            for (int i = 0; i < tokens.size(); i++) { System.out.println("  " + tokens.get(i) + "\t\t" + types.get(i)); }
            System.out.println("------------------------------------------------------------");
        }
        TokenNode result = getTopNode(tokens, types);
        if (VERBOSE) System.out.println("Token Tree: " + result.toString());
        return result;
    }



    /** Constructs a tree with specified array of tokens and corresponding token types. */
    static TokenNode getTopNode(List<String> tokens, List<TokenType> types) throws ParseException {
        TokenNode topNode = new GroupNode(null, TokenType.PARENTHETICAL_OPEN, "TOP");
        TokenNode curNode = topNode;
        for (int i = 0; i < types.size(); i++) {
            addToTree(curNode, tokens.get(i), types.get(i));
            if (VERBOSE) System.out.println(topNode.toString());
            if (VERBOSE) System.out.println("Current node: " + curNode.name);
        }
        if (curNode != topNode) {
            throw new ParseException("Expected more input!", ParseException.ParseErrorCode.ENDED_EARLY);
        }
        return topNode;
    }

    /** Adds specified token to tree, and updates tree's position */
    static void addToTree(TokenNode curNode, String token, TokenType type) throws ParseException {
        if (VERBOSE) System.out.println("Adding token " + token + " to tree");
        if ( ! curNode.canAdd(type) )
            throw new ParseException("Cannot add a token of type " + type + " to node " + curNode + "!",
                    ParseException.ParseErrorCode.INVALID_OPERATOR_POSITION);

        // check for implicit multiplication
        if (curNode instanceof GroupNode && curNode.children.size() > 0 &&
                (type==TokenType.NUMBER || type==TokenType.IDENTIFIER || type==TokenType.FUNCTION
                || type==TokenType.PARENTHETICAL_OPEN || type==TokenType.UNARY_OPERATOR )
                ) {
           addToTree(curNode, "*", TokenType.MULTARY_OPERATOR);
        }

        TokenNode newNode;

        switch (type) {
            case FUNCTION:
                // add a function node to the tree, which is the new current node
                curNode = curNode.addNode(new FunctionNode(curNode, type, token));
                break;
            case UNARY_OPERATOR:
                // add a unary operator to the tree, which is the new current node
                curNode = curNode.addNode(new OperatorNode(curNode, type, token));
                break;
            case PARENTHETICAL_OPEN:
                // add a new group node to the tree, which is the new current node
                curNode = curNode.addNode(new GroupNode(curNode, type, token));
                break;

            case NUMBER:
            case IDENTIFIER:
                // add a basic node to the tree, and return to the last open group
                newNode = new BasicNode(curNode, type, token);
                curNode.addNode(newNode);
                curNode = newNode.groupParent();
                break;

            case PARENTHETICAL_CLOSE:
                // close out the current node, and return to the upper level
                if ( curNode instanceof GroupNode && ((GroupNode)curNode).closesWith(token) ) {
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
                TokenNode depthNode = curNode.findChildAtMaximumDepth(Operators.getDepth(token));
                if (depthNode.name.equals(token) && type==TokenType.MULTARY_OPERATOR) {
                    curNode = depthNode;
                } else {
                    TokenNode parentNode = depthNode.parent;
                    parentNode.removeNode(depthNode);

                    newNode = new OperatorNode(parentNode, type, token);
                    parentNode.addNode(newNode);
                    newNode.addNode(depthNode);
                    depthNode.parent = newNode;

                    curNode = newNode;
                }
                break;
        }
    }
}
