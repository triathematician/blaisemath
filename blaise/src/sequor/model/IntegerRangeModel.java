/* IntegerRangeModel.java
 * Created on February 26, 2007
 */
         
package sequor.model;

import java.beans.PropertyChangeEvent;
import java.util.Vector;

/**
 * Based on the source code for DefaultBoundedRangeModel 
 * @author Elisha Peterson
 */

public class IntegerRangeModel extends BoundedRangeModel<Integer> {
    
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
    
    @Override
    public String toString(){return Integer.toString(value);}
    @Override
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
    
    
    // MORE ADVANCED METHODS
    
    public Vector<Integer> getValueRange(boolean inclusive,Integer shift){
        Vector<Integer> result=new Vector<Integer>();
        if(inclusive){result.add(minimum-shift*step);}else{result.add(minimum+step/2-shift*step);}
        while(result.lastElement()<=(maximum-step-shift*step)){result.add(result.lastElement()+step);}
        return result;
    }
    
    /** Increments the current value.
     * @param loop whether to shift the value back to the beginning if outside the range of values.
     * @return false if the value would be greater than the maximum in range, or if it loops; otherwise true
     */
    public boolean increment(boolean loop){
        if(value+step>maximum){
            if(loop){setValue(minimum);
            }else{setValue(maximum);}
            return false;
        }else{
            setValue(value+step);
            return true;
        }
    }
    
    /** This sets the step size based on the current endpoints and a desired number of "sample points"
     * @param numSteps the desired number of points
     * @param inclusive whether the endpoints should be included or not
     */
    public void setNumSteps(int numSteps,boolean inclusive){
        if(inclusive){
            if(numSteps<2){return;}
            setStep(getRange()/(numSteps-1));
        }else{
            if(numSteps<1){return;}
            setStep(getRange()/numSteps);
        }
    }
    public int getNumSteps(){return (int)((maximum-minimum)/step);}
    
    // IMPLEMENTING ABSTRACT METHODS FROM FIRESCHANGEEVENTS
    
    @Override
    public PropertyChangeEvent getChangeEvent(String s){return new PropertyChangeEvent(this,s,null,getValue());}    
    @Override
    public FiresChangeEvents clone(){return new IntegerRangeModel(value,minimum,maximum,step);}
    @Override
    public void copyValuesFrom(FiresChangeEvents parent){setValue(((IntegerRangeModel)parent).value);}
}