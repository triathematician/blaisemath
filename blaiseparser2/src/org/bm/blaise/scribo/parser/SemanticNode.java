/*
 * SemanticNode.java
 * Created on Nov 26, 2009
 */
package org.bm.blaise.scribo.parser;

import java.util.List;
import javax.swing.tree.TreeNode;

/**
 * <p>
 *   Represents the semantic tree which can be used to actually <i>calculate</i>
 *   values. The primary method retrieves the <i>value</i> of the tree, assuming
 *   it can be calculated, as a class of type <code>valueType()</code>. Secondary methods
 *   allow for retrieving all <i>unknowns</i> within the tree.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface SemanticNode extends TreeNode {
    
    /**
     * Computes and returns the value of the node. Individual nodes representing
     * variables are responsible for keeping track of their own values.
     * 
     * @return value of this node, as an object of type C
     * @throws SemanticTreeEvaluationException if the value cannot be determined
     *   (might happen if variables in the tree are undefined)
     */
    public Object value() throws SemanticTreeEvaluationException;

    /**
     * Returns the object type of the return value as a class.
     * @return class type of the object (should be the same or compatible with the type parameter)
     */
    public Class<?> valueType();

    /**
     * Retrieve all unknown identifiers within the tree.
     * @return an ordered list of (non-repeating) unknowns as strings
     */
    public List<String> unknowns();
}
