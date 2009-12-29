/**
 * SemanticMethodNode.java
 * Created on Dec 27, 2009
 */

package org.bm.blaise.scribo.parser.semantic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bm.blaise.scribo.parser.SemanticNode;
import org.bm.blaise.scribo.parser.SemanticTreeEvaluationException;

/**
 * <p>
 *    This classes builds a semantic node upon a static Java method. The arguments and argument
 *    types must be compatible with the underlying method.
 * </p>
 * @author Elisha Peterson
 */
public class SemanticMethodNode extends SemanticArgumentNodeSupport {

    /** Method used to implement the node. */
    Method method;

    /**
     * Constructs the node using the specified method.
     * @param method the method
     */
    public SemanticMethodNode(Method method, SemanticNode... arguments) {
        super(method.getParameterTypes(), arguments);
        this.method = method;
    }

    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    public Object value() throws SemanticTreeEvaluationException {
        try {
            Object[] args = new Object[arguments.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = arguments[i].value();
            }
            return method.invoke(null, args);
        } catch (Exception ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(arguments) + ": " + ex);
        }
    }

    public Class<?> valueType() {
        return method.getReturnType();
    }
}
