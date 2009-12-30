/*
 * SemanticConstantNode.java
 * Created on Nov 26, 2009, 2:42:23 PM
 */
package org.bm.blaise.scribo.parser.semantic;

import org.bm.blaise.scribo.parser.SemanticTreeEvaluationException;
import java.util.Collections;
import java.util.Map;

/**
 * <p>
 *    A node that has a constant value. This class ensures that the value is not null;
 *    attempts to set a null value result in an <code>IllegalArgumentException</code>.
 *    The name of the constant may be null, however. There is no support for changing
 *    the value once the class has been constructed.
 * </p>
 * @author elisha
 */
public class SemanticConstantNode extends SemanticLeafNodeSupport {

    /** Construct the node with specified value. */
    public SemanticConstantNode(Object value) {
        this(null, value);
    }
    
    /** Construct node with specified value and name */
    public SemanticConstantNode(String name, Object value) {
        if (value == null)
            throw new IllegalArgumentException("Cannot construct a SemanticConstantNode with a null value!");
        this.name = name;
        this.value = value;
    }

    //
    // SemanticNode INTERFACE METHODS
    //
    
    public Object value() throws SemanticTreeEvaluationException {
        return value;
    }

    public Map<String, Class<?>> unknowns() {
        return Collections.EMPTY_MAP;
    }

    public Class<?> valueType() {
        return value.getClass();
    }
}
