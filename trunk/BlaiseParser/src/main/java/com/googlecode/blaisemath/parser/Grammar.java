/**
 * Grammar.java
 * Created on Dec 1, 2009
 */

package com.googlecode.blaisemath.parser;

/*
 * #%L
 * BlaiseParser
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.reflect.Method;
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
 * @see GrammarParser
 * @author Elisha Peterson
 */
public interface Grammar {

    //
    // GENERAL PROPERTIES
    //

    /**
     * Describes whether the grammar is case-sensitive. A case-sensitive grammar
     * interprets versions of the same token with different cases as different tokens.
     * @return <code>true</code> if the grammar is case-sensitive
     */
    public boolean isCaseSensitive();

    //
    // PARENTHETICALS
    //

    /**
     * Returns token associated with opening of a function's list of arguments.
     * @return string token
     */
    public String argumentListOpener();

    /**
     * Returns token separating arguments in a list of arguments.
     * @return string token
     */
    public String argumentListSeparator();

    /**
     * Returns token associated with an implicit space or adjacency of operators, e.g. "3 x" and "3x" become "3*x"
     * @return string token
     */
    public String implicitSpaceOperator();

    /**
     * Returns pairs of strings that represent opening and closing parenthetical statements.
     * 
     * @return an array of length-2 arrays of strings, where the first string represents
     *   an opening parenthetical and the second represents a closing parenthetical
     */
    public String[][] parentheticals();

    //
    // CONSTANTS & VARIABLES
    //

    /**
     * Returns the constants in the grammar, together with their associated values.
     * @return a lookup map that contains the constants
     */
    public Map<String, ? extends Object> constants();

    //
    // OPERATORS
    //

    /** 
     * Return strings representing prefix-unary operators in the grammar, such as the
     * "-" in "-5", or the "!" in "!true". Order does not matter. The keys in the map
     * are the operators used in the grammar; the values are the <b>names</b> of the
     * associated functions. These functions will be "looked up" in the default class
     * locations, and the corresponding functions should all have a single argument.
     *
     * @return a map pairing operator tokens with the method names representing them.
     */
    public Map<String, Method> preUnaryOperators();

    /**
     * Return strings representing postfix-unary operators in the grammar, such as the
     * "!" in "5!" for factorial. Order does not matter. The keys in the map
     * are the operators used in the grammar; the values are the <b>names</b> of the
     * associated functions. These functions will be "looked up" in the default class
     * locations, and the corresponding functions should all have a single argument.
     *
     * @return a map pairing operator tokens with the method names representing them.
     */
    public Map<String, Method> postUnaryOperators();

    /**
     * Return strings representing <i>all binary <b>AND</b> multary operators</i>
     * in the grammar. The keys in the map
     * are the operators used in the grammar; the values are the <b>names</b> of the
     * associated functions. These functions will be "looked up" in the default class
     * locations, and the corresponding functions should all have two or more arguments.
     *
     * @return a map pairing operator tokens with the method names representing them.
     */
    public Map<String, Method> naryOperators();

    /**
     * Return strings representing <i>multary</i> operators in the grammar.
     * These should also be returned by the <code>binaryOperators()</code> method,
     * in which there order is used to determine the order-of-operations. Operators
     * returned by <code>binaryOperators()</code> that are not returned by this
     * method are assumed to be purely <i>binary</i> operators.
     * 
     * The keys in the map
     * are the operators used in the grammar; the values are the <b>names</b> of the
     * associated functions. These functions will be "looked up" in the default class
     * locations, and the corresponding functions should have a single array-based
     * argument.
     *
     * @return a map pairing operator tokens with the method names representing them.
     */
    public String[] multaryOperators();

    /**
     * Return list of operators in order desired for order-of-operations.
     * These should be ordered in a way consistent with order-of-operations.
     * The last entry is the first operator that will be evaluated, and the first entry
     * is the last operator that will be evaluated (and be at the top of the token tree).
     *
     * @return an array of operators in order of operations
     */
    public String[] orderOfOperations();

    //
    // FUNCTIONS
    //

    /**
     * Returns strings representing functions in the grammar.
     * @return an array of strings that represent function names
     */
    public Map<String, Method> functions();
}
