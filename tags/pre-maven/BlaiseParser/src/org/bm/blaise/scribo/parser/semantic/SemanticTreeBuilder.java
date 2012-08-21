/**
 * DefaultSemanticTreeBuilder.java
 * Created on Dec 29, 2009
 */

package org.bm.blaise.scribo.parser.semantic;

import java.lang.reflect.Method;
import java.util.Arrays;
import org.bm.blaise.scribo.parser.Grammar;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.scribo.parser.SemanticNode;

/**
 * <p>
 *    This class generates a semantic tree capable of evaluating real functional expressions.
 * </p>
 * @author Elisha Peterson
 */
class SemanticTreeBuilder {

    /** Grammar used to build the semantic tree. */
    Grammar grammar;

    /** Constructs the tree builder with specified grammar. */
    public SemanticTreeBuilder(Grammar grammar) {
        this.grammar = grammar;
    }

    public SemanticNode buildTree(TokenNode tokenNode) throws ParseException {
        String name = tokenNode.getName().toLowerCase();
        TokenType type = tokenNode.getType();

        switch (type) {

            case PARENTHETICAL_OPEN:    // there is no need to do anything here, as a parenthetical is encoded within the tree's structure
                if (tokenNode.getChildCount() > 0) {
                    return buildTree((TokenNode) tokenNode.firstChild());
                } else {
                    throw new ParseException("Empty expression", ParseException.ParseErrorCode.EMPTY_EXPRESSION);
                }

            case PARENTHETICAL_CLOSE:    // this should never happen
                throw new ParseException("Unexpected node type: " + type, ParseException.ParseErrorCode.PARENTHETICAL_ERROR);

            case NUMBER:
                return new SemanticConstantNode(Double.valueOf(name));

            case IDENTIFIER:    // this will become a constant or a variable
                if (grammar.constants().containsKey(name)) {
                    return new SemanticConstantNode(name, grammar.constants().get(name));
                } else { // TODO - consider making the variable node type more specific
                    return new SemanticVariableNode(name, Object.class);
                }

            case PRE_UNARY_OPERATOR:
                return methodNode(grammar.preUnaryOperators().get(name), tokenNode);

            case POST_UNARY_OPERATOR:
                return methodNode(grammar.postUnaryOperators().get(name), tokenNode);

            case BINARY_OPERATOR:
                return methodNode(grammar.naryOperators().get(name), tokenNode);
                
            case MULTARY_OPERATOR:
                return methodNode(grammar.naryOperators().get(name), tokenNode);

            case FUNCTION:
                // find first non-parenthetical argument
                TokenNode tokenArgument = (TokenNode) tokenNode.firstChild();
                while (tokenArgument.getName().equals(grammar.argumentListOpener()) && tokenArgument.getChildCount() > 0) {
                    tokenArgument = (TokenNode) tokenArgument.firstChild();
                }

                // simple error checking
                if (tokenArgument.getParent().getChildCount() != 1) {
                    throw new ParseException("Argument node of a function has more than one child node (shouldn't happen)", ParseException.ParseErrorCode.UNKNOWN_ERROR);
                }

                if (tokenArgument.getName().equals(grammar.argumentListOpener())) { // this indicates a function with no arguments
                    return new SemanticMethodNode(grammar.functions().get(name));
                } else if (tokenArgument.getName().equals(grammar.argumentListSeparator())) { // this indicates a function with several (>=2) arguments
                    return methodNode(grammar.functions().get(name), tokenArgument);
                } else { // this indicates a function with a single argument
                    return new SemanticMethodNode(grammar.functions().get(name), buildTree(tokenArgument) );
                }
        }

        return null;
    }

    /**
     * Uses children of given node and specified method to create a <code>SemanticMethodNode</code>, which is
     * particularly convenient for parents with lots of children.
     *
     * @param method the method underlying the node to be returned
     * @param parentNode the node whose children will be the argument nodes of the method
     * @param varargs whether to return a variable-argument node
     * @return a node representing a function with all children nodes properly compiled
     * @throws ParseException if the child nodes cannot be properly built
     */
    SemanticMethodNode methodNode(Method method, TokenNode parentNode) throws ParseException {
        SemanticNode[] argNodes = new SemanticNode[parentNode.getChildCount()];
        for (int i = 0; i < argNodes.length; i++) {
            argNodes[i] = buildTree((TokenNode) parentNode.getChildAt(i));
        }
//        System.out.println("getting method " + method + " with arguments " + Arrays.toString(argNodes));
        boolean varargs = method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isArray();
        return varargs ? new SemanticVarargMethodNode(method, argNodes) : new SemanticMethodNode(method, argNodes);
    }
}