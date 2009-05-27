/*
 * DESolution2D.java
 * Created on Oct 17, 2007, 3:01:18 PM
 */

package specto.euclidean2;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import scio.function.FunctionValueException;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import scio.coordinate.R2;
import scio.diffeq.DESolve;
import scio.function.Function;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.component.RangeTimer;
import sequor.model.BooleanModel;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.PointRangeModel;
import sequor.model.StringRangeModel;
import sequor.style.LineStyle;
import sequor.style.VisualStyle;
import specto.Decoration;

/**
 * <p>
 * Represents a solution curve to a differential equation. Visually consists of an initial point,
 * a forwards solution curve, and a backwards solution curve.
 * </p>
 * @author Elisha Peterson
 */
public class DESolution2D extends InitialPointSet2D implements Decoration<Euclidean2,VectorField2D> {

    
    /** The underlying vector field. */
    VectorField2D parent;
    /** The backwards solution. */
    PointSet2D reversePath;
    
    /** The number of solution steps. */
    IntegerRangeModel numSteps = new IntegerRangeModel(500,0,10000);    
    /** The solution step size. */
    DoubleRangeModel stepSize = new DoubleRangeModel(0.1,0.00001,100.0,0.01);
    
    /** Whether to show the "reverse path" */
    BooleanModel showReverse = new BooleanModel(true);
    
    
    
    // CONSTRUCTORS

    public DESolution2D() {
    }
    
    public DESolution2D(Point2D point,VectorField2D parent){
        super(point);
        this.parent=parent;
        setColor(new Color(.5f,0,.5f));
    }
    public DESolution2D(VectorField2D parent){
        super();
        this.parent=parent;
        setColor(new Color(.5f,0,.5f));
    }
    DESolution2D(PointRangeModel initialPoint, VectorField2D parent) {
        super(initialPoint);
        this.parent=parent;
        setColor(new Color(.5f,0,.5f));
    }
    
    // DECORATION METHODS

    public void setParent(VectorField2D parent) {this.parent=parent;}
    public VectorField2D getParent() {return parent;}

    
    
    // COMPUTING AND VISUALIZING
    
    /** Determines whether to use a box visualization of the solution. */
    BooleanModel useBox = new BooleanModel(false);
    /** Separation for the box solution. */
    DoubleRangeModel boxSep = new DoubleRangeModel(.2,.001,5);
    /** Solution curves for the box visualization of the solution. */
    Vector<Vector<R2>> boxSolutions;
    
    
    /** Initializes solution curve models. */
    void initSolutionCurves(){
        if(path==null){
            path=new PointSet2D(parent.getColor());
        }
        if(reversePath==null){
            reversePath=new PointSet2D(parent.getColor());
            reversePath.style.setValue(LineStyle.THIN);
        }
        path.getPath().clear();
        reversePath.getPath().clear();    
    }

