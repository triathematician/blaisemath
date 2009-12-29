/*
 * Parser.java
 * Created on Sep 18, 2007, 5:02:39 PM
 */
package scribo.parser;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import scribo.tree.*;

/**
 * <p>
 * This class parses a string input, produces a function result. It contains all the pattern recognition Strings
 * required to compile the function.
 * </p>
 * 
 * @author Elisha Peterson
 */
public abstract class Parser {

    static final boolean verbose = false;
    // Regular expressions for searching functions...
    static final String XSPACE = "\\s*";
    static final String SOME_SPACE = "\\s+";
    static final String NOSPACE = "[^\\x00-\\x20]";
    static final String DIGIT = "\\d";
    static final String DIGITS = "(\\d+)";
    static final String DOUBLE_BASIC = XSPACE + "(((" + DIGITS + "(\\.)?(" + DIGITS + "?))|" + "(\\.(" + DIGITS + "))))" + XSPACE;
    static final String SIGNED_DOUBLE = XSPACE + "[+-]?" + DOUBLE_BASIC;
    static final String OPERATOR_SET = "+*/^,-";
    static final String NON_OPERATOR = "[^" + OPERATOR_SET + "]";
    static final String OPERATOR_GROUP = NON_OPERATOR + "([" + OPERATOR_SET + "])";
    /** Finds extra space on the beginning and end */
    static Pattern outsideSpacePattern = Pattern.compile("\\s*([^\\s]*)\\s*");
    /** Finds extra space next to parentheses (never required!!) */
    static Pattern insidePSpacePattern = Pattern.compile("\\s*+([\\(\\)])\\s*+");
    /** Finds places with an implicit *, for example 3x and 3(x) -> 3*(x). Assumes a double or constant value followed by a non-operator. */
    static Pattern[] implicitStarPattern = {
        Pattern.compile("(\\d)" + "([^)0123456789\\." + OPERATOR_SET + "])"),
        Pattern.compile("(\\))" + "([^)" + OPERATOR_SET + "])"),
        Pattern.compile("(" + NON_OPERATOR + ")" + "\\s+" + "(" + NON_OPERATOR + ")")};
    /** Finds an inner set of parentheses */
    static Pattern innerParentheticalPattern = Pattern.compile("[(][^()]*[)]");
    /** Finds any kind of parentheticals */
    static Pattern parentheticalPattern = Pattern.compile("[()]");
    /** Finds a general operator */
    static Pattern operatorPattern = Pattern.compile(OPERATOR_GROUP);

    /** Looks for parenthetical mismatch. */
    public static boolean mismatchedParentheses(String s) {
        // find inner sets of parentheses & deletes them
        if (verbose) {
            System.out.println("mismatchedParentheses input: " + s);
        }
        Matcher mp = innerParentheticalPattern.matcher(s);
        while (mp.find()) {
            s = mp.replaceFirst("");
            mp = innerParentheticalPattern.matcher(s);
            if (verbose) {
                System.out.println("  checkpoint: " + s);
            }
        }
        // checks for any leftover parentheses
        if (s.indexOf('(') != -1 || s.indexOf(')') != -1) {
            return true;
        }
        return false;
    }

    /** Splits a list of operators at the given one. Returns vector of expressions corresponding to the sublists. */
    public static Vector<FunctionTreeNode> operatorSplit(String op, List<String> operators, List<FunctionTreeNode> expressions)
            throws FunctionSyntaxException {
        int n = operators.size();
        if (expressions.size() != n + 1) {
            return null;
        }
        int firstOp = operators.indexOf(op);
        if (firstOp == -1) {
            return null;
        }
        Vector<FunctionTreeNode> result = new Vector<FunctionTreeNode>();
        if (firstOp == 0) {
            result.add(expressions.get(0));
        } else {
            result.add(resolveOperators(operators.subList(0, firstOp), expressions.subList(0, firstOp + 1)));
        }
        for (int i = firstOp + 1; i < operators.size(); i++) {
            if (!operators.get(i).equals(op)) {
                continue;
            }
            result.add(resolveOperators(operators.subList(firstOp + 1, i), expressions.subList(firstOp + 1, i + 1)));
            firstOp = i;
        }
        result.add(resolveOperators(operators.subList(firstOp + 1, n), expressions.subList(firstOp + 1, n + 1)));
        return result;
    }

