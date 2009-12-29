/**
 * RealTreeBuilder.java
 * Created on Nov 30, 2009
 */
package org.bm.blaise.scribo.parser.real;

import java.lang.reflect.Method;
import org.bm.blaise.scribo.parser.*;
import org.bm.blaise.scribo.parser.semantic.*;

/**
 * <p>
 *    This class generates a semantic tree capable of evaluating real functional expressions.
 * </p>
 * @author Elisha Peterson
 */
public class RealTreeBuilder implements SemanticTreeBuilder {

    private static final boolean VERBOSE = true;

    public static final RealTreeBuilder INSTANCE = new RealTreeBuilder();

    /** 
     * Constructs a semantic tree representation of the string input.
     * @param string the string representation of a real function
     * @return the top node of a semantic tree
     */
    public SemanticNode buildTree(String string) throws ParseException {
        return buildTree(new Parser(RealGrammar.INSTANCE).parseTree(string));
    }

    public SemanticNode buildTree(TokenNode tokenNode) throws ParseException {
        String name = tokenNode.getName();
        TokenType type = tokenNode.getType();
        switch (type) {

            case IDENTIFIER:
                // TODO - put this inside the grammar ??
                if (name.equalsIgnoreCase("pi")) {
                    return new SemanticConstantNode("pi", Math.PI);
                } else if (name.equalsIgnoreCase("e")) {
                    return new SemanticConstantNode("e", Math.E);
                }
                return new SemanticVariableNode(name, double.class);

            case NUMBER:
                return new SemanticConstantNode(Double.valueOf(name));

            case FUNCTION:
                // TODO - this should probably be done elsewhere primarily, since functional expressions ought to be ubiquitous
                // find first non-parenthetical argument
                TokenNode tokenArgument = (TokenNode) tokenNode.firstChild();
                while (tokenArgument.getName().equals("(") && tokenArgument.getChildCount() > 0)
                    tokenArgument = (TokenNode) tokenArgument.firstChild();
                // error checking
                if (tokenArgument.getParent().getChildCount() != 1)
                    throw new ParseException("Parenthetical has more than one child node (shouldn't happen)", ParseException.ParseErrorCode.UNKNOWN_ERROR);
                // zero argument node
                if (tokenArgument.getName().equals("("))
                    return new SemanticMethodNode(lookupFunction(name, 0));
                // single argument node
                if (tokenArgument.getName().equals(",")) {
                    // multi-argument nodes, denoted by a comma-separated list
                    SemanticNode[] args = new SemanticNode[tokenArgument.getChildCount()];
                    for (int i = 0; i < args.length; i++) {
                        args[i] = buildTree((TokenNode) tokenArgument.getChildAt(i));
                    }
                    // TODO - this lookup needs to depend upon the existence of either an array-based method or an argument-based method!!
                    // for now the avg() method does not work, which takes an array, because the SemanticIteratedMethodNode and SemanticMethodNode
                    // both only know how to deal with one type of argument.
                    return new SemanticIteratedMethodNode(lookupFunction(name, 2), args);
                } else {
                    return new SemanticMethodNode(lookupFunction(name, 1), buildTree(tokenArgument));
                }
                
            case PARENTHETICAL_OPEN:
                // ignore the parenthetical, as it is implied by the tree's structure
                if (tokenNode.getChildCount() > 0)
                    return buildTree((TokenNode) tokenNode.firstChild());
                else
                    throw new ParseException("Empty expression", ParseException.ParseErrorCode.EMPTY_EXPRESSION);

            case UNARY_OPERATOR:
            case POST_UNARY_OPERATOR:
                SemanticNode arg = buildTree((TokenNode) tokenNode.firstChild());
                // TODO - this dynamic naming should be described within the grammar
                if (name.equals("-") || name.equalsIgnoreCase("negation"))
                    return new SemanticMethodNode(lookupFunction("negation", 1), arg);
                if (name.equals("!") || name.equalsIgnoreCase("factorial"))
                    return new SemanticMethodNode(lookupFunction("factorial", 1), arg);
                throw new ParseException("Unsupported unary operator " + name, ParseException.ParseErrorCode.UNRECOGNIZED_SYMBOL);

            case BINARY_OPERATOR:
                SemanticNode arg1 = buildTree((TokenNode) tokenNode.getChildAt(0));
                SemanticNode arg2 = buildTree((TokenNode) tokenNode.getChildAt(1));
                // TODO - this dynamic naming should be described within the grammar
                if (name.equals("-") || name.equalsIgnoreCase("subtract")) {
                    return new SemanticMethodNode(lookupFunction("subtract", 2), arg1, arg2);
                } else if (name.equals("/") || name.equalsIgnoreCase("divide")) {
                    return new SemanticMethodNode(lookupFunction("divide", 2), arg1, arg2);
                } else if (name.equals("^") || name.equalsIgnoreCase("power")) {
                    return new SemanticMethodNode(lookupFunction("power", 2), arg1, arg2);
                } else if (name.equals("%") || name.equalsIgnoreCase("modulus")) {
                    return new SemanticMethodNode(lookupFunction("modulus", 2), arg1, arg2);
                }
                throw new ParseException("Unsupported binary operator " + name, ParseException.ParseErrorCode.UNRECOGNIZED_SYMBOL);

            case MULTARY_OPERATOR:
                SemanticNode[] args2 = new SemanticNode[tokenNode.getChildCount()];
                for (int i = 0; i < args2.length; i++) { 
                    args2[i] = buildTree((TokenNode) tokenNode.getChildAt(i));
                }
                // TODO - this dynamic naming should be described within the grammar
                if (name.equals("+") || name.equalsIgnoreCase("add") || name.equalsIgnoreCase("plus")) {
                    return new SemanticIteratedMethodNode(lookupFunction("add", 2), args2);
                } else if (name.equals("*") || name.equalsIgnoreCase("multiply")) {
                    return new SemanticIteratedMethodNode(lookupFunction("multiply", 2), args2);
                }
                throw new ParseException("Unsupported multary operator " + name, ParseException.ParseErrorCode.UNRECOGNIZED_SYMBOL);

            case PARENTHETICAL_CLOSE:
                throw new IllegalArgumentException("Unexpected node type: " + type);
        }

        return null;
    }

