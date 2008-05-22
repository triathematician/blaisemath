/*
 * BooleanModel.java
 * Created on Mar 15, 2008
 */

package sequor.model;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JCheckBox;
import sequor.FiresChangeEvents;

/**
 * BooleanModel is a data model for a boolean variable.
 * 
 * @author Elisha Peterson
 */
public class BooleanModel extends FiresChangeEvents {
    Boolean value;
    
    public BooleanModel(boolean value){
        this.value=value;
    }

    @Override
    public FiresChangeEvents clone() {return new BooleanModel(value);}
    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {
        if(parent instanceof BooleanModel){
            setValue(((BooleanModel)parent).isTrue());
        }
    }
    
    public boolean isTrue(){return value;}
    public Boolean getValue(){return value;}
    public void setValue(Boolean newValue){
        if(newValue!=value){
            value=newValue;
            fireStateChanged();
        }
    }
    public void toggleValue(){setValue(!value);}

    @Override
    public void setValue(String s) {setValue(Boolean.valueOf(s));}
    @Override
    public PropertyChangeEvent getChangeEvent(String s) {return new PropertyChangeEvent(this,s,null,value);}
    
    /** Returns listener which can be used to set the model to "true" */
    public ActionListener getTrueListener(){
        return new ActionListener(){public void actionPerformed(ActionEvent e) {setValue(true);}};
    }
    /** Returns listener which can be used to set the model to "false" */
    public ActionListener getFalseListener(){
        return new ActionListener(){public void actionPerformed(ActionEvent e) {setValue(false);}};
    }
    /** Returns listener which can be used to toggle the value of the model */
    public ActionListener getToggleListener(){
        return new ActionListener(){public void actionPerformed(ActionEvent e) {toggleValue();}};
    }
    
    /** Returns check box controlled by this model. */
    public JCheckBox getCheckBox() { return new MyCheckBox(this); }
    
    
    // Check box for the model
    
    public static class MyCheckBox extends JCheckBox {  
        BooleanModel bm;
        
        public MyCheckBox(BooleanModel bm) {
            this.bm = bm;
            super.setSelected(bm.isTrue());
            addActionListener(bm.getToggleListener());
        }

        @Override
        public void setSelected(boolean b) { bm.setValue(b); }
        @Override
        public boolean isSelected() { return bm.isTrue(); }
    }
}
