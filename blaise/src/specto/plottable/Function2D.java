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
    
    // RETURNS POINT ON THE FUNCTION AT A GIVEN X VALUE
    public R2 getFunctionPoint(Double input) throws FunctionValueException{
        return new R2(input,function.getValue(input));
    }
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g,RangeTimer t){
        try {
            computePath();
            super.paintComponent(g, t);
        } catch (FunctionValueException ex) {}
    }
    
    @Override
    public void paintComponent(Graphics2D g) {
        try{
            computePath();
            super.paintComponent(g);
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
}
