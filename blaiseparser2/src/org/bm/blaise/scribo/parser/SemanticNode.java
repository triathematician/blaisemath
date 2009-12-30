/*
 * SemanticNode.java
 * Created on Nov 26, 2009
 */
package org.bm.blaise.scribo.parser;

import java.util.Map;
import javax.swing.tree.TreeNode;

/**
 * <p>
 *   Represents the semantic tree which can be used to actually <i>calculate</i>
 *   values. The primary method retrieves the <i>value</i> of the tree, assuming
 *   it can be calculated, as a class of type <code>valueType()</code>. Secondary methods
 *   allow for retrieving all <i>unknowns</i> within the tree and returning the types
 *   of the immediate children of the node.
 * </p>
 * <p>
 *   To be completely precise, the <b>value</b> of a node is the object that the
 *   node returns, possibly a numeric or boolean value or a string, or anything really;
 *   the <b>arguments</b> of a node are the <i>children</i> of the <code>TreeNode</code>
 *   interface or the "subnodes"; the <b>argument types</b> are the class types that
 *   are expected of the arguments; the <b>unknowns</b> are the list of nodes in the
 *   tree with unknown values, identified by names and returned as a table.
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
     * Retrieve all unknown identifiers within the tree, returning them as a map associating
     * the unknown variable name with its underlying class. This will generally be found
     * by recursive introspection of subnodes.
     * @return a map associating unknowns with their classes
     */
    public Map<String, Class<?>> unknowns();

    /**
     * Returns array describing the class types of arguments of the node.
     * @return an array of classes, in the same order as the child nodes at the argument;
     *      if there are no arguments and the node is a "function", should return an empty array;
     *      if the node is a leaf, e.g. a constant or a variable, should return <code>null</code>
     */
    public Class<?>[] getArgumentTypes();
}
