/*
 * Parametric2D.java
 * Created on Sep 27, 2007, 1:12:35 PM
 */

// TODO when function is assigned to this, compute the appropriate range of vector lengths

package specto.euclidean2;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.function.FunctionValueException;
import sequor.component.RangeTimer;
import sequor.model.FunctionTreeModel;
import specto.Plottable;
import scio.coordinate.R2;
import scio.diffeq.DESolve;
import scio.function.BoundedFunction;
import scribo.tree.FunctionTreeRoot;
import sequor.model.DoubleRangeModel2;
import sequor.model.PointRangeModel;
import specto.Animatable;

/**
 * Draws a one-input/two-output curve on the Cartesian Plane. Requires two functions and a range of t-values.
 * @author ae3263
 */
public class VectorField2D extends Plottable<Euclidean2> implements Animatable<Euclidean2>, ChangeListener {
    
    /** The underlying function for the vector field. */
    BoundedFunction<R2,R2> function;
    /** The x function, as a tree. */
    FunctionTreeModel xFunction;
    /** The y function, as a tree. */
    FunctionTreeModel yFunction;
    
    /** A default vector field to use */    
    public static final BoundedFunction<R2,R2> DEFAULT_FUNCTION=new BoundedFunction<R2,R2>(){
        public R2 getValue(R2 p){return new R2(2*p.y,-p.y-5*Math.sin(p.x));}
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
        
    public VectorField2D(){
        this(DEFAULT_FUNCTION);
        xFunction = new FunctionTreeModel("2y");
        yFunction = new FunctionTreeModel("-y-5sin(x)");
    }
    public VectorField2D(BoundedFunction<R2,R2> function){
        this.function=function;
        setColor(Color.BLUE);
        style.setValue(ARROWS);
    }
    public VectorField2D(final FunctionTreeModel functionModel1, final FunctionTreeModel functionModel2) {
        Vector<String> vars = new Vector<String>();
        vars.add("x"); vars.add("y");
        functionModel1.getRoot().setVariables(vars);
        functionModel2.getRoot().setVariables(vars);
        xFunction = functionModel1;
        yFunction = functionModel2;
        function = getVectorFunction(
                (BoundedFunction<R2,Double>) functionModel1.getRoot().getFunction(2),
                (BoundedFunction<R2,Double>) functionModel2.getRoot().getFunction(2));
        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                function = getVectorFunction(
                        (BoundedFunction<R2,Double>) functionModel1.getRoot().getFunction(2),
                        (BoundedFunction<R2,Double>) functionModel2.getRoot().getFunction(2));
                fireStateChanged();
            }
        };
        functionModel1.addChangeListener(cl);
        functionModel2.addChangeListener(cl);   
        setColor(Color.BLUE); 
        style.setValue(ARROWS);    
    }
    
    // HELPERS
    
    public static BoundedFunction<R2, R2> getVectorFunction(final BoundedFunction<R2,Double> fx, final BoundedFunction<R2,Double> fy) {
        return new BoundedFunction<R2, R2>() {
            public R2 getValue(R2 pt) throws FunctionValueException { return new R2(fx.getValue(pt), fy.getValue(pt)); }
            public Vector<R2> getValue(Vector<R2> xx) throws FunctionValueException {
                Vector<Double> xs = fx.getValue(xx);
                Vector<Double> ys = fy.getValue(xx);
                Vector<R2> result = new Vector<R2>(xs.size());
                for(int i=0; i<xs.size(); i++){
                    result.add(new R2(xs.get(i),ys.get(i)));
                }
                return result;
            }
            public R2 minValue() { return new R2(fx.minValue(),fy.minValue()); }
            public R2 maxValue() { return new R2(fx.maxValue(),fy.maxValue()); }
        };
    }
    
    
    // BEAN PATTERNS
    
    /** Whether this element animates. */    
    public boolean animationOn=true;
    public void setAnimationOn(boolean newValue) { animationOn=newValue; }
    public boolean isAnimationOn() { return animationOn; }
    
    public BoundedFunction<R2,R2> getFunction(){return function;}
    public void setFunction(BoundedFunction<R2,R2> function){this.function=function;}

    
    // DRAW METHODS
    
    /** Stores the sample points. */
    Vector<R2> samplePoints;
    
    /** Stores the vectors at these points. */
    Vector<R2> vectors;
    
    /** Stores scaling multiplier. */
    double step;
    
    /** Stores paths representing flowlines. */
    Vector<Vector<R2>> flows;
    
    /** Recompute the vectors for the field. */    
    @Override
    public void recompute(Euclidean2 v) {
        super.recompute(v);
        if (samplePoints==null){ 
            samplePoints = new Vector<R2>(); 
            vectors = new Vector<R2>();
        } else {
            samplePoints.clear();
            vectors.clear();
        }
        Vector<Double> xRange=v.getSparseXRange(30);
        Vector<Double> yRange=v.getSparseYRange(30);
        double stepX=xRange.get(1)-xRange.get(0);
        double stepY=yRange.get(1)-yRange.get(0);
        step=(stepX>stepY)?stepX:stepY;
        R2 point;
        try {
            for (double px:xRange){
                for (double py:yRange){
                        point = new R2(px, py);
                        samplePoints.add(point);
                        vectors.add(DESolve.R2S.getMultipliedVector(function, point, 0.6 * step));
                }
            }
        } catch (FunctionValueException ex) {
            Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Paints the vecotr field. */
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v){
        R2 vector;
        switch(style.getValue()){
            case DOT_LINES:
                for (int i = 0; i < samplePoints.size(); i++) {
                    g.fill(v.dot(samplePoints.get(i),2));
                    g.draw(v.lineSegment(samplePoints.get(i).plus(vectors.get(i)),samplePoints.get(i).minus(vectors.get(i))));
                }
                break;
            case ARROWS:
                Shape arrow;
                for (int i = 0; i < samplePoints.size(); i++) {
                    arrow = v.arrow(samplePoints.get(i), samplePoints.get(i).plus(vectors.get(i)), 5.0);
                    g.draw(arrow);
                    g.fill(arrow);
                }
                break;
            case TRAILS:
                try {
                    for (int i = 0; i < samplePoints.size(); i++) {
                        g.draw(v.path(DESolve.R2S.calcNewton(function, samplePoints.get(i), NUM, .75 * step / NUM)));
                        g.draw(v.path(DESolve.R2S.calcNewton(function, samplePoints.get(i), NUM, -.75*step/NUM)));
                    }
                } catch (FunctionValueException ex) {
                    Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case LINES:
            default:
                for (int i = 0; i < samplePoints.size(); i++) {
                    g.draw(v.lineSegment(samplePoints.get(i).plus(vectors.get(i)),samplePoints.get(i).minus(vectors.get(i))));
                }
                break;
        }
    }
    
    /** Determines whether lines are drawn at random positions and recycled over time or drawn at fixed points. */
    boolean standardFlows = false;
    
    /** Determines the number of random flows. */
    int NUM_RANDOM = 1000;
    
    /** Number to remove each time. */
    int RANDOM_TURNOVER = 10;

    /** Animates vector field (flow lines only) */
    public void paintComponent(Graphics2D g, final Euclidean2 v, final RangeTimer t) {
        if(style.getValue()!=TRAILS){
            paintComponent(g, v);
        } else {
            if(flows==null){
                flows = new Vector<Vector<R2>>();
                try {
                    if(standardFlows) {
                        for (int i = 0; i < samplePoints.size(); i++) {
                            flows.add(DESolve.R2S.calcNewton(function, samplePoints.get(i), NUM, .75 * step / NUM));
                        }
                    } else {
                        for (int i = 0; i < NUM_RANDOM; i++) {
                            flows.add(DESolve.R2S.calcNewton(function, v.getRValue(), NUM, .75*step/NUM));
                        }
                    }
                } catch (FunctionValueException ex) {
                    Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
                }
                t.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        if (e.getActionCommand()==null){return;}
                        if (e.getActionCommand().equals("restart")){
                            flows = new Vector<Vector<R2>>();
                            try {
                                if(standardFlows) {
                                    for (int i = 0; i < samplePoints.size(); i++) {
                                        flows.add(DESolve.R2S.calcNewton(function, samplePoints.get(i), NUM, .75 * step / NUM));
                                    }
                                } else {
                                    for (int i = 0; i < NUM_RANDOM; i++) {
                                        flows.add(DESolve.R2S.calcNewton(function, v.getRValue(), NUM, .75*step/NUM));
                                    }
                                }
                            } catch (FunctionValueException ex) {
                                Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }                    
                });
            } else {
                try {
                    if(standardFlows) {
                        for (int i = 0; i < flows.size(); i++) {
                            DESolve.R2S.calcNewton(function, flows.get(i), t.getSpeed()+2, .75 * step / NUM);
                        }
                    } else {
                        for (int i = 0; i < RANDOM_TURNOVER; i++) {
                            flows.remove(0);
                        }
                        for (int i = 0; i < flows.size(); i++) {
                            DESolve.R2S.calcNewton(function, flows.get(i), t.getSpeed()+2, .75 * step / NUM);
                        }
                        for (int i = 0; i < RANDOM_TURNOVER; i++) {
                            flows.add(DESolve.R2S.calcNewton(function, v.getRValue(), NUM, .75*step/NUM));
                        }                        
                    }
                } catch (FunctionValueException ex) {
                    Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
                }
            }         
            for (int i = 0; i < flows.size(); i++) {
                g.draw(v.path(flows.get(i)));
            }   
        }
    }

    /** Only need a few steps for the animation */
    public int getAnimatingSteps() { return 10; }
    
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
    
    
    // DECORATIONS
    
    /** Returns solution to differential equation at a default initial point. */
    public DESolution2D getFlowCurve() { return new DESolution2D(this); }
    
    /** Returns solution to differential equation at a given initial point. */
    public DESolution2D getFlowCurve(PointRangeModel initialPoint) { 
        return new DESolution2D(initialPoint, this); 
    }
    
    /** Returns a plottable function representing the divergence of this vector field. */
    public PlaneFunction2D getDivergence() {
        final PlaneFunction2D result = new PlaneFunction2D(getDivergence(xFunction, yFunction));
        ChangeListener cl = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                result.setFunction(getDivergence(xFunction, yFunction));
            }            
        };
        xFunction.addChangeListener(cl);
        yFunction.addChangeListener(cl);
        result.setColor(Color.getHSBColor(0.5f,0.5f,0.9f));
        result.name="Divergence";
        return result;
    }
    
    /** Returns a plottable function representing the (scalar) curl of this vector field... the z component of the curl */
    public PlaneFunction2D getScalarCurl() { 
        final PlaneFunction2D result = new PlaneFunction2D(getScalarCurl(xFunction, yFunction));
        ChangeListener cl = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                result.setFunction(getScalarCurl(xFunction, yFunction));
            }            
        };
        xFunction.addChangeListener(cl);
        yFunction.addChangeListener(cl);
        result.setColor(Color.getHSBColor(0.2f,0.5f,0.9f));
        result.name="Curl";
        return result;
    }
    
    
    // STATIC METHODS
    
    /** Returns divergence of the specified vector field. */
    public static BoundedFunction<R2, Double> getDivergence(final FunctionTreeModel fx, final FunctionTreeModel fy) {
        return new BoundedFunction<R2, Double> () {
            String[] vars = {"x", "y"};
            BoundedFunction<R2, Double> fxx = (BoundedFunction<R2, Double>) new FunctionTreeRoot(fx.getRoot().derivativeTree("x")).getFunction(vars);
            BoundedFunction<R2, Double> fyy = (BoundedFunction<R2, Double>) new FunctionTreeRoot(fy.getRoot().derivativeTree("y")).getFunction(vars);
            public Double minValue() { return 0.0; }
            public Double maxValue() { return 5.0; }
            public Double getValue(R2 pt) throws FunctionValueException {
                return fxx.getValue(pt) + fyy.getValue(pt);
            }
            public Vector<Double> getValue(Vector<R2> pts) throws FunctionValueException {
                Vector<Double> resultx = fxx.getValue(pts);
                Vector<Double> resulty = fyy.getValue(pts);
                for(int i=0; i<resultx.size(); i++) {
                    resultx.set(i, resultx.get(i)+resulty.get(i));
                }
                return resultx;
            }            
        };
    }
    
    /** Returns curl of the specified vector field. */
    public static BoundedFunction<R2, Double> getScalarCurl(final FunctionTreeModel fx, final FunctionTreeModel fy) {
        return new BoundedFunction<R2, Double> () {
            String[] vars = {"x", "y"};
            BoundedFunction<R2, Double> fxy = (BoundedFunction<R2, Double>) new FunctionTreeRoot(fx.getRoot().derivativeTree("y")).getFunction(vars);
            BoundedFunction<R2, Double> fyx = (BoundedFunction<R2, Double>) new FunctionTreeRoot(fy.getRoot().derivativeTree("x")).getFunction(vars);
            public Double minValue() { return 0.0; }
            public Double maxValue() { return 5.0; }
            public Double getValue(R2 pt) throws FunctionValueException {
                return fyx.getValue(pt) - fxy.getValue(pt);
            }
            public Vector<Double> getValue(Vector<R2> pts) throws FunctionValueException {
                Vector<Double> resultx = fyx.getValue(pts);
                Vector<Double> resulty = fxy.getValue(pts);
                for(int i=0; i<resultx.size(); i++) {
                    resultx.set(i, resultx.get(i)-resulty.get(i));
                }
                return resultx;
            }            
        };
    }
}

