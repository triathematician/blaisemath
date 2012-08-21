/*
 * Operation.java
 * Created on Sep 30, 2007, 3:28:59 PM
 */

// TODO Add sorting of * and + elements to siimplify
package scribo.tree;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import org.apache.commons.math.FunctionEvaluationException;

/**
 * <p>
 *  This class allows for several subnodes connected by a common operation, such as addition or multiplication.
 * </p>
 * <p>
 *  An addition vector of coefficients is defined to simplify the tree. So for addition, the subnodes are all weighted
 *  by the given coefficients. Constant terms, if present, are the first subnode. This implementation speeds the evaluation
 *  tree somewhat by consolidating computations.
 * </p>
 * 
 * @author Elisha Peterson
 */
public abstract class Operation extends FunctionTreeNode {

    /** Represents array of coefficients corresponding to the array of terms. */
    protected Vector<Double> coefficient;

    /** Initializes with no coefficients and no operation string. */
    public Operation() {
        super();
        initCoefficients();
        initOperationString();
    }

    /** Any number of subnodes is allowed, assuming it is positive. The first subnode
     * should be used to store either the constant summands or the constant coefficient. */
    @Override
    public boolean isValidSubNode() {
        return numSubNodes() > 0;
    }

    /** Initializes other elements for the linear combination. */
    public void initCoefficients() {
        coefficient = new Vector<Double>();
    }

    /** Returns ith coefficient. */
    public double getCoefficient(int i) {
        return coefficient.get(i);
    }

    /** Returns vector of subnode values. */
    public List<? extends List<Double>> getSubNodeValues(String s, List<Double> values) throws FunctionEvaluationException {
        List<List<Double>> result = new Vector<List<Double>>();
        for (int i = 0; i < numSubNodes(); i++) {
            result.add(getSubNode(i).getValue(s, values));
        }
        return result;

    }

    /** Adds several subnodes with the specified coefficient. */
    public FunctionTreeNode addSubNodes(double coeff, Collection<FunctionTreeNode> children) {
        for (FunctionTreeNode ftn : children) {
            addSubNode(coeff, ftn);
        }
        return this;
    }

    /** Adds a node with specified coefficent. */
    public abstract FunctionTreeNode addSubNode(double coeff, FunctionTreeNode child);

    /** Adds a numeric-only subnode, represented by a single double coefficient. */
    public abstract FunctionTreeNode addNumericNode(double coeff);

    /** Abstract implementation of the operation string. */
    public abstract void initOperationString();

    /** Sets the operation string, along with a "depth" that is used for parenthetical nestings. */
    public void setOperationString(String s, int d) {
        nodeName = s;
        depth = d;
    }

    @Override
    public String toString() {
        if (numSubNodes() == 0) {
            return "";
        }
        if (numSubNodes() == 1) {
            return getSubNode(0).toString(this);
        }
        String result = "";
        for (int i = 0; i < numSubNodes() - 1; i++) {
            result += getSubNode(i).toString(this) + nodeName;
        }
        return result + getSubNode(numSubNodes() - 1).toString(this);
    }

    // STATIC METHODS
    /** Returns negative of the node (multiplies by -1) */
    public static FunctionTreeNode negative(FunctionTreeNode f) {
        return new Multiply(-1, f);
    }

    /** Returns an Add class that represents a-b-c type nodes. */
    public static Add subtractNode(Vector<FunctionTreeNode> kids) {
        if (kids.size() == 0) {
            return new Add();
        }
        Add result = new Add(kids.get(0));
        return (Add) result.addSubNodes(-1.0, kids.subList(1, kids.size()));
    }

    /** Returns Add node which subtracts second element from the first. */
    public static Add subtractNode(FunctionTreeNode ftn1, FunctionTreeNode ftn2) {
        Add result = new Add(ftn1);
        return (Add) result.addSubNode(-1.0, ftn2);
    }

    /** Returns a Multiply class representing a/b/c type nodes. */
    public static Multiply divideNode(Vector<FunctionTreeNode> kids) {
        if (kids.size() == 0) {
            return new Multiply();
        }
        Multiply result = new Multiply(kids.get(0));
        return (Multiply) result.addSubNodes(-1.0, kids.subList(1, kids.size()));
    }

