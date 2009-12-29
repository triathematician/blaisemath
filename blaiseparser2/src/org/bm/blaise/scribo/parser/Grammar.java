/**
 * Grammar.java
 * Created on Dec 1, 2009
 */

package org.bm.blaise.scribo.parser;

import java.util.Map;

/**
 * <p>
 *    This interface specifies the functions necessary to parse a generic expression
 *    into a tree of tokens. The methods specify the operators, the parentheticals,
 *    the functions, and the constants in the grammar. Other methods specify whether
 *    tokens are case-sensitive or not, and also specify any synonyms that should be
 *    built-in to the grammar.
 * </p>
 * <p>
 *    Any class that implements this interface should be able to be passed into
 *    the <code>parseTree</code> method in <code>Parser</code>
 *    in order to construct a <i>token tree</i> in the form of a <code>TokenNode</code>.
 *    A <code>SemanticTreeBuilder</code> is then required to convert the token tree
 *    into a <i>semantic tree</i> which permits evaluation.
 * </p>
 * <p>
 *    Multiple grammmars may be combined in order to parse an expression... generally
 *    this will be done. This also permits grammars to be dynamically adjusted.
 * </p>
 *
 * @see Parser
 * @author Elisha Peterson
 */
public interface Grammar {

    //
    // LANGUAGE SPECS
    //

    /** 
     * Return strings representing prefix-unary operators in the grammar, such as the
     * "-" in "-5", or the "!" in "!true". Order does not matter.
     * @return an array containing the operators
     */
    public String[] preUnaryOperators();

    /**
     * Return strings representing postfix-unary operators in the grammar, such as the
     * "!" in "5!" for factorial. Order does not matter.
     * @return an array containing the operators
     */
    public String[] postUnaryOperators();

    /**
     * Return strings representing <i>all binary <b>AND</b> multary operators</i> in the grammar.
     * These should be ordered in a way consistent with order-of-operations. 
     * The last entry is the first operator that will be evaluated, and the first entry
     * is the last operator that will be evaluated (and be at the top of the token tree).
     * @return an array containing the operators
     */
    public String[] naryOperators();

    /**
     * Return strings representing <i>multary</i> operators in the grammar.
     * These should also be returned by the <code>binaryOperators()</code> method,
     * in which there order is used to determine the order-of-operations. Operators
     * returned by <code>binaryOperators()</code> that are not returned by this
     * method are assumed to be purely <i>binary</i> oeprators.
     * @return an array containing the operators
     */
    public String[] multaryOperators();

    /**
     * Returns pairs of strings that represent opening and closing parenthetical statements.
     * @return an array of length-2 arrays of strings, where the first string represents
     *   an opening parenthetical and the second represents a closing parenthetical
     */
    public String[][] parentheticals();

    /**
     * Returns strings representing functions in the grammar.
     * @return an array of strings that represent function names
     */
    public String[] functions();

    /** 
     * Returns strings representing constants in the grammar.
     * @return an array of strings that represent constants
     */
    public String[] constants();


    //
    // SYNONYMS
    //

    /**
     * Return list of token synonyms.
     * @return a map associating key strings with their synonyms; the keys should
     *   be the token name, and the value should be the synonym; generally, these
     *   tokens will be replaced by their synonyms
     */
    public Map<String,String> synonyms();

    /** 
     * Describes whether the grammar is case-sensitive. A case-sensitive grammar
     * interprets versions of the same token with different cases as different tokens.
     * @return <code>true</code> if the grammar is case-sensitive
     */
    public boolean isCaseSensitive();

}
