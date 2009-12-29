/**
 * BlaisePageTree.java
 * Created on Dec 22, 2009
 */

package org.bm.blaise.scribo.page;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * <p>
 *    This class is intended to provide structure to a collection of <i>BlaisePage</i>s.
 *    Initially, it will just allow multiple pages to be placed together in tree format.
 *    An action will be generated whenever a different page is "selected" (possibly requiring
 *    a double-click). This structure will also know how to update when additional pages
 *    are added.
 * </p>
 * <p>
 *    The tree's model is a standard model, with standard <code>DefaultMutableTreeNode</code>
 *    nodes linked with <code>BlaisePage</code> objects.
 * </p>
 * @author Elisha Peterson
 */
public class BlaisePageTree extends JTree {

    public BlaisePageTree() {
        this(new SampleBlaisePage());
    }

    public BlaisePageTree(BlaisePage root) {
        super(new DefaultMutableTreeNode(root));
    }

    public BlaisePage getSelectedPage() {
        return (BlaisePage) ((DefaultMutableTreeNode) getSelectionPath().getLastPathComponent()).getUserObject();
    }

}
