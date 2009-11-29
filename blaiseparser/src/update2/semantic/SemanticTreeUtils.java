/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package update2.semantic;

import java.util.Map;
import javax.swing.tree.TreeNode;

/**
 *
 * @author elisha
 */
public class SemanticTreeUtils {

    /** 
     * Assigns appropriate values to unknowns in a provided tree; works recursively down from the top node.
     *
     * @param valueTable table of values associated to variables, with variable names stored in lowercase
     * @param top the top node of the tree to assign values at
     */
    public static <C> void assignVariables(Map<String,C> valueTable, SemanticNode top) {
        if (top instanceof DefaultVariableNode) {
            assignVariable(valueTable, (DefaultVariableNode) top);
        }
        for (int i = 0; i < top.getChildCount(); i++) {
            assignVariables(valueTable, (SemanticNode) top.getChildAt(i));
        }
    }

    /** 
     * Assigns appropriate value to a single node of variable type, if the node's variable name is contained in the key table.
     *
     * @param valueTable table of values associated to variables, with variable names stored in lowercase
     * @param varNode the node to assign the variable at
     */
    public static <C> void assignVariable(Map<String,C> valueTable, DefaultVariableNode varNode) {
        String varName = varNode.name.toLowerCase();
        if (valueTable.containsKey(varName)) {
            varNode.setValue(valueTable.get(varName));
        }
    }

}
