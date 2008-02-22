/*
 * FunctionModel.java
 * Created on Feb 17, 2008
 */

package sequor.model;

import java.beans.PropertyChangeEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
public class FunctionTreeModel extends FiresChangeEvents implements ChangeListener {
    /** Stores the implemented function tree. */
    FunctionTreeRoot ftr;
    /** Whether the current text is valid or not. */
    boolean valid=true;

    public FunctionTreeModel(){setValue("cos(x)");}

    public FunctionTreeModel(FunctionTreeNode ftn) {this(new FunctionTreeRoot(ftn));}
    public FunctionTreeModel(FunctionTreeRoot ftr){this.ftr=ftr;}
    public FunctionTreeModel(String text,String var){setValue(text);}
    
    // BEAN patterns
    
    public boolean isValid(){return valid;}
    
    public FunctionTreeRoot getRoot(){return ftr;}
    
    @Override
    public void setValue(String s) {
        try {
            if(ftr==null){
                ftr=new FunctionTreeRoot(s);
            }else{
                ftr.setArgumentNode(Parser.parseExpression(s));
            }
            valid=true;
            fireStateChanged();
        } catch (FunctionSyntaxException ex) {
            valid=false;
        }
    }
    
    // REQUIRED METHODS
    
    @Override
    public FiresChangeEvents clone() {return new FunctionTreeModel(ftr);}
    @Override
    public String toLongString() {return ftr.toString();}
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
            ftr.setUnknowns(plm.getParameterList());
            fireStateChanged();
        }
    }

}
