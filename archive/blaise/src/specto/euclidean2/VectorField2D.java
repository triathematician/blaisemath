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
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.function.FunctionValueException;
import sequor.component.RangeTimer;
import sequor.model.FunctionTreeModel;
import specto.Plottable;
import scio.coordinate.R2;
import scio.diffeq.DESolve;
import scio.function.Function;
import scribo.tree.FunctionTreeRoot;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.model.IntegerRangeModel;
import sequor.model.PointRangeModel;
import sequor.model.StringRangeModel;
import sequor.style.LineStyle;
import specto.Animatable;

/**
 * Draws a one-input/two-output curve on the Cartesian Plane. Requires two functions and a range of t-values.
 * @author ae3263
 */
public class VectorField2D extends Plottable<Euclidean2> implements Animatable<Euclidean2>, ChangeListener {
    
    /** The underlying function for the vector field. */
    Function<R2,R2> function;
    /** The x function, as a tree. */
    FunctionTreeModel xFunction;
    /** The y function, as a tree. */
    FunctionTreeModel yFunction;
    
    /** A default vector field to use */    
    public static final Function<R2,R2> DEFAULT_FUNCTION=new Function<R2,R2>(){
        public R2 getValue(R2 p){return new R2(2*p.y,-p.y-5*Math.sin(p.x));}
        @Override
        public Vector<R2> getValue(Vector<R2> x) {
            Vector<R2> result=new Vector<R2>(x.size());
            for(R2 r:x){result.add(getValue(r));}
            return result;
        }
    };
        
    
    // CONSTRUCTORS
        
