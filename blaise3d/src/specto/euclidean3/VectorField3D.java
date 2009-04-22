/*
 * PlaneFunction2D.java
 * 
 * Created on Sep 27, 2007, 1:19:00 PM
 */

package specto.euclidean3;

import specto.euclidean2.*;
import java.awt.Color;
import java.awt.Graphics2D;
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
import sequor.model.FunctionTreeModel;
import scio.coordinate.R3;
import scio.diffeq.DESolve;
import scio.function.BoundedFunction;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.component.RangeTimer;
import sequor.model.IntegerRangeModel;
import specto.Animatable;
import specto.Plottable;

/**
 * <p> 
 * Draws a vector field in space using a projection onto the Cartesian plane.
 * </p>
 * 
 * @author ae3263
 */
public class VectorField3D extends Plottable<Euclidean3> implements Animatable<Euclidean3>, ChangeListener {
    /** The underlying function for the vector field. */
    BoundedFunction<R3,R3> function;    
    
    /** A default vector field to use */    
    public static final BoundedFunction<R3,R3> DEFAULT_FUNCTION=new BoundedFunction<R3,R3>(){
        public R3 getValue(R3 p){return new R3(p.getY()*p.getZ(),-p.getX()*p.getZ(),Math.sqrt(p.getZ()+5));}
        @Override
        public Vector<R3> getValue(Vector<R3> x) {
            Vector<R3> result=new Vector<R3>(x.size());
            for(R3 r:x){result.add(getValue(r));}
            return result;
        }
        public R3 minValue(){return new R3(-5.0,-5.0,-5.0);}
        public R3 maxValue(){return new R3(5.0,5.0,5.0);}
    };
    
    // CONSTRUCTORS
        
    public VectorField3D(){this(DEFAULT_FUNCTION);}
    
    public VectorField3D(BoundedFunction<R3,R3> function){
        this.function=function;
        setColor(Color.BLUE);
        style.setValue(ARROWS);
    }
    
