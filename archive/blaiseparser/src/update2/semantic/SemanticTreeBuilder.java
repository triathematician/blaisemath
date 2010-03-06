/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package update2.semantic;

import update2.token.ParseException;
import update2.token.TokenNode;

/**
 * Takes a tree from a parser and uses it to construct a tree
 * with semantic meaning.
 *
 * @author elisha
 */
public interface SemanticTreeBuilder {

    /** 
     * Translates an entire token tree into a semantic tree.
     * @param parentNode a parent node in the resulting tree object
     * @param tokenNode a token tree's top node
     * @return a semantic tree's top node
     * @throws ParseException if the token tree contains node types that are not supported by this builder
     */
    public SemanticNode buildTree(SemanticNode parentNode, TokenNode tokenNode);

    
}
