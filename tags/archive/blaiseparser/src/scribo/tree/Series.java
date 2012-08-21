/*
 * Series.java
 * Created on Oct 8, 2007, 3:34:28 PM
 */
package scribo.tree;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import org.apache.commons.math.FunctionEvaluationException;
import scribo.parser.FunctionSyntaxException;

/**
 * <p>
 * A series is either an additive series or multiplicative series, where the terms are summed between a minimum
 * and maximum value, with given step size. The
 * </p>
 * 
 * @author Elisha Peterson
 */
public abstract class Series extends Operator {

    /** Creates the series. Requires a local index variable, specified by "var", and a range of values, together with the primary argument node. */
    public Series(FunctionTreeNode ftn, String var, FunctionTreeNode min, FunctionTreeNode max, FunctionTreeNode step) {
        super(ftn);
        addLocalVar(var, null);
        addSubNode(min);
        addSubNode(max);
        addSubNode(step);
    }

    public Series(FunctionTreeNode ftn, String var, FunctionTreeNode min, FunctionTreeNode max) {
        this(ftn, var, min, max, Constant.ONE);
    }

    public Series(FunctionTreeNode ftn, String var, double min, double max, double step) {
        this(ftn, var, new Constant(min), new Constant(max), new Constant(step));
    }

    public Series(FunctionTreeNode ftn, String var, double min, double max) {
        this(ftn, var, new Constant(min), new Constant(max), Constant.ONE);
    }

    public String getIndex() {
        return unknowns.firstKey();
    }

    public FunctionTreeNode getMinNode() {
        return getFirstChild();
    }

    public FunctionTreeNode getMaxNode() {
        return getSecondChild();
    }

    public FunctionTreeNode getStepNode() {
        return getThirdChild();
    }

    @Override
    public boolean isValidSubNode() {
        return (unknowns != null) && (!unknowns.isEmpty()) && (getMinNode() != null) && (getMaxNode() != null);
    }

    @Override
    public String toString() {
        String result = nodeName + "(" + argumentString() + "," + getIndex() + "," + getMinNode().toString() + "," + getMaxNode().toString();
        if (getStepNode() != null) {
            if (getStepNode().equals(Constant.ONE)) {
                result += "," + getStepNode().toString();
            }
        }
        return result + ")";
    }

    @Override
    public boolean isNumber() {
        return getUnknowns().size() == 0 && getMinNode().isNumber() && getMaxNode().isNumber() && getStepNode().isNumber();
    }

    // VALUE METHODS COMBINING ARGUMENT AND UNKNOWN VALUE
    /** Returns array of values produced by the given indexing set, if possible */
    public Vector<Double> getIndexedValues(String s, Double d) throws FunctionEvaluationException {
        double dMin = getMinNode().getValue(s, d);
        double dMax = getMaxNode().getValue(s, d);
        double dStep = getStepNode().getValue(s, d);
        if (dMin > dMax) {
            double temp = dMin;
            dMin = dMax;
            dMax = temp;
        }
        Vector<Double> result = new Vector<Double>();
        for (double i = dMin; i <= dMax; i += dStep) {
            result.add(i);
        }
        return result;
    }

    public List<Double> getIndexedValues(Map<String, Double> table) throws FunctionEvaluationException {
        double dMin = getMinNode().getValue(table);
        double dMax = getMaxNode().getValue(table);
        double dStep = getStepNode().getValue(table);
        if (dMin > dMax) {
            double temp = dMin;
            dMin = dMax;
            dMax = temp;
        }
        Vector<Double> result = new Vector<Double>();
        for (double i = dMin; i <= dMax; i += dStep) {
            result.add(i);
        }
        return result;
    }

    @Override
    public List<Double> getValue(String s, List<Double> d) throws FunctionEvaluationException {
        Vector<Double> result = new Vector<Double>(d.size());
        for (Double x : d) {
            result.add(getValue(s, x));
        }
        return result;
    }