    /** Returns Divide node which subtracts second element from the first. */
    public static Multiply divideNode(FunctionTreeNode ftn1, FunctionTreeNode ftn2) {
        Multiply result = new Multiply(ftn1);
        return (Multiply) result.addSubNode(-1.0, ftn2);
    }

    /** Returns Multiply class representing c*a/b. */
    public static Multiply divideNode(double coeff, FunctionTreeNode ftn1, FunctionTreeNode ftn2) {
        Multiply result = new Multiply();
        result.addNumericNode(coeff);
        result.addSubNode(ftn1);
        result.addSubNode(-1.0, ftn2);
        return result;
    }

    // INSTANCES OF OPERATIONS
    /** 
     * Basic addition operation.
     * 
     * Really, the node represents a linear combination of its subnodes, so that each subnode
     * comes equipped with a coefficient. This combines addition/subtraction into a unified class.
     * The first subnode element is used to represent the constant summand, and all constant values are
     * accumulated here, even before simplification.
     * If told to add an "Add" subNode, adds its children. This is permitted by associativity.
     * 
     * @author Elisha Peterson
     */
    public static class Add extends Operation {

        public Add() {
            super();
        }

        public Add(double d) {
            super();
            addNumericNode(d);
        }

        public Add(double d, FunctionTreeNode a) {
            super();
            addSubNode(d, a);
        }

        public Add(FunctionTreeNode a) {
            super();
            addSubNode(a);
        }

        public Add(FunctionTreeNode a1, FunctionTreeNode a2) {
            super();
            addSubNode(a1);
            addSubNode(a2);
        }

        public Add(Collection<FunctionTreeNode> as) {
            super();
            addSubNodes(as);
        }

        /** Initializes notation for the command, as well as the depth. */
        @Override
        public void initOperationString() {
            setOperationString("+", 5);
        }

        /** Override the standard addSubNode to treat constants in a special way. */
        @Override
        public FunctionTreeNode addSubNode(FunctionTreeNode child) {
            return addSubNode(1.0, child);
        }

        /** Adds a subnode with the specified coefficient. */
        @Override
        public FunctionTreeNode addSubNode(double coeff, FunctionTreeNode child) {
            if (coeff == 0 || child == null) {
                return this;
            }
            if (child instanceof Add) {
                for (int i = 0; i < child.numSubNodes(); i++) {
                    addSubNode(coeff * ((Add) child).getCoefficient(i), child.getSubNode(i));
                }
            } else if (child.isValidSubNode()) {
                try {
                    addNumericNode(coeff * child.getValue());
                } catch (FunctionEvaluationException ex) {
                    super.addSubNode(child);
                    coefficient.add(coeff);
                }
            }
            return this;
        }

        /** Adds a numeric node, if none present. Otherwise, adds value to the existing numeric node. */
        @Override
        public FunctionTreeNode addNumericNode(double coeff) {
            if (coeff == 0) {
                return this;
            }
            if (numSubNodes() == 0) {
                super.addSubNode(new Constant(coeff));
                coefficient.add(1.0);
            } else if (getSubNode(0) instanceof Constant) {
                ((Constant) getSubNode(0)).add(coeff);
            } else {
                children.insertElementAt(new Constant(coeff), 0);
                coefficient.insertElementAt(1.0, 0);
            }
            return this;
        }

        /** Computes the derivative tree corresponding to this function. */
        @Override
        public FunctionTreeNode derivativeTree(Variable v) {
            //may want to add this: if(isNumber()){return Constant.ZERO;}
            Add result = new Add();
            for (int i = 0; i < numSubNodes(); i++) {
                result.addSubNode(coefficient.get(i), getSubNode(i).derivativeTree(v));
            }
            if (result.numSubNodes() == 0) {
                return Constant.ZERO;
            }
            return result;
        }

