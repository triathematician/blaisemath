/**
 * BooleanTreeBuilder.java
 * Created on Dec 26, 2009
 */

package org.bm.blaise.scribo.parser.bool;

import java.lang.reflect.Method;
import org.bm.blaise.scribo.parser.*;
import org.bm.blaise.scribo.parser.ParseException.ParseErrorCode;
import org.bm.blaise.scribo.parser.semantic.*;

/**
 * <p>
 *    This class constructs a tree representing functions on booleans.
 * </p>
 * @author Elisha Peterson
 */
public class BooleanTreeBuilder implements SemanticTreeBuilder {

    public static final BooleanTreeBuilder INSTANCE = new BooleanTreeBuilder();

    /**
     * Constructs a semantic tree representation of the string input.
     * @param string the string representation of a real function
     * @return the top node of a semantic tree
     */
    public SemanticNode buildTree(String string) throws ParseException {
        return buildTree(new Parser(BooleanGrammar.INSTANCE).parseTree(string));
    }

    public SemanticNode buildTree(TokenNode tokenNode) throws ParseException {
        String name = tokenNode.getName();
        TokenType type = tokenNode.getType();
        switch (type) {

            case IDENTIFIER:
                if (name.equalsIgnoreCase("true")) {
                    return new SemanticConstantNode("true", true);
                } else if (name.equalsIgnoreCase("false")) {
                    return new SemanticConstantNode("false", false);
                }
                return new SemanticVariableNode(name, Boolean.class);

            case NUMBER:
                throw new ParseException("Cannot evaluate bool expressions with numbers!", ParseErrorCode.UNSUPPORTED_FEATURE);

            case FUNCTION:
                throw new ParseException("No functions on bool expressions are currently supported!", ParseErrorCode.UNSUPPORTED_FEATURE);

            case PARENTHETICAL_OPEN:
                // ignore the parenthetical, as it is implied by the tree's structure
                if (tokenNode.getChildCount() > 0)
                    return buildTree((TokenNode) tokenNode.firstChild());
                else
                    throw new ParseException("Empty expression", ParseException.ParseErrorCode.EMPTY_EXPRESSION);

            case UNARY_OPERATOR:
            case POST_UNARY_OPERATOR:
                SemanticNode arg = buildTree((TokenNode) tokenNode.firstChild());
                if (name.equals("!") || name.equalsIgnoreCase("not"))
                    return new SemanticMethodNode(lookupFunction("not", 1), arg);
                throw new ParseException("Unsupported unary operator " + name, ParseException.ParseErrorCode.UNRECOGNIZED_SYMBOL);

            case BINARY_OPERATOR:
                SemanticNode arg1 = buildTree((TokenNode) tokenNode.getChildAt(0));
                SemanticNode arg2 = buildTree((TokenNode) tokenNode.getChildAt(1));
                if (name.equals("xor")) {
                    return new SemanticMethodNode(lookupFunction("xor", 2), arg1, arg2);
                }
                throw new ParseException("Unsupported binary operator " + name, ParseException.ParseErrorCode.UNRECOGNIZED_SYMBOL);

            case MULTARY_OPERATOR:
                SemanticNode[] args2 = new SemanticNode[tokenNode.getChildCount()];
                for (int i = 0; i < args2.length; i++) {
                    args2[i] = buildTree((TokenNode) tokenNode.getChildAt(i));
                }
                if (name.equals("&&") || name.equalsIgnoreCase("and")) {
                    return new SemanticIteratedMethodNode(lookupFunction("and", 2), args2);
                } else if (name.equals("||") || name.equalsIgnoreCase("or")) {
                    return new SemanticIteratedMethodNode(lookupFunction("or", 2), args2);
                }
                throw new ParseException("Unsupported multary operator " + name, ParseException.ParseErrorCode.UNRECOGNIZED_SYMBOL);

            case PARENTHETICAL_CLOSE:
                throw new IllegalArgumentException("Unexpected node type: " + type);
        }

        return null;
    }


    /**
     * Looks for function of given name; inputs and outputs must be doubles.
     * @param name the name of the function
     * @param nArgs the number of arguments of the function (of type double)
     */
    public Method lookupFunction(String name, int nArgs) throws ParseException {
        String lookupName = name.toLowerCase();
        Method m = null;
        try {
            if (nArgs == 0)
                m = Math.class.getMethod(lookupName);
            else if (nArgs == 1)
                m = Math.class.getMethod(lookupName, boolean.class);
            else if (nArgs == 2)
                m = Math.class.getMethod(lookupName, boolean.class, boolean.class);
            else if (nArgs == -1)
                m = Math.class.getMethod(lookupName, boolean[].class);
        } catch (NoSuchMethodException ex) {
        } catch (SecurityException ex) {
        }
        if (m!= null && m.getReturnType() != boolean.class) {
            m = null;
        }
        if (m == null) {
            try {
                if (nArgs == 0)
                    m = BooleanTreeBuilder.class.getMethod(lookupName);
                else if (nArgs == 1)
                    m = BooleanTreeBuilder.class.getMethod(lookupName, boolean.class);
                else if (nArgs == 2)
                    m = BooleanTreeBuilder.class.getMethod(lookupName, boolean.class, boolean.class);
                else if (nArgs == -1)
                    m = BooleanTreeBuilder.class.getMethod(lookupName, boolean[].class);
            } catch (NoSuchMethodException ex) {
            } catch (SecurityException ex) {
            }
        }
        if (m == null || m.getReturnType() != boolean.class) {
            if (nArgs == 2) {
                return lookupFunction(name, -1);
            } else {
                throw new ParseException("Cannot find a method corresponding to function " + name, ParseException.ParseErrorCode.UNKNOWN_FUNCTION_NAME);
            }
        }
        return m;
    }


    //
    // STATIC FUNCTIONS TO USE IN SEMANTIC EVALUATION
    //

    public static boolean not(boolean x) { return !x; }
    public static boolean and(boolean x, boolean y) { return x && y; }
    public static boolean or(boolean x, boolean y) { return x || y; }
    public static boolean xor(boolean x, boolean y) { return (x && !y) || (!x && y); }
}
