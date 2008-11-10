/*
 * PlaneFunction2D.java
 * 
 * Created on Sep 27, 2007, 1:19:00 PM
 */

package specto.euclidean2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Vector;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.function.FunctionValueException;
import sequor.model.FunctionTreeModel;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.function.BoundedFunction;
import scio.function.Function;
import sequor.model.DoubleRangeModel;
import specto.PlottableGroup;

/**
 * Draws a two-input/one-output function on the Cartesian Plane. Requires such a function to work. Multiple style settings will be supported.
 * @author ae3263
 */
public class PlaneFunction2DProjected extends PlottableGroup<Euclidean2>{
    BoundedFunction<R2,Double> function;   
    DoubleRangeModel xRange;
    DoubleRangeModel yRange;
    
    Function<R3,R2> projection = new Function<R3,R2>(){
        final R2 xhat = new R2(-.8,-.6);
        final R2 yhat = new R2(.8,-.6);
        final R2 zhat = new R2(0,1);        
        public R2 getValue(R3 pt) throws FunctionValueException {
            return new R2(
                    xhat.x*pt.getX()+yhat.x*pt.getY()+zhat.x*pt.getZ(),
                    xhat.y*pt.getX()+yhat.y*pt.getY()+zhat.y*pt.getZ()
                    );
        }

        public Vector<R2> getValue(Vector<R3> pts) throws FunctionValueException {
            Vector<R2> result = new Vector<R2>();
            for(R3 pt : pts) {
                result.add(new R2(
                    xhat.x*pt.getX()+yhat.x*pt.getY()+zhat.x*pt.getZ(),
                    xhat.y*pt.getX()+yhat.y*pt.getY()+zhat.y*pt.getZ()
                    ));
            }
            return result;
        }    
    };
    
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
        
    public PlaneFunction2DProjected(){this(DEFAULT_FUNCTION);}
    public PlaneFunction2DProjected(BoundedFunction<R2,Double> function){
        this.function=function;
        setColor(Color.ORANGE);
        initCurves();
    }
    public PlaneFunction2DProjected(FunctionTreeModel functionModel) {
        initFunction(functionModel);
        initCurves();
    }
    
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
    
    /** Sets up the curves used to draw the figure. */
    public void initCurves(){
        xRange = new DoubleRangeModel(1,-5,5,.5);
        yRange = new DoubleRangeModel(1,-5,5,.5);
        
        clear();
        
        for (double x : xRange.getValueRange(true, 0.0)) {
            add(new Parametric2D(getProjectedPartial2(x, function, projection),yRange,100));
        }
        for (double y : yRange.getValueRange(true, 0.0)) {
            add(new Parametric2D(getProjectedPartial1(y, function, projection),xRange,100));
        }
    }
    
    // INTIALIZERS
    public BoundedFunction<R2,Double> getFunction() { return function; }
    public BoundedFunction<R2,R2> getGradientFunction() { return getGradient(function); }
    public BoundedFunction<Double,Double> getFunctionX(double y) { return getPartial1(y,function); }
    public BoundedFunction<Double,Double> getFunctionY(double x) { return getPartial2(x,function); }
    
    
    
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
    public static BoundedFunction<Double,Double> getPartial1(final double x2,final BoundedFunction<R2,Double> input) {
        return new BoundedFunction<Double,Double>() {
            public Double minValue() { return -1.0; }
            public Double maxValue() { return 1.0; }
            public Double getValue(Double x) throws FunctionValueException {
                return input.getValue(new R2(x,x2));
            }
            public Vector<Double> getValue(Vector<Double> xx) throws FunctionValueException {
                Vector<Double> result = new Vector<Double>();
                for(Double x : xx) { result.add(input.getValue(new R2(x,x2))); }
                return result;
            }           
        };
    }    
    
    /** Generates a partial function (one value is fixed). */
    public static Function<Double,R2> getProjectedPartial1(final double x2,final BoundedFunction<R2,Double> input,final Function<R3,R2> projection) {
        return new Function<Double, R2>() {
            public R2 getValue(Double x) throws FunctionValueException {
                return projection.getValue(new R3(x,x2,input.getValue(new R2(x,x2))));
            }
            public Vector<R2> getValue(Vector<Double> xx) throws FunctionValueException {
                Vector<R3> inputs = new Vector<R3>();
                for(Double x : xx) { inputs.add(new R3(x,x2,input.getValue(new R2(x,x2)))); }
                return projection.getValue(inputs);
            }
        };
    }    
    
    /** Generates a partial function (one value is fixed). */
    public static BoundedFunction<Double,Double> getPartial2(final double x1,final BoundedFunction<R2,Double> input) {
        return new BoundedFunction<Double,Double>() {
            public Double minValue() { return -1.0; }
            public Double maxValue() { return 1.0; }
            public Double getValue(Double x) throws FunctionValueException {
                return input.getValue(new R2(x1,x));
            }
            public Vector<Double> getValue(Vector<Double> xx) throws FunctionValueException {
                Vector<Double> result = new Vector<Double>();
                for(Double x : xx) { result.add(input.getValue(new R2(x1,x))); }
                return result;
            }           
        };
    }
    
    /** Generates a partial function (one value is fixed). */
    public static Function<Double,R2> getProjectedPartial2(final double x1,final BoundedFunction<R2,Double> input,final Function<R3,R2> projection) {
        return new Function<Double, R2>() {
            public R2 getValue(Double x) throws FunctionValueException {
                return projection.getValue(new R3(x1,x,input.getValue(new R2(x1,x))));
            }
            public Vector<R2> getValue(Vector<Double> xx) throws FunctionValueException {
                Vector<R3> inputs = new Vector<R3>();
                for(Double x : xx) { inputs.add(new R3(x1,x,input.getValue(new R2(x1,x)))); }
                return projection.getValue(inputs);
            }
        };
    }      
}

