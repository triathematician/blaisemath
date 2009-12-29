/*
 * SemanticTreeUtils.java
 * Created Nov 2009
 */

package org.bm.blaise.scribo.parser.semantic;

import org.bm.blaise.scribo.parser.SemanticNode;
import java.util.Map;

/**
 * <p>
 *    This class contains static methods useful for working with semantic trees.
 * </p>
 * @author Elisha Peterson
 */
public class SemanticTreeUtils {

    /** 
     * Assigns appropriate values to unknowns in a provided tree; works recursively down from the top node.
     *
     * @param valueTable table of values associated to variables, with variable names stored in lowercase
     * @param top the top node of the tree to assign values at
     */
    public static <C> void assignVariables(Map<String,C> valueTable, SemanticNode top) {
        if (top instanceof SemanticVariableNode) {
            assignVariable(valueTable, (SemanticVariableNode) top);
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
    public static <C> void assignVariable(Map<String,C> valueTable, SemanticVariableNode varNode) {
        String varName = varNode.name.toLowerCase();
        if (valueTable.containsKey(varName)) {
            varNode.setValue(valueTable.get(varName));
        }
    }

}
