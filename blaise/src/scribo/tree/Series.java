/*
 * Series.java
 * Created on Oct 8, 2007, 3:34:28 PM
 */

package scribo.tree;

import java.util.TreeMap;
import java.util.Vector;
import scribo.parser.FunctionSyntaxException;

/**
 * Implements an indexed series of nodes, which are either summed or multiplied.<br><br>
 * The min/max/step values may be variables themselves.<br><br>
 * 
 * @author Elisha
 */
public abstract class Series extends FunctionTreeFunctionNode {
    Variable index;
    FunctionTreeNode min;
    FunctionTreeNode max;
    FunctionTreeNode step;
    
    public Series(FunctionTreeNode ftn,Variable index,FunctionTreeNode min,FunctionTreeNode max,FunctionTreeNode step){
        super(ftn);
        this.index=index;
        this.min=min;
        this.max=max;
        this.step=step;
    }
    public Series(FunctionTreeNode ftn,Variable index,FunctionTreeNode min,FunctionTreeNode max){
        this(ftn,index,min,max,Constant.ONE);
    }
    public Series(FunctionTreeNode ftn,Variable index,double min,double max,double step){
        this(ftn,index,new Constant(min),new Constant(max),new Constant(step));
    }
    public Series(FunctionTreeNode ftn,Variable index,double min,double max){
        this(ftn,index,new Constant(min),new Constant(max),Constant.ONE);
    }
    
    @Override
    public String toString(){
        String result=getFunctionName()+"("+argumentString()+","+index.toString()+","+min.toString()+","+max.toString();
        if(step.equals(Constant.ONE)){result+=","+step.toString();}
        return result+")";
    }
    
    @Override
    public boolean isNumber(){
        return getVariables().size()==0;
    }
    
    @Override
    public Vector<Variable> getVariables(){
        Vector<Variable> result=argumentNode().getVariables();
        result.remove(index);
        return result;
    }
    
    @Override
    public FunctionTreeNode simplified(){
        return this;
    }

    /** Static method generating a summation given a vector of functiontreenodes representing min/max/step. Throws exception
     * if contains an improper number of arguments.
     * @param arguments
     * @return indexed sum of arguments
     * @throws parser.FunctionSyntaxException
     * @throws java.lang.ArrayIndexOutOfBoundsException
     */    
    public static Sum getSum(Vector<FunctionTreeNode> arguments) throws FunctionSyntaxException,ArrayIndexOutOfBoundsException{
        if(!(arguments.get(1) instanceof Variable)){
            throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_TYPE);                
        }
        if(arguments.size()==4){
            return new Series.Sum(arguments.get(0),(Variable)arguments.get(1),arguments.get(2),arguments.get(3));
        }else if(arguments.size()==5){
            return new Series.Sum(arguments.get(0),(Variable)arguments.get(1),arguments.get(2),arguments.get(3),arguments.get(4));
        }
        throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_NUMBER);                
    }
    
    /** Static method generating a product given a vector of functiontreenodes representing min/max/step. Throws exception
     * if contains an improper number of arguments.
     * @param arguments
     * @return indexed product of arguments
     * @throws parser.FunctionSyntaxException
     * @throws java.lang.ArrayIndexOutOfBoundsException
     */    
    public static Prod getProd(Vector<FunctionTreeNode> arguments) throws FunctionSyntaxException,ArrayIndexOutOfBoundsException{
        if(!(arguments.get(1) instanceof Variable)){
            throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_TYPE);                
        }
        if(arguments.size()==4){
            return new Series.Prod(arguments.get(0),(Variable)arguments.get(1),arguments.get(2),arguments.get(3));
        }else if(arguments.size()==5){
            return new Series.Prod(arguments.get(0),(Variable)arguments.get(1),arguments.get(2),arguments.get(3),arguments.get(4));
        }
        throw new FunctionSyntaxException(FunctionSyntaxException.ARGUMENT_NUMBER);                
    }    
    
    /** Represents an indexed sum of elements (finite) */
    public static class Sum extends Series {    
        public Sum(FunctionTreeNode ftn,Variable index,FunctionTreeNode min,FunctionTreeNode max,FunctionTreeNode step){super(ftn,index,min,max,step);}
        public Sum(FunctionTreeNode ftn,Variable index,FunctionTreeNode min,FunctionTreeNode max){super(ftn,index,min,max);}
        public Sum(FunctionTreeNode ftn,Variable index,double min,double max,double step){super(ftn,index,min,max,step);}
        public Sum(FunctionTreeNode ftn,Variable index,double min,double max){super(ftn,index,min,max);}
        
        public void initFunctionType(){setFunctionNames("sum",null,null,null);}
        public Double getValue(TreeMap<Variable, Double> table){
            double result=0;
            double dMin=min.getValue(table);
            double dMax=max.getValue(table);
            if(dMin>dMax){double temp=dMin;dMin=dMax;dMax=temp;}
            double dStep=step.getValue(table);
            for(double i=dMin;i<=dMax;i+=dStep){table.put(index,i);result+=argumentNode().getValue(table);}
            table.remove(index);
            return result;
        }
    
        @Override
        public FunctionTreeNode derivativeTree(Variable v){
            return new Sum(argumentDerivative(v),index,min,max,step);
        }
    } // class Series.Add
    
    /** Represents an indexed product of elements (finite. */
    public static class Prod extends Series {
        public Prod(FunctionTreeNode ftn,Variable index,FunctionTreeNode min,FunctionTreeNode max,FunctionTreeNode step){super(ftn,index,min,max,step);}
        public Prod(FunctionTreeNode ftn,Variable index,FunctionTreeNode min,FunctionTreeNode max){super(ftn,index,min,max);}
        public Prod(FunctionTreeNode ftn,Variable index,double min,double max,double step){super(ftn,index,min,max,step);}
        public Prod(FunctionTreeNode ftn,Variable index,double min,double max){super(ftn,index,min,max);}
        
        public void initFunctionType(){setFunctionNames("product",null,null,null);}
        public Double getValue(TreeMap<Variable, Double> table){
            double result=1;
            double dMin=min.getValue(table);
            double dMax=max.getValue(table);
            if(dMin>dMax){double temp=dMin;dMin=dMax;dMax=temp;}
            double dStep=step.getValue(table);
            table.put(index,dMin);
            for(double i=dMin;i<=dMax;i+=dStep){table.put(index,i);result*=argumentNode().getValue(table);}
            table.remove(index);
            return result;
        }
    } // class Series.Multiply
}
