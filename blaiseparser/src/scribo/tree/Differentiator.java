/*
 * Differentiator.java
 * Created on Oct 8, 2007, 3:17:28 PM
 */
package scribo.tree;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.math.FunctionEvaluationException;
import scribo.parser.FunctionSyntaxException;

/**
 * This class represents a differentiation operator contained on a tree.<br><br>
 * 
 * @author Elisha
 */
public class Differentiator extends Operator {

    FunctionTreeNode dArg;
    Variable x;
    int n;

    public Differentiator() {
        this(Constant.ZERO, new Variable("x"), 1);
    }

    public Differentiator(FunctionTreeNode argument, String s) {
        this(argument, new Variable(s), 1);
    }

    public Differentiator(FunctionTreeNode argument, Variable x) {
        this(argument, x, 1);
    }

    public Differentiator(FunctionTreeNode argument, Variable x, int n) {
        super(argument);
        this.nodeName = "d";
        this.x = x;
        setDegree(n);
        initDArg();
    }

    @Override
    public String toString() {
        return nodeName + (n == 1 ? "" : "^" + n) + x.toString() + "(" + argumentString() + ")";
    }

    public void setDegree(int n) {
        if (n > 0) {
            this.n = n;
        }
    }

    public void initDArg() {
        if (dArg == null) {
            dArg = getArgument();
            for (int i = 0; i < n; i++) {
                dArg = dArg.derivativeTree(x);
            }
        }
    }

    @Override
    public FunctionTreeNode derivativeTree(Variable v) {
        return new Differentiator(getArgument(), x, n + 1);
    }

    @Override
    public FunctionTreeNode simplified() {
        initDArg();
        return dArg.simplified();
    }

    // VALUE METHODS
    @Override
    public Double getValue(Map<String, Double> table) throws FunctionEvaluationException {
        initDArg();
        return dArg.getValue(table);
    }

    @Override
    public Double getValue(String s, Double d) throws FunctionEvaluationException {
        initDArg();
        return dArg.getValue(s, d);
    }

    @Override
    public List<Double> getValue(String s, List<Double> d) throws FunctionEvaluationException {
        initDArg();
        return dArg.getValue(s, d);
    }

    // STATIC METHODS
    /** Static method generating a derivative given a vector of elements. First element should be a regular argument;
     * remaining elements should be variables.
     * @param arguments
     * @return derivative of elements
     * @throws parser.FunctionSyntaxException
     * @throws java.lang.ArrayIndexOutOfBoundsException
     */
    public static Differentiator getDiff(Vector<FunctionTreeNode> arguments) throws FunctionSyntaxException, ArrayIndexOutOfBoundsException {
        if (!(arguments.get(1) instanceof Variable)) {
            throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_TYPE);
        }
        if (arguments.size() == 2) {
            return new Differentiator(arguments.get(0), (Variable) arguments.get(1));
        }
        throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_NUMBER);
    }

    @Override
    public boolean isValidSubNode() {
        return true;
    }
}
