/*
 * Variable.java
 * Created on Sep 20, 2007, 10:21:02 AM
 */

package scribo.tree;

import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import scio.function.FunctionValueException;

/**
 * A variable in a FunctionTree. Really a placeholder for a double value.<br><br>
 * 
 * @author Elisha
 */
public class Variable extends FunctionTreeLeaf implements Comparable {

    public Variable(){
        nodeName="x";
    }

    public Variable(String s){
        nodeName=s;
    }
    
    public boolean equals(Variable v){
        return nodeName.equals(v.nodeName);
    }

    public boolean equals(String s){
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
    public FunctionTreeNode derivativeTree(Variable v){
        return v.equals(this)
                ? Constant.ONE
                : Constant.ZERO;
    }

    @Override
    public boolean isNumber(){
        return false;
    }

    @Override
    public boolean isValidSubNode(){
        return numSubNodes() == 0
                && nodeName.length() != 0;
    }
    
    @Override
    public TreeSet<String> getUnknowns(){
        TreeSet<String> result=new TreeSet<String>();
        result.add(nodeName);
        return result;
    }
    
    // VALUE METHODS
    
    @Override
    public Double getValue(String v, Double value) throws FunctionValueException {
        if(equals(v)){
            return value;
        }
        throw new FunctionValueException(FunctionValueException.TOO_FEW_INPUTS);
    }

    @Override
    public Double getValue(TreeMap<String,Double> table) throws FunctionValueException {
        for(String v:table.keySet()) {
            if(equals(v)) {
                return table.get(v);
            }
        }
        throw new FunctionValueException(FunctionValueException.TOO_FEW_INPUTS);
    }
    
    @Override
    public Vector<Double> getValue(String v, Vector<Double> values) throws FunctionValueException {
        if(equals(v)){return values;}
        throw new FunctionValueException(FunctionValueException.TOO_FEW_INPUTS);
    }
}
