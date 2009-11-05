/*
 * ArgumentList.java
 * Created on Oct 27, 2007, 12:11:10 PM
 */
package scribo.tree;

import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.math.FunctionEvaluationException;

/**
 * This is a somewhat abstract version of a FunctionTreeNode, which really just stores
 * a bunch of FunctionTreeNode's for later use. Intended to pass to a function requiring
 * multiple inputs.
 * 
 * @author Elisha Peterson
 */
public class ArgumentList extends FunctionTreeNode {

    public ArgumentList() {
        super();
    }

    public ArgumentList(Vector<FunctionTreeNode> arguments) {
        super();
        addSubNodes(arguments);
    }

    public Vector<FunctionTreeNode> arguments() {
        return children;
    }

    @Override
    public boolean isValidSubNode() {
        return false;
    }

    @Override
    public FunctionTreeNode derivativeTree(Variable v) {
        return null;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public Double getValue(Map<String, Double> table) throws FunctionEvaluationException {
        throw new FunctionEvaluationException(0.0);
    }

    @Override
    public Double getValue(String s, Double d) throws FunctionEvaluationException {
        throw new FunctionEvaluationException(d);
    }

    @Override
    public Vector<Double> getValue(String s, List<Double> d) throws FunctionEvaluationException {
        throw new FunctionEvaluationException(0.0);
    }
}
