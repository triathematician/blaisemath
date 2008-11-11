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
import specto.Plottable;
import scio.coordinate.R2;
import scio.function.BoundedFunction;
import scio.function.MeshRoot2D;
import sequor.style.VisualStyle;

/**
 * Draws a two-input/one-output function on the Cartesian Plane. Requires such a function to work. Multiple style settings will be supported.
 * @author ae3263
 */
public class PlaneFunction2D extends Plottable<Euclidean2>{
    BoundedFunction<R2,Double> function;
    Vector<R2> inputs;
    Vector<Double> currentValues;
    
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
        
    public PlaneFunction2D(){this(DEFAULT_FUNCTION);}
    public PlaneFunction2D(BoundedFunction<R2,Double> function){
        this.function=function;
        setColor(Color.ORANGE);
    }
    public PlaneFunction2D(FunctionTreeModel functionModel) {
        initFunction(functionModel);
        setColor(Color.ORANGE);
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
    
    // INTIALIZERS
    public BoundedFunction<R2,Double> getFunction() { return function; }
    public void setFunction(BoundedFunction<R2,Double> function) { this.function = function; fireStateChanged(); }
    public BoundedFunction<R2,R2> getGradientFunction() { return getGradient(function); }
    public BoundedFunction<Double,Double> getFunctionX(double y) { return getPartial1(y,function); }
    public BoundedFunction<Double,Double> getFunctionY(double x) { return getPartial2(x,function); }
    
    
    // VALUE COMPUTATIONS
    
    /** Recomputes the current grid of values of the function. This is needed for particular computations below. */
    @Override
    public void recompute(Euclidean2 v) {
        try {
            inputs = new Vector<R2>();
            for (double px : v.getSparseXRange(20)) {
                for (double py : v.getSparseYRange(20)) {
                    inputs.add(new R2(px, py));
                }
            }
            currentValues = function.getValue(inputs);
        } catch (FunctionValueException ex) {}
    }
    
    // DRAW METHODS
        
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v){
        if (function == null) { return; }
        try {
            g.setColor(getColor());
            double WEIGHT=10/(function.maxValue()-function.minValue());
            double SHIFT=0.0;//-10*function.minValue()/(function.maxValue()-function.minValue())+1;
            double rad;
            switch (style.getValue()) {
                case DOTS:
                    for(int i=0;i<inputs.size();i++){
                        rad = getRadius(currentValues.get(i),1.5*WEIGHT,SHIFT);
                        if (rad < 0) {
                            g.setColor(getColor().brighter());
                            g.fill(v.dot(inputs.get(i),-rad));
                            g.setColor(getColor());
                        } else {
                            g.fill(v.dot(inputs.get(i),rad));                            
                        }
                    }
                    break;                    
                case COLORS:
                    for(int i=0;i<inputs.size();i++){
                        g.setColor(Color.getHSBColor(1.0f-(float)getRadius(currentValues.get(i),WEIGHT/10,SHIFT),0.5f,1.0f));
                        g.fill(v.squareDot(inputs.get(i),10.0));
                    }
                    break;
                case CONTOURS:
                    g.setStroke(VisualStyle.THIN_STROKE);
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
        return value;
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
    public String toString(){return name;}
    
    String name="Plane Function";
        
    
    
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
}

