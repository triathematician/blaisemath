/*
 * Parametric2D.java
 * 
 * Created on Sep 27, 2007, 1:12:35 PM
 */

package specto.plottable;

import java.util.logging.Level;
import java.util.logging.Logger;
import scio.function.FunctionValueException;
import specto.dynamicplottable.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;
import scio.function.Function;
import scio.coordinate.R2;

/**
 * Draws a parametric function on the plane. In other words, it contains two functions which give the x and y coordinates
 * of the function in terms of some other parameter (frequently 't'). The properties of this class which permit it to be
 * plotted are any function from Double->(Double,Double), and min/max values of t.
 * @author ae3263
 */
public class Parametric2D extends PointSet2D {
    
    // PROPERTIES
    
    /** Function which takes in a double and returns a pair of doubles = a point in the plane. */
    Function<Double,R2> function;
    
    /** The default minimum of t */
    double tMin;

    /** The default maximum of t */
    double tMax;
    
    /** The number of points to compute */
    int samplePoints;
    
    /** Range of independent variable values which are required to compute the path of the function.
     * By default, this is controlled within this class, and 2000 points are plotted.
     */
    Vector<Double> tRange;
    
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
    public Parametric2D(){this(DEFAULT_FUNCTION,0.0,2*Math.PI,1000);}
    
    /** Constructor for use with a particular function and range of t values */
    public Parametric2D(Function<Double,R2> function,double tMin,double tMax,int samplePoints){
        setColor(Color.BLUE);
        this.function=function;
        this.tMin=tMin;
        this.tMax=tMax;
        this.samplePoints=samplePoints;
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
        double tStep=(tMax-tMin)/samplePoints;
        if(tRange==null){
            tRange=new Vector<Double>(samplePoints);
        }else{
            tRange.clear();
        }
        for(double d=tMin;d<=tMax;d+=tStep){tRange.add(d);}
        points=function.getValue(tRange);
    }
}
