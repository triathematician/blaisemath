/**
 * SemanticVarargMethodNode.java
 * Created on Dec 27, 2009
 */

package org.bm.blaise.scribo.parser.semantic;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.bm.blaise.scribo.parser.SemanticNode;
import org.bm.blaise.scribo.parser.SemanticTreeEvaluationException;

/**
 * <p>
 *    This classes builds a semantic node upon a static Java method that takes a variable
 *    number of inputs of a single type (as an array)
 * </p>
 * @author Elisha Peterson
 */
public class SemanticVarargMethodNode extends SemanticMethodNode {

    /**
     * Constructs the node using the specified method.
     * @param method the method
     */
    public SemanticVarargMethodNode(Method method, SemanticNode... arguments) {
        super(method, arguments);
        this.method = method;
    }

    @Override
    boolean compatibleArguments(Class<?>[] types1, Class<?>[] types2) {
//        System.out.println("Checking vararg compatibility of " + Arrays.toString(types1) + " and " + Arrays.toString(types2));
        if (super.compatibleArguments(types1, types2)) {
            return true;
        }
        if (types1.length == 1 && types1[0].isArray()) {
            // TODO - also check the compatibility
            return true;
        }
        return false;
    }

    @Override
    public Object value() throws SemanticTreeEvaluationException {
        Class<?> pType = method.getParameterTypes()[0].getComponentType();
        try {
            // use explicit array creation for primitive types
            if (pType.equals(double.class)) {
                double[] args = new double[arguments.length];
                for (int i = 0; i < args.length; i++) {
                    args[i] = (Double) arguments[i].value();
                }
                return method.invoke(null, args);
            } else if (pType.equals(boolean.class)) {
                boolean[] args = new boolean[arguments.length];
                for (int i = 0; i < args.length; i++) {
                    args[i] = (Boolean) arguments[i].value();
                }
                return method.invoke(null, args);
            } else if (pType.equals(int.class)) {
                int[] args = new int[arguments.length];
                for (int i = 0; i < args.length; i++) {
                    args[i] = (Integer) arguments[i].value();
                }
                return method.invoke(null, args);
            } else {
                return value(pType);
            }
        } catch (IllegalAccessException ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(arguments) + ": " + ex + " (invalid method)");
        } catch (InvocationTargetException ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(arguments) + ": " + ex + " (invoking method)");
        } catch (ClassCastException ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(arguments) + ": " + ex + " (incorrect parameters passed to method)");
        }
    }

    <T> Object value(Class<T> t) throws SemanticTreeEvaluationException {
        try {
            T[] args = (T[]) Array.newInstance(t, arguments.length);
            for (int i = 0; i < args.length; i++) {
                args[i] = (T) arguments[i].value();
            }
            return method.invoke(null, args);
        } catch (IllegalAccessException ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(arguments) + ": " + ex);
        } catch (InvocationTargetException ex) {
            throw new SemanticTreeEvaluationException("Failed to evaluate method " + method + " with arguments " + Arrays.toString(arguments) + ": " + ex);
        }
    }
}
