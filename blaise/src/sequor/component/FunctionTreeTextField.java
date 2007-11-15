/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequor.component;

import scribo.parser.*;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import scribo.tree.ArgumentList;
import scribo.tree.FunctionTreeNode;
import scribo.tree.FunctionTreeRoot;
import scribo.tree.Variable;

/**
 * This class is a text field synchronized with an underlying function tree.
 * 
 * @author Elisha
 */
public class FunctionTreeTextField extends JTextField {

    FunctionTreeRoot f;
    
    public FunctionTreeTextField(){
        initEventListening();
        f=new FunctionTreeRoot(new Variable("x"));
        setText("x");
    }
        
    public FunctionTreeTextField(FunctionTreeRoot ftr){
        initEventListening();
        f=ftr;
    }
        
    public FunctionTreeTextField(String text,String var){
        initEventListening();
        setText(text);
    }
    
    public void initEventListening(){
        getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){updateFunctionTree();}
            public void removeUpdate(DocumentEvent e){updateFunctionTree();}
            public void changedUpdate(DocumentEvent e){updateFunctionTree();}
        });
    }
    
    public void updateFunctionTree(){                                          
        try{
            FunctionTreeNode temp=Parser.parseExpression(getText());
            if(temp instanceof ArgumentList){throw new FunctionSyntaxException(FunctionSyntaxException.INCOMPLETE_INPUT);}
            f.setArgumentNode(temp);
            setForeground(Color.BLACK);
            fireStateChanged();
        }catch(FunctionSyntaxException e){
            setForeground(Color.RED);
            System.out.println(e.getMessage()+" for string "+getText());
        }        
    }
    
    
    // BEAN PATTERNS
    
    public String getLabel(){return "f(?)";}
    public FunctionTreeRoot getF(){return f;}
    public void setF(FunctionTreeRoot newValue){
        if(!f.equals(newValue)){
            f=newValue;
        }
    }    
    
    
    // EVENT HANDLING
    
    /** Event handling code copied from DefaultBoundedRangeModel. */      
    protected ChangeEvent changeEvent=new ChangeEvent("Plottable");
    protected EventListenerList listenerList=new EventListenerList();    
    public void addChangeListener(ChangeListener l){listenerList.add(ChangeListener.class,l);}
    public void removeChangeListener(ChangeListener l){listenerList.remove(ChangeListener.class,l);}
    protected void fireStateChanged(){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ChangeListener.class){
                if(changeEvent==null){return;}
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }
        }
    }
}
