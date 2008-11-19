/*
 * Parametric2D.java
 * Created on Sep 27, 2007, 1:12:35 PM
 */

package specto.euclidean3;

import java.awt.Graphics2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import scio.function.FunctionValueException;
import scribo.parser.FunctionSyntaxException;
import sequor.model.FunctionTreeModel;
import java.awt.Color;
import java.util.Vector;
import javax.swing.event.ChangeListener;
import scio.coordinate.R2;
import scio.function.Function;
import scio.coordinate.R3;
import scio.function.BoundedFunction;
import scio.function.Derivative;
import scribo.tree.FunctionTreeRoot;
import sequor.component.RangeTimer;
import sequor.model.DoubleRangeModel;
import sequor.model.PointRangeModel;
import sequor.style.VisualStyle;
import specto.Constrains2D;
import specto.Decoration;
import specto.Plottable;
import specto.PlottableGroup;
import specto.euclidean2.Point2D;

/**
 * Draws a parametric function on the plane. In other words, it contains two functions which give the x and y coordinates
 * of the function in terms of some other parameter (frequently 't'). The properties of this class which permit it to be
 * plotted are any function from Double->(Double,Double), and min/max values of t.
 * @author ElishaPeterson
 */
public class ParametricSurface3D extends PlottableGroup<Euclidean3> implements SampleSet3D, SampleVector3D {
    
    // PROPERTIES
    
    /** Function which takes in a pair of points and returns a pair of doubles = a point in the plane. */
    Function<R2,R3> function;

    /** Range of t values. */
    PointRangeModel uvRange;
    
    /** Defines a default function which is displayed. For now its the unit sphere */
    private static final Function<R2,R3> DEFAULT_FUNCTION=new Function<R2,R3>(){
        @Override
        public R3 getValue(R2 uv){return new R3(Math.cos(uv.y)*Math.sin(uv.x),Math.sin(uv.y)*Math.sin(uv.x),Math.cos(uv.x));}
        @Override
        public Vector<R3> getValue(Vector<R2> pts) {
            Vector<R3> result=new Vector<R3>(pts.size());
            for(R2 pt:pts){result.add(getValue(pt));}
            return result;
        }
    };
        
    public ParametricSurface3D(){this(DEFAULT_FUNCTION,0.0,Math.PI/2.0,0.0,2*Math.PI,10);}
    public ParametricSurface3D(String string) {this();}
    /** Constructor for use with a particular function and range of t values */
    public ParametricSurface3D(Function<R2,R3> function,double uMin,double uMax,double vMin,double vMax,int samplePoints){
        setColor(new Color(100,100,100,200));        
        this.function=function;
        uvRange = new PointRangeModel(uMin,uMax,vMin,vMax);
        uvRange.xModel.setNumSteps(samplePoints,true);
        uvRange.yModel.setNumSteps(samplePoints,true);
    }
    /** Constructs with specified function. */
    public ParametricSurface3D(Function<R2, R3> function, DoubleRangeModel drmu, DoubleRangeModel drmv, int samplePoints) {
        setColor(new Color(100,100,100,200));        
        this.function=function;
        uvRange = new PointRangeModel(drmu, drmv);
        uvRange.xModel.setNumSteps(samplePoints,true);
        uvRange.yModel.setNumSteps(samplePoints,true);
    }
    
