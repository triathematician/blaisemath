/*
 * DESolution2D.java
 * Created on Oct 17, 2007, 3:01:18 PM
 */

package specto.euclidean3;

import java.awt.Color;
import javax.swing.event.ChangeEvent;
import scio.function.FunctionValueException;
import java.awt.Graphics2D;
import javax.swing.event.ChangeListener;
import scio.coordinate.R3;
import scio.diffeq.DESolve;
import sequor.component.RangeTimer;
import specto.Decoration;

/**
 * Represents a solution curve to a differential equation. Visually consists of an initial point,
 * a forwards solution curve, and a backwards solution curve.
 * <br><br>
 * @author ae3263
 */
public class DESolution3D extends InitialPointSet3D implements Decoration<Euclidean3,VectorField3D> {
        
    /** Default number of solution steps */
    public int NUM = 5000;
    public double STEP = 0.5;
    
    /** The starting point. */
    R3 point;
    /** The underlying vector field. */
    VectorField3D parent;
    /** The backwards solution. */
    //PointSet2D reversePath;
    /** Whether to show the "reverse path" */
    boolean showReverse=true;
    
    // CONSTRUCTOR
    
    public DESolution3D(R3 point,VectorField3D parent){
        super(point);
        setParent(parent);
        this.point = new R3(1.0,1.0,1.0);
        setColor(new Color(.5f,0,.5f));
    }
    public DESolution3D(VectorField3D parent){
        super();
        point = new R3(1.0,1.0,1.0);
        setParent(parent);
        setColor(new Color(.5f,0,.5f));
    }

    public DESolution3D(VectorField3D parent, double x0, double y0, double z0) {
        this(parent);
        point = new R3(x0,y0,z0);
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
    public int getAnimatingSteps() { return NUM; }
    
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
        if (v!=null) { return; }
        try {
            initSolutionCurves();
            switch(algorithm){
                case ALGORITHM_RUNGE_KUTTA:
                    path.setPath(DESolve.R3S.calcRungeKutta4(parent.getFunction(),point,NUM,STEP));
//                    reversePath.setPath(calcRungeKutta4(parent.getFunction(),getPoint(),500,-.04));
                    break;
                case ALGORITHM_NEWTON:
                default:
                    path.setPath(DESolve.R3S.calcNewton(parent.getFunction(),point,NUM,STEP));
//                    reversePath.setPath(calcNewton(parent.getFunction(),getPoint(),500,-.04));
                    break;
            }
        } catch (FunctionValueException ex) {}
    }

    // STYLE PARAMETERS

    int algorithm=ALGORITHM_RUNGE_KUTTA;
    public static final int ALGORITHM_NEWTON=0;
    public static final int ALGORITHM_RUNGE_KUTTA=1;
    
    @Override
    public String toString(){return "DE Solution Curve";}
}
