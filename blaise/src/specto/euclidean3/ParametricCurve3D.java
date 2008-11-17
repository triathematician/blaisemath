/*
 * Parametric2D.java
 * Created on Sep 27, 2007, 1:12:35 PM
 */

package specto.euclidean3;

import javax.swing.event.ChangeEvent;
import scio.function.FunctionValueException;
import scribo.parser.FunctionSyntaxException;
import sequor.model.FunctionTreeModel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import scio.coordinate.R3;
import scribo.tree.FunctionTreeRoot;
import sequor.model.DoubleRangeModel;

/**
 * Draws a parametric function on the plane. In other words, it contains two functions which give the x and y coordinates
 * of the function in terms of some other parameter (frequently 't'). The properties of this class which permit it to be
 * plotted are any function from Double->(Double,Double), and min/max values of t.
 * @author ElishaPeterson
 */
public class ParametricCurve3D extends PointSet3D {
    
    // PROPERTIES
    
    /** Function which takes in a double and returns a pair of doubles = a point in the plane. */
    Function<Double,R3> function;

    /** Range of t values. */
    DoubleRangeModel tRange;
    
    /** Defines a default function which is displayed. For now its a "Lissajous" curve */
    private static final Function<Double,R3> DEFAULT_FUNCTION=new Function<Double,R3>(){
        @Override
        public R3 getValue(Double t){return new R3(2*Math.cos(t),2*Math.sin(2*t),t);}
        @Override
        public Vector<R3> getValue(Vector<Double> x) {
            Vector<R3> result=new Vector<R3>(x.size());
            for(Double d:x){result.add(getValue(d));}
            return result;
        }
    };
        
    public ParametricCurve3D(){this(DEFAULT_FUNCTION,0.0,2*Math.PI,1000);}
    public ParametricCurve3D(String string) {this();}
    /** Constructor for use with a particular function and range of t values */
    public ParametricCurve3D(Function<Double,R3> function,double tMin,double tMax,int samplePoints){
        setColor(Color.BLUE);
        this.function=function;
        tRange=new DoubleRangeModel(tMin,tMin,tMax);
        tRange.setNumSteps(samplePoints,true);
    }

    public ParametricCurve3D(Function<Double, R3> function, DoubleRangeModel drm,int samplePoints) {
        setColor(Color.BLUE);
        this.function=function;
        tRange=new DoubleRangeModel(drm.getMinimum(),drm.getMinimum(),drm.getMaximum());
        tRange.setNumSteps(samplePoints,true);
    }
    
    public ParametricCurve3D(final FunctionTreeModel fm1, final FunctionTreeModel fm2, final FunctionTreeModel fm3) {
        tRange = new DoubleRangeModel (0.0, 0.0, 2*Math.PI);
        tRange.setNumSteps(1000, true);
        function = getParametricFunction(
                (Function<Double, Double>) fm1.getRoot().getFunction(1),
                (Function<Double, Double>) fm2.getRoot().getFunction(1),
                (Function<Double, Double>) fm3.getRoot().getFunction(1));
        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                function = getParametricFunction(
                        (Function<Double, Double>) fm1.getRoot().getFunction(1),
                        (Function<Double, Double>) fm2.getRoot().getFunction(1),
                        (Function<Double, Double>) fm3.getRoot().getFunction(1));
                fireStateChanged();
            }
        };
        fm1.addChangeListener(cl);
        fm2.addChangeListener(cl);
    }
    
    // HELPERS
    
    public static Function<Double, R3> getParametricFunction(
            final Function<Double,Double> fx, final Function<Double,Double> fy, final Function<Double,Double> fz) {
        return new Function<Double, R3>() {
            public R3 getValue(Double t) throws FunctionValueException { 
                return new R3(fx.getValue(t), fy.getValue(t), fz.getValue(t)); 
            }
            public Vector<R3> getValue(Vector<Double> ts) throws FunctionValueException {
                Vector<Double> xs = fx.getValue(ts);
                Vector<Double> ys = fy.getValue(ts);
                Vector<Double> zs = fz.getValue(ts);
                Vector<R3> result = new Vector<R3>(xs.size());
                for(int i=0; i<xs.size(); i++){
                    result.add(new R3(xs.get(i),ys.get(i),zs.get(i)));
                }
                return result;
            }
        };
    }
    
    
    // BEAN PATTERNS
    
    public Function<Double,R3> getFunction(){return function;}
    public void setFunction(String fx,String fy,String fz) throws FunctionSyntaxException{
        function=getParametricFunction(
                (Function<Double, Double>) new FunctionTreeRoot(fx).getFunction(),
                (Function<Double, Double>) new FunctionTreeRoot(fy).getFunction(),
                (Function<Double, Double>) new FunctionTreeRoot(fz).getFunction());
    }
    public DoubleRangeModel getModel(){return tRange;}
    
    /** Draws the path. */
    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v) {
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
    public String toString(){return "Parametric Curve";}    
    
}
