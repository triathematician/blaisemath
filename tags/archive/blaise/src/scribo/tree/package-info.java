/*
 * package-info.java
 * 
 * Created on Sep 18, 2007, 5:05:01 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * <p>
 * This package contains what is necessary for a constructed evaluation tree for a function.
 * The components of a tree are the <i>leaves</i>, which are variables and constants used in a problem,
 * and <i>nodes</i>, which are the functions transforming those variables/constants into a result.
 * The nodes may be <i>unary</i>, with a single input, or <i>binary</i>, with two inputs. More flexibility
 * may be added if necessary. For example, multiplication/addition may have multiple sub-nodes.
 * </p>
 * 
 * <p>
 * For the moment, the tree will just represent a real-valued function, possibly of several inputs. The
 * plan is for the tree to be able to resolve an expression and identify the pieces which are *unknown*
 * as the leaves/variables/etc. So, even if the variable isn't <i>explicitly</i> known, the tree will be
 * able to identify it.
 * </p>
 * 
 * <p>
 * Evaluation of the tree will work recursively. If the value isn't known, the tree will simply return a
 * string containing the expression below.
 * </p>
 * 
 * <p> 
 * The tree will maintain a string value of the expression it holds, and will support constructing the string
 * from the underlying tree.
 * </p>
 */
package tree;


