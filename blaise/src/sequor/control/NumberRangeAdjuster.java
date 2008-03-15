/**
 * NumberRangeAdjuster.java
 * Created on Mar 12, 2008
 */

package sequor.control;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
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
public class NumberRangeAdjuster<N extends Number> extends NumberAdjuster<N> {
    NumberAdjuster<N> min;
    NumberAdjuster<N> max;
    NumberAdjuster<N> step;
    
    public NumberRangeAdjuster(double x,double y,BoundedRangeModel<N> model){this(new Point2D.Double(x,y),model);}
    public NumberRangeAdjuster(Point2D.Double position,final BoundedRangeModel<N> model){
        super(position,model);
        setProperties(80,10,HORIZONTAL,STYLE_LINE);
        initControls();
        model.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                min.model.setValue(model.getMinimum());
                max.model.setValue(model.getMaximum());
                step.model.setValue(model.getStep());
            }
        });
    }   
    
    @Override
    public void paintComponent(Graphics2D g){
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.1f));
        g.fill(new Rectangle2D.Double(position.x-length/2-2*width,position.y-width,length+4*width,4*width));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        min.paintComponent(g);
        max.paintComponent(g);
        step.paintComponent(g);
        super.paintComponent(g);
    }
    
    public void initControls(){
        Point2D.Double end1=getEndpoint1();
        Point2D.Double end2=getEndpoint2();
        Point2D.Double mid=new Point2D.Double(position.x,position.y);
        if(orientation==HORIZONTAL){
            end1.x-=width;end1.y+=width;
            end2.x+=width;end2.y+=width;
            mid.y+=2*width;
        }else{
            end1.y-=width;end1.x+=width;
            end2.y+=width;end2.x+=width;
            mid.x+=2*width;
        }
        if(model instanceof IntegerRangeModel){
            min=new NumberAdjuster<N>(end1,(BoundedRangeModel<N>)new IntegerRangeModel((Integer)model.getMinimum(),-Integer.MAX_VALUE,Integer.MAX_VALUE,(Integer)model.getStep()));            
        }else if(model instanceof DoubleRangeModel){
            min=new NumberAdjuster<N>(end1,(BoundedRangeModel<N>)new DoubleRangeModel((Double)model.getMinimum(),-Double.MAX_VALUE,Double.MAX_VALUE,(Double)model.getStep()));            
        }else{
            return;
        }
        min.setProperties(3*width,width,1-orientation,STYLE_JOYSTICK);
        min.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                model.setMinimum(min.model.getValue());
                fireStateChanged();
            }
        });
        if(model instanceof IntegerRangeModel){
            max=new NumberAdjuster<N>(end2,(BoundedRangeModel<N>)new IntegerRangeModel((Integer)model.getMinimum(),-Integer.MAX_VALUE,Integer.MAX_VALUE,(Integer)model.getStep()));            
        }else if(model instanceof DoubleRangeModel){
            max=new NumberAdjuster<N>(end2,(BoundedRangeModel<N>)new DoubleRangeModel((Double)model.getMinimum(),-Double.MAX_VALUE,Double.MAX_VALUE,(Double)model.getStep()));            
        }else{
            return;
        }
        max.setProperties(3*width,width,1-orientation,STYLE_JOYSTICK);
        max.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                model.setMaximum(max.model.getValue());
                fireStateChanged();
            }
        });
        if(model instanceof IntegerRangeModel){
            step=new NumberAdjuster<N>(mid,(BoundedRangeModel<N>)new IntegerRangeModel((Integer)model.getStep(),1,Integer.MAX_VALUE,1));            
        }else if(model instanceof DoubleRangeModel){
            step=new NumberAdjuster<N>(mid,(BoundedRangeModel<N>)new DoubleRangeModel((Double)model.getStep(),0.001,Double.MAX_VALUE,0.001));            
        }else{
            return;
        }
        step.setProperties((int)(.8*length),(int)(.8*width),HORIZONTAL,STYLE_JOYSTICK);
        step.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e){
                model.setStep(step.model.getValue());
                fireStateChanged();
            }
        });
    }

    NumberAdjuster adjuster;
    
    @Override
    public boolean clicked(MouseEvent e) {
        if(min.clicked(e)){
          adjuster=min;
          return true;
        }else if(max.clicked(e)){
            adjuster=max;
            return true;
        }else if(step.clicked(e)){
            adjuster=step;
            return true;
        }
        return super.clicked(e);
    }   

    @Override
    public void mouseDragged(MouseEvent e) {
        if(adjuster!=null){
            adjuster.mouseDragged(e);
        }else{
            super.mouseDragged(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(adjuster!=null){
            adjuster.mouseReleased(e);
            adjuster=null;
        }else{
            super.mouseReleased(e);
        }
    }
}
