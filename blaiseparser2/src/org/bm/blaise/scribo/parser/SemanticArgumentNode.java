/**
 * SemanticArgumentNode.java
 * Created on Nov 30, 2009
 */

package org.bm.blaise.scribo.parser;

/**
 * <p>
 *   This interface adds additional functionality to the <code>SemanticNode</code>
 *   interface in order to allow for arguments to functions of an underlying type.
 * </p>
 *
 * @author Elisha Peterson
 */
public interface SemanticArgumentNode extends SemanticNode {

    /**
     * Returns the argument types associated with the node.
     * @return list of argument types associated with the class.
     */
    public Class<?>[] getParameterTypes();

}
