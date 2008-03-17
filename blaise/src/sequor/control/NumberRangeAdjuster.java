/**
 * NumberRangeAdjuster.java
 * Created on Mar 12, 2008
 */

package sequor.control;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.model.BoundedRangeModel;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;

/**
 * Adjusts four values: minimum, maximum, value, and stepsize.
 * @author Elisha Peterson
 */
public class NumberRangeAdjuster extends VisualControlGroup{
    BoundedRangeModel mainModel;
    BoundedRangeModel minModel;
    BoundedRangeModel maxModel;
    BoundedRangeModel stepModel;
            
    public NumberRangeAdjuster(double x,double y,BoundedRangeModel model){this(new Point2D.Double(x,y),model);}
    public NumberRangeAdjuster(Point2D.Double position,final BoundedRangeModel model){
        super(position.x,position.y,200,50);
        mainModel=model;
        initControls();
        mainModel.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                minModel.setValue(model.getMinimum());
                maxModel.setValue(model.getMaximum());
                stepModel.setValue(model.getStep());
            }
        });
    }   
    
    @Override
    public void paintComponent(Graphics2D g){
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.1f));
        g.fill(boundingBox);
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
        add(new NumberAdjuster(0,0,80,10,NumberAdjuster.HORIZONTAL,NumberAdjuster.STYLE_LINE,mainModel));
        add(new NumberAdjuster(0,0,80,10,NumberAdjuster.VERTICAL,NumberAdjuster.STYLE_JOYSTICK,minModel));
        add(new NumberAdjuster(0,0,80,10,NumberAdjuster.VERTICAL,NumberAdjuster.STYLE_JOYSTICK,maxModel));
        add(new NumberAdjuster(0,0,80,10,NumberAdjuster.HORIZONTAL,NumberAdjuster.STYLE_JOYSTICK,stepModel));
    }
}
