/*
 * VectorFieldTimed2D.java
 * Created on Oct 1, 2008
 */

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.function.FunctionValueException;
import sequor.component.RangeTimer;
import sequor.model.FunctionTreeModel;
import specto.Plottable;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.diffeq.DETimeSolve;
import scio.function.Function;
import sequor.style.LineStyle;
import specto.Animatable;

/**
 * Draws a one-input/two-output curve on the Cartesian Plane. Requires two functions and a range of t-values.
 * @author ae3263
 */
public class VectorFieldTimed2D extends Plottable<Euclidean2> implements Animatable<Euclidean2>, ChangeListener {
    
    /** The underlying function for the vector field... depends on time (3rd element) */
    Function<R3,R2> function;

    /** The x function, as a tree. */
    FunctionTreeModel xFunction;
    
    /** The y function, as a tree. */
    FunctionTreeModel yFunction;
            
    
    // CONSTRUCTORS
            
    public VectorFieldTimed2D(Function<R3,R2> function){
        this.function=function;
        setColor(Color.BLUE);
        style.setValue(ARROWS);
    }

    public VectorFieldTimed2D(){
        this(new FunctionTreeModel("2*y+t"), new FunctionTreeModel("-y*t-5sin(x)"), "t", "x", "y");
    }

    public VectorFieldTimed2D(final FunctionTreeModel functionModel1, final FunctionTreeModel functionModel2) {
        this(functionModel1, functionModel2, "t", "x", "y");
    }

