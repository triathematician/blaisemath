/*
 * ParsedMultivariateVectorialFunction.java
 * Created Dec 29, 2009
 */

package org.bm.blaise.scio.function;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.scribo.parser.SemanticNode;
import org.bm.blaise.scribo.parser.SemanticTreeEvaluationException;
import org.bm.blaise.scribo.parser.grammars.RealGrammar;
import org.bm.blaise.scribo.parser.semantic.SemanticTreeUtils;

/**
 * <p>
 *   This class uses several fucntion strings to parse and create a <code>MultivariateVectorialFunction</code>.
 *   Parsing is handled by the standard <code>RealGrammar</code>.
 * </p>
 * @author Elisha Peterson
 */
public class ParsedMultivariateVectorialFunction implements MultivariateVectorialFunction {
    
    String[] functions;
    String[] vars;
    transient HashMap<String,Double> variableTable;
    transient SemanticNode[] functionTrees;

    /** Construct a real function specified by the given input string.
     * @param funcString the function as a string
     * @throws ParseException if the function cannot be parsed
     */
    public ParsedMultivariateVectorialFunction(String[] funcStrings, String... vars) throws ParseException {
        setFunctionStrings(funcStrings, vars);
    }

    public String[] getFunctionStrings() {
        return functions;
    }

    public String[] getVariables() {
        return vars;
    }

    /**
     * Sets up the function for the given string, along with doing some error checking and building the underlying tree.
     *
     * @param function  array of strings representing the function
     * @throws ParseException  if the String cannot be parsed
     * @throws IllegalArgumentException  if the String function has too many unknowns
     */
    public void setFunctionStrings(String[] functions, String[] vars) throws ParseException {
        SemanticNode[] trees = new SemanticNode[functions.length];
        // compile trees, and check to see if variables are defined properly
        Set<String> parseVars = new HashSet<String>();
        for (int i = 0; i < functions.length; i++) {
            trees[i] = RealGrammar.PARSER.parseTree(functions[i]);
            parseVars.addAll(trees[i].unknowns().keySet());
        }
        if ( ! Arrays.asList(vars).containsAll(parseVars) ) {
            throw new IllegalArgumentException("Functional expressions " + Arrays.asList(functions) + " contain variables that are not included in the provided list: " + Arrays.toString(vars) );
        }
        // set up variable table
        variableTable = new HashMap<String,Double>();
        for (int i = 0; i < vars.length; i++) {
            variableTable.put(vars[i], null);
        }
        this.functions = functions;
        this.vars = vars;
        this.functionTrees = trees;
    }

    @Override
    public String toString() {
        return Arrays.toString(functions) + " @ " + Arrays.toString(vars);
    }

    public double[] value(double[] x) throws FunctionEvaluationException {
        if (x.length != vars.length)
            throw new FunctionEvaluationException(x, "Number of variables in evaluation must match number of variables specified in ParsedMultivariateVectorialFunction.");
        try {
            for (int i = 0; i < vars.length; i++) {
                variableTable.put(vars[i], x[i]);
            }
            double[] result = new double[functions.length];
            for (int i = 0; i < result.length; i++) {
                SemanticTreeUtils.assignVariables(variableTable, functionTrees[i]);
                result[i] = (Double) functionTrees[i].value();
            }
            return result;
        } catch (SemanticTreeEvaluationException ex) {
            throw new FunctionEvaluationException(x, ex.getMessage());
        }
    }



}
