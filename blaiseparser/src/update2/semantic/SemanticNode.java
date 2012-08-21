/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package update2.semantic;

import java.util.List;
import javax.swing.tree.TreeNode;
import org.apache.commons.math.FunctionEvaluationException;

/**
 * <p>
 *   Represents the semantic tree which can be used to actually <i>calculate</i>
 *   values. The primary method retrieves the <i>value</i> of the tree, assuming
 *   it can be calculated, as a class of type <code>C</code>. Secondary methods
 *   allow for retrieving all <i>unknowns</i> within the tree.
 * </p>
 * @author elisha
 */
public interface SemanticNode<C> extends TreeNode {

    /**
     * Computes and returns the value of the node. Individual nodes representing
     * variables are responsible for keeping track of their own values.
     * 
     * @return value of this node, as an object of type C
     * @throws FunctionEvaluationException if the value cannot be determined
     *   (might happen if variables in the tree are undefined)
     */
    public C value() throws FunctionEvaluationException;

    /**
     * Retrieve all unknown identifiers within the tree.
     * @return an ordered list of (non-repeating) unknowns as strings
     */
    public List<String> unknowns();

//    public static void main(String[] args) {
//        try {
//            Method m = Math.class.getMethod("sin", double.class);
//            System.out.println("Result: " + m.invoke(null, 1.0));
//        } catch (NoSuchMethodException ex) {
//            Logger.getLogger(SemanticNode.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (SecurityException ex) {
//            Logger.getLogger(SemanticNode.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(SemanticNode.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(SemanticNode.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(SemanticNode.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    

}