    public VectorField3D(final FunctionTreeModel fm1, final FunctionTreeModel fm2, final FunctionTreeModel fm3) {
        Vector<String> vars = new Vector<String>();
        vars.add("x"); vars.add("y"); vars.add("z");
        fm1.getRoot().setVariables(vars);
        fm2.getRoot().setVariables(vars);
        fm3.getRoot().setVariables(vars);
        function = getVectorFunction(
                (BoundedFunction<R3,Double>) fm1.getRoot().getFunction(3),
                (BoundedFunction<R3,Double>) fm2.getRoot().getFunction(3),
                (BoundedFunction<R3,Double>) fm3.getRoot().getFunction(3)
                );
        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                function = getVectorFunction(
                        (BoundedFunction<R3,Double>) fm1.getRoot().getFunction(3),
                        (BoundedFunction<R3,Double>) fm2.getRoot().getFunction(3),
                        (BoundedFunction<R3,Double>) fm3.getRoot().getFunction(3)
                        );
                fireStateChanged();
            }
        };
        fm1.addChangeListener(cl);
        fm2.addChangeListener(cl);  
        fm3.addChangeListener(cl);   
        setColor(Color.BLUE); 
        style.setValue(ARROWS);    
    }
    
    // HELPERS
    
    public static BoundedFunction<R3, R3> getVectorFunction(final BoundedFunction<R3,Double> fx, final BoundedFunction<R3,Double> fy, final BoundedFunction<R3,Double> fz) {
        return new BoundedFunction<R3, R3>() {
            public R3 getValue(R3 pt) throws FunctionValueException { return new R3(fx.getValue(pt), fy.getValue(pt), fz.getValue(pt)); }
            public Vector<R3> getValue(Vector<R3> xx) throws FunctionValueException {
                Vector<Double> xs = fx.getValue(xx);
                Vector<Double> ys = fy.getValue(xx);
                Vector<Double> zs = fz.getValue(xx);
                Vector<R3> result = new Vector<R3>(xs.size());
                for(int i=0; i<xs.size(); i++){
                    result.add(new R3(xs.get(i),ys.get(i),zs.get(i)));
                }
                return result;
            }
            public R3 minValue() { return new R3(fx.minValue(),fy.minValue(),fz.minValue()); }
            public R3 maxValue() { return new R3(fx.maxValue(),fy.maxValue(),fz.maxValue()); }
        };
    }

    public VectorField3D(final FunctionTreeModel fm1, final FunctionTreeModel fm2, final FunctionTreeModel fm3,
            String varx, String vary, String varz) {
        Vector<String> vars = new Vector<String>();
        vars.add(varx); vars.add(vary); vars.add(varz);
        final String[] varsArray = {varx, vary, varz};
        fm1.getRoot().setVariables(vars);
        fm2.getRoot().setVariables(vars);
        fm3.getRoot().setVariables(vars);
        function = getVectorFunction(
                (BoundedFunction<R3,Double>) fm1.getRoot().getFunction(varsArray),
                (BoundedFunction<R3,Double>) fm2.getRoot().getFunction(varsArray),
                (BoundedFunction<R3,Double>) fm3.getRoot().getFunction(varsArray)
                );
        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                function = getVectorFunction(
                        (BoundedFunction<R3,Double>) fm1.getRoot().getFunction(varsArray),
                        (BoundedFunction<R3,Double>) fm2.getRoot().getFunction(varsArray),
                        (BoundedFunction<R3,Double>) fm3.getRoot().getFunction(varsArray)
                        );
                fireStateChanged();
            }
        };
        fm1.addChangeListener(cl);
        fm2.addChangeListener(cl);
        fm3.addChangeListener(cl);
        setColor(Color.BLUE);
        style.setValue(ARROWS);
    }
        
        
        
    // BEAN PATTERNS
    
    /** Whether this element animates. */    
    public boolean animationOn=true;

    public void setAnimationOn(boolean newValue) { animationOn=newValue; }
    public boolean isAnimationOn() { return animationOn; }
        
    public BoundedFunction<R3,R3> getFunction(){return function;}

    public void setFunction(BoundedFunction<R3,R3> function){this.function=function;}
    
        
    // DRAW METHODS
    
    /** Stores the sample points. */
    Vector<R3> samplePoints;    
    /** Stores the vectors at these points. */
    Vector<R3> vectors;
    /** Stores the vector length for proper scaling. */
    TreeSet<Double> lengths;

    /** Stores length multiplier. */
    double lengthMultiplier=1;
    /** Stores scaling multiplier. */
    double step;
    
    /** Stores paths representing flowlines. */
    Vector<Vector<R3>> flows;
    
    /** Recompute the vectors for the field. */   
    @Override
    public void recompute(Euclidean3 v) {
        super.recompute(v);
        if (samplePoints==null){ 
            samplePoints = new Vector<R3>(); 
            vectors = new Vector<R3>();
            lengths = new TreeSet<Double>();
        } else {
            samplePoints.clear();
            vectors.clear();
            lengths.clear();
        }
        step = (v.xRange.getStep()>v.yRange.getStep())?v.xRange.getStep():v.yRange.getStep();
        step = (step>v.zRange.getStep())?step:v.zRange.getStep();
        R3 point;
        try {
            R3 vec;
            for (double px:v.xRange.getValueRange(true, 0.0)){
                for (double py:v.yRange.getValueRange(true, 0.0)){
                    for (double pz:v.zRange.getValueRange(true, 0.0)){
                        point = new R3(px, py, pz);
                        samplePoints.add(point);
                        vec = DESolve.R3S.getMultipliedVector(function, point, 0.2 * step);
                        vectors.add(vec);
                        lengths.add(vec.magnitudeSq());
                    }
                }
            }
            lengthMultiplier = .8*step / Math.sqrt((Double) lengths.toArray()[(int)(.9*lengths.size())]);
            for (R3 vector : vectors) {
                vector.multiplyBy(lengthMultiplier);
            }
        } catch (FunctionValueException ex) {
            Logger.getLogger(VectorField3D.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Paints the vecotr field. */
    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v){
        int NUM = flowLength.getValue();
        switch(style.getValue()){
            case ARROWS:
                for (int i = 0; i < samplePoints.size(); i++) {
                    v.drawArrow(g, samplePoints.get(i), samplePoints.get(i).plus(vectors.get(i)), 5.0);
                }
                break;
            case TRAILS:
                try {
                    for (int i = 0; i < samplePoints.size(); i++) {
                        v.drawPath(g, DESolve.R3S.calcNewton(function, samplePoints.get(i), NUM, .75 * step / NUM));
                        v.drawPath(g, DESolve.R3S.calcNewton(function, samplePoints.get(i), NUM, -.75*step/NUM));
                    }
                } catch (FunctionValueException ex) {
                    Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case DOT_LINES:
                for (int i = 0; i < samplePoints.size(); i++) {
                    v.fillDot(g, samplePoints.get(i), 2);
                    v.drawLineSegment(g, samplePoints.get(i).plus(vectors.get(i).times(.5)), samplePoints.get(i).minus(vectors.get(i).times(.5)));
                }
                break;
            case LINES:
            default:
                for (int i = 0; i < samplePoints.size(); i++) {
                    v.drawLineSegment(g, samplePoints.get(i).plus(vectors.get(i).times(.5)), samplePoints.get(i).minus(vectors.get(i).times(.5)));
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
    public void paintComponent(Graphics2D g, final Euclidean3 v, final RangeTimer t) {
        final int NUM = flowLength.getValue();
        if(style.getValue()!=TRAILS){
            paintComponent(g, v);
        } else {
            if(flows==null){
                flows = new Vector<Vector<R3>>();
                try {
                    if(standardFlows) {
                        for (int i = 0; i < samplePoints.size(); i++) {
                            flows.add(DESolve.R3S.calcNewton(function, samplePoints.get(i), NUM, .75 * step / NUM));
                        }
                    } else {
                        for (int i = 0; i < numRandom.getValue(); i++) {
                            flows.add(DESolve.R3S.calcNewton(function, v.getR3Value(), NUM, .75*step/NUM));
                        }
                    }
                } catch (FunctionValueException ex) {
                    Logger.getLogger(VectorField2D.class.getName()).log(Level.SEVERE, null, ex);
                }
                t.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        if (e.getActionCommand()==null){return;}
                        if (e.getActionCommand().equals("restart")){
                            flows = new Vector<Vector<R3>>();
                            try {
                                if(standardFlows) {
                                    for (int i = 0; i < samplePoints.size(); i++) {
                                        flows.add(DESolve.R3S.calcNewton(function, samplePoints.get(i), NUM, .75 * step / NUM));
                                    }
                                } else {
                                    for (int i = 0; i < numRandom.getValue(); i++) {
                                        flows.add(DESolve.R3S.calcNewton(function, v.getR3Value(), NUM, .75*step/NUM));
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
                            DESolve.R3S.calcNewton(function, flows.get(i), t.getSpeed()+2, .75 * step / NUM);
                        }
                    } else {
                        for (int i = 0; i < turnover.getValue(); i++) {
                            flows.remove(0);
                        }
                        for (int i = 0; i < flows.size(); i++) {
                            DESolve.R3S.calcNewton(function, flows.get(i), t.getSpeed()+2, .75 * step / NUM);
                        }
                        for (int i = 0; i < turnover.getValue(); i++) {
                            flows.add(DESolve.R3S.calcNewton(function, v.getR3Value(), NUM, .75*step/NUM));
                        }                        
                    }
                } catch (FunctionValueException ex) {
                    Logger.getLogger(VectorField3D.class.getName()).log(Level.SEVERE, null, ex);
                }
            }    
            for (int i = 0; i < flows.size(); i++) {
                v.drawPath(g, flows.get(i));
            }   
        }
    }

    /** Only need a few steps for the animation */
    public int getAnimatingSteps() { return 10; }
    
    
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
    
    
    // STATIC METHODS
        
//    public static BoundedFunction<R3, R3> getCurlFunction(final BoundedFunction<R3,Double> fx, final BoundedFunction<R3,Double> fy, final BoundedFunction<R3,Double> fz) {
//        return new BoundedFunction<R3, R3>() {
//        };
//    }

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
        public void stateChanged(ChangeEvent e) { VectorField3D.this.fireStateChanged(); }
    }
}

