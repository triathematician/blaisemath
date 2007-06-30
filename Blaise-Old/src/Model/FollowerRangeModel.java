package Model;

import Interface.BModel;
import javax.swing.BoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <b>FollowerRangeModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 26, 2007, 1:00 PM</i><br><br>
 *
 * Codes a range model for use by a slider or another GUI element. Its values
 *   are stored in the sourceModel. This also handles the conversion between
 *   integer values and the underlying Cartesian (double) values.<br><br>
 *
 * If the data is represented graphically in a plot window, then the multiplier
 *   may be negative for the y-value. The conversion is stored by adjusting the
 *   stored minimum value and the multiplier. In particular,<br><br>
 *
 *        sourceValue = (integerValue-minimum)/multiplier + sourceMinimum <br><br>
 *
 * The value is not actually stored in this class. The multiplier is changed
 *   whenever the value in the underlying model is changed.
 */
public class FollowerRangeModel extends BModel implements BoundedRangeModel,ChangeListener {
    private DoubleRangeModel sourceModel; //the real model
    
    private int maximum = 1000;
    private int minimum = 0;
    private int extent = 0;
    private double multiplier = 1.0;
    private double xMultiplier = 1.0;
    private boolean isAdjusting = false;
    
    /** Creates a FollowerRangeModel that gets its state from sourceModel. */
    public FollowerRangeModel(DoubleRangeModel sourceModel) {
        this.sourceModel = sourceModel;
        sourceModel.addChangeListener(this);
    }
    
    public double getDoubleMax(){return sourceModel.getMaximum();}
    public void setDoubleMax(double newMaximum){sourceModel.setMaximum(newMaximum);}
    
    public double getDoubleMin(){return sourceModel.getMinimum();}
    public void setDoubleMin(double newMinimum){sourceModel.setMinimum(newMinimum);}
    
    public double getDoubleValue(){return sourceModel.getValue();}
    public void setDoubleValue(double newValue){sourceModel.setValue(newValue);}
    
    public void setRangeProperties(double newValue,double newMin,double newMax){
        sourceModel.setRangeProperties(newValue,newMin,newMax);
    }
    
    // the following routines require conversion to integer range 0-1000, done using the multiplier
    public double getMultiplier(){return multiplier;}
    //System.out.println("multiplier:"+multiplier);
    public double getXMultiplier(){return xMultiplier;}
    public void setXMultiplier(double xm){xMultiplier=xm;}
    public int getMaximum(){return maximum;}
    public void setMaximum(int newMaximum){maximum=newMaximum;}
    public int getMinimum(){return minimum;}
    public void setMinimum(int newMinimum){minimum=newMinimum;}
    public int getValue(){return(int)((sourceModel.getValue()-sourceModel.getMinimum())*getMultiplier()+getMinimum());}
    public void setValue(int newValue){sourceModel.setValue((newValue-getMinimum())/getMultiplier()+sourceModel.getMinimum());}
    public void setFlipValue(int newFlipValue){setValue(getMaximum()-newFlipValue);}
    public int getExtent(){return extent;}
    public void setExtent(int newExtent){extent=newExtent;}
    public boolean getValueIsAdjusting(){return isAdjusting;}
    public void setValueIsAdjusting(boolean b){isAdjusting=b;}
    
    public void setValue(String s){sourceModel.setValue(s);}
    public String getString(){return sourceModel.getString();}
    public String getLongString(){return ""+minimum+"<="+getValue()+"<="+maximum+"->"+sourceModel.getLongString();}
    
    //The only method in the ChangeListener interface... updates multiplier
    public void stateChanged(ChangeEvent e){
        multiplier=(double)(maximum-minimum)/(double)sourceModel.getRange();
        //System.out.println("max"+maximum+" min"+minimum+" range"+sourceModel.getRange());
        fireStateChanged();
    }
    
    public void setRangeProperties(int value,int extent,int min,int max,boolean adjusting){
        sourceModel.setRangeProperties(value/getMultiplier()+sourceModel.getMinimum(),
                sourceModel.getMinimum(),sourceModel.getMaximum());
    }
}