    //
    // UTILITY METHOD TO PROVIDE LOOKUPS
    //

    /**
     * Looks for function of given name; inputs and outputs must be doubles.
     * @param name the name of the function
     * @param nArgs the number of arguments of the function (of type double)
     */
    public Method lookupFunction(String name, int nArgs) throws ParseException {
        // TODO - this method is really useful!! should probably make an abstraction
        String lookupName = name.toLowerCase();
        if (RealGrammar.SYNONYM_MAP.containsKey(lookupName)) {
            lookupName = RealGrammar.SYNONYM_MAP.get(lookupName);
        }
        Method m = null;
        try {
            if (nArgs == 0)
                m = Math.class.getMethod(lookupName);
            else if (nArgs == 1)
                m = Math.class.getMethod(lookupName, double.class);
            else if (nArgs == 2)
                m = Math.class.getMethod(lookupName, double.class, double.class);
            else if (nArgs == -1)
                m = Math.class.getMethod(lookupName, double[].class);
        } catch (NoSuchMethodException ex) {
        } catch (SecurityException ex) {
        }
        if (m!= null && m.getReturnType() != double.class) {
            m = null;
        }
        if (m == null) {
            try {
                if (nArgs == 0)
                    m = RealTreeBuilder.class.getMethod(lookupName);
                else if (nArgs == 1)
                    m = RealTreeBuilder.class.getMethod(lookupName, double.class);
                else if (nArgs == 2)
                    m = RealTreeBuilder.class.getMethod(lookupName, double.class, double.class);
                else if (nArgs == -1)
                    m = RealTreeBuilder.class.getMethod(lookupName, double[].class);
            } catch (NoSuchMethodException ex) {
            } catch (SecurityException ex) {
            }
        }
        if (m == null || m.getReturnType() != double.class) {
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

    public static double log2(double arg) {
        return Math.log(arg)/Math.log(2);
    }
    public static double frac(double arg) {
        return arg % 1;
    }
    public static double random(double min, double max) {
        return Math.random()*(max-min) + min;
    }

    //
    // UNARY OPERATORS AS FUNCTIONS
    //

    public static double negation(double arg) {
        return -arg;
    }
    public static double factorial(double arg) {
        double result = 1;
        while (arg > 1) {
            result *= arg;
            arg--;
        }
        return result;
    }

    //
    // BINARY OPERATORS AS FUNCTIONS
    //

    public static double subtract(double arg1, double arg2) {
        return arg1 - arg2;
    }

    public static double divide(double arg1, double arg2) {
        return arg1 / arg2;
    }

    public static double power(double arg1, double arg2) {
        return Math.pow(arg1, arg2);
    }

    public static double modulus(double arg1, double arg2) {
        return arg1 % arg2;
    }

    //
    // MULTARY OPERATORS AS FUNCTIONS
    //

    public static double add(double arg1, double arg2) {
        return arg1 + arg2;
    }

    public static double multiply(double arg1, double arg2) {
        return arg1 * arg2;
    }

    //
    // GENERAL MULTARY FUNCTIONS
    //

    public static double average(double[] xs) {
        return sum(xs)/xs.length;
    }

    public static double sum(double[] xs) {
        double result = 0.0;
        for (int i = 0; i < xs.length; i++) {
            result += xs[i];
        }
        return result;
    }
}
