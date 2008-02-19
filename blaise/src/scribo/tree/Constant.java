/*
 * Constant.java
 * Created on Sep 20, 2007, 8:38:07 AM
 */

package scribo.tree;

import java.util.TreeMap;

/**
 * @author Elisha
 */
public class Constant extends FunctionTreeLeaf {    
    protected double value;
    private String s=null;
    
    public Constant(){value=0;}
    public Constant(double v){value=v;}
    public Constant(double v,String s){value=v;this.s=s;}
    
    @Override
    public Double getValue(){return value;}
    public Double getValue(TreeMap<Variable,Double> table) {return value;}
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
    
    public String toString(){
        if(s!=null){return s;}
        return toString(value);
    }
    
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
    
    // SPECIFIC INSTANCES IN THE FORM OF STATIC INNER CLASSES
    
    public static final double PHI_VALUE=(1+Math.sqrt(5))/2;
    
    public static final Constant E=new Constant(Math.E,"e");
    public static final Constant PI=new Constant(Math.PI,"pi");
    public static final Constant PHI=new Constant(PHI_VALUE,"phi");
    public static final Constant ZERO=new Constant(0.0);
    public static final Constant ONE=new Constant(1.0);
}
