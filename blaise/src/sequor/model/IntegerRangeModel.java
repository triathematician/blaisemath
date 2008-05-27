/* IntegerRangeModel.java
 * Created on February 26, 2007
 */
         
package sequor.model;

import javax.swing.event.ChangeEvent;
import sequor.FiresChangeEvents;
import java.util.Vector;
import javax.swing.event.ChangeListener;

/**
 * Based on the source code for DefaultBoundedRangeModel 
 * @author Elisha Peterson
 */

public class IntegerRangeModel extends BoundedRangeModel<Integer> {
    
    
    // CONSTRUCTORS
    
    public IntegerRangeModel(){super(0,-10,10,1);}
    public IntegerRangeModel(int newValue,int newMin,int newMax){super(newValue,newMin,newMax,1);}
    public IntegerRangeModel(int newValue,int newMin,int newMax,int step){super(newValue,newMin,newMax,step);}
    
    
    // GETTERS & SETTERS
    
    @Override
    public boolean setStep(Integer step){
        step=Math.abs(step);
        if(step<getRange() && ((this.step==null) || !this.step.equals(step))){
            this.step=step;
            return true;
        }
        return false;
    }
    @Override
    public void doubleStep(){setStep(2*step);}
    @Override
    public void halfStep(){setStep(step>=2?step/2:1);}
    @Override
    public int getStepNumber() {
        return (int)((value-minimum)/(double)step);
    }
    @Override
    public void setValue(String s) {setValue(Integer.valueOf(s));}
    /** Sets value as a percentage of length. */
    @Override
    public void setValuePercent(double percent){
        setValue((int)Math.round(minimum+percent*(maximum-minimum)));
    }
    @Override
    public Integer getRange(){
        if (maximum.equals(Integer.MAX_VALUE) || minimum.equals(-Integer.MAX_VALUE)){
            return Integer.MAX_VALUE;
        }
        return (maximum-minimum);
    }

        
    // MORE ADVANCED METHODS
    
    /** Increments the current value.
     * @param loop whether to shift the value back to the beginning if outside the range of values.
     * @return false if the value would be greater than the maximum in range, or if it loops; otherwise true
     */
    public boolean increment(boolean loop,int n){
        if(value+n*step>maximum){
            if(loop){setValue(minimum);
            }else{setValue(maximum);}
            return false;
        }else if(value+n*step<minimum){
            if(loop){setValue(maximum);
            }else{setValue(minimum);}
            return false;
        }else{
            setValue(value+n*step);
            return true;
        }
    }
        
    /* Gets at the range of values contained in this data model.
     * @param inclusive whether the vector includes the endpoints or not
     * @param shift usually 0, this specifies whether to shift the entire range of values or not
     * @return vector containing all elements in the range of values.
     */
    @Override
    public Vector<Integer> getValueRange(boolean inclusive,Integer shift){
        Vector<Integer> result=new Vector<Integer>();
        if(inclusive){result.add(minimum-shift*step);}else{result.add(minimum+step/2-shift*step);}
        while(result.lastElement()<=(maximum-step-shift*step)){result.add(result.lastElement()+step);}
        return result;
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
    public int getNumSteps(){return (int)Math.round((maximum-minimum)/(double)step);}
    

    // METHODS FROM FiresChangeEvents
    
    @Override
    public FiresChangeEvents clone(){return new IntegerRangeModel(value,minimum,maximum,step);}

    // CREATE new models to use for min/max adjustments
    
    @Override
    public BoundedRangeModel getMinModel() {
        final IntegerRangeModel result = new IntegerRangeModel(minimum, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1);
        result.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                setMinimum(result.getValue());
            }
        });
        return result;
    }

    @Override
    public BoundedRangeModel getMaxModel() {
        final IntegerRangeModel result = new IntegerRangeModel(maximum, -Integer.MAX_VALUE, Integer.MAX_VALUE, 1);
        result.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                setMaximum(result.getValue());
            }
        });
        return result;
    }
}