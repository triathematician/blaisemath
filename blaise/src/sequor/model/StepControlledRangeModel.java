/**
 * StepControlledRangeModel.java
 * Created on Mar 18, 2008
 */

package sequor.model;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Uses "steps" to adjust the value of an underlying range model. By default has three settings for adjusting
 * the value in the parent model, by step, by 10*step, and by 100*step.
 * 
 * @author Elisha Peterson
 */
@XmlRootElement(name="stepControlledRangeModel")
public class StepControlledRangeModel extends IntegerRangeModel {
    BoundedRangeModel parent;
    
    public StepControlledRangeModel(){this(new DoubleRangeModel2());}
    public StepControlledRangeModel(final BoundedRangeModel parent){
        super(0,-3,3,1);
        this.parent=parent;     
        addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                if(getValue()<0){
                    parent.increment(false,-(int)Math.pow(10,-getValue()-1));
                }else if(getValue()>0){
                    parent.increment(false,(int)Math.pow(10,getValue()-1));
                }
            }
        });
    }
    
    public void reset(){
        setValue(0);
    }
    @Override
    public String toString(){return parent.toString();}
}
