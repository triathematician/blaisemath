/*
 * ParsedUnivariateRealFunction.java
 * Created Dec 29, 2009
 */

package org.bm.blaise.scio.function;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.scribo.parser.SemanticNode;
import org.bm.blaise.scribo.parser.SemanticTreeEvaluationException;
import org.bm.blaise.scribo.parser.grammars.RealGrammar;
import org.bm.blaise.scribo.parser.semantic.SemanticTreeUtils;
import org.bm.blaise.scribo.parser.semantic.TokenParser;

/**
 * <p>
 *   This class wraps the <code>ParsedRealFunction</code> class defined in the
 *   <b>blaiseparser2</b> library in order to implement <code>UnivariateRealFunction</code>.
 *   Parsing is handled by the <code>RealTreeBuilder</code> class with grammar
 *   defined by <code>RealGrammar</code>.
 * </p>
 * @author Elisha Peterson
 */
public class ParsedUnivariateRealFunction implements UnivariateRealFunction {
    
    String function;
    transient String variable;
    transient HashMap<String,Double> variableTable;
    transient SemanticNode functionTree;

    /** Construct a real function specified by the given input string.
     * @param funcString the function as a string
     * @throws ParseException if the function cannot be parsed
     */
    public ParsedUnivariateRealFunction(String funcString) throws ParseException {
        setFunctionString(funcString);
    }

    public String getFunctionString() {
        return function;
    }

    /**
     * Sets up the function for the given string, along with doing some error checking and building the underlying tree.
     *
     * @param function  the String representation of the function
     * @throws ParseException  if the String cannot be parsed
     * @throws IllegalArgumentException  if the String function has too many unknowns
     */
    public void setFunctionString(String function) throws ParseException {
        SemanticNode newTree = new TokenParser(RealGrammar.INSTANCE).parseTree(function);
        Set<String> vars = newTree.unknowns().keySet();
        if (vars.size() > 1) {
            throw new IllegalArgumentException("Cannot construct a UnivariateRealFunction: the function " + function + " has more than 1 input.");
        }
        variableTable = new HashMap<String,Double>();
        variable = vars.size() == 1 ? vars.toArray(new String[]{})[0] : "dummy";
        variableTable.put(variable, null);
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
            SemanticTreeUtils.assignVariables(variableTable, functionTree);
            return (Double) functionTree.value();
        } catch (SemanticTreeEvaluationException ex) {
            throw new FunctionEvaluationException(x, ex.getMessage());
        }
    }



}
