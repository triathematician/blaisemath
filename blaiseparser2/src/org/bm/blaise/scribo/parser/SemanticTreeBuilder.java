/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scribo.parser;

import org.bm.blaise.scribo.parser.SemanticNode;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.scribo.parser.TokenNode;

/**
 * Takes a tree from a parser and uses it to construct a tree
 * with semantic meaning.
 *
 * @author elisha
 */
public interface SemanticTreeBuilder {

    /** 
     * Translates an entire token tree into a semantic tree.
     *
     * @param tokenNode a token tree's top node
     * @return a semantic tree's top node
     * @throws ParseException if the token tree contains node types that are not supported by this builder
     */
    public SemanticNode buildTree(TokenNode tokenNode) throws ParseException;

    
}
