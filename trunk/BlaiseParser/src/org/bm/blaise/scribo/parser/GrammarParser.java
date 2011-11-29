/*
 * GrammarParser.java
 * Created Dec 30, 2009
 */

package org.bm.blaise.scribo.parser;

/**
 * <p>
 *   Converts an input String into a <code>SemanticNode</code>, using an associated
 *   <code>grammar</code>. The primary method of interest is <code>parseTree</code>,
 *   which handles the conversion of a string input into a semantic tree (returning
 *   the top node of that tree).
 * </p>
 *
 * @author elisha
 */
public interface GrammarParser {

    /**
     * Sets grammar backing the parser.
     * @param grammar the grammar to use in parsing
     */
    public void setGrammar(Grammar grammar);

    /**
     * Converts a string expression into a token tree.
     * @param expression the string input to the parser
     * @return a <code>SemanticNode</code> which is the top node in the tree
     * @throws ParseException if the parser fails in some way to parse the input
     */
    public SemanticNode parseTree(String expression) throws ParseException;

}
