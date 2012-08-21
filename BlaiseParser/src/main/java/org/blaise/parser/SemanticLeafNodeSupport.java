/**
 * SemanticLeafNode.java
 * Created on Dec 27, 2009
 */

package org.blaise.parser;

import java.util.Collections;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 * <p>
 *    This abstract class implements methods as appropriate for a leaf node, which
 *    does not permit any arguments. Sub-classes must still implement the
 *    methods in the <code>SemanticNode</code> interface, meaning the <code>value()</code>
 *    and <code>unknowns()</code> methods.
 * </p>
 * @author Elisha Peterson
 */
abstract class SemanticLeafNodeSupport implements SemanticNode {

    /** The parent object of the node */
    SemanticNode parent;
    /** The name of the node. */
    String name;
    /** The value of the node. */
    Object value;

    /** Basic constructor for the leaf. */
    protected SemanticLeafNodeSupport() {
        this.parent = null;
    }

    /** Constructs the leaf with a name and a value. */
    protected SemanticLeafNodeSupport(String name, Object value) {
        this.parent = null;
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (name == null)
            return "" + value;
        else
            return name + (value == null ? "" : "=" + value);
    }


    //
    // SemanticNode METHODS
    //

    public Class[] getParameterTypes() {
        return new Class[]{};
    }


    //
    // TreeNode METHODS
    //

    public TreeNode getParent() {
        return parent;
    }

    public boolean isLeaf() {
        return true;
    }

    public boolean getAllowsChildren() {
        return false;
    }

    public int getChildCount() {
        return 0;
    }

    public int getIndex(TreeNode node) {
        return -1;
    }

    public Enumeration children() {
        return (Enumeration) Collections.emptyList();
    }

    public TreeNode getChildAt(int childIndex) {
        throw new ArrayIndexOutOfBoundsException("SemanticLeafNode does not support children.");
    }
}
