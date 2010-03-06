/**
 * BoundedRangeSliderBox.java
 * Created on Mar 12, 2008
 */

package sequor.control;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.model.BoundedRangeModel;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.StepControlledRangeModel;

/**
 * Adjusts four values: minimum, maximum, value, and stepsize.
 * @author Elisha Peterson
 */
public class BoundedRangeSliderBox extends SliderBox {
    
    BoundedRangeModel mainModel;
    BoundedRangeModel minModel;
    BoundedRangeModel maxModel;
    BoundedRangeModel stepModel;
            
    public BoundedRangeSliderBox(Point position,BoundedRangeModel model){this(position.x,position.y,model);}
    public BoundedRangeSliderBox(int x,int y,final BoundedRangeModel model){
        super(x,y,150,40);        
        adjusterGirth=15;
        mainModel=model;
        initControls();
        initListening();
    }
    
    public void initListening(){
        minModel.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e){mainModel.setMinimum(minModel.getValue());}});
        maxModel.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e){mainModel.setMaximum(maxModel.getValue());}});
        stepModel.addChangeListener(new ChangeListener(){public void stateChanged(ChangeEvent e){mainModel.setStep(stepModel.getValue());}});
        mainModel.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                minModel.setValue(mainModel.getMinimum());
                maxModel.setValue(mainModel.getMaximum());
                stepModel.setValue(mainModel.getStep());
            }
        });
    }   
    
    @Override
    public void paintComponent(Graphics2D g){
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.1f));
        g.fill(getBounds());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        super.paintComponent(g);
    }
    
    public void initControls(){
        if(mainModel==null){return;}
        if(mainModel instanceof IntegerRangeModel){
            minModel=new IntegerRangeModel((Integer)mainModel.getMinimum(),-Integer.MAX_VALUE,Integer.MAX_VALUE);
            maxModel=new IntegerRangeModel((Integer)mainModel.getMaximum(),-Integer.MAX_VALUE,Integer.MAX_VALUE);
            stepModel=new IntegerRangeModel((Integer)mainModel.getStep(),1,Integer.MAX_VALUE);
        }else if(mainModel instanceof DoubleRangeModel){
            minModel=new DoubleRangeModel((Double)mainModel.getMinimum(),-Double.MAX_VALUE,Double.MAX_VALUE);
            maxModel=new DoubleRangeModel((Double)mainModel.getMaximum(),-Double.MAX_VALUE,Double.MAX_VALUE);
            stepModel=new DoubleRangeModel((Double)mainModel.getStep(),.001,Double.MAX_VALUE,.001);
        }
        add(new NumberSlider(0,0,mainModel));
        add(new NumberSlider(0,0,new StepControlledRangeModel(minModel)));
        add(new NumberSlider(0,0,new StepControlledRangeModel(stepModel)));
        add(new NumberSlider(0,0,new StepControlledRangeModel(maxModel)));
    }
}
