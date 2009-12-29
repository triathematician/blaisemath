/**
 * SemanticArgumentNodeSupport.java
 * Created on Nov 30, 2009
 */

package org.bm.blaise.scribo.parser.semantic;

import org.bm.blaise.scribo.parser.SemanticNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.TreeNode;
import org.bm.blaise.scribo.parser.SemanticArgumentNode;

/**
 * <p>
 *    This class provides some basic functionality for semantic nodes, implementing
 *    all features except for the <code>value()</code> method in the
 *    <code>SemanticNode</code> interface, and all features in the
 *    <code>TreeNode</code> interface.
 * </p>
 * <p>
 *    One of the parameters of the class is an array of <i>arguments</i>, whose types
 *    are specified by an array of classes <code>Class[]</code>. The class types must
 *    be supplied within the constructor, and the
 * </p>
 * @author Elisha Peterson
 */
public abstract class SemanticArgumentNodeSupport implements SemanticArgumentNode {

    /** The parent object of the node */
    SemanticNode parent;
    /** Argument types */
    Class[] argTypes;
    /** The arguments of the node */
    SemanticNode[] arguments;

    /**
     * Construct the node with multiple arguments.
     * @param argTypes an array of classes representing the required types of arguments
     * @param arguments an array of nodes representing the arguments; the types should be compatible with the <code>argTypes</code> array.
     * @throws IllegalArgumentException if the arguments do not match the supplied argument types
     */
    public SemanticArgumentNodeSupport(Class<?>[] argTypes, SemanticNode... arguments) {
        this.parent = null;
        setArguments(argTypes, arguments);
    }

    /**
     * Sets the arguments. Checks to ensure that the number of arg types is the same as the number of
     * arguments and that the arguments have values that may be appropriately assigned.
     * @param argTypes an array of classes representing the required types of arguments
     * @param arguments an array of nodes representing the arguments; the types should be compatible with the <code>argTypes</code> array.
     * @throws IllegalArgumentException if the arguments do not match the stored argument types
     */
    protected void setArguments(Class<?>[] argTypes, SemanticNode... arguments) {
        // type checking
        if (arguments.length == argTypes.length) {
//            for (int i = 0; i < arguments.length; i++) {
//                if (!argTypes[i].isAssignableFrom(arguments[i].valueType()))
//                    throw new IllegalArgumentException("Argument " + arguments[i].valueType() + " is not an object of type " + argTypes[i]);
//            }
        } else {
            throw new IllegalArgumentException("Must have an equal number of arguments and argument types.");
        }
        this.argTypes = argTypes;
        this.arguments = arguments;
    }

    //
    // SemanticNode & SemanticArgumentNode INTERFACE METHODS
    //

    public List<String> unknowns() {
        List<String> result = new ArrayList<String>();
        for (SemanticNode tn : arguments) {
            for (Object s : tn.unknowns()) {
                if ( ! result.contains((String) s) ) {
                    result.add((String) s);
                }
            }
        }
        return result;
    }

    //
    // TreeNode INTERFACE METHODS
    //

    public TreeNode getParent() {
        return parent;
    }

    public TreeNode getChildAt(int childIndex) {
        return arguments[childIndex];
    }

    public int getChildCount() {
        return arguments.length;
    }

    public int getIndex(TreeNode node) {
        return Arrays.asList(arguments).indexOf(node);
    }

    public boolean isLeaf() {
        return arguments.length == 0;
    }

    public Enumeration children() {
        return (Enumeration) Arrays.asList(arguments);
    }

    public boolean getAllowsChildren() {
        return true;
    }
}
