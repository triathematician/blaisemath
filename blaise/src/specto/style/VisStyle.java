/*
 * VisStyle.java
 * Created on Feb 20, 2008
 */

package specto.style;

import sequor.model.StringRangeModel;

/**
 * Contains a list of style parameters for general use; automatically generates menus and comboboxes for selection.
 * Also defines several static methods.
 * 
 * @author Elisha Peterson
 */
public class VisStyle extends StringRangeModel{
    public VisStyle(){super();}
    public VisStyle(String[] s,int newValue,int newMin,int newMax){super(s,newValue,newMin,newMax);}
    public void cycleStyle(){
        if(getValue()==getMaximum()){
            setValue(getMinimum());
        }else{
            setValue(getValue()+1);
        }
    }
}
