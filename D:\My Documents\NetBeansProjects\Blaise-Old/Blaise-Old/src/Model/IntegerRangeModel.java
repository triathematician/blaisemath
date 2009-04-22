package Model;

import Interface.BModel;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;

/**
 * <b>DoubleRangeModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 26, 2007, 11:57 AM</i><br><br>
 *
 * Based on the source code for DefaultBoundedRangeModel (borrowed from the Sun
 * Java website), this class stores its value as a double, rather than an int.
 **/

public class IntegerRangeModel extends BModel implements Serializable {
    
    private int maximum = 10;
    private int minimum = -10;
    private int value = 0;
    private int step = 1;
    
    // Components required for the bean
    public IntegerRangeModel(){}
    public IntegerRangeModel(int newValue,int newMin,int newMax){
        setRangeProperties(newValue,newMin,newMax);
    }
    public IntegerRangeModel(int newValue,int newMin,int newMax,int step){
        setRangeProperties(newValue,newMin,newMax);
        setStep(step);
    }
    
    // required getters & setters
    public int getMaximum(){return maximum;}
    public void setMaximum(int newMaximum){setRangeProperties(value,minimum,newMaximum);}
    public int getMinimum(){return minimum;}
    public void setMinimum(int newMinimum){setRangeProperties(value,newMinimum,maximum);}
    public int getValue(){return value;}
    public void setValue(int newValue){setRangeProperties(newValue,minimum,maximum);}
    public void setValue(String s){setValue(Integer.valueOf(s));}
    public int getStep(){return step;}
    public void setStep(int newValue){step=newValue;}
    
    public String toString(){return Integer.toString(value);}
    public String toLongString(){return ""+minimum+"<="+value+"<="+maximum;}
    
    public int getRange(){return (maximum-minimum);}
    
    /**
     * Routine which changes the values stored by the model. All other routines
     * which adjust values must change this.
     */
    public void setRangeProperties(int newValue,int newMin,int newMax){
        // adjust values
        if(newMax<newMin){int temp=newMin; newMin=newMax; newMax=temp;}
        if(newValue>newMax){newValue=newMax;}
        if(newValue<newMin){newValue=newMin;}
        
        // determine if changes are required
        boolean changeMin=(newMin!=minimum);
        boolean changeMax=(newMax!=maximum);
        boolean changeValue=(newValue!=value);
        if(changeMin){minimum=newMin;}
        if(changeMax){maximum=newMax;}
        if(changeValue){value=newValue;}
        if(changeMin||changeMax||changeValue){fireStateChanged();}
    }
    
    public PropertyChangeEvent getChangeEvent(String s){return new PropertyChangeEvent(this,s,null,getValue());}
}