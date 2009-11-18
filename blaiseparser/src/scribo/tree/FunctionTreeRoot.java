/*
 * FunctionTreeRoot.java
 * Created on Sep 21, 2007, 2:58:57 PM
 */

// TODO Add equality root node.
// TODO Add list of variables at FunctionRoot/setting variable values at the top
// TODO Remove dependence on FunctionTreeFunctionNode
// TODO Change Variable collection to a set
package scribo.tree;

import deprecated.RealFunction;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.Vector;
import org.apache.commons.math.FunctionEvaluationException;
import scio.coordinate.formal.EuclideanElement;
import deprecated.Function;
import scribo.parser.FunctionSyntaxException;
import scribo.parser.Parser;

/**
 * This class represents the root of a FunctionTree. In particular, every tree which is constructed passes all
 * information through a root node, which is this one.
 * <p>
 * The class stores a list of variables (whose values are "parameters") and parameters (whose values are "knowns" although
 * they can be changed). The distinction is important because fundamentally the function f(x)=a*sin(b(x+c))+d should be
 * plotted in two-dimensions, although nominally there are five parameters on the righthand side. Other classes which use
 * this one should be able to "observe" this fact and know what to do with the parameters and the variables automatically.
 * This, the part that is important for any class which utilizes this one is (i) how to get at and adjust parameters, (ii) how
 * to get at the variables, and (iii) how to evaluate the function at a particular value or range of values.
 * </p>
 * <p>
 * Note that this is the only class which "cares" that an unknown is either a parameter or a variable; any other FunctionTreeNode
 * treats the two types exactly the same.
 * </p>
 * @author Elisha Peterson
 */
public class FunctionTreeRoot extends FunctionTreeFunctionNode implements FunctionRoot<Double> {

    // VARIABLES
    /** Whether variables/parameters are auto-decided */
    boolean autoVariables = true;
    /** Variables required to obtain a value. The order of the variables is important as it determines how
     * the class evaluates based upon a list of inputs.
     */
    Vector<String> variables;
    /** Parameters associated with the tree.. along with the variables. If not passed directly to "getValue",
     * the values will be looked up in this table. */
    TreeMap<String, Double> parameters;

    // CONSTRUCTORS
    /** Constructs as the zero function. */
    public FunctionTreeRoot() {
        this(Constant.ZERO);
    }

    /** Constructs with given expression. */
    public FunctionTreeRoot(String s) throws FunctionSyntaxException {
        this(Parser.parseExpression(s));
    }

    /** Constructs with a particular tree node as the main argument. */
    public FunctionTreeRoot(FunctionTreeNode c) {
        variables = new Vector<String>();
        parameters = new TreeMap<String, Double>();
        addSubNode(c);
        variables.addAll(c.getUnknowns());
    }


    // INITIALIZERS
    /** Provides the name of the function during initialization. */
    @Override
    public void initFunctionType() {
        setFunctionNames("ROOT", null, null, null);
    }

    // HANDLING CHILD NODES
    /** This node is not permitted to be added to other nodes. */
    @Override
    public boolean isValidSubNode() {
        return false;
    }

    /** Changes the primary child node, which is the root of the function tree. */
    @Override
    public void setArgumentNode(FunctionTreeNode argument) {
        super.setArgumentNode(argument);
        // Revise list of variables
        if (!autoVariables) {
            return;
        }
        Collection<String> vars = argument.getUnknowns();
        for (String s : vars) {
            if (!(variables.contains(s) || parameters.containsKey(s))) {
                variables.add(s);
            }
        }
        for (String s : (Vector<String>) variables.clone()) {
            if (!vars.contains(s)) {
                variables.remove(s);
            }
        }
    }


    // HANDLING VARIABLES AND PARAMETERS
    /** Returns current list of variables. */
    public Vector<String> getVariables() {
        return variables;
    }

    /** Forces the list of variables to a particular set. This turns off automatic variable detection. */
    public void setVariables(Vector<String> vars) {
        setAutoVariables(false);
        variables = vars;
    }

    /** Forces the list of variables to a particular set. This turns off automatic variable detection. */
    public void setVariables(String[] vars) {
        setAutoVariables(false);
        variables = new Vector<String>();
        for (int i = 0; i < vars.length; i++) {
            variables.add(vars[i]);
        }
    }

