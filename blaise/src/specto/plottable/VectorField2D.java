/*
 * Parametric2D.java
 * Created on Sep 27, 2007, 1:12:35 PM
 */

// TODO when function is assigned to this, compute the appropriate range of vector lengths

package specto.plottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Vector;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import scio.function.FunctionValueException;
import specto.Plottable;
import scio.coordinate.R2;
import specto.decoration.DESolution2D;
import specto.visometry.Euclidean2;

/**
 * Draws a one-input/two-output curve on the Cartesian Plane. Requires two functions and a range of t-values.
 * @author ae3263
 */
public class VectorField2D extends Plottable<Euclidean2> implements ChangeListener {
    Function<R2,R2> function;
    public static final Function<R2,R2> DEFAULT_FUNCTION=new Function<R2,R2>(){
        public R2 getValue(R2 p){return new R2(p.y,-p.y-5*Math.sin(p.x));}
        @Override
        public Vector<R2> getValue(Vector<R2> x) {
            Vector<R2> result=new Vector<R2>(x.size());
            for(R2 r:x){result.add(getValue(r));}
            return result;
        }
        public R2 minValue(){return new R2(-5.0,-5.0);}
        public R2 maxValue(){return new R2(5.0,5.0);}
    };
        
    
    // CONSTRUCTORS
        
    public VectorField2D(){this(DEFAULT_FUNCTION);}
    public VectorField2D(Function<R2,R2> function){
        setColor(Color.GRAY);
        this.function=function;
    }
    
    
    // BEAN PATTERNS
    
    public Function<R2,R2> getFunction(){return function;}
    public void setFunction(Function<R2,R2> function){this.function=function;}
    
    // DRAW METHODS
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v){
        R2 point;
        R2 vector;
        Vector<Double> xRange=v.getSparseXRange(30);
        Vector<Double> yRange=v.getSparseYRange(30);
        double stepX=xRange.get(1)-xRange.get(0);
        double stepY=yRange.get(1)-yRange.get(0);
        double step=(stepX>stepY)?stepX:stepY;
        switch(style.getValue()){
        case DOT_LINES:
            for(double px:xRange){
                for(double py:yRange){
                    try {
                        point=new R2(px,py);
                        g.fill(v.dot(point,2));
                        vector=DESolution2D.getMultipliedVector(function,point,0.6*step);
                        g.draw(v.lineSegment(point.plus(vector),point.minus(vector)));   
                    } catch (FunctionValueException ex) {}
                }
            }
            break;
        case LINES:
            for(double px:xRange){
                for(double py:yRange){
                    try {
                        point=new R2(px,py);
                        vector=DESolution2D.getMultipliedVector(function,point,0.6*step);
                        g.draw(v.lineSegment(point.plus(vector),point.minus(vector)));   
                    } catch (FunctionValueException ex) {}
                }
            }
            break;
        case ARROWS:
            Shape arrow;
            for(double px:xRange){
                for(double py:yRange){
                    try {
                        point=new R2(px,py);
                        vector=DESolution2D.getMultipliedVector(function,point,0.8*step);   
                        arrow=v.arrow(point,point.plus(vector),5.0);
                        g.draw(arrow);
                        g.fill(arrow);
                    } catch (FunctionValueException ex) {}
                }
            }
            break;
        case TRAILS:
            for(double px:xRange){
                for(double py:yRange){
                    try{
                        point=new R2(px,py);
                        g.draw(v.path(DESolution2D.calcNewton(function, point, NUM, .75*step/NUM)));
                        g.draw(v.path(DESolution2D.calcNewton(function, point, NUM, -.75*step/NUM)));
                    }catch(FunctionValueException e){}
                }
            }
            break;
        }
    }
    
    int NUM=10;
    
    // STYLE METHODS

    public static final int LINES=0;            // lines in the direction of the curves
    public static final int DOT_LINES=1;        // dots with lines through them
    public static final int ARROWS=2;           // arrows in the direction of the curves
    public static final int TRAILS=3;           // plot a few points forward and back from the current point
    static final String[] styleStrings={"Slopelines","Slopelines with dots","Vectors","Flows"};
    
    @Override
    public String[] getStyleStrings() {return styleStrings;}
    @Override
    public String toString(){return "Vector field";}
}

