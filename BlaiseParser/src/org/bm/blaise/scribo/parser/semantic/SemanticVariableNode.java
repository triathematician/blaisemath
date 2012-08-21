/*
 * SemanticVariableNode.java
 * Created on Nov 26, 2009, 2:42:23 PM
 */
package org.bm.blaise.scribo.parser.semantic;

import org.bm.blaise.scribo.parser.SemanticTreeEvaluationException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *    A node that has a variable value. The value may be either null or set specifically.
 *    If the value is null, then calls to <code>value()</code> throw a <code>SemanticTreeEvaluationException</code>.
 *    Whether or not the value is null, calls to <code>unknowns()</code> returns a list
 *    with a single string corresponding to the name of the variable. There is also
 *    a <code>name</code> string associated with the variable. This string is required
 *    to construct the class, and attemmpts to set a null string lead to an <code>IllegalArgumentException</code>.
 * </p>
 * @author elisha
 */
class SemanticVariableNode extends SemanticLeafNodeSupport {

    /** Type of the variable value. */
    Class<?> valueType;

    /** Construct the node with specified variable. */
    public SemanticVariableNode(String name, Class<?> valueType) {
        if (name == null)
            throw new IllegalArgumentException("Cannot construct a SemanticVariableNode with a null name.");
        this.name = name;
        this.value = null;
        this.valueType = valueType;
    }

    //
    // GET/SET value methods
    //

    public void setValue(Object value) {
        this.value = value;
    }

    public void clearValue() {
        this.value = null;
    }

    //
    // SemanticNode methods
    //

    public Object getValue() throws SemanticTreeEvaluationException {
        if (value == null)
            throw new SemanticTreeEvaluationException(SemanticTreeEvaluationException.VARIABLE);
        return value;
    }

    public Map<String, Class> getVariables() {
        HashMap<String, Class> result = new HashMap<String, Class>();
        result.put(name, valueType);
        return result;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public void assignVariables(Map<String, ?> valueTable) {
        String varName = name.toLowerCase();
        if (valueTable.containsKey(varName))
            setValue(valueTable.get(varName));
    }
}