    /** Returns current list of parameters. */
    public TreeMap<String, Double> getParameters() {
        return parameters;
    }

    /** Sets up the entire list of parameters, replacing what is already there. */
    public void setParameters(TreeMap<String, Double> parameters) {
        this.parameters.clear();
        this.parameters.putAll(parameters);
        variables.removeAll(this.parameters.keySet());
    }

    /** Sets up list of parameters using an array, replacing what is already there. */
    public void setParameters(Object[][] parameters) {
        TreeMap<String, Double> parameterTree = new TreeMap<String, Double>();
        try {
            for (int i = 0; i < parameters.length; i++) {
                parameterTree.put((String) parameters[i][0], (Double) parameters[i][1]);
            }
            setParameters(parameterTree);
        } catch (ClassCastException e) {
            System.out.println("error in parameter list");
        }
    }

    /** Returns number of variables. */
    public int getNumVariables() {
        return variables.size();
    }

    /** Returns number of parameters. */
    public int getNumParameters() {
        return parameters.size();
    }

    /** Checks auto-variables flag. */
    public boolean isAutoVariables() {
        return autoVariables;
    }

    /** Sets auto-variables flag. */
    public void setAutoVariables(boolean newValue) {
        this.autoVariables = newValue;
    }

    // OVERRIDE SUBMETHODS FROM FUNCTIONTREEFUNCTIONNODE
    /** Returns a new tree representing the derivative with respect to the specified variable. */
    @Override
    public FunctionTreeNode derivativeTree(Variable v) {
        return numSubNodes() == 1
                ? new FunctionTreeRoot(children.get(0).derivativeTree(v))
                : null;
    }

    /** Represents the simplified version of this tree. Does this by checking to see if the tree may
     * be represented by a constant; otherwise just simplifies the argument.
     * @return a <code>FunctionTreeRoot</code> representing the simplifed tree.
     */
    @Override
    public FunctionTreeNode simplified() {
        try {
            return new FunctionTreeRoot(new Constant(getValue()).simplified());
        } catch (FunctionEvaluationException e) {
            return new FunctionTreeRoot(argumentSimplified());
        }
    }

    // METHODS TO RETURN VALUE
    /** Default value function for <code>Function(Double,Double)</code>, which is first
     * implemented by <code>FunctionTreeFunctionNode</code>.
     * @param value the input value
     * @return value of the tree evaluated at the given value
     * @throws scio.function.FunctionValueException if there is more than 1 variable in the tree
     */
    public Double getValue(Double value) throws FunctionEvaluationException {
        if (variables != null && variables.size() >= 1) {
            return getValue(variables.get(0), value);
        }
        // by default try to get the constant value
        return getValue("NOINPUTVARIABLE", value);
    }

    /** Default value function for <code>Function(Double,Double)</code>, which is first
     * implemented by <code>FunctionTreeFunctionNode</code>.
     * @param values the input values
     * @return value of the tree evaluated at the given value
     * @throws scio.function.FunctionValueException if there is more than 1 variable in the tree
     */
    @Override
    public List<Double> getValue(List<Double> values) throws FunctionEvaluationException {
        if (variables != null && variables.size() >= 1) {
            return getValue(variables.get(0), values);
        }
        // by default try to get the constant value
        return getValue("NOINPUTVARIABLE", values);
    }

    /** Returns value for a single variable value.
     * @param var the variable
     * @param value the value of the variable
     * @return value of the tree evaluated at the given variable
     * @throws scio.function.FunctionValueException if there is more than 1 variable in the tree
     */
    @Override
    public Double getValue(String var, Double value) throws FunctionEvaluationException {
        if (parameters == null || parameters.isEmpty()) {
            return children.get(0).getValue(var, value);
        }
        TreeMap<String, Double> table = new TreeMap<String, Double>();
        table.put(var, value);
        return getValue(table);
    }

    /** Returns value for an array of values for a single variable.
     * @param var the variable
     * @param values the value of the variable
     * @return vector of values of the tree evaluated at the given variable
     * @throws scio.function.FunctionValueException if there is more than 1 variable in the tree
     */
    @Override
    public List<Double> getValue(String var, List<Double> values) throws FunctionEvaluationException {
        if (parameters == null || parameters.isEmpty()) {
            return children.get(0).getValue(var, values);
        }
        TreeMap<String, Double> table = new TreeMap<String, Double>();
        table.putAll(parameters);
        Vector<Double> result = new Vector<Double>(values.size());
        for (Double x : values) {
            table.put(var, x);
            result.add(children.get(0).getValue(table));
        }
        return result;
    }

