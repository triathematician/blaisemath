/*
 * Parametric2D.java
 * Created on Sep 27, 2007, 1:12:35 PM
 */

package specto.plottable;

import scio.function.FunctionValueException;
import specto.dynamicplottable.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;
import scio.function.Function;
import scio.coordinate.R2;
import sequor.model.DoubleRangeModel;
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
        @Override
        public R2 minValue(){return new R2(-2,-2);}
        @Override
        public R2 maxValue(){return new R2(2,2);}
        };
        
    /** Default constructor uses the default function and t between 0 and 2pi */
    public Parametric2D(Euclidean2 vis){this(vis,DEFAULT_FUNCTION,0.0,2*Math.PI,1000);}
    
    /** Constructor for use with a particular function and range of t values */
    public Parametric2D(Euclidean2 vis,Function<Double,R2> function,double tMin,double tMax,int samplePoints){
        super(vis);
        setColor(Color.BLUE);
        this.function=function;
        tRange=new DoubleRangeModel(tMin,tMin,tMax);
        tRange.setNumSteps(samplePoints,true);
    }
    
    // TODO should not recompute path every time. Just when it's necessary

    /** Draws the path. */
    @Override
    public void paintComponent(Graphics2D g) {
        try {
            computePath();
            super.paintComponent(g);
        } catch (FunctionValueException ex) {}
    }
    
    /** Computes the path over the given range */
    public void computePath() throws FunctionValueException{
        points=function.getValue(tRange.getValueRange(true,0.0));
    }
}
