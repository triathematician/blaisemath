/*
 * Parametric2D.java
 * Created on Sep 27, 2007, 1:12:35 PM
 */

package specto.plottable;

import javax.swing.event.ChangeEvent;
import scio.function.FunctionValueException;
import sequor.model.FunctionTreeModel;
import sequor.model.PointRangeModel;
import specto.dynamicplottable.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.text.NumberFormat;
import java.util.Vector;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import scio.coordinate.R2;
import scio.function.BoundedFunction;
import scio.function.Derivative;
import sequor.component.RangeTimer;
import sequor.model.DoubleRangeModel;
import sequor.model.ParametricModel;
import specto.Animatable;
import specto.style.LineStyle;
import specto.visometry.Euclidean2;

/**
 * Draws a parametric function on the plane. In other words, it contains two functions which give the x and y coordinates
 * of the function in terms of some other parameter (frequently 't'). The properties of this class which permit it to be
 * plotted are any function from Double->(Double,Double), and min/max values of t.
 * @author ElishaPeterson
 */
public class Parametric2D extends PointSet2D {
    
    // PROPERTIES
    
    /** Function which takes in a double and returns a pair of doubles = a point in the plane. */
    Function<Double,R2> function;

    /** Range of t values. */
    DoubleRangeModel tRange;
    
    /** Defines a default function which is displayed. For now its a "Lissajous" curve */
    private static final Function<Double,R2> DEFAULT_FUNCTION=new Function<Double,R2>(){
        @Override
        public R2 getValue(Double x){return new R2(2*Math.cos(x),2*Math.sin(2*x));}
        @Override
        public Vector<R2> getValue(Vector<Double> x) {
            Vector<R2> result=new Vector<R2>(x.size());
            for(Double d:x){result.add(getValue(d));}
            return result;
        }
    };
        
