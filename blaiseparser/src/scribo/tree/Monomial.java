package scribo.tree;

/**
 * Special class representing monomials, of the form ax^b.
 */
public class Monomial extends Variable {

    Double coefficient;
    Integer power;

    public Monomial() {
        coefficient = 1.0;
        power = 1;
        depth = 5;
    }

    public Monomial(double coefficient, String s, int power) {
        super(s);
        this.coefficient = coefficient;
        this.power = power;
        depth = 5;
    }

    @Override
    public String toString() {
        if (power == 0) {
            return "1";
        }
        String result = "";
        if (coefficient != 1) {
            if (coefficient % 1 == 0) {
                result += Integer.toString((int) (double) coefficient);
            } else {
                result += coefficient;
            }
        }
        result += nodeName;
        if (power != 1) {
            result += "^" + power;
        }
        return result;
    }

    @Override
    public FunctionTreeNode derivativeTree(Variable v) {
        return (power == 0) ? Constant.ZERO : new Monomial(coefficient * power, nodeName, power - 1);
    }
}
