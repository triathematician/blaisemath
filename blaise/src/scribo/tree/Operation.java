/*
 * Operation.java
 * Created on Sep 30, 2007, 3:28:59 PM
 */

// TODO Add &&, ||, ! operators
// TODO Unify Add,Divide,Power classe
// TODO Add sorting of * and + elements to siimplify
// TODO create special node class with double coefficient parameter corresponding to something, e.g. for add its the coefficient; for multiply the power

package scribo.tree;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

/**
 * Handles basic operations on nodes.<br><br>
 * Includes addition, implemented as a linear combination of elements, and multiplication,
 * implemented as a combination of products and power. Constant terms, if present, are the
 * first subnode.
 * 
 * @author Elisha Peterson
 */
public abstract class Operation extends FunctionTreeNode {
    
    /** String corresponding to the operation's symbol */
    protected String opSymbol;
    /** Represents array of coefficients corresponding to the array of terms. */
    protected Vector<Double> coefficient;

    public Operation(){super();initCoefficients();initOperationString();}
    /** Any number of subnodes is allowed, assuming it is positive. The first subnode
     * should be used to store either the constant summands or the constant coefficient. */
    public boolean isValidSubNode(){return numSubNodes()>0;}
    
    /** Initializes other elements for the linear combination. */
    public void initCoefficients(){coefficient=new Vector<Double>();}
    /** Returns ith coefficient. */        
    public double getCoefficient(int i){return coefficient.get(i);}
    
    /** Adds several subnodes with the specified coefficient. */
    public FunctionTreeNode addSubNodes(double coeff,Collection<FunctionTreeNode> children){
        for(FunctionTreeNode ftn:children){addSubNode(coeff,ftn);}
        return this;
    }
    /** Subclasses should describe how to do this... */
    public abstract FunctionTreeNode addSubNode(double coeff,FunctionTreeNode child);
    public abstract FunctionTreeNode addNumericNode(double coeff);

    public abstract void initOperationString();
    public void setOperationString(String s,int d){opSymbol=s;depth=d;}
    public String toString(){
        if(numSubNodes()==0){return "";}
        if(numSubNodes()==1){return getSubNode(0).toString(this);}
        String result=getSubNode(0).toString(this)+opSymbol;
        for(int i=1;i<numSubNodes()-1;i++){result+=getSubNode(i).toString(this)+opSymbol;}
        return result+getSubNodes().get(numSubNodes()-1).toString(this);
    }
    
    
    // STATIC METHODS

    /** Returns negative of the node (multiplies by -1) */
    public static FunctionTreeNode negative(FunctionTreeNode f){return new Multiply(-1,f);}
    
    /** Returns an Add class that represents a-b-c type nodes. */
    public static Add subtractNode(Vector<FunctionTreeNode> kids){
        if(kids.size()==0){return new Add();}
        Add result=new Add(kids.get(0));
        return (Add)result.addSubNodes(-1.0,kids.subList(1,kids.size()));
    }
    
    /** Returns Add node which subtracts second element from the first. */
    public static Add subtractNode(FunctionTreeNode ftn1,FunctionTreeNode ftn2){
        Add result=new Add(ftn1);
        return (Add)result.addSubNode(-1.0,ftn2);
    }
    
    /** Returns a Multiply class representing a/b/c type nodes. */
    public static Multiply divideNode(Vector<FunctionTreeNode> kids){
        if(kids.size()==0){return new Multiply();}
        Multiply result=new Multiply(kids.get(0));
        return (Multiply)result.addSubNodes(-1.0,kids.subList(1,kids.size()));
    }
    
    /** Returns Divide node which subtracts second element from the first. */
    public static Multiply divideNode(FunctionTreeNode ftn1,FunctionTreeNode ftn2){
        Multiply result=new Multiply(ftn1);
        return (Multiply)result.addSubNode(-1.0,ftn2);
    }
    
