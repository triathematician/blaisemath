/*
 * DESolution2D.java
 * Created on Oct 17, 2007, 3:01:18 PM
 */

package specto.decoration;

import scio.function.FunctionValueException;
import specto.dynamicplottable.*;
import specto.plottable.PointSet2D;
import java.awt.Graphics2D;
import scio.function.Function;
import sequor.component.RangeTimer;
import scio.coordinate.R2;
import specto.plottable.VectorField2D;
import specto.visometry.Euclidean2;

/**
 * Represents a solution curve to a differential equation. Visually consists of an initial point,
 * a forwards solution curve, and a backwards solution curve.
 * <br><br>
 * @author ae3263
 */
public class DESolution2D extends InitialPointSet2D {
    /** The underlying vector field. */
    Function<R2,R2> function;
    /** The backwards solution. */
    PointSet2D reversePath;
    /** Whether to show the "reverse path" */
    boolean showReverse=true;
    
    public DESolution2D(Point2D parent){
        super(parent);
        function=VectorField2D.DEFAULT_FUNCTION;
    }
    
    /** Initializes solution curve models. */
    void initSolutionCurves(){
        if(path==null){
            path=new PointSet2D(visometry);
        }
        if(reversePath==null){
            reversePath=new PointSet2D(visometry);
            reversePath.setStyle(PointSet2D.DOTTED);
        }
        path.getPath().clear();
        reversePath.getPath().clear();
    }
    
    /** Re-calculates the solution curves, using Newton's Method.
     * @param steps     The number of iterations.
     * @param stepSize  The size of path added at each step.
     */
    void calcNewton(int steps,double stepSize) throws FunctionValueException{
        initSolutionCurves();
        R2 cur=((Point2D)getParent()).getPoint();
        R2 dir=new R2(0,0);
        for(int i=0;i<steps;i++){
            path.getPath().add(cur.plus(dir));
            dir=function.getValue(cur).scaledToLength(stepSize);
            cur=cur.plus(dir);
        }
        cur=((Point2D)getParent()).getPoint();
        dir=new R2(0,0);
        for(int i=0;i<steps;i++){            
            reversePath.getPath().add(cur.minus(dir));
            dir=function.getValue(cur).scaledToLength(stepSize);
            cur=cur.minus(dir);
        }
    }

    @Override
    public void paintComponent(Graphics2D g) {
        if(path!=null){path.paintComponent(g);}
        if(showReverse&&reversePath!=null){reversePath.paintComponent(g);}
    }

    @Override
    public void paintComponent(Graphics2D g,RangeTimer t){
        if(path!=null){path.paintComponent(g,t);}
        if(showReverse&&reversePath!=null){reversePath.paintComponent(g,t);}
    }

    @Override
    public void recompute() {
        try {
            calcNewton(500, .04);
        } catch (FunctionValueException ex) {}
    }
}