        /** 
         * Simplify summands. 
         * Combines all constants into a single element.
         * Combines like summands into a single summand.
         * The basic algorithm is to look at the basic type of each summand and search for
         * other summands with that same basic type, unless they have already been added.
         */
        @Override
        public FunctionTreeNode simplified() {
            // First check to see if result is numeric.
            try {
                return new Constant(getValue()).simplified();
            } catch (FunctionEvaluationException e) {
            }
            Vector<Boolean> added = new Vector<Boolean>();
            for (int i = 0; i < children.size(); i++) {
                added.add(false);
            }

            double constantSummand = 0;
            double coeff = 0;
            Add result = new Add();
            for (int i = 0; i < numSubNodes(); i++) {
                if (added.get(i)) {
                    continue;
                }
                added.set(i, true);
                try {
                    constantSummand += coefficient.get(i) * getSubNode(i).getValue();
                } catch (FunctionEvaluationException e) {
                    coeff = coefficient.get(i);
                    for (int j = i + 1; j < numSubNodes(); j++) { // check for a repeated node
                        if (added.get(j)) {
                            continue;
                        }
                        if (getSubNode(i).simplified().equals(getSubNode(j).simplified())) {
                            added.set(j, true);
                            coeff += coefficient.get(j);
                        }
                    }
                    result.addSubNode(coeff, getSubNode(i).simplified());
                }
            }
            result.addNumericNode(constantSummand);
            if (result.numSubNodes() == 0) {
                return Constant.ZERO;
            }
            if (result.numSubNodes() == 1 && result.coefficient.get(0) == 1) {
                return result.getSubNode(0);
            }
            return result;
        } // Add.simplified

        /** Returns string representation of ith term, including the sign term. */
        public String toString(int i) {
            if (i >= coefficient.size()) {
                return "";
            }
            if (coefficient.get(i) == 1) {
                return getSubNode(i).toString(this);
            }
            if (coefficient.get(i) == -1) {
                return "-" + getSubNode(i).toString(this);
            }
            return Constant.toString(coefficient.get(i)) + getSubNode(i).toString(this);
        } // Add.toString

        /** Overrides string-conversion method. */
        @Override
        public String toString() {
            if (numSubNodes() == 0) {
                return "";
            }
            if (numSubNodes() == 1) {
                return toString(0);
            }
            String result = toString(0);
            String plus;
            for (int i = 1; i < numSubNodes(); i++) {
                plus = toString(i);
                if (plus.startsWith("-")) {
                    result += plus;
                } else {
                    result += "+" + plus;
                }
            }
            return result;
        } // Add.toString

        // METHODS TO RETURN VALUE
        @Override
        public Double getValue(String s, Double d) throws FunctionEvaluationException {
            Double result = 0.0;
            for (int i = 0; i < numSubNodes(); i++) {
                result += coefficient.get(i) * getSubNode(i).getValue(s, d);
            }
            return result;
        }

        @Override
        public Double getValue(Map<String, Double> table) throws FunctionEvaluationException {
            Double result = 0.0;
            for (int i = 0; i < numSubNodes(); i++) {
                result += coefficient.get(i) * getSubNode(i).getValue(table);
            }
            return result;
        }

        @Override
        public List<Double> getValue(String s, List<Double> d) throws FunctionEvaluationException {
            List<? extends List<Double>> subValues = getSubNodeValues(s, d);
            Vector<Double> result = new Vector<Double>(d.size());
            double curValue;
            for (int j = 0; j < d.size(); j++) {
                curValue = 0.0;
                for (int i = 0; i < numSubNodes(); i++) {
                    curValue += coefficient.get(i) * subValues.get(i).get(j);
                }
                result.add(curValue);
            }
            return result;
        }
    } // class Operation.Add

    /**
     * This class represents multiplication of an arbitrary number of nodes. Each node has a
     * coefficient that represents its power in the product.
     */
    public static class Multiply extends Operation {

        public Multiply() {
            super();
        }

        public Multiply(double d) {
            super();
            addNumericNode(d);
        }

        public Multiply(FunctionTreeNode a) {
            super();
            addSubNode(a);
        }

        public Multiply(double c, FunctionTreeNode a1) {
            super();
            addNumericNode(c);
            addSubNode(a1);
        }

        public Multiply(FunctionTreeNode a1, FunctionTreeNode a2) {
            super();
            addSubNode(a1);
            addSubNode(a2);
        }

        public Multiply(double c, FunctionTreeNode a1, FunctionTreeNode a2) {
            super();
            addNumericNode(c);
            addSubNode(a1);
            addSubNode(a2);
        }

        public Multiply(FunctionTreeNode a1, FunctionTreeNode a2, FunctionTreeNode a3) {
            super();
            addSubNode(a1);
            addSubNode(a2);
            addSubNode(a3);
        }

