/*
 * Function2D.java
 * Created on Sep 27, 2007, 12:35:22 PM
 */

package specto.euclidean2;

import java.awt.AlphaComposite;
import java.awt.Color;
import javax.swing.JMenu;
import javax.swing.event.ChangeEvent;
import sequor.model.PointRangeModel;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import scio.coordinate.R2;
import scio.function.Derivative;
import scio.function.FunctionValueException;
import sequor.component.RangeTimer;
import sequor.model.FunctionTreeModel;
import sequor.style.LineStyle;
import specto.Animatable;
import specto.Constrains2D;
import specto.Decoration;

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
    public Function2D(String string) {this(new FunctionTreeModel(string));}
    public Function2D(FunctionTreeModel ftm, Color color) {this(ftm);setColor(color);}
    public Function2D(FunctionTreeModel functionModel){setFunctionModel(functionModel);}
    
    // BEAN PATTERNS
    
    public Function<Double,Double> getFunction(){return function;}
    
    /** Returns point on function at given x value. */
    public R2 getFunctionPoint(Double input) throws FunctionValueException{
        return new R2(input,function.getValue(input));
    }
    
    /** Sets function based on model. */
    public void setFunctionModel(final FunctionTreeModel functionModel){
        function = (Function<Double, Double>) functionModel.getRoot().getFunction();
        functionModel.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                function = (Function<Double, Double>) functionModel.getRoot().getFunction();
                fireStateChanged();
            }
        });
    }
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        if (function == null) { return; }
        try{
            computePath(v);
            super.paintComponent(g,v);
        }catch(FunctionValueException e){}
    }
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v,RangeTimer t){
        if (function == null) { return; }
        try {
            computePath(v);
            super.paintComponent(g,v,t);
        } catch (FunctionValueException ex) {}
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
    public PointRangeModel getConstraintModel() {
        FunctionPointModel result = new FunctionPointModel();
        addChangeListener(result);
        return result;
    }
       
    @Override
    public String toString(){return "Function";}
    
    /** Returns a point with a line through it representing the slope of the function at that point. Can also be displayed as a vector or ray
     * from the particular point.
     * @return Point2D object which can be added to a plot
     */
    public Point2D getPointSlope() {return new DerivativePoint(getConstraintModel());}
    
    
    // INNER CLASSES
    
    /** A point restricted to this function. */
    class FunctionPointModel extends PointRangeModel {
        public FunctionPointModel(){this(0.0);}
        public FunctionPointModel(double x0){setTo(x0,0.0);}

        @Override
        public void setTo(double x0, double y0) {
            try {
                xModel.setValue(x0);
                yModel.setValue(function.getValue(x0));
            } catch (FunctionValueException ex) {
            } catch (NullPointerException ex) {}

        }        
    } // class Function2D.FunctionPointModel
    
    
    /** A point on this function with ability to display derivative vector and/or tangent line. */
    class DerivativePoint extends Point2D implements Decoration<Euclidean2,Function2D>, Animatable<Euclidean2> {
        double slope;
        Segment2D vector;
        PointRangeModel endVector;
        public DerivativePoint(PointRangeModel prm){
            super(prm);
            endVector=new PointRangeModel(prm.getX(),prm.getY());
            vector=new Segment2D(prm,endVector);
            vector.style.setValue(Segment2D.LINE_VECTOR);
            vector.setEditable(false);
            vector.addChangeListener(this);
        }
        
        /** Whether this element animates. */    
        public boolean animationOn=true;
        public void setAnimationOn(boolean newValue) { animationOn=newValue; }
        public boolean isAnimationOn() { return animationOn; }
        
        @Override
        public void recompute(Euclidean2 v) {
            try {
                slope = Derivative.approximateDerivative(function, prm.getX(), v.getDrawWidth()/1000);
                endVector.setTo(getPoint().plus(new R2(1.,slope).multipliedBy(1/Math.sqrt(1+slope*slope))));
            } catch (FunctionValueException ex) {
                System.out.println("error computing derivative approximation");
            }
        }

        @Override
        public void paintComponent(Graphics2D g, Euclidean2 v) {
            vector.paintComponent(g,v);
            g.setStroke(LineStyle.STROKES[LineStyle.VERY_DOTTED]);
            g.draw(v.lineSegment(prm.getX(), 0, prm.getX(), prm.getY()));
            g.setStroke(LineStyle.STROKES[LineStyle.THIN]);
            // draw a triangle slope as well
            Shape s = v.triangle(prm.getX(), prm.getY(), prm.getX() + 1, prm.getY() + slope, prm.getX() + 1, prm.getY() );
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g.fill(s);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            g.draw(s);
            super.paintComponent(g,v);
        }
            
        @Override
        public String toString(){return "Point on Function";}

        public void setParent(Function2D parent) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public Function2D getParent() {return Function2D.this;}

        public void paintComponent(Graphics2D g, Euclidean2 v, RangeTimer t) {
            int pos=t.getCurrentIntValue();
            prm.setTo(pos>=points.size()?points.lastElement():points.get(pos));
            paintComponent(g,v);
        }

        public int getAnimatingSteps() {return Function2D.this.getAnimatingSteps();}

        @Override
        public JMenu getOptionsMenu() {
            return vector.style.appendToMenu(super.getOptionsMenu());
        }
    } // class Function2D.DerivativePoint
}
