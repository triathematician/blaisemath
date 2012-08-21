/*
 * Style.java
 * Created on Feb 20, 2008
 */

package sequor.style;

import java.awt.Graphics2D;
import sequor.model.StringRangeModel;

/**
 * <p>
 * Contains a list of style parameters for general use; automatically generates menus and comboboxes for selection.
 * Also defines several static methods.
 * </p>
 * 
 * @author Elisha Peterson
 */
public abstract class Style extends StringRangeModel {

    public Style(){
        super();
    }

    public Style(String[] s){
        super(s);
    }

    public Style(String[] s, int newValue, int newMin, int newMax){
        super(s, newValue, newMin, newMax);
    }

    public void cycleStyle(){
        if(getValue()==getMaximum()){
            setValue(getMinimum());
        }else{
            setValue(getValue()+1);
        }
    }

    /** Sets up drawing canvas for this style. */
    public abstract void apply(Graphics2D g);
}
