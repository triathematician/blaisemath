/*
 * FunctionSampleSet.java
 * Created on Feb 27, 2008
 */

package specto.euclidean2;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;
import scio.coordinate.R2;
import scio.function.FunctionValueException;
import sequor.model.DoubleRangeModel;
import specto.Decoration;
import specto.Plottable;
import specto.euclidean2.Function2D;
import specto.euclidean2.PointSet2D;
import specto.euclidean2.Euclidean2;

/**
 * FunctionSampleSet displays a finite set of values of a function in some way... for example, boxes for Riemann sums; sticks and dots;
 * the area under the function, etc. What makes this distinctive is that the portion of the function which is plotted must be specified.
 * 
 * @author Elisha Peterson
 */
public class FunctionSampleSet extends Plottable<Euclidean2> implements Decoration<Euclidean2,Function2D> {
    /** The underlying function. */
    Function2D parent;
    
    /** whether to sample from the left (-0.5), right (0.5), or center (0) of a Riemann sum range. */
    double rSampleShift=0.0;
    DoubleRangeModel valueRange;
    
    public FunctionSampleSet(Function2D parent){this(parent,-4.0,4.0,20);}
    public FunctionSampleSet(Function2D parent,double leftPoint,double rightPoint,int numSamples){
        this.parent=parent;
        valueRange=new DoubleRangeModel(leftPoint,leftPoint,rightPoint);
        valueRange.setNumSteps(numSamples, isInclusive());
        setColor(Color.RED);
    }

    // BEAN PATTERNS
    
    public Function2D getParent(){return parent;}
    public void setParent(Function2D parent){this.parent=parent;}
    
    public DoubleRangeModel getModel(){return valueRange;}
    
    Vector<R2> getSamplePoints() throws FunctionValueException{
        Vector<R2> result=new Vector<R2>();
        Vector<Double> inputs=valueRange.getValueRange(isInclusive(),style.getValue()==STYLE_RIEMANN_SUM?rSampleShift:0);
        Function2D fnParent=(Function2D)parent;
        for(Double d:inputs){result.add(fnParent.getFunctionPoint(d));}
        return result;
    }
    
    
    // DRAW METHODS
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        try {
            Vector<R2> samplePoints = getSamplePoints();
            double width = valueRange.getStep();
            R2 pta;
            switch(style.getValue()){
                case STYLE_DOTS:
                    v.fillDots(g,samplePoints,4);
                    break;
                case STYLE_STICKS_DOTS:
                    v.fillDots(g,samplePoints,4);
                case STYLE_STICKS:
                    g.setStroke(PointSet2D.strokes[PointSet2D.MEDIUM]);
                    for(R2 point : samplePoints){
                        g.draw(v.lineSegment(new R2(point.x,0), point));
                    }
                    break;
                case STYLE_RIEMANN_SUM:            
                    for (R2 point : samplePoints) {
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                        g.draw(v.rectangle(point.x - width / 2 + width * rSampleShift, 0.0, point.x + width / 2 + width * rSampleShift, point.y));
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                        g.fill(v.rectangle(point.x - width / 2 + width * rSampleShift, 0.0, point.x + width / 2 + width * rSampleShift, point.y));
                    }
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));  
                    break;
                case STYLE_TRAPEZOID:
                    for(int i=0;i<samplePoints.size()-1;i++){
                        pta=samplePoints.get(i);
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                        g.draw(v.trapezoid(pta.x,0.0,pta.x,pta.y,pta.x+width,samplePoints.get(i+1).y,pta.x+width,0.0));
                        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                        g.fill(v.trapezoid(pta.x,0.0,pta.x,pta.y,pta.x+width,samplePoints.get(i+1).y,pta.x+width,0.0));
                    }              
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));  
                    break;
                case STYLE_AREA:
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                    for(int i=0;i<samplePoints.size()-1;i++){
                        pta=samplePoints.get(i);
                        g.fill(v.trapezoid(pta.x,0.0,pta.x,pta.y,pta.x+width,samplePoints.get(i+1).y,pta.x+width,0.0));
                    }              
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));    
                    break;
            }
        } catch (FunctionValueException ex) {return;}
    }
    
    // STYLE PARAMETERS    
   
    public static final int STYLE_RIEMANN_SUM = 0;
    public static final int STYLE_TRAPEZOID = 1;
    public static final int STYLE_AREA = 2;
    public static final int STYLE_DOTS = 3;
    public static final int STYLE_STICKS = 4;
    public static final int STYLE_STICKS_DOTS = 5;
    
    public boolean isInclusive(){return style.getValue()!=0;}

    final static String[] styleStrings={"Riemann Sums","Trapezoids","Area","Dots","Sticks","Dots & Sticks"};
    @Override
    public String[] getStyleStrings() {return styleStrings;}
    @Override
    public String toString(){return "Function Sample Set";}
}
