/*
 * FunctionModel.java
 * Created on Feb 17, 2008
 */

package sequor.model;

import sequor.FiresChangeEvents;
import java.beans.PropertyChangeEvent;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import scio.function.Function;
import scribo.parser.FunctionSyntaxException;
import scribo.parser.Parser;
import scribo.tree.FunctionTreeNode;
import scribo.tree.FunctionTreeRoot;

/**
 * This class represents a model which interfaces with an underlying function tree. It contains support for passing parameter tables to and from the
 * function.
 * 
 * @author Elisha Peterson
 */
@XmlRootElement(name="function")
public class FunctionTreeModel extends FiresChangeEvents implements ChangeListener {
    /** Stores the implemented function tree. */
    FunctionTreeRoot ftr;
    /** Whether the current text is valid or not. */
    boolean valid=true;
    /** Stores variables used in the underlying FunctionTree. */

    public FunctionTreeModel(){this("cos(x)");}
    public FunctionTreeModel(String text) {this(text,"x");}
    public FunctionTreeModel(String text,String var){setValue(text,var);}
    public FunctionTreeModel(String text,String[] vars){setValue(text,vars);}
    
    public FunctionTreeModel(FunctionTreeNode ftn) {this(new FunctionTreeRoot(ftn));}
    public FunctionTreeModel(FunctionTreeRoot ftr){this.ftr=ftr;}
    
    // BEAN patterns
    
    public boolean isValid(){return valid;}
    
    @XmlTransient
    public FunctionTreeRoot getRoot(){return ftr;}
    public void setRoot(FunctionTreeRoot ftr){setValue(ftr.argumentString());}
    
    @XmlAttribute
    public String getValue() { return toString(); }
    @Override
    public void setValue(String s) {
        try {
            if(ftr==null){ftr=new FunctionTreeRoot(s);}
            else{ftr.setArgumentNode(Parser.parseExpression(s));}
            valid=true;
            fireStateChanged();
        } catch (FunctionSyntaxException ex) {
            valid=false;
        }
    }
    public void setValue(String s, String var) {
        try {
            if(ftr==null){ftr=new FunctionTreeRoot(s);}
            else{ftr.setArgumentNode(Parser.parseExpression(s));}
            Vector<String> vars = new Vector<String>();
            vars.add(var);
            ftr.setVariables(vars);
            valid=true;
            fireStateChanged();
        } catch (FunctionSyntaxException ex) {
            valid=false;
        }
    }
    public void setValue(String s, String[] vars) {
        try {
            if(ftr==null){
                ftr = new FunctionTreeRoot(s);
            } else {
                ftr.setArgumentNode(Parser.parseExpression(s));
            }
            ftr.setVariables(vars);
            valid = true;
            fireStateChanged();
        } catch (FunctionSyntaxException ex) {
            valid = false;
        }
    }
        
    public Function<?,Double> getFunction(){ return ftr.getFunction(); }
    public Function<?,Double> getFunction(String[] vars){ return ftr.getFunction(vars); }
    
    // REQUIRED METHODS
    
    @Override
    public FiresChangeEvents clone() {return new FunctionTreeModel(ftr);}

    @Override
    public String toString(){return ftr.argumentString();}
    @Override
    public PropertyChangeEvent getChangeEvent(String s) {return new PropertyChangeEvent(this,s,null,ftr);}
    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {
        if(parent instanceof FunctionTreeModel){
            ftr.setArgumentNode(((FunctionTreeModel)parent).getRoot().argumentNode());
        }        
    }
    
    // EVENT HANDLING

    /** If the state changes, it means either the underlying function has changed, or that the list of parameters has changed. */
    @Override
    public void stateChanged(ChangeEvent e) {
        if(e.getSource() instanceof ParameterListModel){
            ParameterListModel plm=(ParameterListModel)e.getSource();
            ftr.setParameters(plm.getParameterList());
            fireStateChanged();
        }
    }

}
