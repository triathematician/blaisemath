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
public class SliderIntegerEditor extends DefaultBoundedRangeModel implements ChangeListener {    
    IntegerRangeModel sourceModel; //the real model
        
    /**
     * Creates a SpinnerIntegerEditor that gets its state from sourceModel.
     */
    public SliderIntegerEditor(){initializeModels();}
    public SliderIntegerEditor(IntegerRangeModel sourceModel){initializeModels(sourceModel);}
    public SliderIntegerEditor(int value,int min,int max){initializeModels(new IntegerRangeModel(value,min,max));}
    
    // initializes given a model
    public void initializeModels(IntegerRangeModel model){
        sourceModel=model;
        sourceModel.addChangeListener(this);
    }
    
    // initialize underlying models
    public void initializeModels(){
        if(sourceModel==null){sourceModel=new IntegerRangeModel();}
        sourceModel.addChangeListener(this);
    }
    
    // getters & setters
    public int getIntegerMax(){return sourceModel.getMaximum();}
    public void setMaximum(int newMaximum){sourceModel.setMaximum(newMaximum);}
    
    public int getIntegerMin(){return sourceModel.getMinimum();}
    public void setMinimum(int newMinimum){sourceModel.setMinimum(newMinimum);}
    
    public int getIntegerValue(){return sourceModel.getValue();}
    public void setValue(int newValue){sourceModel.setValue(newValue);}
    
    public void setRangeProperties(int newValue,int newMin,int newMax){
        sourceModel.setRangeProperties(newValue,newMin,newMax);
    }
            
    //The only method in the ChangeListener interface...
    public void stateChanged(ChangeEvent e){fireStateChanged();}
}
