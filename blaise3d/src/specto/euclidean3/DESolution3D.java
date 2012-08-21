/*
 * DESolution2D.java
 * Created on Oct 17, 2007, 3:01:18 PM
 */

package specto.euclidean3;

import java.awt.Color;
import javax.swing.event.ChangeEvent;
import scio.function.FunctionValueException;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.ChangeListener;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.diffeq.DESolve;
import sequor.Settings;
import sequor.SettingsProperty;
import sequor.component.RangeTimer;
import sequor.model.DoubleRangeModel;
import sequor.model.IntegerRangeModel;
import sequor.model.StringRangeModel;
import specto.Decoration;
import specto.euclidean2.Point2D;
import specto.euclidean2.PointSet2D;

/**
 * Represents a solution curve to a differential equation. Visually consists of an initial point,
 * a forwards solution curve, and a backwards solution curve.
 * <br><br>
 * @author ae3263
 */
public class DESolution3D extends InitialPointSet3D implements Decoration<Euclidean3,VectorField3D> {

    /** The underlying vector field. */
    VectorField3D parent;
    
    /** The number of solution steps. */
    IntegerRangeModel numSteps = new IntegerRangeModel(500,0,10000);
    /** The solution step size. */
    DoubleRangeModel stepSize = new DoubleRangeModel(0.1,0.00001,100.0,0.01);

    // /** Whether to show the "reverse path" */
    // BooleanModel showReverse = new BooleanModel(true);
    ///** The backwards solution. */
    //PointSet3D reversePath;

    /** Phase path. */
    PointSet2D phasePath;
    /** xt path. */
    PointSet2D xPath;
    /** yt path. */
    PointSet2D yPath;
    
    
    // CONSTRUCTOR

    public DESolution3D(VectorField3D parent){
        super();
        setParent(parent);
        setColor(new Color(.5f,0,.5f));
    }

    public DESolution3D(VectorField3D parent, double x0, double y0, double z0) {
        this(parent);
        setPoint(new R3(x0,y0,z0));
    }

    public DESolution3D(VectorField3D parent,R3 point){
        super(point);
        setParent(parent);
        setColor(new Color(.5f,0,.5f));
    }

