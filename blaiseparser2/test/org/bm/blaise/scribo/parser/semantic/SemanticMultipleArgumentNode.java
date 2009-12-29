/**
 * SemanticMultiargumentNode.java
 * Created on Nov 30, 2009
 */
package org.bm.blaise.scribo.parser.semantic;

import org.bm.blaise.scribo.parser.semantic.*;
import org.bm.blaise.scribo.parser.SemanticTreeEvaluationException;
import org.bm.blaise.scribo.parser.SemanticNode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.MutableTreeNode;

/**
 * <p>
 *    This class represents a node in a tree with multiple arguments. Overriding
 *    classes must be able to convert the provided argument nodes into a resulting
 *    object of type C.
 * </p>
 * <p>
 *    The node is backed by a method that acts on the arguments.
 * </p>
 * @param <C> the type of result
 * @author Elisha Peterson
 */
public class SemanticMultipleArgumentNode extends SemanticArgumentNodeSupport {

    Method func;
    Class[] argumentTypes;
    Class returnType;

    public SemanticMultipleArgumentNode(Method func, SemanticNode[] arguments) {
        super(arguments);
        setFunction(func);
    }

    private void setFunction(Method func) {
        argumentTypes = func.getParameterTypes();
        returnType = func.getReturnType();
        // TODO - check that return type is compatible with generic type
        boolean valid = true;
        if (!valid)
            throw new IllegalArgumentException("Attempt to provide an improper function to a binary construct.");
        this.func = func;
    }

    @Override
    public String toString() {
        return func.getName();
    }

    /** By default, use a binary function to pass iteratively through the arguments. */
    public Object value(Object... args) throws SemanticTreeEvaluationException {
        try {
            if (func.getParameterTypes()[0].equals(double[].class)) {
                double[] argd = new double[args.length];
                for (int i = 0; i < argd.length; i++) {
                    argd[i] = (Double) args[i];
                }
                return func.invoke(null, argd);
            } else {
                Object result = func.invoke(null, args[0], args[1]);
                for (int i = 2; i < args.length; i++) {
                    result = func.invoke(null, result, args[i]);
                }
                return result;
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SemanticBinaryArgumentNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SemanticBinaryArgumentNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SemanticBinaryArgumentNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new SemanticTreeEvaluationException(SemanticTreeEvaluationException.BINARY_NODE);
    }

    public Object value() throws SemanticTreeEvaluationException {
        Object[] args = new Object[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            args[i] = arguments[i].value();
        }
        return value(args);
    }
    
    public void add(SemanticNode node) {
        SemanticNode[] newArgs = new SemanticNode[arguments.length + 1];
        System.arraycopy(arguments, 0, newArgs, 0, arguments.length);
        newArgs[arguments.length] = node;
        this.arguments = newArgs;
    }
}
