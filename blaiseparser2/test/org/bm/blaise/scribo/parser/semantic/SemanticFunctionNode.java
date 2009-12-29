/**
 * SemanticFunctionNode.java
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

/**
 * <p>
 *    This class is an implementation of a function within a semantic tree.
 *    It supports only a single argument, so that these functions represent
 *    functions with single inputs and single outputs.
 *    It requires a method which has a single argument of type <code>C</code>
 *    and returns an object of the same type.
 * </p>
 * @param <C> the coordinate type in use
 * @author Elisha Peterson
 */
public class SemanticFunctionNode extends SemanticArgumentNodeSupport {

    Method func;

    /** Construct a function with no arguments. */
    public SemanticFunctionNode(Method func) {
        setFunction(func);
    }

    /** Construct a function with a single argument. */
    public SemanticFunctionNode(Method func, SemanticNode argument) {
        super(argument);
        setFunction(func);
    }

    private void setFunction(Method func) {
        Class[] argumentTypes = func.getParameterTypes();
        Class returnType = func.getReturnType();
        boolean valid = argumentTypes.length == 0 || argumentTypes.length == 1;
        // TODO - improve validity checking
        if (!valid) {
            throw new IllegalArgumentException("Attempt to provide an improper function to a binary construct.");
        }
        this.func = func;
    }

    @Override
    public String toString() {
        return func.getName();
    }

    public SemanticNode argument() {
        return arguments.length > 0 ? arguments[0] : null;
    }

    /**
     * Computes the value of the function applied to the given input.
     * @param input the input variable to the function
     * @return the output of the function
     * @throws FunctionEvaluationException if unable to evaluate the result for some reason
     */
    public Object value(Object input) throws SemanticTreeEvaluationException {
        try {
            if (input == null) {
                return func.invoke(null);
            } else {
                return func.invoke(null, input);
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SemanticFunctionNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SemanticFunctionNode.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SemanticFunctionNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new SemanticTreeEvaluationException(SemanticTreeEvaluationException.FUNCTION_NODE);
    }
    
    /**
     * Attempts to use provided method to evaluate function.
     * @return value of function applied to the argument node
     * @throws FunctionEvaluationException if the function cannot be evaluated properly
     */
    public Object value() throws SemanticTreeEvaluationException {
        return getChildCount() == 1 ? value(argument().value()) : value(null);
    }
}
