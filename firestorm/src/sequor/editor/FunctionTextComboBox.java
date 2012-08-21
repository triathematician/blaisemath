/*
 * FunctionTextComboBox.java
 * Created on Feb 20, 2008
 */

package sequor.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scribo.tree.FunctionTreeRoot;
import sequor.model.FunctionTreeModel;
import sequor.SettingsProperty;

/**
 * This class maintains a combo box containing a function, and maintains a history of past (valid) functions which have been computed.
 * The user must hit "Enter" to store the function in the box.
 * @author Elisha Peterson
 */
public class FunctionTextComboBox extends JComboBox implements ChangeListener {
    
    private static int MAX_FUNCTIONS=10;
    
    FunctionTreeModel ftm;
    
    public FunctionTextComboBox(){this(new FunctionTreeModel());}   
    public FunctionTextComboBox(String text,String var){this(new FunctionTreeModel(text,var));}
    public FunctionTextComboBox(FunctionTreeModel ftm){
        this.ftm=ftm;
        initEventListening();
        setMinimumSize(new Dimension(50, 20));
        setMaximumSize(new Dimension(200, 25));
    }    
    public void initEventListening(){
        setEditable(true);
        addItem(ftm.getRoot().argumentString());
        addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                if(getSelectedItem() instanceof String){
                    ftm.setValue((String)getSelectedItem());                
                }
            }
        });
        ftm.addChangeListener(this);
    }

    @Override
    public void setSelectedItem(Object anObject) {
        if(anObject instanceof String){
            super.setSelectedItem(anObject);
        }else if(anObject instanceof SettingsProperty){
            super.setSelectedItem(((SettingsProperty)anObject).getModel().toString());
        }
    }
    
    

    // BEAN PATTERNS
    
    /** Calls up the function corresponding to the underlying text tree. */
    public FunctionTreeRoot getF(){return ftm.getRoot();}
    
    // EVENT HANDLING
    
    /** Adds item to the box if not already there. */
    @Override
    public void addItem(Object o){
        if(o instanceof String){
            String s=(String)o;
            for(int i=0;i<getItemCount();i++){
                if(s.equals(getItemAt(i))){return;}
            }
            super.addItem(o);
        }
    }
    
    /** If the underlying tree compiles correctly, set the text color to red; otherwise to black. */
    @Override
    public void stateChanged(ChangeEvent e) {
        if(ftm.isValid()){
            setForeground(Color.BLACK);
            addItem(ftm.toString());
            setSelectedItem(ftm.toString());
        }else{
            setForeground(Color.RED);
        }
    }
    
        
}
