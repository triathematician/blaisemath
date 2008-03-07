/*
 * Function2D.java
 * Created on Sep 27, 2007, 12:35:22 PM
 */

package specto.plottable;

import java.awt.Color;
import javax.swing.event.ChangeEvent;
import sequor.model.PointRangeModel;
import specto.dynamicplottable.*;
import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import scio.coordinate.R2;
import scio.function.FunctionValueException;
import sequor.component.IntegerRangeTimer;
import sequor.model.FunctionTreeModel;
import specto.Constrains2D;
import specto.visometry.Euclidean2;

/**
 * Draws a one-input/one-output function on the Cartesian Plane. Requires a function to work.
 * 
 * @author Elisha Peterson
 */
public class Function2D extends PointSet2D implements Constrains2D{
    boolean swapXY=false;
    Function<Double,Double> function;
        
    public Function2D(){this(new FunctionTreeModel());}
    public Function2D(Function<Double,Double> function){this.function=function;}
    public Function2D(FunctionTreeModel ftm, Color color) {this(ftm);setColor(color);}
    public Function2D(final FunctionTreeModel functionModel){
        function=functionModel.getRoot();
        functionModel.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {function=functionModel.getRoot();fireStateChanged();}
        });
    }
    
    // BEAN PATTERNS
    
    public Function<Double,Double> getFunction(){return function;}
    
    /** Returns point on function at given x value. */
    public R2 getFunctionPoint(Double input) throws FunctionValueException{
        return new R2(input,function.getValue(input));
    }
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v,IntegerRangeTimer t){
        try {
            computePath(v);
            super.paintComponent(g,v,t);
        } catch (FunctionValueException ex) {}
    }
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        try{
            computePath(v);
            super.paintComponent(g,v);
        }catch(FunctionValueException e){}
    }
    public void computePath(Euclidean2 v) throws FunctionValueException {
        Vector<Double> xValues=v.getXRange();
        points.clear();
        Vector<Double> yValues=function.getValue(xValues);
        if(swapXY){Vector<Double> temp=xValues;xValues=yValues;yValues=temp;}
        for(int i=0;i<xValues.size();i++){
            points.add(new R2(xValues.get(i),yValues.get(i)));
        }
    }

    @Override
    public PointRangeModel getConstraintModel() {return new FunctionPointModel(function);}
    
    
    // INNER CLASSES
    class FunctionPointModel extends PointRangeModel {
        Function<Double,Double> function;

        public FunctionPointModel(Function<Double, Double> function) {this(function,0.0);}
        public FunctionPointModel(Function<Double,Double> function,double x0){
            super();
            this.function = function;
            setTo(x0,0.0);        
        }

        @Override
        public void setTo(double x0, double y0) {
            try {
                xModel.setValue(x0);
                yModel.setValue(function.getValue(x0));
            } catch (FunctionValueException ex) {
            } catch (NullPointerException ex) {}

        }        
    } // class Function2D.FunctionPointModel
}
