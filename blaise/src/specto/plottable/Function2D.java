/*
 * Function2D.java
 * Created on Sep 27, 2007, 12:35:22 PM
 */

package specto.plottable;

import java.awt.Color;
import javax.swing.event.ChangeEvent;
import specto.dynamicplottable.*;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import sequor.component.RangeTimer;
import scio.coordinate.R2;
import scio.function.FunctionValueException;
import sequor.model.FunctionTreeModel;
import specto.visometry.Euclidean2;

/**
 * Draws a one-input/one-output function on the Cartesian Plane. Requires a function to work.
 * 
 * @author Elisha Peterson
 */
public class Function2D extends PointSet2D{
    boolean swapXY=false;
    Function<Double,Double> function;
        
    public Function2D(Euclidean2 vis,Function<Double,Double> function){super(vis);this.function=function;}
    public Function2D(Euclidean2 vis){this(vis,new FunctionTreeModel());}
    public Function2D(Euclidean2 vis,FunctionTreeModel ftm, Color color) {this(vis,ftm);setColor(color);}
    public Function2D(Euclidean2 vis,FunctionTreeModel functionModel){
        super(vis);
        function=functionModel.getRoot();
        functionModel.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                fireStateChanged();
            }
        });
    }
    
    @Override
    public void paintComponent(Graphics2D g,RangeTimer t){
        try {
            g.setColor(color);
            g.setStroke(stroke);
            computePath();
            switch (style) {
                case CONTINUOUS:
                    super.paintComponent(g, t);
                    break;
                case POLYGONAL:
                    break;
                case BARS:
                    drawBars(g);
                    break;
                case CBARS:
                    super.paintComponent(g, t);
                    drawBars(g);
                    break;
            }
        } catch (FunctionValueException ex) {}
    }
    
    @Override
    public void paintComponent(Graphics2D g) {
        try{
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
        }catch(FunctionValueException e){}
    }
    public void computePath() throws FunctionValueException {
        Vector<Double> xValues=visometry.getXRange();
        points.clear();
        Vector<Double> yValues=function.getValue(xValues);
        if(swapXY){Vector<Double> temp=xValues;xValues=yValues;yValues=temp;}
        for(int i=0;i<xValues.size();i++){
            points.add(new R2(xValues.get(i),yValues.get(i)));
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
    
    private static int style=CONTINUOUS;
}
