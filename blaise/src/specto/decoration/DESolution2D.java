/*
 * DESolution2D.java
 * Created on Oct 17, 2007, 3:01:18 PM
 */

package specto.decoration;

import javax.swing.JMenu;
import scio.function.FunctionValueException;
import specto.dynamicplottable.*;
import specto.plottable.PointSet2D;
import java.awt.Graphics2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import specto.Animatable;
import sequor.component.RangeTimer;
import scio.coordinate.R2;
import specto.Decoration;
import specto.plottable.VectorField2D;
import specto.visometry.Euclidean2;

/**
 * Represents a solution curve to a differential equation. Visually consists of an initial point,
 * a forwards solution curve, and a backwards solution curve.
 * <br><br>
 * @author ae3263
 */
public class DESolution2D extends Decoration<Point2D,Euclidean2> implements Animatable,ChangeListener{
    /** The underlying vector field. */
    Function<R2,R2> function;
    /** The forwards solution. */
    PointSet2D fSolution;
    /** The backwards solution. */
    PointSet2D rSolution;
    
    public DESolution2D(){
        setParent(new Point2D());
        function=VectorField2D.DEFAULT_FUNCTION;
        addChangeListener(this);
    }
    
    public DESolution2D(Point2D parent){
        setParent(parent);
        function=VectorField2D.DEFAULT_FUNCTION;
        addChangeListener(this);
    }
    
    /** Initializes solution curve models. */
    void initSolutionCurves(){
        if(fSolution==null){
            fSolution=new PointSet2D();
            fSolution.setVisometry(visometry);
        }
        if(rSolution==null){
            rSolution=new PointSet2D();
            rSolution.setVisometry(visometry);
            rSolution.setStyle(PointSet2D.DOTTED);
        }
        fSolution.getPath().clear();
        rSolution.getPath().clear();
    }
    
    /** Re-calculates the solution curves, using Newton's Method.
     * @param steps     The number of iterations.
     * @param stepSize  The size of path added at each step.
     */
    void calcNewton(int steps,double stepSize) throws FunctionValueException{
        initSolutionCurves();
        R2 cur=getParent().getPoint();
        R2 dir=new R2(0,0);
        for(int i=0;i<steps;i++){
            fSolution.getPath().add(cur.plus(dir));
            dir=function.getValue(cur).scaledToLength(stepSize);
            cur=cur.plus(dir);
        }
        cur=getParent().getPoint();
        dir=new R2(0,0);
        for(int i=0;i<steps;i++){            
            rSolution.getPath().add(cur.minus(dir));
            dir=function.getValue(cur).scaledToLength(stepSize);
            cur=cur.minus(dir);
        }
    }

    @Override
    public void paintComponent(Graphics2D g) {
        if(fSolution!=null){fSolution.paintComponent(g);}
        if(rSolution!=null){rSolution.paintComponent(g);}
    }

    @Override
    public void paintComponent(Graphics2D g,RangeTimer t){
        if(fSolution!=null){fSolution.paintComponent(g,t);}
        if(rSolution!=null){rSolution.paintComponent(g,t);}
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        recompute();
    }

    @Override
    public void recompute() {
        try {
            calcNewton(500, .04);
        } catch (FunctionValueException ex) {}
    }

    @Override
    public JMenu getOptionsMenu() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