    /** Returns Multiply class representing c*a/b. */
    public static Multiply divideNode(double coeff,FunctionTreeNode ftn1,FunctionTreeNode ftn2){
        Multiply result=new Multiply();
        result.addNumericNode(coeff);
        result.addSubNode(ftn1);
        result.addSubNode(-1.0,ftn2);
        return result;
    }
    
    
    
    // INSTANCES OF OPERATIONS
    
    /** 
     * Basic addition operation.<br><br>
     * Really, the node represents a linear combination of its subnodes, so that each subnode
     * comes equipped with a coefficient. This combines addition/subtraction into a unified class.
     * The first subnode element is used to represent the constant summand, and all constant values are
     * accumulated here, even before simplification.
     * If told to add an "Add" subNode, adds its children. This is permitted by associativity.
     * @author Elisha Peterson
     */   
    public static class Add extends Operation {        
        public Add(){super();}
        public Add(double d){super();addNumericNode(d);}
        public Add(double d,FunctionTreeNode a){super();addSubNode(d,a);}
        public Add(FunctionTreeNode a){super();addSubNode(a);}
        public Add(FunctionTreeNode a1, FunctionTreeNode a2){super();addSubNode(a1);addSubNode(a2);}
        public Add(Collection<FunctionTreeNode> as){super();addSubNodes(as);}
        
        /** Initializes notation for the command, as well as the depth. */
        public void initOperationString(){setOperationString("+",5);}
        
        /** Override the standard addSubNode to treat constants in a special way. */
        @Override
        public FunctionTreeNode addSubNode(FunctionTreeNode child){return addSubNode(1.0,child);}
        
        /** Adds a subnode with the specified coefficient. */
        public FunctionTreeNode addSubNode(double coeff,FunctionTreeNode child){
            if(coeff==0||child==null){return this;}
            if(child instanceof Add){
                for(int i=0;i<child.numSubNodes();i++){
                    addSubNode(coeff*((Add)child).getCoefficient(i),child.getSubNode(i));
                }
            }else if(child.isValidSubNode()){
                if(child.isNumber()){addNumericNode(coeff*child.getValue());
                }else{super.addSubNode(child);coefficient.add(coeff);
                }
            }
            return this;
        }
        
        /** Adds a numeric node, if none present. Otherwise, adds value to the existing numeric node. */
        public FunctionTreeNode addNumericNode(double coeff){
            if(coeff==0){return this;}
            if(numSubNodes()==0){super.addSubNode(new Constant(coeff));coefficient.add(1.0);
            }else if(getSubNode(0) instanceof Constant){((Constant)getSubNode(0)).add(coeff);
            }else{getSubNodes().insertElementAt(new Constant(coeff),0);coefficient.insertElementAt(1.0,0);
            }
            return this;
        }

        /** Returns value given table of variables. */
        public Double getValue(TreeMap<Variable, Double> table){
            Double result=0.0;
            try{
                for(int i=0;i<numSubNodes();i++){result+=coefficient.get(i)*getSubNode(i).getValue(table);}
            }catch(NullPointerException e){
                return null;
            }
            return result;
        }
        