    /** Returns value given the list of values specified within the table... these are a list of variable values,
     * to which pre-existing parameters will be added. So the table should NOT include any parameters.
     * @param table mapping of variable values, as doubles
     * @return double value of the evaluated tree
     * @throws scio.function.FunctionValueException if the values do not cover all variables
     */
    @Override
    public Double getValue(Map<String, Double> table) throws FunctionEvaluationException {
        table.putAll(parameters);
        return children.get(0).getValue(table);
    }

    // FUNCTION INTERFACE METHODS
    //public ParameterFunction getParameterFunction(){return null;}
    /** Returns a function of the specified list of variables.
     * @param vars an ordered array of variables
     * @return a function whose type depends on the number of variables
     */
    public Function<?, Double> getFunction(final String[] vars) {
        setVariables(vars);
        return getFunction();
    }

    /** Returns function with number of inputs that depends on the number of variables in the tree's "variables" parameter.
     * @return function with input of arbitrary type and output of double type
     */
    public Function<?, Double> getFunction() {
        switch (variables.size()) {
            case 0:
                return null;
            case 1:
                return new OneInput();
            case 2:
                return new TwoInput();
            default:
                return new NInput();
        }
    }

    /** Inner function with a single input. */
    class OneInput implements RealFunction {

        public Double getValue(Double x) throws FunctionEvaluationException {
            return FunctionTreeRoot.this.getValue(variables.get(0), x);
        }

        public List<Double> getValue(List<Double> xx) throws FunctionEvaluationException {
            return FunctionTreeRoot.this.getValue(variables.get(0), xx);
        }

        public double value(double x) throws FunctionEvaluationException {
            return FunctionTreeRoot.this.getValue(variables.get(0), x);
        }
    }

    /** Inner function with two inputs. */
    class TwoInput implements Function<Point2D.Double, Double> {

        public Double getValue(Point2D.Double x) throws FunctionEvaluationException {
            TreeMap<String, Double> table = new TreeMap<String, Double>();
            try {
                table.put(variables.get(0), x.x);
                table.put(variables.get(1), x.y);
            } catch (NoSuchElementException e) {
                throw new FunctionEvaluationException(0.0, "Too Few Inputs", new Object[]{});
            }
            return FunctionTreeRoot.this.getValue(table);
        }

        public List<Double> getValue(List<Point2D.Double> x) throws FunctionEvaluationException {
            Vector<Double> result = new Vector<Double>();
            TreeMap<String, Double> table = new TreeMap<String, Double>();
            for (int i = 0; i < x.size(); i++) {
                for (int j = 0; j < variables.size(); j++) {
                    table.put(variables.get(0), x.get(i).x);
                    table.put(variables.get(1), x.get(i).y);
                }
                result.add(FunctionTreeRoot.this.getValue(table));
            }
            table.putAll(parameters);
            return result;
        }
    }

    /** Inner function with arbitrary number of inputs. */
    class NInput implements Function<EuclideanElement, Double> {

        public Double getValue(EuclideanElement x) throws FunctionEvaluationException {
            TreeMap<String, Double> table = new TreeMap<String, Double>();
            for (int i = 0; i < variables.size(); i++) {
                try {
                    table.put(variables.get(i), x.getCoordinate(i));
                } catch (NoSuchElementException e) {
                    break;
                }
            }
            table.putAll(parameters);
            return FunctionTreeRoot.this.getValue(table);
        }

        public List<Double> getValue(List<EuclideanElement> x) throws FunctionEvaluationException {
            Vector<Double> result = new Vector<Double>();
            TreeMap<String, Double> table = new TreeMap<String, Double>();
            table.putAll(parameters);
            for (int i = 0; i < x.size(); i++) {
                for (int j = 0; j < variables.size(); j++) {
                    table.put(variables.get(j), x.get(i).getCoordinate(j));
                }
                result.add(FunctionTreeRoot.this.getValue(table));
            }
            return result;
        }
    }
}
