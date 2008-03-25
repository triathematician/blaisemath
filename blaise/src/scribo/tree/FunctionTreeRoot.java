/*
 * FunctionTreeRoot.java
 * Created on Sep 21, 2007, 2:58:57 PM
 */

// TODO Add equality root node.
// TODO Add list of variables at FunctionRoot/setting variable values at the top
// TODO Remove dependence on FunctionTreeFunctionNode
// TODO Change Variable collection to a set

package scribo.tree;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import scio.coordinate.Euclidean;
import scio.coordinate.R1;
import scio.function.BoundedFunction;
import scribo.parser.FunctionSyntaxException;
import scio.function.FunctionValueException;
import scio.function.ParameterFunction;
import scribo.parser.Parser;

/**
 * This class represents the root of a FunctionTree. In particular, every tree which is constructed passes all
 * information through a root node, which is this one.
 * <p>
 * The class stores a list of variables (whose values are "parameters") and parameters (whose values are "knowns" although
 * they can be changed). The distinction is important because fundamentally the function f(x)=a*sin(b(x+c))+d should be
 * plotted in two-dimensions, although nominally there are five parameters on the righthand side. Other classes which use
 * this one should be able to "observe" this fact and know what to do with the parameters and the variables automatically.
 * This, the part that is important for any class which utilizes this one is (i) how to get at and adjust parameters, (ii) how
 * to get at the variables, and (iii) how to evaluate the function at a particular value or range of values.
 * </p>
 * <p>
 * Note that this is the only class which "cares" that an unknown is either a parameter or a variable; any other FunctionTreeNode
 * treats the two types exactly the same.
 * </p>
 * @author Elisha Peterson
 */
public class FunctionTreeRoot extends FunctionTreeFunctionNode implements FunctionRoot {
    
    
    // VARIABLES
    
    /** Variables required to obtain a value. */
    TreeSet<String> variables;
    /** Parameters associated with the tree.. along with the variables. If not passed directly to "getValue",
     * the values will be looked up in this table.
     */
    TreeMap<String,Double> parameters;
    
    
    // CONSTRUCTORS
    
    public FunctionTreeRoot(String s) throws FunctionSyntaxException {
        this(Parser.parseExpression(s));
    }
    
    public FunctionTreeRoot(FunctionTreeNode c){
        addSubNode(c);
        variables=c.getUnknowns();
        parameters=new TreeMap<String,Double>();
    }    
    
    
    // HANDLING UNKNOWN PARAMETERS

    /** Sets up an entire list of parameters. */
    public void setUnknowns(TreeMap<String,Double> values){
        parameters.putAll(values);
        variables.removeAll(values.keySet());
    }
    
    /** Returns current list of variables. */
    public Set<String> getVariables(){return variables;}    
    /** Returns current list of parameters. */
    public Set<String> getParameters(){return parameters.keySet();}
    /** Returns number of variables. */
    public int getNumVariables(){return variables.size();}
    /** Returns number of parameters. */
    public int getNumParameters(){return parameters.size();}
    
    
    // OVERRIDE SUBMETHODS FROM FUNCTIONTREEFUNCTIONNODE
    
    @Override
    public String toString(){
        return "Root "
                + (parameters==null?"":(" , "+parameters.toString()));
    }
    @Override
    public FunctionTreeNode derivativeTree(Variable v){
        return numSubNodes()==1?argumentDerivative(v):null;
    }
    @Override
    public boolean isValidSubNode(){return false;}    
    @Override
    public FunctionTreeNode simplified(){
        try{
            return new Constant(getValue()).simplified();
        }catch(FunctionValueException e){
            return new FunctionTreeRoot(argumentSimplified());
        }
    }
    @Override
    public void initFunctionType(){}

    
    // METHODS TO RETURN VALUE
    
    @Override
    public Double getValue(TreeMap<String, Double> table) throws FunctionValueException {
        table.putAll(parameters);
        return argumentValue(table);
    }
    @Override
    public Double getValue(String s, Double d) throws FunctionValueException {
        if(parameters==null||parameters.isEmpty()){return argumentValue(s,d);}
        TreeMap<String,Double> table=new TreeMap<String,Double>();
        table.put(s,d);
        return getValue(table);
    }
    @Override
    public Vector<Double> getValue(String s, Vector<Double> d) throws FunctionValueException {
        // TODO make more efficient!
        if(parameters==null||parameters.isEmpty()){return argumentValue(s,d);}
        TreeMap<String,Double> table=new TreeMap<String,Double>();
        table.putAll(parameters);
        Vector<Double> result=new Vector<Double>(d.size());
        for(Double x:d){
            table.put(s,x);
            result.add(argumentValue(table));
        }
        return result;
    }
    
    

    // FUNCTION INTERFACE METHODS
    
    public BoundedFunction getFunction(){return getDoubleFunction();}
    public ParameterFunction getParameterFunction(){return null;}
    
    /** Returns a generic function corresponding to this tree. The type of the function will depend on the number of inputs. */
    public BoundedFunction<Euclidean,Double> getDoubleFunction() {
        int n=variables.size();
        return new BoundedFunction<Euclidean,Double>(){
            @Override
            public Double getValue(Euclidean x) throws FunctionValueException {
                TreeMap<String,Double> table=new TreeMap<String,Double>();
                Iterator iter=variables.iterator();
                for(int i=0;i<variables.size();i++){                    
                    table.put((String)iter.next(),x.getElement(i));
                }
                table.putAll(parameters);
                return FunctionTreeRoot.this.getValue(table);
            }      
            @Override
            public Vector<Double> getValue(Vector<Euclidean> x) throws FunctionValueException {
                Vector<Double> result=new Vector<Double>();
                TreeMap<String,Double> table=new TreeMap<String,Double>();
                table.putAll(parameters);
                Iterator iter=variables.iterator();
                for(int i=0;i<x.size();i++){
                    for(int j=0;j<variables.size();j++){                    
                        table.put((String)iter.next(),x.get(i).getElement(j));
                    }
                    result.add(FunctionTreeRoot.this.getValue(table));
                }
                return result;
            }
            @Override
            public Double minValue() { return 0.0; }
            @Override
            public Double maxValue() { return 0.0; }
        };
    }

    /** Default value function for Function<Double,Double>. Required as part of FunctionTreeFunctionNode. */
    public Double getValue(Double x) throws FunctionValueException {
        int n=variables.size();
        if(n!=1){throw new FunctionValueException();}
        return getDoubleFunction().getValue(new R1(x));
    }
}