        /** Computes the derivative tree corresponding to this function. */
        public FunctionTreeNode derivativeTree(Variable v) {
            //may want to add this: if(isNumber()){return Constant.ZERO;}
            Add result=new Add();
            for(int i=0;i<numSubNodes();i++){
                result.addSubNode(coefficient.get(i),getSubNode(i).derivativeTree(v));
            }
            if(result.numSubNodes()==0){return Constant.ZERO;}
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
            if(isNumber()){return new Constant(getValue()).simplified();}
            Vector<Boolean> added=new Vector<Boolean>();
            for(FunctionTreeNode ftn:getSubNodes()){added.add(false);}
            double constantSummand=0;
            double coeff=0;
            Add result=new Add();
            for(int i=0;i<numSubNodes();i++){
                if(added.get(i)){continue;}
                added.set(i,true);
                if(getSubNode(i).isNumber()){
                    constantSummand+=coefficient.get(i)*getSubNode(i).getValue();
                }else{
                    coeff=coefficient.get(i);
                    for(int j=i+1;j<numSubNodes();j++){
                        if(added.get(j)){continue;}
                        if(getSubNode(i).simplified().equals(getSubNode(j).simplified())){
                            added.set(j,true);
                            coeff+=coefficient.get(j);
                        }
                    }
                    result.addSubNode(coeff,getSubNode(i).simplified());
                }
            }
            result.addNumericNode(constantSummand);
            if(result.numSubNodes()==0){return Constant.ZERO;}
            return result;
        } // Add.simplified()
        
        /** Returns string representation of ith term, including the sign term. */
        public String toString(int i){
            if(i>=coefficient.size()){return "";}
            if(coefficient.get(i)==1){return getSubNode(i).toString(this);}
            if(coefficient.get(i)==-1){return "-"+getSubNode(i).toString(this);}
            return Constant.toString(coefficient.get(i))+getSubNode(i).toString(this);
        }
        
        /** Overrides string-conversion method. */
        @Override
        public String toString(){
            if(numSubNodes()==0){return "";}
            if(numSubNodes()==1){return toString(0);}
            String result=toString(0);
            String plus;
            for(int i=1;i<numSubNodes();i++){
                plus=toString(i);
                if(plus.startsWith("-")){result+=plus;
                }else{result+="+"+plus;}
            }
            return result;
        }
        
    } // class Operation.Add
    
        /** 
     * Basic addition operation.<br><br>
     * Really, the node represents a product of elements to powers, so that each subnode
     * comes equipped with an integer power. This combines multiplication/division/powers into a unified class.
     * The first subnode element is used to represent the constant summand, and all constant values are
     * accumulated here, even before simplification.
     * If told to add an "Add" subNode, adds its children. This is permitted by associativity.
     * @author Elisha Peterson
     */   
    public static class Multiply extends Operation {
        
        public Multiply(){super();}
        public Multiply(double d){super();addNumericNode(d);}
        public Multiply(FunctionTreeNode a){super();addSubNode(a);}
        public Multiply(double c, FunctionTreeNode a1){super();addNumericNode(c);addSubNode(a1);}
        public Multiply(FunctionTreeNode a1, FunctionTreeNode a2){super();addSubNode(a1);addSubNode(a2);}
        public Multiply(double c, FunctionTreeNode a1, FunctionTreeNode a2){super();addNumericNode(c);addSubNode(a1);addSubNode(a2);}
        public Multiply(FunctionTreeNode a1, FunctionTreeNode a2, FunctionTreeNode a3){super();addSubNode(a1);addSubNode(a2);addSubNode(a3);}
        public Multiply(double c, FunctionTreeNode a1, FunctionTreeNode a2, FunctionTreeNode a3){super();addNumericNode(c);addSubNode(a1);addSubNode(a2);addSubNode(a3);}
        public Multiply(Collection<FunctionTreeNode> as){super();addSubNodes(as);}
        
        /** Initializes notation for the command, as well as the depth. */
        public void initOperationString(){setOperationString("*",3);}
        
        /** Override the standard addSubNode to treat constants in a special way. */
        @Override
        public FunctionTreeNode addSubNode(FunctionTreeNode child){return addSubNode(1.0,child);}
        
        /** Adds a subnode with the specified coefficient. */
        public FunctionTreeNode addSubNode(double coeff,FunctionTreeNode child){
            if(coeff==0||child==null){return this;}
            if(child instanceof Multiply){
                for(int i=0;i<child.numSubNodes();i++){
                    addSubNode(coeff*((Multiply)child).getCoefficient(i),child.getSubNode(i));
                }
            }else if(child.isValidSubNode()){
                if(child.isNumber()){addNumericNode(Math.pow(child.getValue(),coeff));
                }else{super.addSubNode(child);coefficient.add(coeff);
                }
            }
            return this;
        }
        