    // STATIC METHODS
    /** Static method generating a summation given a vector of functiontreenodes representing min/max/step. Throws exception
     * if contains an improper number of arguments.
     * @param arguments
     * @return indexed sum of arguments
     * @throws parser.FunctionSyntaxException
     * @throws java.lang.ArrayIndexOutOfBoundsException
     */
    public static Sum getSum(List<FunctionTreeNode> arguments) throws FunctionSyntaxException, ArrayIndexOutOfBoundsException {
        if (!(arguments.get(1) instanceof Variable)) {
            throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_TYPE);
        }
        if (arguments.size() == 4) {
            return new Series.Sum(arguments.get(0), ((Variable) arguments.get(1)).toString(), arguments.get(2), arguments.get(3));
        } else if (arguments.size() == 5) {
            return new Series.Sum(arguments.get(0), ((Variable) arguments.get(1)).toString(), arguments.get(2), arguments.get(3), arguments.get(4));
        }
        throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_NUMBER);
    }

    /** Static method generating a product given a vector of functiontreenodes representing min/max/step. Throws exception
     * if contains an improper number of arguments.
     * @param arguments
     * @return indexed product of arguments
     * @throws parser.FunctionSyntaxException
     * @throws java.lang.ArrayIndexOutOfBoundsException
     */
    public static Prod getProd(List<FunctionTreeNode> arguments) throws FunctionSyntaxException, ArrayIndexOutOfBoundsException {
        if (!(arguments.get(1) instanceof Variable)) {
            throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_TYPE);
        }
        if (arguments.size() == 4) {
            return new Series.Prod(arguments.get(0), ((Variable) arguments.get(1)).toString(), arguments.get(2), arguments.get(3));
        } else if (arguments.size() == 5) {
            return new Series.Prod(arguments.get(0), ((Variable) arguments.get(1)).toString(), arguments.get(2), arguments.get(3), arguments.get(4));
        }
        throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_NUMBER);
    }

    /** Represents an indexed sum of elements (finite) */
    public static class Sum extends Series {

        public Sum(FunctionTreeNode ftn, String var, FunctionTreeNode min, FunctionTreeNode max, FunctionTreeNode step) {
            super(ftn, var, min, max, step);
            nodeName = "sum";
        }

        public Sum(FunctionTreeNode ftn, String var, FunctionTreeNode min, FunctionTreeNode max) {
            super(ftn, var, min, max);
            nodeName = "sum";
        }

        public Sum(FunctionTreeNode ftn, String var, double min, double max, double step) {
            super(ftn, var, min, max, step);
            nodeName = "sum";
        }

        public Sum(FunctionTreeNode ftn, String var, double min, double max) {
            super(ftn, var, min, max);
            nodeName = "sum";
        }

        @Override
        public FunctionTreeNode derivativeTree(Variable v) {
            return new Sum(argumentDerivative(v), getIndex(), getMinNode(), getMaxNode(), getStepNode());
        }

        // VALUE METHODS
        @Override
        public Double getValue(String s, Double d) throws FunctionEvaluationException {
            TreeMap<String, Double> table = new TreeMap<String, Double>();
            table.put(s, d);
            double result = 0;
            for (Double x : getIndexedValues(s, d)) {
                table.put(getIndex(), x);
                result += getArgument().getValue(table);
            }
            return result;
        }

        @Override
        public Double getValue(Map<String, Double> table) throws FunctionEvaluationException {
            double result = 0;
            for (Double x : getIndexedValues(table)) {
                table.put(getIndex(), x);
                result += getArgument().getValue(table);
            }
            table.remove(getIndex());
            return result;
        }
    } // class Series.Add

    /** Represents an indexed sum of elements (finite) */
    public static class Prod extends Series {

        public Prod(FunctionTreeNode ftn, String var, FunctionTreeNode min, FunctionTreeNode max, FunctionTreeNode step) {
            super(ftn, var, min, max, step);
            nodeName = "prod";
        }

        public Prod(FunctionTreeNode ftn, String var, FunctionTreeNode min, FunctionTreeNode max) {
            super(ftn, var, min, max);
            nodeName = "prod";
        }

        public Prod(FunctionTreeNode ftn, String var, double min, double max, double step) {
            super(ftn, var, min, max, step);
            nodeName = "prod";
        }

        public Prod(FunctionTreeNode ftn, String var, double min, double max) {
            super(ftn, var, min, max);
            nodeName = "prod";
        }

        @Override
        public FunctionTreeNode derivativeTree(Variable v) {
            // TODO make this work
            return null;
        }

        // VALUE METHODS
        @Override
        public Double getValue(String s, Double d) throws FunctionEvaluationException {
            TreeMap<String, Double> table = new TreeMap<String, Double>();
            table.put(s, d);
            double result = 1;
            for (Double x : getIndexedValues(s, d)) {
                table.put(getIndex(), x);
                result *= getArgument().getValue(table);
            }
            return result;
        }

        @Override
        public Double getValue(Map<String, Double> table) throws FunctionEvaluationException {
            double result = 1;
            for (Double x : getIndexedValues(table)) {
                table.put(getIndex(), x);
                result *= getArgument().getValue(table);
            }
            table.remove(getIndex());
            return result;
        }
    } // class Series.Add
}