    /**
     * <p>
     * Parses a list of expressions, and operators used to combine them.
     * For example, parsing {+,-,*,^} with {2,e,sin(x),4,x^3} gives 2+e-sin(x)*4^(x^3).
     * </p>
     * <p>
     * The order of operations is as follows:
     * <ul>
     *  <li> , (comma)
     *  <li> ||, &&, !
     *  <li> >, <, >=, <=, ==, !=
     *  <li> +, -, *, /, ^
     * </ul>
     * </p>
     *
     * @param operators the list of n-1 operators that combines the expressions
     * @param expressions the expressions combined by the operators
     * @return compiled tree having the proper order of operations
     * @throws scribo.parser.FunctionSyntaxException if there is a problem compiling the tree
     */
    public static FunctionTreeNode resolveOperators(List<String> operators, List<FunctionTreeNode> expressions) throws FunctionSyntaxException {
        if (verbose) {
            System.out.println("resolveOperators called with " + operators.toString());
            try {
                String temp = "";
                for (int i = 0; i < expressions.size(); i++) {
                    temp += expressions.get(i).toString() + (i != expressions.size() ? ", " : "");
                }
                System.out.println("             and expressions [" + temp + "]");
            } catch (NullPointerException e) {
            }
        }

        // If this isn't true, this function shouldn't be called!
        int n = operators.size();
        if (expressions.size() != n + 1) {
            throw new FunctionSyntaxException(FunctionSyntaxException.OPERATOR);
        }
        // base case
        if (n == 0) {
            return expressions.get(0);
        }

        // Check for comma-delimited list
        Vector<FunctionTreeNode> opNodes = operatorSplit(",", operators, expressions);
        if (opNodes != null) {
            return new ArgumentList(opNodes);
        }

//        // Check for boolean operators
//        opNodes=operatorSplit("||",operators,expressions);
//        if(opNodes!=null){return new Boolean.Or(opNodes);}
//        opNodes=operatorSplit("&&",operators,expressions);
//        if(opNodes!=null){return new Boolean.And(opNodes);}
//        opNodes=operatorSplit("!",operators,expressions);
//        if(opNodes!=null){return new Boolean.Not(opNodes);}
//
//        // Check for equality operators
//        opNodes=operatorSplit(">",operators,expressions);
//        if(opNodes!=null){return new Equality.Greater(opNodes);}
//        opNodes=operatorSplit("<",operators,expressions);
//        if(opNodes!=null){return new Equality.Less(opNodes);}
//        opNodes=operatorSplit(">=",operators,expressions);
//        if(opNodes!=null){return new Equality.GreaterEqual(opNodes);}
//        opNodes=operatorSplit("<=",operators,expressions);
//        if(opNodes!=null){return new Equality.LessEqual(opNodes);}
//        opNodes=operatorSplit("==",operators,expressions);
//        if(opNodes!=null){return new Equality.Equal(opNodes);}
//        opNodes=operatorSplit("!=",operators,expressions);
//        if(opNodes!=null){return new Equality.NotEqual(opNodes);}

        // Check in order of operations for each of the main operations.
        opNodes = operatorSplit("+", operators, expressions);
        if (opNodes != null) {
            return new Operation.Add(opNodes);
        }
        opNodes = operatorSplit("-", operators, expressions);
        if (opNodes != null) {
            return Operation.subtractNode(opNodes);
        }
        opNodes = operatorSplit("*", operators, expressions);
        if (opNodes != null) {
            return new Operation.Multiply(opNodes);
        }
        opNodes = operatorSplit("/", operators, expressions);
        if (opNodes != null) {
            return Operation.divideNode(opNodes);
        }
        opNodes = operatorSplit("^", operators, expressions);
        if (opNodes != null) {
            return new Operation.Power(opNodes);
        }

        // If nothing matches the known set of operators, return an error
        throw new FunctionSyntaxException(FunctionSyntaxException.OPERATOR);
    }

