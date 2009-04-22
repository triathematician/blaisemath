/*
 * Constant.java
 * Created on Sep 20, 2007, 8:38:07 AM
 */

package scribo.tree;

import java.util.TreeMap;
import java.util.Vector;
import scio.function.FunctionValueException;

/**
 * @author Elisha
 */
public class Constant extends FunctionTreeLeaf {      
    /** Contains value associated with this node. */
    protected Double value;
    
    public Constant(){value=0.0;}
    public Constant(double v){value=v;}
    public Constant(double v,String s){value=v;this.nodeName=s;}
    
    /** Sets value of this leaf. */
    public void setValue(Double d){value=d;}
    
    @Override
    public boolean equals(double d){return value==d;}
    public boolean equals(Constant c){return value==c.value;}
    @Override
    public boolean isNumber(){return true;}
    
    public static String toString(Double d){
        if(d==null){return "";}
        if(d%1==0){return Integer.toString((int)(double)d);}
        return Double.toString(d);
    }
    
    @Override 
    public String toString(){
        if(nodeName!=null){return nodeName;}
        return toString(value);
    }
    
    @Override
    public FunctionTreeNode derivativeTree(Variable v){return ZERO;}
    @Override
    public FunctionTreeNode simplified(){
        if(equals(Math.E)){return E;}
        else if(equals(Math.PI)){return PI;}
        else if(equals(PHI_VALUE)){return PHI;}
        else if(equals(1.0)){return ONE;}
        else if(equals(0.0)){return ZERO;}
        return this;
    }
    
    // OPERATIONS ON THE VALUE
    public void add(Double plus){if(plus!=null){value+=plus;}}
    public void multiplyBy(Double plus){if(plus!=null){value*=plus;}}
    
    

    // METHODS TO RETRIEVE THE VALUE
    
    /** Returns value. */
    @Override
    public Double getValue(){return value;}
    /** Returns value given a table of values */
    /** Returns value given a single input. */
    @Override
    public Double getValue(String s, Double d) throws FunctionValueException {return value;}
    @Override
    public Double getValue(TreeMap<String, Double> table) {return value;}  
    /** Returns values given a list of variable assignments */
    @Override
    public Vector<Double> getValue(String v, Vector<Double> values) {
        if(value==null){return null;}
        Vector<Double> result=new Vector<Double>(values.size());
        for(int i=0;i<values.size();i++){result.add(value);}
        return result;
    }
    
    
    
    // SPECIFIC INSTANCES IN THE FORM OF STATIC INNER CLASSES
    
    public static final double PHI_VALUE=(1+Math.sqrt(5))/2;
    
    public static final Constant E=new Constant(Math.E,"e");
    public static final Constant PI=new Constant(Math.PI,"pi");
    public static final Constant PHI=new Constant(PHI_VALUE,"phi");
    public static final Constant ZERO=new Constant(0.0);
    public static final Constant ONE=new Constant(1.0);

}
