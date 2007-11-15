/*
 * Function2D.java
 * 
 * Created on Sep 27, 2007, 12:35:22 PM
 */

package specto.dynamicplottable;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Vector;
import scio.function.DoubleFunction;
import scio.function.Function;
import sequor.component.RangeTimer;
import scio.coordinate.R2;

/**
 * Draws a one-input/one-output function on the Cartesian Plane. Requires a function to work.
 * <br><br>
 * @author ae3263
 */
public class Function2D extends PointSet2D{
    boolean swapXY=false;
    DoubleFunction function;
    private static final DoubleFunction DEFAULT_FUNCTION=new DoubleFunction(){
        public Double getValue(Double x){return 2*Math.cos(x);}
        public Double minValue(){return -2.0;}
        public Double maxValue(){return +2.0;}
        public int getNumInputs(){return 1;}
        public Double getValue(Vector<Double> x){return getValue(x.firstElement());}
        };
        
    public Function2D(){this(DEFAULT_FUNCTION);}
    public Function2D(Function<Double,Double> function){setFunction(function);}
    public Function2D(DoubleFunction function){setFunction(function);}

    public void setFunction(final Function<Double,Double> newFunction){
        setFunction(new DoubleFunction(){
            public Double getValue(Double x){return newFunction.getValue(x);}
            public Double minValue(){return newFunction.minValue();}
            public Double maxValue(){return newFunction.maxValue();}
            public int getNumInputs(){return 1;}
            public Double getValue(Vector<Double> x){return newFunction.getValue(x.firstElement());}        
        });
    }
    public void setFunction(DoubleFunction newFunction){
        this.function=newFunction;
    }
    
    @Override
    public void paintComponent(Graphics2D g,RangeTimer t){
        g.setColor(color);
        g.setStroke(stroke);
        computePath();
        switch(style){
        case CONTINUOUS:
            super.paintComponent(g,t);
            break;
        case POLYGONAL:
            break;
        case BARS:
            drawBars(g);
            break;
        case CBARS:
            super.paintComponent(g,t);
            drawBars(g);
            break;
        }
    }
    
    @Override
    public void paintComponent(Graphics2D g) {
        g.setColor(color);
        g.setStroke(stroke);
        computePath();
        switch(style){
        case CONTINUOUS:
            super.paintComponent(g);
            break;
        case POLYGONAL:
            break;
        case BARS:
            drawBars(g);
            break;
        case CBARS:
            super.paintComponent(g);
            drawBars(g);
            break;
        }
    }
    public void computePath(){
        Vector<Double> xRange=visometry.getXRange();
        points.clear();
        for(Double d:xRange){
            if(function.getValue(d)==null){continue;}
            if(!swapXY){points.add(new R2(d,function.getValue(d)));}
            else{points.add(new R2(function.getValue(d),d));}
        }
    }
    public Vector<R2> decimatedPath(int n){
        Vector<R2> result=new Vector<R2>();
        for(int i=0;i<points.size();i++){
            if(i%n==0){result.add(points.get(i));}
        }
        return result;
    }
    public void drawBars(Graphics2D g){
        if(points.size()<2){return;}
        g.setStroke(new BasicStroke(.5f));
        Vector<R2> temp=decimatedPath(10);
        double width=5*(points.get(1).x-points.get(0).x);
        Shape r;
        for(R2 p:temp){
            r=visometry.rectangle(p.x-width,0,p.x+width,p.y);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,(float).2));
            g.fill(r);
            g.setPaintMode();
            g.draw(r);
        }
    }
        
    
    // STYLES    
    /** Displays function normally. */
    private static final int CONTINUOUS=0;
    /** Displays function as a polygonal approximation. */
    private static final int POLYGONAL=1;
    /** Displays function as a series of bars. */
    private static final int BARS=2;
    /** Displays function as a series of bars, plus the function. */
    private static final int CBARS=3;
    
    private static int style=CBARS;
}
