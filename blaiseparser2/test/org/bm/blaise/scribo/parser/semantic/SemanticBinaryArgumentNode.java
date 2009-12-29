/**
 * SemanticBinaryNode.java
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
 *    This class describes functions which are binary on two nodes
 * </p>
 * @author Elisha Peterson
 */
public class SemanticBinaryArgumentNode extends SemanticArgumentNodeSupport {

    Method func;

    public SemanticBinaryArgumentNode(Method func, SemanticNode argument1, SemanticNode argument2) {
        super(argument1, argument2);
        setFunction(func);
    }

    private void setFunction(Method func) {
        Class[] argTypes = func.getParameterTypes();
        boolean valid = argTypes.length == 2;
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

    public SemanticNode getFirstArgument() {
        return arguments[0];
    }

    public SemanticNode getSecondArgument() {
        return arguments[1];
    }

    public Object value(Object arg1, Object arg2) throws SemanticTreeEvaluationException {
        try {
            return func.invoke(null, arg1, arg2);
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
        return value(getFirstArgument().value(), getSecondArgument().value());
    }
}