    public DESolution3D(VectorField3D parent, final Point2D point) {
        super(new SlicePoint(point));
        point.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                ((SlicePoint)prm).update(point);
                DESolution3D.this.recompute(null);
                DESolution3D.this.fireStateChanged();
            }
        });
        setParent(parent);
        setColor(new Color(.5f,0,.5f));
    }

    public DESolution3D(VectorField3D parent, Point3D point) {
        super(point);
        setParent(parent);
        setColor(new Color(.5f,0,.5f));
    }


    // ALLOWS FOR 2D CONTROL
    
    static class SlicePoint extends R3 {
        public SlicePoint(Point2D p2d) {
            update(p2d);
        }

        public void update(Point2D p2d) {
            x = p2d.getModel().getX();
            y = p2d.getModel().getY();
            z = 0;
        }
    }


    // DECORATION METHODS

    public void setParent(VectorField3D parent) {
        this.parent=parent;
        parent.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) { recompute(null); }
        });
        recompute(null);
    }
    public VectorField3D getParent() {return parent;}
        
    /** Initializes solution curve models. */
    void initSolutionCurves(){
        if(path==null){
            path=new PointSet3D(parent.getColor());
        }
//        if(reversePath==null){
//            reversePath=new PointSet2D(parent.getColor());
//            reversePath.style.setValue(PointSet2D.DOTTED);
//        }
        path.getPath().clear();
//        reversePath.getPath().clear();
    }

    @Override
    public int getAnimatingSteps() { return numSteps.getValue(); }

    public void setNumSteps(int i) { numSteps.setValue(i); }
    public void setStepSize(double step) { stepSize.setValue(step); }

    // PAINT METHODS
    
    
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v) {
        if(path!=null){path.paintComponent(g,v);}
//        if(showReverse&&reversePath!=null){
//            g.setComposite(VisualStyle.COMPOSITE2);
//            reversePath.paintComponent(g,v);
//            g.setComposite(AlphaComposite.SrcOver);
//        }
    }

    @Override
    public void paintComponent(Graphics2D g,Euclidean3 v,RangeTimer t){
        if(path!=null){path.paintComponent(g,v,t);}
//        if(showReverse&&reversePath!=null){
//            g.setComposite(VisualStyle.COMPOSITE2);
//            reversePath.paintComponent(g,v,t);
//            g.setComposite(AlphaComposite.SrcOver);
//        }
    }

    @Override
    public void recompute(Euclidean3 v) {
        try {
            initSolutionCurves();
            switch(algorithm.getValue()){
                case ALGORITHM_RUNGE_KUTTA:
                    path.setPath(DESolve.R3S.calcRungeKutta4(parent.getFunction(),getPoint(),numSteps.getValue(),stepSize.getValue()));
//                    reversePath.setPath(calcRungeKutta4(parent.getFunction(),getPoint(),500,-.04));
                    break;
                case ALGORITHM_NEWTON:
                default:
                    path.setPath(DESolve.R3S.calcNewton(parent.getFunction(),getPoint(),numSteps.getValue(),stepSize.getValue()));
//                    reversePath.setPath(calcNewton(parent.getFunction(),getPoint(),500,-.04));
                    break;
            }
        } catch (FunctionValueException ex) {}

        if (phasePath != null) {
            Vector<R2> phase = new Vector<R2>();
            for (R3 p : path.points) { phase.add(new R2(p.x,p.y)); }
            phasePath.setPath(phase);
            phasePath.redraw();
        }

        if (xPath != null) {
            Vector<R2> phase = new Vector<R2>();
            for (R3 p : path.points) { phase.add(new R2(p.z,p.x)); }
            xPath.setPath(phase);
            xPath.redraw();
        }

        if (yPath != null) {
            Vector<R2> phase = new Vector<R2>();
            for (R3 p : path.points) { phase.add(new R2(p.z,p.y)); }
            yPath.setPath(phase);
            yPath.redraw();
        }
    }


    // PARTIAL PATHS

    /** Returns coords 2/3 of the solution. If t is p.z this is the "phase" portrait. */
    public PointSet2D getPathPhase() {
        phasePath = new PointSet2D();
        Vector<R2> phase = new Vector<R2>();
        for (R3 p : path.points) { phase.add(new R2(p.x,p.y)); }
        phasePath.setPath(phase);
        return phasePath;
    }
    
    /** Returns coords 1/2 of the solution. If t is p.z this is the "x,t" portrait. */
    public PointSet2D getPathX() {
        xPath = new PointSet2D();
        Vector<R2> phase = new Vector<R2>();
        for (R3 p : path.points) { phase.add(new R2(p.z,p.x)); }
        xPath.setPath(phase);
        return xPath;
    }

    /** Returns coords 1/3 of the solution. If t is p.z this is the "y,t" portrait. */
    public PointSet2D getPathY() {
        yPath = new PointSet2D();
        Vector<R2> phase = new Vector<R2>();
        for (R3 p : path.points) { phase.add(new R2(p.z,p.y)); }
        yPath.setPath(phase);
        return yPath;
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
            //add(new SettingsProperty("show reverse",showReverse,Settings.EDIT_BOOLEAN));
            add(new SettingsProperty("color",color,Settings.EDIT_COLOR));
            add(new SettingsProperty("path style",path.style,Settings.EDIT_COMBO));
            add(new SettingsProperty("animation style",path.animateStyle,Settings.EDIT_COMBO));
        }
        /** Event handling... all taken care of by the models */
        @Override
        public void stateChanged(ChangeEvent e) { 
            DESolution3D.this.fireStateChanged();
        }
    }
}