    public VectorField2D(){
        this(DEFAULT_FUNCTION);
    }
    public VectorField2D(Function<R2,R2> function){
        this.function=function;
        setColor(Color.BLUE);
        style.setValue(ARROWS);
    }
    public VectorField2D(FunctionTreeModel functionModel1, FunctionTreeModel functionModel2) {
        this(functionModel1, functionModel2, "x", "y");
    }
    public VectorField2D(final FunctionTreeModel functionModel1, final FunctionTreeModel functionModel2, String varx, String vary) {
        Vector<String> vars = new Vector<String>();
        vars.add(varx); vars.add(vary);
        final String[] varsArray = {varx, vary};
        functionModel1.getRoot().setVariables(vars);
        functionModel2.getRoot().setVariables(vars);
        xFunction = functionModel1;
        yFunction = functionModel2;
        function = getVectorFunction(
                (Function<R2,Double>) functionModel1.getRoot().getFunction(varsArray),
                (Function<R2,Double>) functionModel2.getRoot().getFunction(varsArray));
        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                function = getVectorFunction(
                        (Function<R2,Double>) functionModel1.getRoot().getFunction(varsArray),
                        (Function<R2,Double>) functionModel2.getRoot().getFunction(varsArray));
                fireStateChanged();
            }
        };
        functionModel1.addChangeListener(cl);
        functionModel2.addChangeListener(cl);   
        setColor(Color.BLUE); 
        style.setValue(ARROWS);    
    }
    
    // HELPERS
    
    public static Function<R2, R2> getVectorFunction(final Function<R2,Double> fx, final Function<R2,Double> fy) {
        return new Function<R2, R2>() {
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
        };
    }
    
    
    // BEAN PATTERNS
    
    /** Whether this element animates. */    
    public boolean animationOn=true;
    public void setAnimationOn(boolean newValue) { animationOn=newValue; }
    public boolean isAnimationOn() { return animationOn; }
    
    public Function<R2,R2> getFunction(){return function;}
    public void setFunction(Function<R2,R2> function){this.function=function;}

    
    // DRAW METHODS
    
    /** Stores the sample points. */
    Vector<R2> samplePoints;    
    /** Stores the vectors at these points. */
    Vector<R2> vectors;
    /** Stores the vector length for proper scaling. */
    TreeSet<Double> lengths;

    /** Stores length multiplier. */
    double lengthMultiplier=1;
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
            lengths = new TreeSet<Double>();
        } else {
            samplePoints.clear();
            vectors.clear();
            lengths.clear();
        }
        Vector<Double> xRange=v.getSparseXRange(30);
        Vector<Double> yRange=v.getSparseYRange(30);
        step = Math.min(xRange.get(1)-xRange.get(0), yRange.get(1)-yRange.get(0));
        R2 point;
        try {
            R2 vec;
            // generate regularly spaced vectors
            for (double px:xRange){
                for (double py:yRange){
                        point = new R2(px, py);
                        samplePoints.add(point);
                        vec = DESolve.R2S.getMultipliedVector(function, point, 1);
                        vectors.add(vec);
                        lengths.add(vec.magnitudeSq());
                }
            }
            lengthMultiplier = .8*step / Math.sqrt((Double) lengths.toArray()[(int)(.9*lengths.size())]);
            for (R2 vector : vectors) {
                vector.multiplyBy(lengthMultiplier);
            }
        } catch (FunctionValueException ex) {
            Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Paints the vecotr field. */
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v){
        g.setStroke(LineStyle.STROKES[LineStyle.VERY_THIN]);
        int NUM = flowLength.getValue();
        switch(style.getValue()){
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
                        g.draw(v.path(DESolve.R2S.calcNewton(function, samplePoints.get(i), NUM, .75*step/NUM)));
                        g.draw(v.path(DESolve.R2S.calcNewton(function, samplePoints.get(i), NUM, -.75*step/NUM)));
                    }
                } catch (FunctionValueException ex) {
                    Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case DOT_LINES:
                for (int i = 0; i < samplePoints.size(); i++) {
                    g.fill(v.dot(samplePoints.get(i),2));
                    g.draw(v.lineSegment(samplePoints.get(i).plus(vectors.get(i).times(.5)),samplePoints.get(i).minus(vectors.get(i).times(.5))));
                }
                break;
            case LINES:
            default:
                for (int i = 0; i < samplePoints.size(); i++) {
                    g.draw(v.lineSegment(samplePoints.get(i).plus(vectors.get(i).times(.5)),samplePoints.get(i).minus(vectors.get(i).times(.5))));
                }
                break;
        }
    }
    
    /** Determines whether lines are drawn at random positions and recycled over time or drawn at fixed points. */
    boolean standardFlows = false;    
    /** Determines the number of random flows. */
    IntegerRangeModel numRandom = new IntegerRangeModel(1000,0,10000,10);    
    /** Number to remove each time. */
    IntegerRangeModel turnover = new IntegerRangeModel(10,0,1000,1);
    /** Length of flow lines (in # of steps. */
    IntegerRangeModel flowLength = new IntegerRangeModel(10,0,100,1);

    /** Animates vector field (flow lines only) */
    public void paintComponent(Graphics2D g, final Euclidean2 v, final RangeTimer t) {
        g.setStroke(LineStyle.STROKES[LineStyle.VERY_THIN]);
        final int NUM = flowLength.getValue();
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
                        for (int i = 0; i < numRandom.getValue(); i++) {
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
                                    for (int i = 0; i < numRandom.getValue(); i++) {
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
                        for (int i = 0; i < turnover.getValue(); i++) {
                            flows.remove(0);
                        }
                        for (int i = 0; i < flows.size(); i++) {
                            DESolve.R2S.calcNewton(function, flows.get(i), t.getSpeed()+2, .75 * step / NUM);
                        }
                        for (int i = 0; i < turnover.getValue(); i++) {
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
    public int getAnimatingSteps() { return flowLength.getValue(); }
    
    
    // STYLE METHODS

    public static final int LINES=0;            // lines in the direction of the curves
    public static final int DOT_LINES=1;        // dots with lines through them
    public static final int ARROWS=2;           // arrows in the direction of the curves
    public static final int TRAILS=3;           // plot a few points forward and back from the current point
    static final String[] styleStrings={"Slopelines","Slopelines with dots","Vectors","Flows"};
    
    @Override
    public String[] getStyleStrings() {
        return styleStrings;
    }
    
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
        if (xFunction == null || yFunction == null) {
            return null;
        }
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
        if (xFunction == null || yFunction == null) {
            return null;
        }
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
    public static Function<R2, Double> getDivergence(final FunctionTreeModel fx, final FunctionTreeModel fy) {
        return new Function<R2, Double> () {
            String[] vars = {"x", "y"};
            Function<R2, Double> fxx = (Function<R2, Double>) ((FunctionTreeRoot)fx.getRoot().derivativeTree("x")).getFunction(vars);
            Function<R2, Double> fyy = (Function<R2, Double>) ((FunctionTreeRoot)fy.getRoot().derivativeTree("y")).getFunction(vars);
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
    public static Function<R2, Double> getScalarCurl(final FunctionTreeModel fx, final FunctionTreeModel fy) {
        return new Function<R2, Double> () {
            String[] vars = {"x", "y"};
            Function<R2, Double> fxy = (Function<R2, Double>) ((FunctionTreeRoot)fx.getRoot().derivativeTree("y")).getFunction(vars);
            Function<R2, Double> fyx = (Function<R2, Double>) ((FunctionTreeRoot)fy.getRoot().derivativeTree("x")).getFunction(vars);
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

    // SETTINGS

    /** Overrides to allow setting custom options. */
    @Override
    public JMenu getOptionsMenu() {
        JMenu menu = super.getOptionsMenu();
        menu.add(new JSeparator(),0);
        JMenuItem mi = new JMenuItem("Vector Field Options");
        mi.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (ps==null) { ps = new FlowSettings(); }
                if (psd==null) { psd = ps.getDialog(null, false); }
                psd.setVisible(true);
            }
        });
        menu.add(mi,0);
        return menu;
    }

    FlowSettings ps;
    JDialog psd;

    /** Inner class controls options for displaying the curve... starting point, ending point, and number of steps */
    public class FlowSettings extends Settings {
        /** Initializes, and sets up listening. */
        public FlowSettings() {
            setName("Vector Field Options");
            add(new SettingsProperty("style",style,Settings.EDIT_COMBO));
            add(new SettingsProperty("color",color,Settings.EDIT_COLOR));
            add(new SettingsProperty("flow length",flowLength,Settings.EDIT_INTEGER_SLIDER));
            add(new SettingsProperty("# flows",numRandom,Settings.EDIT_INTEGER));
            add(new SettingsProperty("flow turnover",turnover,Settings.EDIT_INTEGER_SLIDER));
        }
        /** Event handling... all taken care of by the models */
        @Override
        public void stateChanged(ChangeEvent e) { VectorField2D.this.fireStateChanged(); }
    }
}

