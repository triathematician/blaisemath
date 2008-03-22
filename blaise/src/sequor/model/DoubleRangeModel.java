package sequor.model;

import sequor.FiresChangeEvents;
import java.util.Vector;

/**
 * <b>DoubleRangeModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 26, 2007, 11:57 AM</i><br><br>
 *
 * Based on the source code for DefaultBoundedRangeModel (borrowed from the Sun
 * Java website), this class stores its value as a double, rather than an int.
 **/

public class DoubleRangeModel extends BoundedRangeModel<Double> {
    
    /** Default initializer. */
    public DoubleRangeModel(){super(0.0,-1.0,1.0,0.1);}
    /** Initializes with particular values. The stepsize is by default 0.1.
     * @param newValue the current value of the model
     * @param newMin the minimum value possible
     * @param newMax the maximum value possible
     */
    public DoubleRangeModel(double newValue,double newMin,double newMax){super(newValue,newMin,newMax,0.1);}
    /** Initializes with particular values.
     * @param newValue the current value of the model
     * @param newMin the minimum value possible
     * @param newMax the maximum value possible
     * @param step the increment size
     */
    public DoubleRangeModel(double newValue,double newMin,double newMax,double step){super(newValue,newMin,newMax,step);}
    
    
    // GETTERS & SETTERS
    
    @Override
    public boolean setStep(Double step){
        if(step<getRange()&& !this.step.equals(step)){
            this.step=step;
            return true;
        }
        return false;
    }
    @Override
    public void doubleStep(){setStep(2*step);}
    @Override
    public void halfStep(){setStep(step/2);}
    @Override
    public int getStepNumber() {
        return (int)((value-minimum)/(double)step);
    }
    @Override
    public void setValue(String s) {setValue(Double.valueOf(s));}
    /** Sets value as a percentage of length. */
    @Override
    public void setValuePercent(double percent){
        setValue(minimum+percent*(maximum-minimum));
    }
    @Override
    public Double getRange(){return (maximum-minimum);}
    
    
    // MORE ADVANCED METHODS
    
    /** Increments the current value.
     * @param loop whether to shift the value back to the beginning if outside the range of values.
     * @return false if the value would be greater than the maximum in range, or if it loops; otherwise true
     */
    public boolean increment(boolean loop, int n){
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
    
    /** Returns list of doubles specified by the current min, max, and step size.
     * @param inclusive whether to include the endpoints in the range; if not, starts at minimum+step/2
     * @return Vector of Double's containing the values
     */
    public Vector<Double> getValueRange(boolean inclusive,Double shift){
        Vector<Double> result=new Vector<Double>();
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
    public int getNumSteps(){return (int)Math.round((maximum-minimum)/step);}
        
    @Override
    public FiresChangeEvents clone(){return new DoubleRangeModel(value,minimum,maximum,step);}
}