    /** Computes portion at the top nesting level of the expression. */
    public static boolean[] levelZero(String s) {
        boolean[] result = new boolean[s.length()];
        int leftCount = 0;
        int rightCount = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                leftCount++;
            }
            result[i] = (leftCount == rightCount);
            if (s.charAt(i) == ')') {
                rightCount++;
            }
        }
        return result;
    }

    /** Returns tree corresponding to the function specified by the string
     * @param s the input string containing the function to be parsed
     * @return a compiled function tree based upon the input function
     * @throws scribo.parser.FunctionSyntaxException if there is a syntax problem in the compilation
     */
    public static FunctionTreeNode parseExpression(String s) throws FunctionSyntaxException {
        if (verbose) {
            System.out.println("parseExpression input =" + s);
        }

        if (s.equals("")) {
            throw new FunctionSyntaxException(FunctionSyntaxException.INCOMPLETE_INPUT);
        }
        if (mismatchedParentheses(s)) {
            throw new FunctionSyntaxException(FunctionSyntaxException.PARENTHETICAL);
        }

        // STEP 1A. HANDLE SPACES AND IMPLICIT MULTIPLICATION
        Matcher mp = outsideSpacePattern.matcher(s);
        if (mp.matches()) {
            s = mp.group(1);
        }
        if (verbose) {
            System.out.println("  space handled 1=" + s);
        }
        mp = insidePSpacePattern.matcher(s);
        if (mp.find()) {
            mp.reset();
            StringBuffer sb = new StringBuffer();
            while (mp.find()) {
                mp.appendReplacement(sb, mp.group(1));
            }
            mp.appendTail(sb);
            s = sb.toString();
        }

        if (verbose) {
            System.out.println("  space handled 1.5=" + s);
        }
        for (int i = 0; i < implicitStarPattern.length; i++) {
            mp = implicitStarPattern[i].matcher(s);
            if (mp.find()) {
                mp.reset();
                StringBuffer sb = new StringBuffer();
                while (mp.find()) {
                    mp.appendReplacement(sb, mp.group(1) + "*" + mp.group(2));
                }
                mp.appendTail(sb);
                s = sb.toString();
            }
        }
        if (verbose) {
            System.out.println("  space handled 2=" + s);
        }
        s.replaceAll(XSPACE, "");
        if (verbose) {
            System.out.println("  space handled 3=" + s);
        }

        // STEP 1B. HANDLE STARTING WITH -
        if (s.startsWith("-")) {
            s = "0" + s;
        }
        if (verbose) {
            System.out.println("  space handled 4=" + s);
        }

        // STEP 2A. CHECK FOR OPERATORS OUTSIDE ALL PARENTHESES
        Vector<Integer> positions = new Vector<Integer>();
        boolean[] levelZero = levelZero(s);
        mp = operatorPattern.matcher(s);
        while (mp.find()) {
            if (levelZero[mp.start() + 1]) {
                positions.add(mp.start() + 1);
            }
        }
        if (positions.contains(s.length() - 1)) {
            throw new FunctionSyntaxException(FunctionSyntaxException.INCOMPLETE_INPUT, s.length());
        }
        if (verbose) {
            System.out.println("  operator find 5=" + positions.toString());
        }

        // STEP 2B. SPLIT THE EXPRESSION AT THE LEVEL-ZERO OPERATORS
        if (positions.size() > 0) {
            Vector<String> operators = new Vector<String>();
            Vector<FunctionTreeNode> expressions = new Vector<FunctionTreeNode>();
            expressions.add(parseExpression(s.substring(0, positions.firstElement())));
            for (int i = 0; i < positions.size() - 1; i++) {
                operators.add(s.substring(positions.get(i), positions.get(i) + 1));
                expressions.add(parseExpression(s.substring(positions.get(i) + 1, positions.get(i + 1))));
            }
            operators.add(s.substring(positions.lastElement(), positions.lastElement() + 1));
            expressions.add(parseExpression(s.substring(positions.lastElement() + 1)));
            return resolveOperators(operators, expressions);
        }

        // STEP 4. NO LEVEL-ZERO OPERATORS, BUT PARENTHESES
        int left = s.indexOf('(');
        if (left != -1) {
            if (!s.endsWith(")")) {
                throw new FunctionSyntaxException(FunctionSyntaxException.PARENTHETICAL, s.length());
            }
            if (s.startsWith("(")) { // strip parentheses
                return parseExpression(s.substring(1, s.length() - 1));
            } else { // return function!
                return FunctionTreeFactory.getFunction(s.substring(0, left), parseExpression(s.substring(left)));
            }
        }

        // STEP 5. NO OPERATORS AT ALL; EXPRESSION IS A LEAF
        return FunctionTreeFactory.getLeaf(s);
    }
}