        public Multiply(double c, FunctionTreeNode a1, FunctionTreeNode a2, FunctionTreeNode a3) {
            super();
            addNumericNode(c);
            addSubNode(a1);
            addSubNode(a2);
            addSubNode(a3);
        }

        public Multiply(Collection<FunctionTreeNode> as) {
            super();
            addSubNodes(as);
        }

        /** Initializes notation for the command, as well as the depth. */
        public void initOperationString() {
            setOperationString("*", 3);
        }

        /** Override the standard addSubNode to treat constants in a special way. */
        @Override
        public FunctionTreeNode addSubNode(FunctionTreeNode child) {
            return addSubNode(1.0, child);
        }

        /** Adds a subnode with the specified coefficient. */
        @Override
        public FunctionTreeNode addSubNode(double coeff, FunctionTreeNode child) {
            if (coeff == 0 || child == null) {
                return this;
            }
            if (child instanceof Multiply) {
                for (int i = 0; i < child.numSubNodes(); i++) {
                    addSubNode(coeff * ((Multiply) child).getCoefficient(i), child.getSubNode(i));
                }
            } else if (child.isValidSubNode()) {
                try {
                    addNumericNode(Math.pow(child.getValue(), coeff));
                } catch (FunctionEvaluationException e) {
                    super.addSubNode(child);
                    coefficient.add(coeff);
                }
            }
            return this;
        }

        /** Adds a numeric node, if none present. Otherwise, adds value to the existing numeric node. */
        @Override
        public FunctionTreeNode addNumericNode(double coeff) {
            if (coeff == 0) {
                coefficient.clear();
                children.clear();
            }
            if (coeff == 1) {
                return this;
            }
            if (numSubNodes() == 0) {
                super.addSubNode(new Constant(coeff));
                coefficient.add(1.0);
            } else if (getSubNode(0) instanceof Constant) {
                ((Constant) getSubNode(0)).multiplyBy(coeff);
            } else {
                children.insertElementAt(new Constant(coeff), 0);
                coefficient.insertElementAt(1.0, 0);
            }
            return this;
        }

        /** Computes the derivative tree corresponding to this function. */
        @Override
        public FunctionTreeNode derivativeTree(Variable v) {
            //may want to add this: if(isNumber()){return Constant.ZERO;}
            Vector<FunctionTreeNode> summands = new Vector<FunctionTreeNode>();
            Multiply summand;
            double power;
            for (int i = 0; i < numSubNodes(); i++) {
                if (getSubNode(i).isNumber()) {
                    continue;
                }
                summand = new Multiply();
                for (int j = 0; j < numSubNodes(); j++) {
                    power = coefficient.get(j);
                    if (i == j) {
                        if (power != 1) {
                            summand.addNumericNode(power);
                            summand.addSubNode(power - 1, getSubNode(i));
                        }
                        summand.addSubNode(getSubNode(i).derivativeTree(v));
                    } else {
                        summand.addSubNode(power, getSubNode(j));
                    }
                }
                summands.add(summand);
            }
            if (summands.size() == 0) {
                return Constant.ZERO;
            }
            if (summands.size() == 1) {
                return summands.get(0);
            }
            return new Add(summands);
        }

        /** 
         * Simplify elements. 
         * Combines all constants into a single element.
         * Combines like product elements into a single element.
         * The basic algorithm is to look at the basic type of each element and search for
         * other elements with that same basic type, unless they have already been multiplied.
         */
        @Override
        public FunctionTreeNode simplified() {
            // First check to see if result is numeric.
            try {
                return new Constant(getValue()).simplified();
            } catch (FunctionEvaluationException e) {
            }
            Vector<Boolean> multiplied = new Vector<Boolean>();
            for (int i = 0; i < children.size(); i++) {
                multiplied.add(false);
            }
            double constantElement = 1;
            double coeff = 0;
            Multiply result = new Multiply();
            for (int i = 0; i < numSubNodes(); i++) {
                if (multiplied.get(i)) {
                    continue;
                }
                multiplied.set(i, true);
                if (coefficient.get(i) == 0) {
                    continue;
                }
                try {
                    constantElement *= Math.pow(getSubNode(i).getValue(), coefficient.get(i));
                } catch (FunctionEvaluationException ex) {
                    coeff = coefficient.get(i);
                    for (int j = i + 1; j < numSubNodes(); j++) {
                        if (multiplied.get(j)) {
                            continue;
                        }
                        if (getSubNode(i).simplified().equals(getSubNode(j).simplified())) {
                            multiplied.set(j, true);
                            coeff += coefficient.get(j);
                        }
                    }
                    result.addSubNode(coeff, getSubNode(i).simplified());
                }
            }
            result.addNumericNode(constantElement);
            if (result.numSubNodes() == 0) {
                return Constant.ONE;
            }
            try {
                return result.addVersion();
            } catch (FunctionEvaluationException e) {
                return result;
            }
        } // Multiply.simplified()

