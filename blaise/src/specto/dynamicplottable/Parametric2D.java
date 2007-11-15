/*
 * Parametric2D.java
 * 
 * Created on Sep 27, 2007, 1:12:35 PM
 */

package specto.dynamicplottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;
import scio.function.Function;
import scio.coordinate.R2;
import specto.visometry.Euclidean2;

/**
 * Draws a one-input/two-output curve on the Cartesian Plane. Requires two functions and a range of t-values.
 * @author ae3263
 */
public class Parametric2D extends PointSet2D {
    Function<Double,R2> function;
    Vector<Double> tRange;
    private static final Function<Double,R2> DEFAULT_FUNCTION=new Function<Double,R2>(){
        public R2 getValue(Double x){return new R2(2*Math.cos(x),2*Math.sin(1.99*x));}
        public R2 minValue(){return new R2(-2,-2);}
        public R2 maxValue(){return new R2(2,2);}
        };
    double tMin,tMax;
    public Parametric2D(){this(DEFAULT_FUNCTION,0.0,2*Math.PI);}
    public Parametric2D(Function<Double,R2> function,double tMin,double tMax){
        setColor(Color.BLUE);
        this.function=function;
        this.tMin=tMin;
        this.tMax=tMax;
    }

    @Override
    public void paintComponent(Graphics2D g) {
        computePath();
        super.paintComponent(g);
    }
    public void computePath(){
        tRange=new Vector<Double>();
        for(double d=tMin;d<=tMax;d+=(tMax-tMin)/500){tRange.add(d);}
        points.clear();
        for(Double d:tRange){points.add(function.getValue(d));}
    }
}