        /** Adds a numeric node, if none present. Otherwise, adds value to the existing numeric node. */
        public FunctionTreeNode addNumericNode(double coeff){
            if(coeff==0){coefficient.clear();getSubNodes().clear();}
            if(coeff==1){return this;}
            if(numSubNodes()==0){super.addSubNode(new Constant(coeff));coefficient.add(1.0);
            }else if(getSubNode(0) instanceof Constant){((Constant)getSubNode(0)).multiplyBy(coeff);
            }else{getSubNodes().insertElementAt(new Constant(coeff),0);coefficient.insertElementAt(1.0,0);
            }
            return this;
        }

        /** Returns value given table of variables. */
        public Double getValue(TreeMap<Variable, Double> table){
            Double result=1.0;
            try{
                for(int i=0;i<numSubNodes();i++){result*=Math.pow(getSubNode(i).getValue(table),coefficient.get(i));}
            }catch(NullPointerException e){
                return null;
            }
            return result;
        }
        
        /** Computes the derivative tree corresponding to this function. */
        public FunctionTreeNode derivativeTree(Variable v) {
            //may want to add this: if(isNumber()){return Constant.ZERO;}
            Vector<FunctionTreeNode> summands=new Vector<FunctionTreeNode>();
            Multiply summand;
            double power;
            for(int i=0;i<numSubNodes();i++){
                if(getSubNode(i).isNumber()){continue;}
                summand=new Multiply();
                for(int j=0;j<numSubNodes();j++){
                    power=coefficient.get(j);
                    if(i==j){
                        if(power!=1){
                            summand.addNumericNode(power);
                            summand.addSubNode(power-1,getSubNode(i));
                        }
                        summand.addSubNode(getSubNode(i).derivativeTree(v));
                    }else{
                        summand.addSubNode(power,getSubNode(j));
                    }
                }
                summands.add(summand);
            }
            if(summands.size()==0){return Constant.ZERO;}
            if(summands.size()==1){return summands.get(0);}
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
            if(isNumber()){return new Constant(getValue()).simplified();}
            Vector<Boolean> multiplied=new Vector<Boolean>();
            for(FunctionTreeNode ftn:getSubNodes()){multiplied.add(false);}
            double constantElement=1;
            double coeff=0;
            Multiply result=new Multiply();
            for(int i=0;i<numSubNodes();i++){
                if(multiplied.get(i)){continue;}
                multiplied.set(i,true);
                if(getSubNode(i).isNumber()){
                    constantElement*=Math.pow(getSubNode(i).getValue(),coefficient.get(i));
                }else{
                    coeff=coefficient.get(i);
                    for(int j=i+1;j<numSubNodes();j++){
                        if(multiplied.get(j)){continue;}
                        if(getSubNode(i).simplified().equals(getSubNode(j).simplified())){
                            multiplied.set(j,true);
                            coeff+=coefficient.get(j);
                        }
                    }
                    result.addSubNode(coeff,getSubNode(i).simplified());
                }
            }
            result.addNumericNode(constantElement);
            if(result.numSubNodes()==0){return Constant.ONE;}
            return result.addVersion();
        } // Multiply.simplified()
        
        /** Returns consolidated version of the Multiply. Incorporates all subnodes which are
         * Multiply or Power into this node. */
        public Multiply consolidated(){
            return null;
        }
        
        /** Returns Add version of the Multiply if possible to convert it to that. */
        public FunctionTreeNode addVersion(){
            switch(numSubNodes()){
            case 1:
                if(coefficient.get(0)==1){
                    return new Add(getSubNode(0));
                }
                break;
            case 2:
                int numericNode=getSubNode(0).isNumber()?0:1;
                if(getSubNode(numericNode).isNumber()&&coefficient.get(1-numericNode)==1){
                    return new Add(Math.pow(getSubNode(numericNode).getValue(),coefficient.get(numericNode)),
                            getSubNode(1-numericNode));
                }
                break;
            }
            return this;
        }
        