    public Parametric2D(){this(DEFAULT_FUNCTION,0.0,2*Math.PI,1000);}
    public Parametric2D(String string) {this();}
    /** Constructor for use with a particular function and range of t values */
    public Parametric2D(Function<Double,R2> function,double tMin,double tMax,int samplePoints){
        setColor(Color.BLUE);
        this.function=function;
        tRange=new DoubleRangeModel(tMin,tMin,tMax);
        tRange.setNumSteps(samplePoints,true);
    }
    public Parametric2D(final FunctionTreeModel functionModel1, final FunctionTreeModel functionModel2) {
        tRange = new DoubleRangeModel (0.0, 0.0, 2*Math.PI);
        tRange.setNumSteps(1000, true);
        function = getParametricFunction(
                (Function<Double, Double>) functionModel1.getRoot().getFunction(1),
                (Function<Double, Double>) functionModel2.getRoot().getFunction(1));
        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                function = getParametricFunction(
                        (Function<Double, Double>) functionModel1.getRoot().getFunction(1),
                        (Function<Double, Double>) functionModel2.getRoot().getFunction(1));
                fireStateChanged();
            }
        };
        functionModel1.addChangeListener(cl);
        functionModel2.addChangeListener(cl);
    }
    
    // HELPERS
    
    public static Function<Double, R2> getParametricFunction(final Function<Double,Double> fx, final Function<Double,Double> fy) {
        return new Function<Double, R2>() {
            public R2 getValue(Double x) throws FunctionValueException { return new R2(fx.getValue(x), fy.getValue(x)); }
            public Vector<R2> getValue(Vector<Double> xx) throws FunctionValueException {
                Vector<Double> xs = fx.getValue(xx);
                Vector<Double> ys = fy.getValue(xx);
                Vector<R2> result = new Vector<R2>(xs.size());
                for(int i=0; i<xs.size(); i++){
                    result.add(new R2(xs.get(i),ys.get(i)));
                }
                return result;
            }
        };
    }
    
    
    // BEAN PATTERNS
    
    public Function<Double,R2> getFunction(){return function;}
    public void setFunction(String fx,String fy){
        this.function=new ParametricModel(fx,fy).getFunction();
    }
    public DoubleRangeModel getModel(){return tRange;}
    
    // TODO should not recompute path every time. Just when it's necessary

    /** Draws the path. */
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        if (function == null) { return; }
        try {
            computePath();
            super.paintComponent(g,v);
        } catch (FunctionValueException ex) {}
    }
    
    /** Computes the path over the given range */
    public void computePath() throws FunctionValueException{
        points=function.getValue(tRange.getValueRange(true,0.0));
    }    
    
    
    // STYLE
        
    @Override
    public String toString(){return "Parametric Function";}
    
    /** Returns t value at closest point on path. */
    public double getTime(R2 point){
        R2 closest=getClosestPoint(point.x,point.y);
        int i;
        for(i=0;i<points.size();i++){
            if(closest.equals(points.get(i))){break;}
        }
        return tRange.getMinimum()+i*tRange.getStep();
    }
    
    
    // DECORATIONS
    
    /** Returns a point with a line through it representing the slope of the function at that point. Can also be displayed as a vector or ray
     * from the particular point.
     * @return Point2D object which can be added to a plot
     */
    public Point2D getPointSlope() { return new ParametricPoint(); }
    
    
    // INNER CLASSES
    
    /** Represents a point on the parametric function with the ability to display position, velocity, acceleration vectors. */
    public class ParametricPoint extends Point2D implements Animatable<Euclidean2> {
        R2 velocity;
        R2 acceleration;
        
        DoubleRangeModel tModel;
        
        public ParametricPoint(){
            setModel(getConstraintModel());
            tModel = new DoubleRangeModel(getTime(getPoint()),tRange.getMinimum(),tRange.getMaximum(),tRange.getStep());
            tModel.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e) {
                    if(adjusting){return;}
                    try {
                        setPoint(function.getValue(tModel.getValue()));
                    } catch (FunctionValueException ex) {}
                }
            });
            addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e) {
                    if(!e.getSource().equals(tModel)){
                        adjusting=true;
                        tModel.setRangeProperties(getTime(getPoint()),tRange.getMinimum(),tRange.getMaximum());
                        adjusting=false;
                    }
                }
            });
            
            Parametric2D.this.addChangeListener(this);
        }
        
        /** Returns data model which can be used to control the time of this point. */
        public DoubleRangeModel getTimeModel(){return tModel;}
        
        @Override
        public void recompute() {
            try {
                super.setLabel("t="+NumberFormat.getInstance().format(tModel.getValue()));
                velocity = Derivative.approximateDerivative(function, tModel.getValue(), .001);
                acceleration = Derivative.approximateDerivativeTwo(function, tModel.getValue(), .001);
            } catch (FunctionValueException ex) {
                System.out.println("error");
            }
        }

        @Override
        public void paintComponent(Graphics2D g, Euclidean2 v) {
            R2 position=getPoint();
            g.setColor(Color.BLACK);
            g.draw(v.arrow(new R2(),position,8.0));
            super.paintComponent(g,v);
            g.setStroke(LineStyle.BASIC_STROKE);
            if(velocity!=null){
                g.setColor(Color.RED);
                g.draw(v.arrow(position,position.plus(velocity),8.0));
            }
            if(acceleration!=null){
                g.setColor(Color.PINK);
                g.draw(v.arrow(position,position.plus(acceleration),8.0));
            }
        } 
            
        @Override
        public String toString(){return "Point on Curve";}

        public void paintComponent(Graphics2D g, Euclidean2 v, RangeTimer t) {
            int pos=t.getCurrentIntValue();
            prm.setTo(pos>=points.size()?points.lastElement():points.get(pos));
            paintComponent(g,v);
        }

        public int getAnimatingSteps() {return Parametric2D.this.getAnimatingSteps();}

        @Override
        public void stateChanged(ChangeEvent e) {
            if (!e.getSource().equals(this)) {
                changeEvent = e;
                redraw();
            }
        }
    } // class Parametric2D.ParametricPoint
}
