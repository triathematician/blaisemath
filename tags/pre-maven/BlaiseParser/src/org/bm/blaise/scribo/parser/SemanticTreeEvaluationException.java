/**
 * SemanticTreeEvaluationException.java
 * Created on Dec 26, 2009
 */

package org.bm.blaise.scribo.parser;

/**
 * <p>
 *    This exception is generated when a semantic tree cannot be completely evaluated.
 *    This might happen, for example, when one of the nodes in the tree is a variable
 *    without a value, or when one of the nodes is an unrecognized function token,
 *    or when the actual evaluation contains a numerical error (e.g. divide by zero).
 * </p>
 * @author Elisha Peterson
 */
public class SemanticTreeEvaluationException extends Exception {

    public final static int UNKNOWN = 0;
    public final static int VARIABLE = 1;
    public final static int NULL_CONSTANT = 2;
    public final static int BINARY_NODE = 3;
    public final static int FUNCTION_NODE = 4;

    int errorCode = UNKNOWN;

    final static String[] messages = {
        "Unknown error",
        "Attempt to evaluate a variable node without a value.",
        "Attempt to evaluate a constant node with a null value.",
        "Problem evaluating a binary node.",
        "Problem evaluation a function node."
    };

    public SemanticTreeEvaluationException() {
        this(UNKNOWN);
    }

    public SemanticTreeEvaluationException(int errorCode) {
        super(messages[errorCode]);
        this.errorCode = errorCode;
    }

    public SemanticTreeEvaluationException(String message) {
        super(message);
    }
}
