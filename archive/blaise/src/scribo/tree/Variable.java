/*
 * Variable.java
 * Created on Sep 20, 2007, 10:21:02 AM
 */

// TODO Think about implementing static variables for "x", "t", etc.

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
    public Variable(){nodeName="x";}
    public Variable(String s){nodeName=s;}
    
    public boolean equals(Variable v){return nodeName.equals(v.nodeName);}
    public boolean equals(String s){return nodeName.equals(s);}
    
    @Override
    public String toString() {return nodeName;}
    @Override
    public FunctionTreeNode derivativeTree(Variable v){return v.equals(this)?Constant.ONE:Constant.ZERO;}
    @Override
    public boolean isNumber(){return false;}
    @Override
    public boolean isValidSubNode(){return numSubNodes()==0&&!nodeName.isEmpty();}
    @Override
    public TreeSet<String> getUnknowns(){
        TreeSet<String> result=new TreeSet<String>();
        result.add(toString());
        return result;
    }
    
    // VALUE METHODS
    
    @Override
    public Double getValue(String v, Double value) throws FunctionValueException {
        if(equals(v)){return value;}
        throw new FunctionValueException();
    }
    @Override
    public Double getValue(TreeMap<String,Double> table) throws FunctionValueException {
        for(String v:table.keySet()){if(equals(v)){return table.get(v);}}
        throw new FunctionValueException();
    }
    @Override
    public Vector<Double> getValue(String v, Vector<Double> values) throws FunctionValueException {
        if(equals(v)){return values;}
        throw new FunctionValueException();
    }
    
    
    
    /** Special class representing monomials, of the form ax^b. */
    public static class Monomial extends Variable {
        Double coefficient;
        Integer power;
            
        public Monomial(){super();depth=4;coefficient=1.0;power=1;depth=5;}
        public Monomial(double coefficient,String s,int power){super(s);depth=4;this.coefficient=coefficient;this.power=power;depth=5;}

        @Override
        public String toString(){
            if(power==0){return "1";}
            String result="";
            if(coefficient!=1){if(coefficient%1==0){result+=Integer.toString((int)(double)coefficient);}else{result+=coefficient;}}
            result+=nodeName;
            if(power!=1){result+="^"+power;}
            return result;
        }
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return (power==0)?Constant.ZERO:new Monomial(coefficient*power,nodeName,power-1);}
    }

    @Override
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }
}
