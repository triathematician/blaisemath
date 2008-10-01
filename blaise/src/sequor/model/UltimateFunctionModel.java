/**
 * UltimateFunctionModel.java
 * Created on Mar 24, 2008
 */

package sequor.model;

import java.beans.PropertyChangeEvent;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import scio.function.Function;
import scribo.parser.FunctionSyntaxException;
import scribo.tree.FunctionRoot;
import scribo.tree.FunctionTreeRoot;
import sequor.FiresChangeEvents;

/**
 * Data model controlling an underlying function, with the ability to detect and pass on changes in the number of variables, the number
 * of parameters, the number of outputs, and the function itself.
 * 
 * @author Elisha Peterson
 */
@XmlRootElement(name="ultimateFunctionModel")
public class UltimateFunctionModel extends FiresChangeEvents {
    FunctionRoot function;
    SortedListModel<String> variables;
    SortedListModel<String> parameters;
    
    public UltimateFunctionModel() throws FunctionSyntaxException {this("sin(x)");}
    
    public UltimateFunctionModel(String text) throws FunctionSyntaxException{
        function=new FunctionTreeRoot(text);
        variables=new SortedListModel(function.getVariables());
        parameters=new SortedListModel(function.getParameters());
    }
    
    
    // BEAN PATTERNS
    
    @XmlAttribute
    public void setFunction(String newFunction){
        try{
            function=new FunctionTreeRoot(newFunction);
            variables.setList(function.getVariables());
            parameters.setList(function.getVariables());
            fireStateChanged();
        }catch(FunctionSyntaxException e){}
    }
    public String getFunction(){return function.toString();}
    
    public Function getFunctionObject(){return function.getFunction();}    
    public TreeSet<String> getVariables(){return variables.getList();}
    public TreeSet<String> getParameters(){return parameters.getList();}

    
    // METHODS FOR FiresChangeEvents

    @Override
    public FiresChangeEvents clone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setValue(String s) {setFunction(s);}

    @Override
    public PropertyChangeEvent getChangeEvent(String s) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