    public VectorFieldTimed2D(final FunctionTreeModel functionModel1, final FunctionTreeModel functionModel2, String vart, String varx, String vary) {
        final String[] varsArray = {vart, varx, vary};
        functionModel1.getRoot().setVariables(varsArray);
        functionModel2.getRoot().setVariables(varsArray);
        xFunction = functionModel1;
        yFunction = functionModel2;
        function = getVectorFunction(
                (Function<R3,Double>) functionModel1.getRoot().getFunction(),
                (Function<R3,Double>) functionModel2.getRoot().getFunction());
        ChangeListener cl = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                function = getVectorFunction(
                        (Function<R3,Double>) functionModel1.getRoot().getFunction(),
                        (Function<R3,Double>) functionModel2.getRoot().getFunction());
                fireStateChanged();
            }
        };
        functionModel1.addChangeListener(cl);
        functionModel2.addChangeListener(cl);
        setColor(Color.BLUE);
        style.setValue(ARROWS);    
    }


    // HELPERS

    /** Returns a vector function, given two input functions. */
    public static Function<R3, R2> getVectorFunction(final Function<R3,Double> fx, final Function<R3,Double> fy) {
        return new Function<R3, R2>() {
            public R2 getValue(R3 pt) throws FunctionValueException { 
                return new R2(fx.getValue(pt), fy.getValue(pt));
            }
            public Vector<R2> getValue(Vector<R3> xx) throws FunctionValueException {
                Vector<R2> result = new Vector<R2>(xx.size());
                for(int i=0; i < xx.size(); i++){
                    result.add(new R2(fx.getValue(xx.get(i)), fy.getValue(xx.get(i))));
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
    
    public Function<R3,R2> getFunction(){return function;}
    public void setFunction(Function<R3,R2> function){this.function=function;}

    
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
    public void recompute(Euclidean2 v) { recompute(v, 0); }
    public void recompute(Euclidean2 v, double time) {
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
        double stepX=xRange.get(1)-xRange.get(0);
        double stepY=yRange.get(1)-yRange.get(0);
        step=(stepX>stepY)?stepX:stepY;
        R2 point;
        try {
            R2 vec;
            for (double px:xRange){
                for (double py:yRange){
                        point = new R2(px, py);
                        samplePoints.add(point);
                        vec = DETimeSolve.R2T.getMultipliedVector(function, time*2*Math.PI/1000, point, 1);
                        vectors.add(vec);
                        lengths.add(vec.magnitudeSq());
                }
            }
            lengthMultiplier = .8*step / Math.sqrt((Double) lengths.toArray()[(int)(.9*lengths.size())]);
            for (R2 vector : vectors) {
                vector.multiplyBy(lengthMultiplier);
            }
        } catch (FunctionValueException ex) {
            Logger.getLogger(VectorFieldTimed2D.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Paints the vecotr field. */
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v){
        g.setStroke(LineStyle.STROKES[LineStyle.VERY_THIN]);
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
                        g.draw(v.path(DETimeSolve.R2T.calcNewton(function, samplePoints.get(i), NUM, .75 * step / NUM)));
                        g.draw(v.path(DETimeSolve.R2T.calcNewton(function, samplePoints.get(i), NUM, -.75*step/NUM)));
                    }
                } catch (FunctionValueException ex) {
                    Logger.getLogger(VectorFieldTimed2D.class.getName()).log(Level.SEVERE, null, ex);
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
    int NUM_RANDOM = 1000;
    
    /** Number to remove each time. */
    int RANDOM_TURNOVER = 10;

    /** Animates vector field (flow lines only) */
    public void paintComponent(Graphics2D g, final Euclidean2 v, final RangeTimer t) {
        g.setStroke(LineStyle.STROKES[LineStyle.VERY_THIN]);
        if(style.getValue()!=TRAILS){
            recompute(v, (Integer)t.getCurrentValue());
            paintComponent(g, v);
        } else {
            if(flows==null){
                flows = new Vector<Vector<R2>>();
                try {
                    if(standardFlows) {
                        for (int i = 0; i < samplePoints.size(); i++) {
                            flows.add(DETimeSolve.R2T.calcNewton(function, samplePoints.get(i), NUM, .75 * step / NUM));
                        }
                    } else {
                        for (int i = 0; i < NUM_RANDOM; i++) {
                            flows.add(DETimeSolve.R2T.calcNewton(function, v.getRValue(), NUM, .75*step/NUM));
                        }
                    }
                } catch (FunctionValueException ex) {
                    Logger.getLogger(VectorFieldTimed2D.class.getName()).log(Level.SEVERE, null, ex);
                }
                t.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        if (e.getActionCommand()==null){return;}
                        if (e.getActionCommand().equals("restart")){
                            flows = new Vector<Vector<R2>>();
                            try {
                                if(standardFlows) {
                                    for (int i = 0; i < samplePoints.size(); i++) {
                                        flows.add(DETimeSolve.R2T.calcNewton(function, samplePoints.get(i), NUM, .75 * step / NUM));
                                    }
                                } else {
                                    for (int i = 0; i < NUM_RANDOM; i++) {
                                        flows.add(DETimeSolve.R2T.calcNewton(function, v.getRValue(), NUM, .75*step/NUM));
                                    }
                                }
                            } catch (FunctionValueException ex) {
                                Logger.getLogger(VectorFieldTimed2D.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }                    
                });
            } else {
                try {
                    if(standardFlows) {
                        for (int i = 0; i < flows.size(); i++) {
                            DETimeSolve.R2T.calcNewton(function, flows.get(i), t.getSpeed()+2, .75 * step / NUM);
                        }
                    } else {
                        for (int i = 0; i < RANDOM_TURNOVER; i++) {
                            flows.remove(0);
                        }
                        for (int i = 0; i < flows.size(); i++) {
                            DETimeSolve.R2T.calcNewton(function, flows.get(i), t.getSpeed()+2, .75 * step / NUM);
                        }
                        for (int i = 0; i < RANDOM_TURNOVER; i++) {
                            flows.add(DETimeSolve.R2T.calcNewton(function, v.getRValue(), NUM, .75*step/NUM));
                        }                        
                    }
                } catch (FunctionValueException ex) {
                    Logger.getLogger(VectorFieldTimed2D.class.getName()).log(Level.SEVERE, null, ex);
                }
            }         
            for (int i = 0; i < flows.size(); i++) {
                g.draw(v.path(flows.get(i)));
            }   
        }
    }

    /** Only need a few steps for the animation */
    public int getAnimatingSteps() { return 250; }
    
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
    
}

