/*
 * FunctionSampleSet.java
 * Created on Feb 27, 2008
 */

package specto.decoration;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeListener;
import scio.coordinate.R2;
import scio.function.FunctionValueException;
import sequor.component.RangeTimer;
import sequor.model.DoubleRangeModel;
import specto.Animatable;
import specto.Decoration;
import specto.plottable.Function2D;
import specto.plottable.PointSet2D;
import specto.visometry.Euclidean2;

/**
 * FunctionSampleSet displays a finite set of values of a function in some way... for example, boxes for Riemann sums; sticks and dots;
 * the area under the function, etc. What makes this distinctive is that the portion of the function which is plotted must be specified.
 * 
 * @author Elisha Peterson
 */
public class FunctionSampleSet extends Decoration<Euclidean2> implements Animatable{
   
    /** whether range of values includes the max and min */
    boolean inclusive=false;
    /** whether to sample from the left (-0.5), right (0.5), or center (0) of a Riemann sum range. */
    double rSampleShift=0.2;
    DoubleRangeModel valueRange;
    
    public FunctionSampleSet(Function2D parent){this(parent,-4.0,4.0,20);}
    public FunctionSampleSet(Function2D parent,double leftPoint,double rightPoint,int numSamples){
        super(parent);
        valueRange=new DoubleRangeModel(leftPoint,leftPoint,rightPoint);
        valueRange.setNumSteps(numSamples, inclusive);
        setColor(Color.RED);
    }

    Vector<R2> getSamplePoints() throws FunctionValueException{
        Vector<R2> result=new Vector<R2>();
        Vector<Double> inputs=valueRange.getValueRange(inclusive,style==STYLE_RIEMANN_SUM?rSampleShift:0);
        Function2D fnParent=(Function2D)parent;
        for(Double d:inputs){result.add(fnParent.getFunctionPoint(d));}
        return result;
    }
    
    @Override
    public void paintComponent(Graphics2D g) {
        try {
            Vector<R2> samplePoints = getSamplePoints();
            double width = valueRange.getStep();
            g.setColor(color);
            if(style==STYLE_DOTS||style==STYLE_STICKS_DOTS){
                visometry.fillDots(g,samplePoints,4);
            }
            if(style==STYLE_STICKS||style==STYLE_STICKS_DOTS){    
                g.setStroke(PointSet2D.strokes[PointSet2D.MEDIUM]);
                for(R2 point : samplePoints){
                    g.draw(visometry.lineSegment(new R2(point.x,0), point));
                }
            }
            else if(style==STYLE_RIEMANN_SUM){
                for (R2 point : samplePoints) {
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g.draw(visometry.rectangle(point.x - width / 2 + width * rSampleShift, 0.0, point.x + width / 2 + width * rSampleShift, point.y));
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                    g.fill(visometry.rectangle(point.x - width / 2 + width * rSampleShift, 0.0, point.x + width / 2 + width * rSampleShift, point.y));
                }
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));  
            }
            else if(style==STYLE_TRAPEZOID){
                R2 pta;
                for(int i=0;i<samplePoints.size()-1;i++){
                    pta=samplePoints.get(i);
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                    g.draw(visometry.trapezoid(pta.x,0.0,pta.x,pta.y,pta.x+width,samplePoints.get(i+1).y,pta.x+width,0.0));
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                    g.fill(visometry.trapezoid(pta.x,0.0,pta.x,pta.y,pta.x+width,samplePoints.get(i+1).y,pta.x+width,0.0));
                }              
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));  
            }
            else if(style==STYLE_AREA){
                R2 pta;
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                for(int i=0;i<samplePoints.size()-1;i++){
                    pta=samplePoints.get(i);
                    g.fill(visometry.trapezoid(pta.x,0.0,pta.x,pta.y,pta.x+width,samplePoints.get(i+1).y,pta.x+width,0.0));
                }              
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));                  
            }
        } catch (FunctionValueException ex) {return;}
    }
    @Override
    public void paintComponent(Graphics2D g, RangeTimer t) {
        paintComponent(g);
    }
    @Override
    public JMenu getOptionsMenu() {
        JMenu result=new JMenu("Function Sample Set");
        result.add(new JMenuItem("Color",null));
        result.addSeparator();
        result.add(new JMenuItem("Riemann Sums")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(STYLE_RIEMANN_SUM);}            
        });
        result.add(new JMenuItem("Trapezoids")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(STYLE_TRAPEZOID);}            
        });
        result.add(new JMenuItem("Area")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(STYLE_AREA);}            
        });
        result.add(new JMenuItem("Dots")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(STYLE_DOTS);}            
        });
        result.add(new JMenuItem("Sticks")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(STYLE_STICKS);}            
        });
        result.add(new JMenuItem("Dots & Sticks")).addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){setStyle(STYLE_STICKS_DOTS);}            
        });
        return result;
    }
    @Override
    public void recompute() {}
    
    // STYLE PARAMETERS    
    private int style=STYLE_RIEMANN_SUM;
    
    public static final int STYLE_RIEMANN_SUM = 0;
    public static final int STYLE_TRAPEZOID = 100;
    public static final int STYLE_AREA = 101;
    public static final int STYLE_DOTS = 102;
    public static final int STYLE_STICKS = 103;
    public static final int STYLE_STICKS_DOTS = 104;
    
    public void setStyle(int newStyle){
        if(style!=newStyle){
            style=newStyle;
            if(style<100 && inclusive){inclusive=false;
            }else{inclusive=true;}
            parent.redraw();
        }
    }
}
