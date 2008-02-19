/*
 * FunctionTreeRoot.java
 * Created on Sep 21, 2007, 2:58:57 PM
 */

// TODO Add equality root node.
// TODO Add list of variables at FunctionRoot/setting variable values at the top
// TODO Change to generic Function<Double,Double> method...
// TODO Remove dependence on FunctionTreeFunctionNode
// TODO Change Variable collection to a set

package scribo.tree;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;
import scio.function.DoubleFunction;
import scribo.parser.FunctionSyntaxException;
import scribo.parser.Parser;

/**
 * <p>
 * This class represents the root of a FunctionTree. In particular, every tree which is constructed passes all
 * information through a root node, which is this one.
 * </p>
 * <p>
 * The class stores a list of variables (whose values are "unknowns") and parameters (whose values are "knowns" although
 * they can be changed). The distinction is important because fundamentally the function f(x)=a*sin(b(x+c))+d should be
 * plotted in two-dimensions, although nominally there are five unknowns on the righthand side. Other classes which use
 * this one should be able to "observe" this fact and know what to do with the parameters and the variables automatically.
 * This, the part that is important for any class which utilizes this one is (i) how to get at and adjust parameters, (ii) how
 * to get at the variables, and (iii) how to evaluate the function at a particular value or range of values.
 * </p>
 * <p>
 * </p>
 * @author Elisha Peterson
 */
public class FunctionTreeRoot extends FunctionTreeFunctionNode implements DoubleFunction{
    
    
    // VARIABLES
    
    /** Variables required to obtain a value. */
    Vector<Variable> variables;
    /** Parameters associated with the tree.. along with the variables. If not passed directly to "getValue",
     * the values will be looked up in this table.
     */
    TreeMap<Variable,Double> unknowns;
    
    
    // CONSTRUCTORS
    
    public FunctionTreeRoot(String s) throws FunctionSyntaxException {
        this(Parser.parseExpression(s));
    }
    
    public FunctionTreeRoot(FunctionTreeNode c){
        addSubNode(c);
        variables=c.getVariables();
        unknowns=new TreeMap<Variable,Double>();
    }    
    
    
    // HANDLING UNKNOWN PARAMETERS

    /** Sets up an entire list of parameters. */
    public void setUnknowns(TreeMap<Variable,Double> values){
        unknowns.putAll(values);
        variables.removeAll(values.keySet());
    }
    
    
    // OVERRIDE SUBMETHODS FROM FUNCTIONTREEFUNCTIONNODE
    
    @Override
    public Double getValue(TreeMap<Variable,Double> table){
        if(numSubNodes()!=1){return null;}
        table.putAll(unknowns);
        return argumentValue(table);
    }
    @Override
    public String toString(){
        return "Root "
                + (unknowns==null?"":(" , "+unknowns.toString()));
    }
    @Override
    public FunctionTreeNode derivativeTree(Variable v){
        return numSubNodes()==1?argumentDerivative(v):null;
    }
    @Override
    public boolean isValidSubNode(){return false;}    
    @Override
    public FunctionTreeNode simplified(){
        return isNumber()?new Constant(getValue()).simplified():new FunctionTreeRoot(argumentSimplified());
    }
    public Class inverseFunctionClass(){return null;}
    public void initFunctionType(){}

    
    // FUNCTION INTERFACE METHODS
    
    public int getNumInputs() {
        if(variables==null){return 0;}
        return variables.size();
    }

    public Double getValue(Vector<Double> xs) {
        if(xs.size()<getNumInputs()){return null;}
        for(int i=0;i<xs.size();i++){
            unknowns.put(variables.get(i),xs.get(i));
        }
        return getValue(unknowns);
    }

    public Double getValue(Double x) {
        return getValue(variables.firstElement(),x);
    }

    public Double minValue() { return 0.0; }

    public Double maxValue() { return 0.0; }
}
