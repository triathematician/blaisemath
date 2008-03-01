/*
 * VisualStyle.java
 * Created on Feb 20, 2008
 */

package specto;

import sequor.model.ComboBoxRangeModel;

/**
 * Contains a list of style parameters for general use; automatically generates menus and comboboxes for selection.
 * Also defines several static methods.
 * 
 * @author Elisha Peterson
 */
public class VisualStyle extends ComboBoxRangeModel{
    public VisualStyle(){super();}
    public VisualStyle(String[] s,int newValue,int newMin,int newMax){super(s,newValue,newMin,newMax);}
    public void cycleStyle(){
        if(getValue()==getMaximum()){
            setValue(getMinimum());
        }else{
            setValue(getValue()+1);
        }
    }
}
