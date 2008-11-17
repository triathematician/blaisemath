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
import scio.coordinate.R2;
import scio.function.Function;
import scio.coordinate.R3;
import scio.function.BoundedFunction;
import scribo.tree.FunctionTreeRoot;
import sequor.model.DoubleRangeModel;
import sequor.model.PointRangeModel;
import specto.PlottableGroup;

/**
 * Draws a parametric function on the plane. In other words, it contains two functions which give the x and y coordinates
 * of the function in terms of some other parameter (frequently 't'). The properties of this class which permit it to be
 * plotted are any function from Double->(Double,Double), and min/max values of t.
 * @author ElishaPeterson
 */
public class ParametricSurface3D extends PlottableGroup<Euclidean3> {
    
    // PROPERTIES
    
    /** Function which takes in a pair of points and returns a pair of doubles = a point in the plane. */
    Function<R2,R3> function;

    /** Range of t values. */
    PointRangeModel uvRange;
    
    /** Defines a default function which is displayed. For now its the unit sphere */
    private static final Function<R2,R3> DEFAULT_FUNCTION=new Function<R2,R3>(){
        @Override
        public R3 getValue(R2 uv){return new R3(Math.cos(uv.x)*Math.sin(uv.y),Math.sin(uv.x)*Math.sin(uv.y),Math.cos(uv.y));}
        @Override
        public Vector<R3> getValue(Vector<R2> pts) {
            Vector<R3> result=new Vector<R3>(pts.size());
            for(R2 pt:pts){result.add(getValue(pt));}
            return result;
        }
    };
        
    public ParametricSurface3D(){this(DEFAULT_FUNCTION,0.0,2*Math.PI,0.0,Math.PI,10);}
    public ParametricSurface3D(String string) {this();}
    /** Constructor for use with a particular function and range of t values */
    public ParametricSurface3D(Function<R2,R3> function,double uMin,double uMax,double vMin,double vMax,int samplePoints){
        setColor(Color.DARK_GRAY);        
        this.function=function;
        uvRange = new PointRangeModel(uMin,uMax,vMin,vMax);
        uvRange.xModel.setNumSteps(samplePoints,true);
        uvRange.yModel.setNumSteps(samplePoints,true);
    }
    /** Constructs with specified function. */
    public ParametricSurface3D(Function<R2, R3> function, DoubleRangeModel drmu, DoubleRangeModel drmv, int samplePoints) {
        setColor(Color.DARK_GRAY);        
        this.function=function;
        uvRange = new PointRangeModel(drmu, drmv);
        uvRange.xModel.setNumSteps(samplePoints,true);
        uvRange.yModel.setNumSteps(samplePoints,true);
    }
    
