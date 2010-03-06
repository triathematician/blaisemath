/*
 * PlaneFunction2D.java
 * 
 * Created on Sep 27, 2007, 1:19:00 PM
 */

package specto.euclidean3;

import java.awt.Color;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.function.FunctionValueException;
import sequor.model.FunctionTreeModel;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.function.BoundedFunction;
import sequor.model.DoubleRangeModel;
import specto.Plottable;
import specto.PlottableGroup;

/**
 * Draws a two-input/one-output function on the Cartesian Plane. Requires such a function to work. Multiple style settings will be supported.
 * @author ae3263
 */
public class Function3D extends PlottableGroup<Euclidean3>{
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
    
    // CONSTRUCTORS
        
    public Function3D(){this(DEFAULT_FUNCTION);}
    public Function3D(BoundedFunction<R2,Double> function){
        this.function=function;
        setColor(new Color(100,100,100,200));        
    }
    public Function3D(FunctionTreeModel functionModel) {
        initFunction(functionModel);
        setColor(new Color(100,100,100,200));        
    }
    
    // INITIALIZER METHODS
    
    public void initFunction(final FunctionTreeModel functionModel) {
        Vector<String> vars = new Vector<String>();
        vars.add("x"); vars.add("y");
        functionModel.getRoot().setVariables(vars);
        function = (BoundedFunction<R2, Double>) functionModel.getFunction();
        functionModel.addChangeListener(new ChangeListener(){
            @Override
            public void stateChanged(ChangeEvent e) {
                function = (BoundedFunction<R2, Double>) functionModel.getFunction(2);
                fireStateChanged();
            }
        });
    }
    
    
    // OVERRIDE

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
    
    /** Sets up the curves used to draw the figure. */
    public void initCurves(Euclidean3 v){        
        clear();
        
        int SAMPLES = 11;
        
        DoubleRangeModel dx = new DoubleRangeModel(v.xRange.getMinimum(), v.xRange.getMinimum(), v.xRange.getMaximum());
        dx.setNumSteps(SAMPLES, true);
        DoubleRangeModel dy = new DoubleRangeModel(v.yRange.getMinimum(), v.yRange.getMinimum(), v.yRange.getMaximum());
        dy.setNumSteps(SAMPLES, true);
        
        for (double x : dx.getValueRange(true, 0.0)) {
            add(new ParametricCurve3D(getPartial2(x, function),v.yRange,100));
        }
        for (double y : dy.getValueRange(true, 0.0)) {
            add(new ParametricCurve3D(getPartial1(y, function),v.xRange,100));
        }
        
        for (Plottable p : plottables) {
            p.setColor(getColor());
        }
    }
    
    // STYLE
    
    @Override
    public String toString() { return "Function"; }
    
    // INTIALIZERS
    
    public BoundedFunction<R2,Double> getFunction() { return function; }
    public BoundedFunction<R2,R2> getGradientFunction() { return getGradient(function); }
    
    
    
    // STATIC METHODS
    
    /** Generate gradient vector field. */
    public static BoundedFunction<R2,R2> getGradient(final BoundedFunction<R2,Double> input) {
        return new BoundedFunction<R2,R2>() {
            public R2 minValue() { return new R2(-1,-1); }
            public R2 maxValue() { return new R2(1,1); }
            public R2 getValue(R2 x) throws FunctionValueException {
                R2 xShift = new R2(x.x + .0001, x.y);
                R2 yShift = new R2(x.x, x.y + .0001);
                double value = input.getValue(x);
                return new R2((input.getValue(xShift)-value)/.0001,(input.getValue(yShift)-value)/.0001);
            }
            public Vector<R2> getValue(Vector<R2> xx) throws FunctionValueException {
                Vector<R2> result = new Vector<R2>();
                for(R2 x : xx) { result.add(getValue(x)); }
                return result;
            }           
        };
    }
    
    /** Generates a partial function (one value is fixed). */
    public static BoundedFunction<Double,R3> getPartial1(final double y,final BoundedFunction<R2,Double> input) {
        return new BoundedFunction<Double,R3>() {
            public R3 minValue() { return new R3(-1.0,-1.0,-1.0); }
            public R3 maxValue() { return new R3(1.0,1.0,1.0); }
            public R3 getValue(Double x) throws FunctionValueException {
                return new R3(x,y,input.getValue(new R2(x,y)));
            }
            public Vector<R3> getValue(Vector<Double> xs) throws FunctionValueException {
                Vector<R3> result = new Vector<R3>();
                for(Double x : xs) { result.add(new R3(x,y,input.getValue(new R2(x,y)))); }
                return result;
            }           
        };
    }
    
    /** Generates a partial function (one value is fixed). */
    public static BoundedFunction<Double,R3> getPartial2(final double x,final BoundedFunction<R2,Double> input) {
        return new BoundedFunction<Double,R3>() {
            public R3 minValue() { return new R3(-1.0,-1.0,-1.0); }
            public R3 maxValue() { return new R3(1.0,1.0,1.0); }
            public R3 getValue(Double y) throws FunctionValueException {
                return new R3(x,y,input.getValue(new R2(x,y)));
            }
            public Vector<R3> getValue(Vector<Double> ys) throws FunctionValueException {
                Vector<R3> result = new Vector<R3>();
                for(Double y : ys) { result.add(new R3(x,y,input.getValue(new R2(x,y)))); }
                return result;
            }           
        };
    } 
}

