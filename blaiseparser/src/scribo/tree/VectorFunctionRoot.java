/**
 * VectorFunctionRoot.java
 * Created on Mar 24, 2008
 */

package scribo.tree;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import scio.function.Function;
import scio.function.ParameterFunction;
import scribo.parser.FunctionSyntaxException;
import scribo.parser.Parser;

/**
 * Represents a function with multiple outputs.
 * 
 * @author Elisha Peterson
 */
public class VectorFunctionRoot extends ArgumentList implements FunctionRoot {
    
    // VARIABLES
    
    /** Variables required to obtain a value. */
    TreeSet<String> variables;
    /** Parameters associated with the tree.. along with the variables. If not passed directly to "getValue",
     * the values will be looked up in this table.
     */
    TreeMap<String,Double> parameters;
    
    
    // CONSTRUCTORS
    
    public VectorFunctionRoot(Vector<String> cs) throws FunctionSyntaxException{
        parameters=new TreeMap<String,Double>();
        variables=new TreeSet<String>();
        for(String s:cs){addSubNode(Parser.parseExpression(s));}
    }
    public VectorFunctionRoot(FunctionTreeNode result) {
        parameters=new TreeMap<String,Double>();
        variables=new TreeSet<String>();
        if(result instanceof ArgumentList){
            for(FunctionTreeNode ftn:result.children){addSubNode(ftn);}
        }else{
            addSubNode(result);
        }
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
    
    
    // FUNCTION QUERIES
    
    public Function getFunction(){return null;}
    public ParameterFunction getParameterFunction(){return null;}
}