    public ParametricSurface3D(final FunctionTreeModel fm1, final FunctionTreeModel fm2, final FunctionTreeModel fm3) {
        setColor(Color.DARK_GRAY);        
        uvRange = new PointRangeModel(0.0, 10.0, 0.0, 10.0);
        uvRange.xModel.setNumSteps(1000,true);
        uvRange.yModel.setNumSteps(1000,true);
        function = getParametricFunction(
                (Function<R2, Double>) fm1.getRoot().getFunction(2),
                (Function<R2, Double>) fm2.getRoot().getFunction(2),
                (Function<R2, Double>) fm3.getRoot().getFunction(2));
        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                function = getParametricFunction(
                        (Function<R2, Double>) fm1.getRoot().getFunction(2),
                        (Function<R2, Double>) fm2.getRoot().getFunction(2),
                        (Function<R2, Double>) fm3.getRoot().getFunction(2));
                fireStateChanged();
            }
        };
        fm1.addChangeListener(cl);
        fm2.addChangeListener(cl);
    }
    
    // HELPERS
    
    public static Function<R2, R3> getParametricFunction(
            final Function<R2,Double> fx, final Function<R2,Double> fy, final Function<R2,Double> fz) {
        return new Function<R2, R3>() {
            public R3 getValue(R2 pt) throws FunctionValueException { 
                return new R3(fx.getValue(pt), fy.getValue(pt), fz.getValue(pt)); 
            }
            public Vector<R3> getValue(Vector<R2> pts) throws FunctionValueException {
                Vector<Double> xs = fx.getValue(pts);
                Vector<Double> ys = fy.getValue(pts);
                Vector<Double> zs = fz.getValue(pts);
                Vector<R3> result = new Vector<R3>(xs.size());
                for(int i=0; i<xs.size(); i++){
                    result.add(new R3(xs.get(i),ys.get(i),zs.get(i)));
                }
                return result;
            }
        };
    }
    
    
    // BEAN PATTERNS
    
    public Function<R2,R3> getFunction(){return function;}
    public void setFunction(String fx,String fy,String fz) throws FunctionSyntaxException{
        function=getParametricFunction(
                (Function<R2, Double>) new FunctionTreeRoot(fx).getFunction(),
                (Function<R2, Double>) new FunctionTreeRoot(fy).getFunction(),
                (Function<R2, Double>) new FunctionTreeRoot(fz).getFunction());
    }
    
    /** Returns value range model */
    public PointRangeModel getDomainModel(){return uvRange;}
    

    // HANDLES THE INDIVIDUAL CURVES AND DRAWING THE SURFACE
        
    /** Sets up the curves used to draw the figure. */
    public void initCurves(Euclidean3 v){        
        clear();
        
        for (double x : uvRange.xModel.getValueRange(true, 0.0)) {
            add(new ParametricCurve3D(getSliceFixedX(x, function),uvRange.yModel,100));
        }
        for (double y : uvRange.yModel.getValueRange(true, 0.0)) {
            add(new ParametricCurve3D(getSliceFixedY(y, function),uvRange.xModel,100));
        }
    }

    @Override
    public void recompute(Euclidean3 v, boolean recomputeAll) {
        super.recompute(v, recomputeAll);
        initCurves(v);
    }

    @Override
    public void recompute(Euclidean3 v) {
        super.recompute(v);
        initCurves(v);
    }
    
    
    // STYLE
        
    @Override
    public String toString(){return "Parametric Surface";}
        

    // HELPERS    
    
    /** Generates a partial function (one value is fixed). */
    public static BoundedFunction<Double,R3> getSliceFixedY(final double y,final Function<R2,R3> input) {
        return new BoundedFunction<Double,R3>() {
            public R3 minValue() { return new R3(-1.0,-1.0,-1.0); }
            public R3 maxValue() { return new R3(1.0,1.0,1.0); }
            public R3 getValue(Double x) throws FunctionValueException {
                return input.getValue(new R2(x,y));
            }
            public Vector<R3> getValue(Vector<Double> xs) throws FunctionValueException {
                Vector<R3> result = new Vector<R3>();
                for(Double x : xs) { result.add(input.getValue(new R2(x,y))); }
                return result;
            }           
        };
    }
    
    /** Generates a partial function (one value is fixed). */
    public static BoundedFunction<Double,R3> getSliceFixedX(final double x,final Function<R2,R3> input) {
        return new BoundedFunction<Double,R3>() {
            public R3 minValue() { return new R3(-1.0,-1.0,-1.0); }
            public R3 maxValue() { return new R3(1.0,1.0,1.0); }
            public R3 getValue(Double y) throws FunctionValueException {
                return input.getValue(new R2(x,y));
            }
            public Vector<R3> getValue(Vector<Double> ys) throws FunctionValueException {
                Vector<R3> result = new Vector<R3>();
                for(Double y : ys) { result.add(input.getValue(new R2(x,y))); }
                return result;
            }           
        };
    } 
    
    
    // STANDARD SURFACES
    
    /** Represents a sphere of given radius and center. */
    static class Sphere extends ParametricSurface3D {
        public Sphere(final R3 c, final Double r) {
            super(new Function<R2,R3>(){
                    @Override
                    public R3 getValue(R2 uv){return new R3(
                            c.x+r*Math.cos(uv.x)*Math.sin(uv.y),
                            c.y+r*Math.sin(uv.x)*Math.sin(uv.y),
                            c.z+r*Math.cos(uv.y));}
                    @Override
                    public Vector<R3> getValue(Vector<R2> pts) {
                        Vector<R3> result=new Vector<R3>(pts.size());
                        for(R2 pt:pts){result.add(getValue(pt));}
                        return result;
                    }
           },0.0,2*Math.PI,0.0,Math.PI,10);
        }
    }
}
