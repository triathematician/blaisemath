/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package update2.semantic;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;
import org.apache.commons.math.FunctionEvaluationException;

/**
 * <p>A node that has a variable value.</p>
 * @author elisha
 */
public class DefaultVariableNode<C> implements SemanticNode<C> {

    /** The value associated with the variable */
    C value;
    /** The parent of theis tree node */
    TreeNode parent;
    /** The name of the variable */
    String name;

    public DefaultVariableNode(TreeNode parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    //
    // GET/SET value methods
    //

    public C getValue() { return value; }
    public void setValue(C value) { this.value = value; }
    public void clearValue() { this.value = null; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override public String toString() { return name + ( value == null ? "" : "[" + value + "]"); }


    // SemanticNode methods

    public C value() throws FunctionEvaluationException {
        if (value == null)
            // TODO - should this be a different exception??
            throw new FunctionEvaluationException(0.0, "Attempt to evaluate unassigned variable!");
        return value;
    }
    public List<String> unknowns() { return Arrays.asList(name); }

    // TreeNode methods

    public TreeNode getChildAt(int childIndex) { throw new ArrayIndexOutOfBoundsException("Variable nodes cannot have children."); }
    public int getChildCount() { return 0; }
    public TreeNode getParent() { return parent; }
    public int getIndex(TreeNode node) { return -1; }
    public boolean getAllowsChildren() { return false; }
    public boolean isLeaf() { return true; }
    public Enumeration children() { return (Enumeration) Collections.EMPTY_LIST; }

}
