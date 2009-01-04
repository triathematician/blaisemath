package sequor.editor;

import sequor.model.*;
import sequor.model.DoubleRangeModel2;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <b>SpinnerDoubleEditor.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 26, 2007, 1:01 PM</i><br><br>
 * 
 * Implements a spinner whose data (other than stepsize) is based in another
 *   source model, which basically implements the data as a range.
 */
public class SpinnerDoubleEditor extends SpinnerNumberModel implements ChangeListener {
    
    DoubleRangeModel2 sourceModel; //the real model
    protected Double stepSize=0.1;
        
    /**
     * Creates a SpinnerDoubleEditor that gets its state from sourceModel.
     */
    public SpinnerDoubleEditor(){initializeModels();}
    public SpinnerDoubleEditor(DoubleRangeModel2 sourceModel){initializeModels(sourceModel);}
    public SpinnerDoubleEditor(Double value,Double min,Double max,Double stepSize){initializeModels(new DoubleRangeModel2(value,min,max,stepSize));}
    
    // initializes given a model
    public void initializeModels(DoubleRangeModel2 model){
        sourceModel=model;
        sourceModel.addChangeListener(this);
        stepSize=sourceModel.getStep();
    }
    
    // initialize underlying models
    public void initializeModels(){
        if(sourceModel==null){sourceModel=new DoubleRangeModel2();}
        sourceModel.addChangeListener(this);
    }
    
    // getters & setters
    public Double getDoubleMax(){return sourceModel.getMaximum();}
    public void setMaximum(Double newMaximum){sourceModel.setMaximum(newMaximum);}
    
    public Double getDoubleMin(){return sourceModel.getMinimum();}
    public void setMinimum(Double newMinimum){sourceModel.setMinimum(newMinimum);}
    
    public Double getDoubleValue(){return sourceModel.getValue();}
    public void setValue(Double newValue){sourceModel.setValue(newValue);}
    
    public void setRangeProperties(Double newValue,Double newMin,Double newMax){
        sourceModel.setRangeProperties(newValue,newMin,newMax);
    }
    
    // more getters & setters
    
    public Comparable getMaximum(){return getDoubleMax();}
    public Comparable getMinimum(){return getDoubleMin();}
    public Object getNextValue(){return getDoubleValue()+stepSize;}
    public Number getNumber(){return getDoubleValue();}
    public Object getPreviousValue(){return getDoubleValue()-stepSize;}
    public Number getStepSize(){return stepSize;}
    public Object getValue(){return getDoubleValue();}

    // the following routines are required for spinners...     
    public void setStepSize(Number stepSize){this.stepSize=stepSize.doubleValue();}
    public void setValue(Object value){if(value instanceof Number){setValue(((Number)value).doubleValue());}}
        
    //The only method in the ChangeListener interface...
    public void stateChanged(ChangeEvent e){fireStateChanged();}
}
