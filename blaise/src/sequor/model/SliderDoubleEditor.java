/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequor.model;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author ae3263
 */
public class SliderDoubleEditor extends DefaultBoundedRangeModel implements ChangeListener {    
    DoubleRangeModel sourceModel; //the real model
    protected double stepSize=1;
        
    /**
     * Creates a SpinnerIntegerEditor that gets its state from sourceModel.
     */
    public SliderDoubleEditor(){initializeModels();}
    public SliderDoubleEditor(DoubleRangeModel sourceModel){initializeModels(sourceModel);}
    public SliderDoubleEditor(double value,double min,double max,double stepSize){initializeModels(new DoubleRangeModel(value,min,max,stepSize));}
    
    // initializes given a model
    public void initializeModels(DoubleRangeModel model){
        sourceModel=model;
        sourceModel.addChangeListener(this);
        stepSize=sourceModel.getStep();
    }
    
    // initialize underlying models
    public void initializeModels(){
        if(sourceModel==null){sourceModel=new DoubleRangeModel();}
        sourceModel.addChangeListener(this);
    }
    
    // getters & setters
    public double getMax(){return sourceModel.getMaximum();}
    public void setMaximum(double newMaximum){sourceModel.setMaximum(newMaximum);}
    
    public double getMin(){return sourceModel.getMinimum();}
    public void setMinimum(double newMinimum){sourceModel.setMinimum(newMinimum);}

    public void setRangeProperties(int newValue,int newMin,int newMax){
        sourceModel.setRangeProperties(newValue,newMin,newMax);
    }
        
    //The only method in the ChangeListener interface...
    public void stateChanged(ChangeEvent e){fireStateChanged();}
}
