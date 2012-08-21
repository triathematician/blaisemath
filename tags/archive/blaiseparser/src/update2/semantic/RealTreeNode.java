/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package update2.semantic;

import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;
import org.apache.commons.math.FunctionEvaluationException;

/**
 *
 * @author elisha
 */
public class RealTreeNode implements SemanticNode<Double>{

    public Double value() throws FunctionEvaluationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<String> unknowns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TreeNode getChildAt(int childIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getChildCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TreeNode getParent() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getIndex(TreeNode node) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean getAllowsChildren() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isLeaf() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Enumeration children() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