        /** Returns consolidated version of the Multiply. Incorporates all subnodes which are
         * Multiply or Power into this node. */
        public Multiply consolidated() {
            return null;
        }

        /** Returns Add version of the Multiply if possible to convert it to that. */
        public FunctionTreeNode addVersion() throws FunctionEvaluationException {
            switch (numSubNodes()) {
                case 1:
                    if (coefficient.get(0) == 1) {
                        return getSubNode(0);
                    } else {
                        return new Power(getSubNode(0), coefficient.get(0));
                    }
                case 2:
                    if (getSubNode(0).isNumber() && getSubNode(1).isNumber()) {
                        return new Constant(getValue(0) * getValue(1));
                    } else if (getSubNode(0).isNumber()) {
                        if (coefficient.get(1) == 1) {
                            return new Add(getValue(0), getSubNode(1));
                        }
                    } else if (getSubNode(1).isNumber()) {
                        if (coefficient.get(0) == 1) {
                            return new Add(getValue(1), getSubNode(0));
                        }
                    }
                    break;
            }
            return this;
        }

        /** Returns string representation of ith term, including the sign term. */
        public String toString(int i) {
            if (i >= coefficient.size()) {
                return "";
            }
            if (coefficient.get(i) == 1) {
                return getSubNode(i).toString(this);
            }
            if (coefficient.get(i) == -1) {
                return "/" + getSubNode(i).toString(this);
            }
            return getSubNode(i).toString(this) + "^" + Constant.toString(coefficient.get(i));
        }

        /** Overrides string-conversion method. */
        @Override
        public String toString() {
            if (numSubNodes() == 0) {
                return "";
            }
            String result = toString(0);
            if (result.startsWith("/")) {
                result = "1" + result;
            }
            String times;
            for (int i = 1; i < numSubNodes(); i++) {
                times = toString(i);
                if (times.startsWith("/")) {
                    result += times;
                } else {
                    result += "*" + times;
                }
            }
            if (result.startsWith("-1*")) {
                return "-" + result.substring(3);
            }
            return result;
        }

        // VALUE METHODS
        public Double getValue(int i) throws FunctionEvaluationException {
            return Math.pow(getSubNode(i).getValue(), coefficient.get(i));
        }

        @Override
        public Double getValue(String s, Double d) throws FunctionEvaluationException {
            Double result = 1.0;
            for (int i = 0; i < numSubNodes(); i++) {
                result *= Math.pow(getSubNode(i).getValue(s, d), coefficient.get(i));
            }
            return result;
        }

        public Double getValue(Map<String, Double> table) throws FunctionEvaluationException {
            Double result = 1.0;
            for (int i = 0; i < numSubNodes(); i++) {
                result *= Math.pow(getSubNode(i).getValue(table), coefficient.get(i));
            }
            return result;
        }

        @Override
        public List<Double> getValue(String s, List<Double> d) throws FunctionEvaluationException {
            List<? extends List<Double>> subValues = getSubNodeValues(s, d);
            Vector<Double> result = new Vector<Double>(d.size());
            double curValue;
            for (int j = 0; j < d.size(); j++) {
                curValue = 1.0;
                for (int i = 0; i < numSubNodes(); i++) {
                    curValue *= Math.pow(subValues.get(i).get(j), coefficient.get(i));
                }
                result.add(curValue);
            }
            return result;
        }
    } // class Operation.Multiply

