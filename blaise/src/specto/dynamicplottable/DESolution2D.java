/*
 * DESolution2D.java
 * Created on Oct 17, 2007, 3:01:18 PM
 */

package specto.dynamicplottable;

import java.awt.Graphics2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.function.Function;
import specto.Animatable;
import sequor.component.RangeTimer;
import scio.coordinate.R2;
import specto.plottable.VectorField2D;

/**
 * Represents a solution curve to a differential equation. Visually consists of an initial point,
 * a forwards solution curve, and a backwards solution curve.
 * <br><br>
 * @author ae3263
 */
public class DESolution2D extends Point2D implements Animatable,ChangeListener{
    /** The underlying vector field. */
    Function<R2,R2> function;
    /** The forwards solution. */
    PointSet2D fSolution;
    /** The backwards solution. */
    PointSet2D rSolution;
    
    public DESolution2D(){
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
    void calcNewton(int steps,double stepSize){
        initSolutionCurves();
        R2 cur=point;
        R2 dir=new R2(0,0);
        for(int i=0;i<steps;i++){
            fSolution.getPath().add(cur.plus(dir));
            dir=function.getValue(cur).scaledToLength(stepSize);
            cur=cur.plus(dir);
        }
        cur=point;
        dir=new R2(0,0);
        for(int i=0;i<steps;i++){            
            rSolution.getPath().add(cur.minus(dir));
            dir=function.getValue(cur).scaledToLength(stepSize);
            cur=cur.minus(dir);
        }
    }

    @Override
    public void paintComponent(Graphics2D g) {
        super.paintComponent(g);
        if(fSolution!=null){fSolution.paintComponent(g);}
        if(rSolution!=null){rSolution.paintComponent(g);}
    }

    public void paintComponent(Graphics2D g,RangeTimer t){
        super.paintComponent(g);
        if(fSolution!=null){fSolution.paintComponent(g,t);}
        if(rSolution!=null){rSolution.paintComponent(g,t);}
    }

    public void stateChanged(ChangeEvent e) {
        calcNewton(500,.04);
    }
}
