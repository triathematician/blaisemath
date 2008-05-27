/*
 * PlaneFunction2D.java
 * 
 * Created on Sep 27, 2007, 1:19:00 PM
 */

package specto.plottable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;
import scio.function.FunctionValueException;
import specto.Plottable;
import scio.coordinate.R2;
import scio.function.BoundedFunction;
import scio.function.MeshRoot2D;
import specto.style.LineStyle;
import specto.visometry.Euclidean2;

/**
 * Draws a two-input/one-output function on the Cartesian Plane. Requires such a function to work. Multiple style settings will be supported.
 * @author ae3263
 */
public class PlaneFunction2D extends Plottable<Euclidean2>{
    BoundedFunction<R2,Double> function;
    
    
    /** Default function used for testing the method. */
    private static final BoundedFunction<R2,Double> DEFAULT_FUNCTION=new BoundedFunction<R2,Double>(){
        @Override
        public Double getValue(R2 p){return 
                //p.x;}
                //p.x*p.x + Math.cos(p.y)*Math.exp(p.y); }
                2-Math.cos(p.x)*Math.cos(p.x)-Math.sin(p.y)*Math.sin(p.y);}
        @Override
        public Double minValue(){return 0.0;}
        @Override
        public Double maxValue(){return 4.0;}
        @Override
        public Vector<Double> getValue(Vector<R2> x) {
            Vector<Double> result=new Vector<Double>(x.size());
            for(R2 r:x){result.add(getValue(r));}
            return result;
        }
    };
        
        
    public PlaneFunction2D(){this(DEFAULT_FUNCTION);}
    public PlaneFunction2D(BoundedFunction<R2,Double> function){
        this.function=function;
        setColor(Color.ORANGE);
    }
    
    // DRAW METHODS
        
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v){
        try {
            g.setColor(getColor());
            Vector<R2> inputs;
            Vector<Double> result;
            double WEIGHT=10/(function.maxValue()-function.minValue());
            double SHIFT=-10*function.minValue()/(function.maxValue()-function.minValue())+1;
            switch (style.getValue()) {
                case DOTS:
                    inputs = new Vector<R2>();
                    for (double px : v.getSparseXRange(20)) {
                        for (double py : v.getSparseYRange(20)) {
                            inputs.add(new R2(px, py));
                        }
                    }
                    result = function.getValue(inputs);
                    for(int i=0;i<inputs.size();i++){
                        g.fill(v.dot(inputs.get(i),getRadius(result.get(i),1.5*WEIGHT,SHIFT)));
                    }
                    break;                    
                case COLORS:
                    inputs = new Vector<R2>();
                    for (double px : v.getSparseXRange(20)) {
                        for (double py : v.getSparseYRange(20)) {
                            inputs.add(new R2(px, py));
                        }
                    }
                    result = function.getValue(inputs);
                    for(int i=0;i<inputs.size();i++){
                        g.setColor(Color.getHSBColor(1.0f-(float)getRadius(result.get(i),WEIGHT/10,SHIFT),0.5f,1.0f));
                        g.fill(v.squareDot(inputs.get(i),10.0));
                    }
                    break;
                case CONTOURS:
                    g.setStroke(LineStyle.THIN_STROKE);
                    for (double zValue = function.minValue() ; zValue <= function.maxValue() ; zValue += 1/(2*WEIGHT) ) {
                        g.draw( MeshRoot2D.findRoots(function, zValue, v.getActualMin(), v.getActualMax(),
                                10*v.getDrawWidth()/v.getWindowWidth(), 0.01, 100)
                                .createTransformedShape(v.getAffineTransformation()) );
                    }
                    break;
                case DENSITY:
                default:
                    break;
            }
        } catch (FunctionValueException ex) {}
    }
    
    /** Computes radius of a given point with specified multiplier and shift. */
    public double getRadius(double value,double weight,double shift) throws FunctionValueException{
        value=value*weight+shift;
        return(value<0)?0:value;
    }

    // STYLE METHODS

    public static final int DOTS=0;
    public static final int COLORS=1;
    public static final int CONTOURS=2;
    public static final int DENSITY=3;
    
    public static final String[] styleStrings={"Dots","Color Boxes","Contours"};
    @Override
    public String[] getStyleStrings() {return styleStrings;}
    @Override
    public String toString(){return "Plane Function";}
}

