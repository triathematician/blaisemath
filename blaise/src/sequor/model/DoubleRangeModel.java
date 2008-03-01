package sequor.model;

import java.beans.PropertyChangeEvent;
import java.util.Vector;
import javax.swing.BoundedRangeModel;

/**
 * <b>DoubleRangeModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 26, 2007, 11:57 AM</i><br><br>
 *
 * Based on the source code for DefaultBoundedRangeModel (borrowed from the Sun
 * Java website), this class stores its value as a double, rather than an int.
 **/

public class DoubleRangeModel extends FiresChangeEvents {
    
    private double maximum = 1.0;
    private double minimum = -1.0;
    private double value = 0.0;
    private double step = 0.1;
    
    // Components required for the bean
    public DoubleRangeModel(){}
    public DoubleRangeModel(double newValue,double newMin,double newMax){
        setRangeProperties(newValue,newMin,newMax);
    }
    public DoubleRangeModel(double newValue,double newMin,double newMax,double step){
        setRangeProperties(newValue,newMin,newMax);
        setStep(step);
    }
    
    // required getters & setters
    public double getMaximum(){return maximum;}
    public void setMaximum(double newMaximum){setRangeProperties(value,minimum,newMaximum);}
    public double getMinimum(){return minimum;}
    public void setMinimum(double newMinimum){setRangeProperties(value,newMinimum,maximum);}
    public double getValue(){return value;}
    public void setValue(double newValue){setRangeProperties(newValue,minimum,maximum);}
    public void setValue(String s){setValue(Double.valueOf(s));}
    public double getStep(){return step;}
    public void setStep(double newValue){step=newValue;}
    public String toString(){return Double.toString(value);}
    public String toLongString(){return ""+minimum+"<="+value+"<="+maximum;}
    
    // MORE ADVANCED METHODS
    
    /** Returns the range of values. */
    public Double getRange(){return maximum-minimum;}
    /** Returns list of doubles specified by the current min, max, and step size.
     * @param inclusive whether to include the endpoints in the range; if not, starts at minimum+step/2
     * @return Vector of Double's containing the values
     */
    public Vector<Double> getValueRange(boolean inclusive,double shift){
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
    
    /**
     * Routine which changes the values stored by the model. All other routines
     * which adjust values must change this.
     */
    public void setRangeProperties(double newValue,double newMin,double newMax){
        // adjust values
        if(newMax<newMin){double temp=newMin; newMin=newMax; newMax=temp;}
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
    
    @Override
    public FiresChangeEvents clone(){return new DoubleRangeModel(value,minimum,maximum,step);}
    @Override
    public void copyValuesFrom(FiresChangeEvents parent){setValue(((DoubleRangeModel)parent).value);}
}