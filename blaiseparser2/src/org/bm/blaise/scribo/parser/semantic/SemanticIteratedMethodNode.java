/**
 * SemanticIteratedMethodNode.java
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
public class SemanticIteratedMethodNode extends SemanticMethodNode {

    /**
     * Constructs the node using the specified method.
     * @param method the method
     */
    public SemanticIteratedMethodNode(Method method, SemanticNode... arguments) {
        super(method, arguments);
        this.method = method;
    }

    @Override
    protected void setArguments(Class<?>[] argTypes, SemanticNode... arguments) {
        // type checking
        if (arguments.length == argTypes.length || argTypes.length == 2 && arguments.length >= 2) {
//            for (int i = 0; i < arguments.length; i++) {
//                if (!argTypes[i].isAssignableFrom(arguments[i].valueType()))
//                    throw new IllegalArgumentException("Argument " + arguments[i].valueType() + " is not an object of type " + argTypes[i]);
//            }
        } else {
            throw new IllegalArgumentException("Must have an equal number of arguments (" + arguments.length + " given) and argument types (" + argTypes.length + " given).");
        }
        this.argTypes = argTypes;
        this.arguments = arguments;
    }

    @Override
    public Object value() throws SemanticTreeEvaluationException {
        try {
            Object[] args = new Object[arguments.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = arguments[i].value();
            }
            if (arguments.length == argTypes.length) {
                return method.invoke(null, args);
            } else {
                return iteratedValue(method, args);
            }
        } catch (Exception ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(arguments) + ": " + ex);
        }
    }

    /** By default, use a binary function to pass iteratively through the arguments. */
    static Object iteratedValue(Method method, Object... args) throws SemanticTreeEvaluationException {
        try {
            Object result = method.invoke(null, args[0], args[1]);
            for (int i = 2; i < args.length; i++) {
                result = method.invoke(null, result, args[i]);
            }
            return result;
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SemanticIteratedMethodNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SemanticIteratedMethodNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SemanticIteratedMethodNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new SemanticTreeEvaluationException(SemanticTreeEvaluationException.FUNCTION_NODE);
    }
}
