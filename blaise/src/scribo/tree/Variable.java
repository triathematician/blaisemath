/*
 * Variable.java
 * Created on Sep 20, 2007, 10:21:02 AM
 */

// TODO Think about implementing static variables for "x", "t", etc.

package scribo.tree;

import java.util.TreeMap;
import java.util.Vector;

/**
 * A variable in a FunctionTree. Really a placeholder for a double value.<br><br>
 * 
 * @author Elisha
 */
public class Variable extends FunctionTreeLeaf implements Comparable {
    Double value;
    String string;
    
    public Variable(){value=null;string="x";}
    public Variable(String s){string=s;}
    
    public boolean equals(Variable v){return string.equals(v.string);}
    public boolean equals(String s){return string.equals(s);}

    public void setValue(Double newValue){value=newValue;}
    @Override
    public Double getValue(){return value;}
    public Double getValue(TreeMap<Variable,Double> table) {
        //for(Variable v:table.keySet()){System.out.println("variable called with "+v.toString()+" taken to "+table.get(v).toString());}
        for(Variable v:table.keySet()){if(v.equals(this)){return table.get(v);}}
        return null;
    }
    public String toString() {return string;}
    public FunctionTreeNode derivativeTree(Variable v){return v.equals(this)?Constant.ONE:Constant.ZERO;}
    @Override
    public boolean isNumber(){return false;}
    @Override
    public boolean isValidSubNode(){return numSubNodes()==0&&!string.isEmpty();}
    @Override
    public Vector<Variable> getVariables(){Vector<Variable> result=new Vector<Variable>();result.add(this);return result;}
    
    
    /** Special class representing monomials, of the form ax^b. */
    public static class Monomial extends Variable {
        Double coefficient;
        Integer power;
            
        public Monomial(){super();depth=4;coefficient=1.0;power=1;depth=5;}
        public Monomial(double coefficient,String s,int power){super(s);depth=4;this.coefficient=coefficient;this.power=power;depth=5;}

        @Override
        public Double getValue(){return coefficient*Math.pow(value,power);}
        @Override
        public String toString(){
            if(power==0){return "1";}
            String result="";
            if(coefficient!=1){if(coefficient%1==0){result+=Integer.toString((int)(double)coefficient);}else{result+=coefficient;}}
            result+=string;
            if(power!=1){result+="^"+power;}
            return result;
        }
        @Override
        public FunctionTreeNode derivativeTree(Variable v){return (power==0)?Constant.ZERO:new Monomial(coefficient*power,string,power-1);}
    }

    @Override
    public int compareTo(Object o) {
        return toString().compareTo(o.toString());
    }
}