    /** Power operation. Requires at least two nodes. If there are more than two, the elements
     * are paired from the right first rather than the left, e.g. 2^3^4 = 2^(3^4).
     */
    public static class Power extends Operation {

        public Power() {
            setSubNodes(Constant.ONE, Constant.ONE);
        }

        public Power(FunctionTreeNode base, double power) {
            setSubNodes(base, new Constant(power));
        }

        public Power(FunctionTreeNode base, FunctionTreeNode power) {
            setSubNodes(base, power);
        }

        public Power(List<FunctionTreeNode> as) {
            addSubNodes(as);
        }

        @Override
        public boolean isValidSubNode() {
            return numSubNodes() >= 2;
        }

        @Override
        public void initOperationString() {
            setOperationString("^", 1);
        }

        /** Sets sub nodes with first as the base, and second as the power. */
        public void setSubNodes(FunctionTreeNode base, FunctionTreeNode power) {
            addSubNode(base);
            addSubNode(power);
        }

        /** Returns the base of the power, which is by default the first node. */
        public FunctionTreeNode basePart() {
            return getSubNode(0);
        }

        /** Returns the power portion, which is everything past the first node. */
        public FunctionTreeNode powerPart() {
            switch (numSubNodes()) {
                case 0:
                    return Constant.ONE;
                case 1:
                    return Constant.ONE;
                case 2:
                    return getSubNode(1);
                default:
                    return new Power(children.subList(1, numSubNodes()));
            }
        }

        /** Returns derivative tree for the power. */
        @Override
        public FunctionTreeNode derivativeTree(Variable v) {
            if (powerPart().isNumber()) {
                try {
                    return new Operation.Multiply(powerPart(), new Power(basePart(), powerPart().getValue() - 1), basePart().derivativeTree(v));
                } catch (FunctionEvaluationException ex) {
                }
            } else if (basePart().equals(Constant.E)) {
                return new Operation.Multiply(powerPart().derivativeTree(v), this);
            } else if (basePart().isNumber()) {
                try {
                    return new Operation.Multiply(Math.log(basePart().getValue()), this, powerPart().derivativeTree(v));
                } catch (FunctionEvaluationException ex) {
                }
            } else {
                return new Operation.Add(
                        new Operation.Multiply(powerPart(), new Power(basePart(), subtractNode(powerPart(), Constant.ONE)), basePart().derivativeTree(v)),
                        new Operation.Multiply(this, new Exponential.Log(basePart()), powerPart().derivativeTree(v)));
            }
            return null;
        }

        @Override
        public FunctionTreeNode simplified() {
            try {
                return new Constant(getValue()).simplified();
            } catch (FunctionEvaluationException e) {
            }
            if (powerPart().isNumber()) {
                try {
                    if (powerPart().equals(0)) {
                        return Constant.ONE;
                    } else if (powerPart().equals(1)) {
                        return basePart();
                    }
                    Multiply result = new Multiply();
                    result.addSubNode(powerPart().getValue(), basePart());
                    return result;
                } catch (FunctionEvaluationException ex) {
                }
            }
            if (basePart().equals(1)) {
                return Constant.ONE;
            }
            if (basePart().equals(0)) {
                return Constant.ZERO;
            }
            return this;
        }

        @Override
        public FunctionTreeNode addSubNode(double coeff, FunctionTreeNode child) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public FunctionTreeNode addNumericNode(double coeff) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        // VALUE METHODS
        @Override
        public Double getValue(String s, Double d) throws FunctionEvaluationException {
            return Math.pow(basePart().getValue(s, d), powerPart().getValue(s, d));
        }

        @Override
        public Double getValue(Map<String, Double> table) throws FunctionEvaluationException {
            return Math.pow(basePart().getValue(table), powerPart().getValue(table));
        }

        /** Returns list of values given a particular variable assignment. */
        @Override
        public List<Double> getValue(String s, List<Double> d) throws FunctionEvaluationException {
            List<Double> baseValues = basePart().getValue(s, d);
            List<Double> powerValues = powerPart().getValue(s, d);
            Vector<Double> result = new Vector<Double>(d.size());
            for (int j = 0; j < d.size(); j++) {
                result.add(Math.pow(baseValues.get(j), powerValues.get(j)));
            }
            return result;
        }
    } // POWER SUB-CLASS
} // OPERATION CLASS