    public ParametricSurface3D(final FunctionTreeModel fm1, final FunctionTreeModel fm2, final FunctionTreeModel fm3) {
        setColor(new Color(100,100,100,200));        
        uvRange = new PointRangeModel(0.0, 10.0, 0.0, 10.0);
        uvRange.xModel.setNumSteps(10,true);
        uvRange.yModel.setNumSteps(10,true);
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
        
    /** Return sampling points used for the region. */
    public Vector<R3> getSampleSet(int options) {
        boolean inclusive = (options == 0);
        try {
            return function.getValue(uvRange.getValueRange(inclusive, 0.0, 0.0));
        } catch (FunctionValueException ex) {
            Logger.getLogger(ParametricSurface3D.class.getName()).log(Level.SEVERE, null, ex);
            return new Vector<R3>();
        }
    }

    double ARROW_SCALE = 0.1;
    
    /** Returns sampling vectors used for the region... here the vectors point in the direction of the normal. */
    public Vector<R3[]> getSampleVectors(int options) {
        Vector<R3[]> result = new Vector<R3[]>();
        boolean inclusive = (options == 0);
        try {
            for (R2 pt : uvRange.getValueRange(inclusive, 0.0, 0.0)) {
                R3[] temp = {function.getValue(pt), getNormal(function, pt.x, pt.y).times(ARROW_SCALE) };
                result.add(temp);
            }
            return result;
        } catch (FunctionValueException ex) {
            Logger.getLogger(ParametricSurface3D.class.getName()).log(Level.SEVERE, null, ex);
            return new Vector<R3[]>();
        }
    }

    /** Returns sampling vectors used for the region... here the vectors point in the direction of the normal. */
    public Vector<R3[]> getSampleVectors(int options, Function<R3,R3> vectorField) {
        Vector<R3[]> result = new Vector<R3[]>();
        boolean inclusive = (options == 0);
        try {
            for (R2 pt : uvRange.getValueRange(inclusive, 0.0, 0.0)) {
                R3[] temp = {function.getValue(pt), vectorField.getValue(function.getValue(pt))};
                result.add(temp);
            }
            return result;
        } catch (FunctionValueException ex) {
            Logger.getLogger(ParametricSurface3D.class.getName()).log(Level.SEVERE, null, ex);
            return new Vector<R3[]>();
        }
    }
    
    /** Sets up the curves used to draw the figure. */
    public void initCurves(Euclidean3 v){        
        clear();
        
        for (double x : uvRange.xModel.getValueRange(true, 0.0)) {
            add(new ParametricCurve3D(getSliceFixedX(x, function),uvRange.yModel,100));
        }
        for (double y : uvRange.yModel.getValueRange(true, 0.0)) {
            add(new ParametricCurve3D(getSliceFixedY(y, function),uvRange.xModel,100));
        }
        
        for (Plottable p : plottables) {
            p.setColor(getColor());
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

    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v) {
        super.paintComponent(g, v);
    }
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean3 v, RangeTimer t) {
        super.paintComponent(g, v);
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
    
    /** Returns tangent vector in direction of x */
    public static R3 getTangentX(Function<R2,R3> function, double x, double y) throws FunctionValueException {
        return Derivative.approximateDerivative(getSliceFixedY(y, function), x, .001);
    }
    /** Returns tangent vecotr in direction of y */
    public static R3 getTangentY(Function<R2,R3> function, double x, double y) throws FunctionValueException {
        return Derivative.approximateDerivative(getSliceFixedX(x, function), y, .001);
    }
    /** Returns normal vector */
    public static R3 getNormal(Function<R2,R3> function, double x, double y) throws FunctionValueException {
        return getTangentX(function, x, y).cross(getTangentY(function, x, y));
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
    
    
    // DECORATIONS
    
    /** Returns a point on the surface. */
    public SurfacePoint getSurfacePoint() { return new SurfacePoint(uvRange); }    
    /** Returns normal vector field. */
    public SurfaceNormals getNormalVectors() { return new SurfaceNormals(); }
    /** Returns vector field on the surface. */
    public SurfaceField getSurfaceField(VectorField3D field) { return new SurfaceField(field); }
    
    /** A point which you can move around on the surface. */
    public class SurfacePoint extends Point3D implements Decoration<Euclidean3,ParametricSurface3D>, Constrains2D {
        
        Segment3D normal;
        Segment3D dx;
        Segment3D dy;
        PointRangeModel domain;
        
        public SurfacePoint(PointRangeModel prm){
            super();
            setColor(Color.RED);
            domain=new PointRangeModel(prm.getX(),prm.getY());
            domain.xModel.setRangeProperties(prm.getX(), prm.getMinX(), prm.getMaxX());
            domain.yModel.setRangeProperties(prm.getY(), prm.getMinY(), prm.getMaxY());
            domain.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e) {
                    try {
                        R3 pt = function.getValue(domain.getPoint());
                        SurfacePoint.super.setPoint(pt);
                        R3 pdx = getTangentX(function, domain.getX(), domain.getY());
                        R3 pdy = getTangentY(function, domain.getX(), domain.getY());
                        R3 pn = pdx.cross(pdy);
                        dx.setTo(pt.minus(pdx.times(.5)),pt.plus(pdx.times(.5)));
                        dy.setTo(pt.minus(pdy.times(.5)),pt.plus(pdy.times(.5)));
                        normal.setTo(pt,pt.plus(pn));
                        fireStateChanged();
                    } catch (FunctionValueException ex) {
                        Logger.getLogger(ParametricSurface3D.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            normal=new Segment3D(this.prm, this.getPoint());
            normal.style.setValue(Segment3D.LINE_VECTOR);
            normal.setEditable(false);
            normal.addChangeListener(this);
            dx=new Segment3D(this.prm, this.getPoint());
            dx.style.setValue(Segment3D.LINE_VECTOR);
            dx.setEditable(false);
            dx.addChangeListener(this);
            dy=new Segment3D(this.prm, this.getPoint());
            dy.style.setValue(Segment3D.LINE_VECTOR);
            dy.setEditable(false);
            dy.addChangeListener(this);
        }
        
        // BEAN PATTERNS
        
        public PointRangeModel getConstraintModel() { return domain; }
        public Point2D getConstrainedPoint() { Point2D result = new Point2D(domain); result.setColor(getColor()); return result; }
        
        public void setParent(ParametricSurface3D parent) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ParametricSurface3D getParent() {
            return ParametricSurface3D.this;
        }
        
        // PAINT METHODS

        @Override
        public void paintComponent(Graphics2D g, Euclidean3 v) {
            g.setComposite(VisualStyle.COMPOSITE5);
            g.setColor(Color.YELLOW);
            Vector<R3> sfcPath = new Vector<R3>();
            sfcPath.add(dx.getPoint1().plus(dy.getPoint1()).minus(getPoint()));
            sfcPath.add(dx.getPoint1().plus(dy.getPoint2()).minus(getPoint()));
            sfcPath.add(dx.getPoint2().plus(dy.getPoint2()).minus(getPoint()));
            sfcPath.add(dx.getPoint2().plus(dy.getPoint1()).minus(getPoint()));
            sfcPath.add(dx.getPoint1().plus(dy.getPoint1()).minus(getPoint()));
            dx.paintComponent(g, v);
            dy.paintComponent(g, v);
            v.fillPath(g, sfcPath);
            g.setColor(getColor());
            normal.paintComponent(g, v);
            g.setComposite(VisualStyle.COMPOSITE10);
            super.paintComponent(g, v);
        }        
    }
    
    
    /** Draws a vector field on a surface... optionally displays also the curl of the field.
     */
    public class SurfaceField extends Plottable<Euclidean3> implements Decoration<Euclidean3,ParametricSurface3D> {

        VectorField3D field;
        
        public SurfaceField(VectorField3D field) {
            this.field = field;
        }
        
        @Override
        public void paintComponent(Graphics2D g, Euclidean3 v) {
            g.setColor(getColor());
            v.drawArrows(g, getSampleVectors(1, field.getFunction()), 5.0);
        }

        @Override
        public String[] getStyleStrings() {
            String[] result = {};
            return result;
        }

        public void setParent(ParametricSurface3D parent) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ParametricSurface3D getParent() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    /** Draws normal vectors on the surface.
     */
    public class SurfaceNormals extends Plottable<Euclidean3> implements Decoration<Euclidean3,ParametricSurface3D> {
                
        @Override
        public void paintComponent(Graphics2D g, Euclidean3 v) {
            g.setColor(new Color(255, 100, 100, 150));
            v.drawArrows(g, getSampleVectors(1), 5.0);
        }

        @Override
        public String[] getStyleStrings() {
            String[] result = {};
            return result;
        }

        public void setParent(ParametricSurface3D parent) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public ParametricSurface3D getParent() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
