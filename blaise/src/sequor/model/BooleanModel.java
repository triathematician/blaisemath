/*
 * BooleanModel.java
 * Created on Mar 15, 2008
 */

package sequor.model;

import java.beans.PropertyChangeEvent;
import sequor.FiresChangeEvents;

/**
 * BooleanModel is a data model for a boolean variable.
 * 
 * @author Elisha Peterson
 */
public class BooleanModel extends FiresChangeEvents {
    boolean value;
    
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
    public void setValue(boolean newValue){
        if(newValue!=value){
            value=newValue;
            fireStateChanged();
        }
    }

    @Override
    public void setValue(String s) {setValue(Boolean.valueOf(s));}
    @Override
    public PropertyChangeEvent getChangeEvent(String s) {return new PropertyChangeEvent(this,s,null,value);}
}