        /** Returns string representation of ith term, including the sign term. */
        public String toString(int i){
            if(i>=coefficient.size()){return "";}
            if(coefficient.get(i)==1){return getSubNode(i).toString(this);}
            if(coefficient.get(i)==-1){return "/"+getSubNode(i).toString(this);}
            return getSubNode(i).toString(this)+"^"+Constant.toString(coefficient.get(i));
        }
        
        /** Overrides string-conversion method. */
        @Override
        public String toString(){
            if(numSubNodes()==0){return "";}
            String result=toString(0);
            if(result.startsWith("/")){result="1"+result;}
            String times;
            for(int i=1;i<numSubNodes();i++){
                times=toString(i);
                if(times.startsWith("/")){result+=times;
                }else{result+="*"+times;}
            }
            if(result.startsWith("-1*")){return "-"+result.substring(3);}
            return result;
        }
        
    } // class Operation.Multiply
    
    
    /** Power operation. */
    public static class Power extends Operation {
        public Power(){super();setSubNodes(Constant.ONE,Constant.ONE);}
        public Power(FunctionTreeNode base,double power){super();setSubNodes(base,new Constant(power));}
        public Power(FunctionTreeNode base,FunctionTreeNode power){super();setSubNodes(base,power);}
        public Power(List<FunctionTreeNode> as){super();addSubNodes(as);}
        
        @Override
        public boolean isValidSubNode(){return numSubNodes()==2;}
        
        public void initOperationString(){setOperationString("^",1);}
        public void setSubNodes(FunctionTreeNode base,FunctionTreeNode power){addSubNode(base);addSubNode(power);}
        public FunctionTreeNode basePart(){return getSubNode(0);}
        public FunctionTreeNode powerPart(){return (numSubNodes()==2)?getSubNode(1):new Power(getSubNodes().subList(1,numSubNodes()));}
                
        public Double getValue(TreeMap<Variable,Double> table){return Math.pow(basePart().getValue(table),powerPart().getValue(table));}
        public FunctionTreeNode derivativeTree(Variable v) {
            if(powerPart().isNumber()){
                return new Operation.Multiply(powerPart(),new Power(basePart(),powerPart().getValue()-1),basePart().derivativeTree(v));
            }else if(basePart().equals(Constant.E)){
                return new Operation.Multiply(powerPart().derivativeTree(v),this);
            }else if(basePart().isNumber()){
                return new Operation.Multiply(Math.log(basePart().getValue()),this,powerPart().derivativeTree(v));
            }else{
                return new Operation.Add(
                    new Operation.Multiply(powerPart(),new Power(basePart(),subtractNode(powerPart(),Constant.ONE)),basePart().derivativeTree(v)),
                    new Operation.Multiply(this,new Exponential.Log(basePart()),powerPart().derivativeTree(v)));
            }
        }

        @Override
        public FunctionTreeNode simplified(){
            if(isNumber()){return new Constant(getValue()).simplified();}
            if(powerPart().isNumber()){
                if(powerPart().equals(0)){return Constant.ONE;
                }else if(powerPart().equals(1)){return basePart();}
                Multiply result=new Multiply();
                result.addSubNode(powerPart().getValue(),basePart());
                return result;
            }
            if(basePart().equals(1)){return Constant.ONE;}
            if(basePart().equals(0)){return Constant.ZERO;}
            return this;
        }

        public FunctionTreeNode addSubNode(double coeff, FunctionTreeNode child) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public FunctionTreeNode addNumericNode(double coeff) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    } // POWER SUB-CLASS

} // OPERATION CLASS
