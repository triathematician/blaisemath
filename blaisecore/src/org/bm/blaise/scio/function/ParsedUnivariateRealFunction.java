/*
 * ParsedUnivariateRealFunction.java
 * Created Dec 29, 2009
 */

package org.bm.blaise.scio.function;

import java.util.HashMap;
import java.util.Set;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.scribo.parser.SemanticNode;
import org.bm.blaise.scribo.parser.SemanticTreeEvaluationException;
import org.bm.blaise.scribo.parser.grammars.RealGrammar;
import org.bm.blaise.scribo.parser.semantic.SemanticTreeUtils;

/**
 * <p>
 *   This class wraps the <code>ParsedRealFunction</code> class defined in the
 *   <b>blaiseparser2</b> library in order to implement <code>UnivariateRealFunction</code>.
 *   Parsing is handled by the <code>RealTreeBuilder</code> class with grammar
 *   defined by <code>RealGrammar</code>.
 * </p>
 * <p>
 *   A parameter table stores values for any "parameters", which means any unknowns
 *   that ARE NOT the specified variable.
 * </p>
 * @author Elisha Peterson
 */
public class ParsedUnivariateRealFunction implements UnivariateRealFunction {
    
    String function;
    String variable;
    RealParameterTable parameters;
    
    transient HashMap<String,Double> variableTable;
    transient SemanticNode functionTree;

    /** Construct a real function specified by the given input string.
     * @param funcString the function as a string
     * @throws ParseException if the function cannot be parsed
     */
    public ParsedUnivariateRealFunction(String funcString, String var) throws ParseException {
        this(funcString, var, new RealParameterTable());
    }

    /** Construct a real function specified by the given input string.
     * @param funcString the function as a string
     * @throws ParseException if the function cannot be parsed
     */
    public ParsedUnivariateRealFunction(String funcString, String var, RealParameterTable parameters) throws ParseException {
        this.parameters = parameters;
        setFunctionString(funcString, var);
    }

    public String getFunctionString() {
        return function;
    }

    /**
     * Sets up the function for the given string. Requires a variable to be passed in.
     * If this variable is null, the current variable will not be changed. If both the
     * current variable and this variable are null, the variable will become the first
     * variable that is found.
     *
     * @param function the String representation of the function
     * @param var the variable used by the function
     * @throws ParseException  if the String cannot be parsed
     * @throws IllegalArgumentException  if the String function has too many unknowns
     */
    public void setFunctionString(String function, String var) throws ParseException {
        SemanticNode newTree = RealGrammar.PARSER.parseTree(function);
        Set<String> unknowns = newTree.unknowns().keySet();
        // use the specified variable if possible; otherwise use what's already here or pick the first one in the list of unknowns
        if (var == null) {
            if (variable == null) {
                if (unknowns.size() > 0) {
                    variable = (String) unknowns.toArray()[0];
                } else {
                    variable = "UNKNOWN_VARIABLE";
                }
            }
        } else if (!var.equals(variable)) {
            this.variable = var;
        }
        variableTable = new HashMap<String,Double>();
        variableTable.put(variable,null);
        // if unknown is not the variable, insert into the parameter table
        for (String u : unknowns) {
            if ( ! (u.equals(variable) || parameters.containsKey(u)) ) {
                parameters.put(u, 0.0);
            }
        }
        this.function = function;
        this.functionTree = newTree;
    }

    @Override
    public String toString() {
        return function;
    }

    public double value(double x) throws FunctionEvaluationException {
        try {
            variableTable.put(variable, x);
            variableTable.putAll(parameters);
            SemanticTreeUtils.assignVariables(variableTable, functionTree);
            return (Double) functionTree.value();
        } catch (SemanticTreeEvaluationException ex) {
            throw new FunctionEvaluationException(x, ex.getMessage());
        }
    }



}
