/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequor.editor;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.event.ChangeEvent;
import scribo.parser.*;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import scribo.tree.FunctionTreeRoot;
import sequor.model.FunctionTreeModel;

/**
 * This class is a text field synchronized with an underlying function tree. The FunctionTreeModel contains the
 * underlying tree, while this class interoperates with the actual text field.
 * 
 * @author Elisha Peterson
 */
public class FunctionTextField extends JTextField implements ChangeListener {
    
    FunctionTreeModel ftm;
    
    public FunctionTextField(){this(new FunctionTreeModel());}   
    public FunctionTextField(String text,String var){this(new FunctionTreeModel(text,var));}
    public FunctionTextField(FunctionTreeModel ftm){
        this.ftm=ftm;
        initEventListening();
        setMinimumSize(new Dimension(50, 20));
        setMaximumSize(new Dimension(200, 25));
    }
    
    public void initEventListening(){
        setText(ftm.getRoot().argumentString());
        getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e){ftm.setValue(getText());}
            @Override
            public void removeUpdate(DocumentEvent e){ftm.setValue(getText());}
            @Override
            public void changedUpdate(DocumentEvent e){ftm.setValue(getText());}
        });
        ftm.addChangeListener(this);
    }

    // BEAN PATTERNS    

    public FunctionTreeModel getFunctionTreeModel() { return ftm; }
    public void setFunctionTreeModel(FunctionTreeModel ftm) { this.ftm = ftm; }
    
    /** Calls up the function corresponding to the underlying text tree. */
    public FunctionTreeRoot getF(){return ftm.getRoot();}
    
    // EVENT HANDLING
    
    /** If the underlying tree compiles correctly, set the text color to red; otherwise to black. */
    @Override
    public void stateChanged(ChangeEvent e) {
        setForeground(ftm.isValid()?Color.BLACK:Color.RED);
    }
    
}
