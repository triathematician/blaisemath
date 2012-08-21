/*
 * Variable.java
 * Created on Sep 20, 2007, 10:21:02 AM
 */
package scribo.tree;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.apache.commons.math.FunctionEvaluationException;

/**
 * A variable in a FunctionTree. Really a placeholder for a double value.<br><br>
 * 
 * @author Elisha
 */
public class Variable extends FunctionTreeLeaf implements Comparable {

    public Variable() {
        nodeName = "x";
    }

    public Variable(String s) {
        nodeName = s;
    }

    public boolean equals(Variable v) {
        return nodeName.equals(v.nodeName);
    }

    public boolean equals(String s) {
        return nodeName.equals(s);
    }

    public int compareTo(Object o) {
        return nodeName.compareTo(o.toString());
    }

    @Override
    public String toString() {
        return nodeName;
    }

    @Override
    public FunctionTreeNode derivativeTree(Variable v) {
        return v.equals(this)
                ? Constant.ONE
                : Constant.ZERO;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isValidSubNode() {
        return numSubNodes() == 0 && nodeName.length() != 0;
    }

    @Override
    public TreeSet<String> getUnknowns() {
        TreeSet<String> result = new TreeSet<String>();
        result.add(nodeName);
        return result;
    }

    // VALUE METHODS
    @Override
    public Double getValue(String v, Double value) throws FunctionEvaluationException {
        if (equals(v)) {
            return value;
        }
        throw new FunctionEvaluationException(0.0, "Too Few Inputs", new Object[]{});
    }

    @Override
    public Double getValue(Map<String, Double> table) throws FunctionEvaluationException {
        for (String v : table.keySet()) {
            if (equals(v)) {
                return table.get(v);
            }
        }
        throw new FunctionEvaluationException(0.0, "Too Few Inputs", new Object[]{});
    }

    @Override
    public List<Double> getValue(String v, List<Double> values) throws FunctionEvaluationException {
        if (equals(v)) {
            return values;
        }
        throw new FunctionEvaluationException(0.0, "Too Few Inputs", new Object[]{});
    }
}
