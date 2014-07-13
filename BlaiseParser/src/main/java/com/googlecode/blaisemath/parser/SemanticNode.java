/*
 * SemanticNode.java
 * Created on Nov 26, 2009
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

import java.util.Map;
import javax.swing.tree.TreeNode;

/**
 * <p>
 *   Represents the semantic tree which can be used to actually <i>calculate</i>
 *   values. The primary method retrieves the <i>value</i> of the tree, assuming
 *   it can be calculated, as a class of type <code>getValueType()</code>. Secondary methods
 *   allow for retrieving all <i>variables</i> within the tree and returning the types
 *   of the immediate children of the node (called <i>parameters</i>).
 * </p>
 * <p>
 *   To be completely precise:
 *  <ul>
 *  <li> The <b>value</b> of a node is the object that the
 *   node returns, possibly a numeric or boolean value or a string, or anything really.
 *  <li> The <b>parameters</b> of a node are the <i>children</i> of the <code>TreeNode</code>
 *   interface or the "subnodes"; the <b>parameter types</b> are the class types that
 *   are expected of the arguments.
 *  <li> Finally, the <b>variables</b> are the list of nodes in the
 *   tree with unknown values, identified by names and returned as a table.
 *  </ul>
 * </p>
 *
 * @author Elisha Peterson
 */
public interface SemanticNode extends TreeNode {

    /**
     * Returns array describing the class types of arguments of the node.
     * @return an array of classes, in the same order as the child nodes at the argument;
     *      if there are no arguments and the node is a "function", should return an empty array;
     *      if the node is a leaf, e.g. a constant or a variable, should return a 0-length array.
     */
    public Class[] getParameterTypes();

    /**
     * Returns the object type of the return value as a class.
     * @return class type of the object (should be the same or compatible with the type parameter)
     */
    public Class getValueType();
    
    /**
     * Computes and returns the value of the node. Individual nodes representing
     * variables are responsible for keeping track of their own values.
     * 
     * @return value of this node, as an object of type specified by <code>getReturnType()</code>
     * @throws SemanticTreeEvaluationException if the value cannot be determined
     *   (might happen if variables in the tree are undefined)
     */
    public Object getValue() throws SemanticTreeEvaluationException;

    /**
     * Assigns values to all variables within the tree.
     * @param valueTable the values as a map
     */
    public void assignVariables(Map<String, ? extends Object> valueTable);
    
    /**
     * Retrieve all unknown variables within the tree, returning them as a map associating
     * the unknown variable name with its underlying class. This will generally be found
     * by recursive introspection of subnodes.
     * @return a map associating unknowns with their classes
     */
    public Map<String, Class> getVariables();
}
