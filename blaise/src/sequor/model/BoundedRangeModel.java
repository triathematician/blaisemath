/**
 * BoundedRangeModel.java
 * Created on Mar 3, 2008
 */

package sequor.model;

import java.beans.PropertyChangeEvent;
import java.text.NumberFormat;
import sequor.FiresChangeEvents;
import java.util.Vector;
import javax.swing.event.ChangeEvent;

/**
 * Abstract class for models which have the ability to increment their value and get/set the number of steps. These should eventually be compatible with
 * the RangeTimer method.
 * @author Elisha Peterson
 */
public abstract class BoundedRangeModel<N extends Number> extends FiresChangeEvents {    
    
    // VALUES
    
    protected N minimum;
    protected N value;
    protected N maximum;
    protected N step;  
    
    
    // BASIC CONSTRUCTOR
    
    public BoundedRangeModel(N value,N minimum,N maximum,N step){
        this.minimum=minimum;
        this.maximum=maximum;
        this.value=value;
        this.step=step;
    }
    
    
    // PRIMARY GETTERS AND SETTERS
    
    public N getMaximum(){return maximum;}
    public N getValue(){return value;}
    public N getMinimum(){return minimum;}
    public N getStep(){return step;}
    
    public void setMaximum(N maximum){setRangeProperties(value,minimum,maximum,step);}
    public void setValue(N value){setRangeProperties(value,minimum,maximum,step);}
    public void setMinimum(N minimum){setRangeProperties(value,minimum,maximum,step);}
    public abstract boolean setStep(N step);   
    
    
    // ADVANCED PROPERTY SETTINGS
    
    public void setRangeProperties(N newValue,N newMin,N newMax){setRangeProperties(newValue,newMin,newMax,step);}
    public void setRangeProperties(N newValue,N newMin,N newMax,N newStep){
        Comparable cValue=(Comparable)newValue;
        Comparable cMin=(Comparable)newMin;
        Comparable cMax=(Comparable)newMax;
        // adjust values
        if(cMax.compareTo(cMin)<0){N temp=newMin; newMin=newMax; newMax=temp;}
        if(cValue.compareTo(cMax)>0){newValue=newMax;}
        if(cValue.compareTo(cMin)<0){newValue=newMin;}
        
        // determine if changes are required
        boolean changed=false;
        if(!newMin.equals(minimum)){minimum=newMin;changed=true;}
        if(!newMax.equals(maximum)){maximum=newMax;changed=true;}
        if(!newValue.equals(value)){value=newValue;changed=true;}
        if(setStep(newStep)){changed=true;}
        if(changed){changeEvent=new ChangeEvent(this);fireStateChanged();}
    }
    
    // ABSTRACT GETTERS & SETTERS
     
    public abstract void setNumSteps(int numSteps,boolean inclusive);
    public boolean increment(boolean loop){return increment(loop,1);}
    public abstract boolean increment(boolean loop,int n);
    public abstract void setValuePercent(double percent);
    public abstract void doubleStep();
    public abstract void halfStep();
    
    /** Returns the size of the possible range. */
    public abstract N getRange();
    /** Returns current position in terms of (approximate) number of steps from the beginning of the range.*/
    public abstract int getStepNumber();
    /** Returns the number of steps in the entire range. */
    public abstract int getNumSteps();    
    /** Returns closest percent to the specified percent. */
    public double closestPercent(double percent){
        return ((int)Math.round(percent*getNumSteps()))/(double)getNumSteps();
    }
    /** Returns how far along the range of values the current value is, as a number between 0 and 1. */
    public double getValuePercent(){
        return getStepNumber()/(double)getNumSteps();
    }

    /* Gets at the range of values contained in this data model.
     * @param inclusive whether the vector includes the endpoints or not
     * @param shift usually 0, this specifies whether to shift the entire range of values or not
     * @return vector containing all elements in the range of values.
     */
    public abstract Vector<N> getValueRange(boolean inclusive,N shift);

    
    // METHODS FROM FiresChangeEvents
    
    @Override
    public String toString(){return NumberFormat.getInstance().format(value);}
    @Override
    public String toLongString(){return ""+minimum+"<="+value+"<="+maximum;}
    @Override
    public void copyValuesFrom(FiresChangeEvents parent) {
        BoundedRangeModel<N> brm=(BoundedRangeModel<N>)parent;
        setRangeProperties(brm.value,brm.minimum,brm.maximum,brm.step);
    }
    @Override
    public PropertyChangeEvent getChangeEvent(String s){return new PropertyChangeEvent(this,s,null,getValue());}  
}