    @Override
    public void recompute(Euclidean2 v) {
        R2 pt = getPoint();
        Function<R2,R2> fn = parent.getFunction();
        try {
            initSolutionCurves();
            switch(algorithm.getValue()){
                case ALGORITHM_RUNGE_KUTTA:
                    path.setPath(DESolve.R2S.calcRungeKutta4(fn,pt,numSteps.getValue(),stepSize.getValue()));
                    reversePath.setPath(DESolve.R2S.calcRungeKutta4(fn,pt,numSteps.getValue(),-stepSize.getValue()));
                    break;
                case ALGORITHM_NEWTON:
                default:
                    path.setPath(DESolve.R2S.calcNewton(fn,pt,numSteps.getValue(),stepSize.getValue()));
                    reversePath.setPath(DESolve.R2S.calcNewton(fn,pt,numSteps.getValue(),-stepSize.getValue()));
                    break;
            }
            if (useBox.isTrue()) {
                boxSolutions = new Vector<Vector<R2>>();
                double sep = boxSep.getValue();
                R2[] corner = {pt.plus(sep,sep), pt.plus(-sep,sep), pt.plus(-sep,-sep), pt.plus(sep,-sep)};
                switch(algorithm.getValue()){
                    case ALGORITHM_RUNGE_KUTTA:
                        for(int i=0;i<4;i++){
                            boxSolutions.add(DESolve.R2S.calcRungeKutta4(fn,corner[i],numSteps.getValue(),stepSize.getValue()));
                        }
                        break;
                    case ALGORITHM_NEWTON:
                    default:
                        for(int i=0;i<4;i++){
                            boxSolutions.add(DESolve.R2S.calcNewton(fn,corner[i],numSteps.getValue(),stepSize.getValue()));
                        }
                        break;
                }
            } 
        } catch (FunctionValueException ex) {}
    }
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        super.paintComponent(g, v);
        if(showReverse.isTrue()&&reversePath!=null){
            g.setComposite(VisualStyle.COMPOSITE2);
            reversePath.paintComponent(g,v);
            g.setComposite(AlphaComposite.SrcOver);
        }
    }

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v,RangeTimer t){
        super.paintComponent(g, v, t);
        if(useBox.isTrue()) {
            int pos=t.getCurrentIntValue();
            int posB=pos<0?0:(pos>=boxSolutions.get(0).size()?boxSolutions.get(0).size()-1:pos);
            Vector<R2> rect = new Vector<R2>();
            for (int i = 0; i < 4; i++) {
                rect.add(boxSolutions.get(i).get(posB));
            }
            g.setComposite(VisualStyle.COMPOSITE5);
            g.setColor(Color.YELLOW);
            g.fill(v.closedPath(rect));
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1.0f));
//            g.draw(v.lineSegment(boxSolutions.get(0).get(posB),boxSolutions.get(2).get(posB)));
//            g.draw(v.lineSegment(boxSolutions.get(1).get(posB),boxSolutions.get(3).get(posB)));
            g.draw(v.closedPath(rect));
            g.setComposite(AlphaComposite.SrcOver);
        } else {
            if(showReverse.isTrue()&&reversePath!=null){
                g.setComposite(VisualStyle.COMPOSITE2);
                reversePath.paintComponent(g,v,t);
                g.setComposite(AlphaComposite.SrcOver);
            }
        }
    }

    // STYLE PARAMETERS

    StringRangeModel algorithm = new StringRangeModel(algoStrings, ALGORITHM_RUNGE_KUTTA);
    public static final int ALGORITHM_NEWTON=0;
    public static final int ALGORITHM_RUNGE_KUTTA=1;
    public static final String[] algoStrings = {"Newton", "Runge-Kutta"};

    @Override
    public String toString(){ return "DE Solution Curve ("+algoStrings[algorithm.getValue()]+")"; }
    
    // SETTINGS

    /** Overrides to allow setting custom options. */
    @Override
    public JMenu getOptionsMenu() {
        JMenu menu = super.getOptionsMenu();
        menu.add(new JSeparator(),0);
        JMenuItem mi = new JMenuItem("Solution Options");
        mi.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                if (ps==null) { ps = new SolutionSettings(); }
                if (psd==null) { psd = ps.getDialog(null, false); }
                psd.setVisible(true);
            }
        });
        menu.add(mi,0);
        return menu;
    }

    SolutionSettings ps;
    JDialog psd;

    /** Inner class controls options for displaying the curve... starting point, ending point, and number of steps */
    public class SolutionSettings extends Settings {
        /** Initializes, and sets up listening. */
        public SolutionSettings() {
            setName("Differential Equation Solution Settings");
            add(new SettingsProperty("algorithm",algorithm,Settings.EDIT_COMBO));
            add(new SettingsProperty("# steps",numSteps,Settings.EDIT_INTEGER));
            add(new SettingsProperty("step size",stepSize,Settings.EDIT_DOUBLE));
            add(new SettingsProperty("show reverse",showReverse,Settings.EDIT_BOOLEAN));
            add(new SettingsProperty("color",color,Settings.EDIT_COLOR));
            add(new SettingsProperty("path style",path.style,Settings.EDIT_COMBO));
            add(new SettingsProperty("animation style",path.animateStyle,Settings.EDIT_COMBO));
            add(new SettingsProperty("show box",useBox,Settings.EDIT_BOOLEAN,"displays box representing movement of solutions at nearby starting points, when animating"));
            add(new SettingsProperty("size",boxSep,Settings.EDIT_DOUBLE_SLIDER));
        }
        /** Event handling... all taken care of by the models */
        @Override
        public void stateChanged(ChangeEvent e) { DESolution2D.this.fireStateChanged(); }
    }
}
