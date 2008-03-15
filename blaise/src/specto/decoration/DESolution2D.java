/*
 * DESolution2D.java
 * Created on Oct 17, 2007, 3:01:18 PM
 */

package specto.decoration;

import specto.dynamicplottable.InitialPointSet2D;
import scio.function.FunctionValueException;
import specto.dynamicplottable.*;
import specto.plottable.PointSet2D;
import java.awt.Graphics2D;
import java.util.Vector;
import scio.function.Function;
import scio.coordinate.R2;
import sequor.component.IntegerRangeTimer;
import specto.Decoration;
import specto.plottable.VectorField2D;
import specto.visometry.Euclidean2;

/**
 * Represents a solution curve to a differential equation. Visually consists of an initial point,
 * a forwards solution curve, and a backwards solution curve.
 * <br><br>
 * @author ae3263
 */
public class DESolution2D extends InitialPointSet2D implements Decoration<Euclidean2,VectorField2D> {
    
    /** The underlying vector field. */
    VectorField2D parent;
    /** The backwards solution. */
    PointSet2D reversePath;
    /** Whether to show the "reverse path" */
    boolean showReverse=true;
    
    // CONSTRUCTOR
    
    public DESolution2D(Point2D point,VectorField2D parent){
        super(point);
        this.parent=parent;
    }
    public DESolution2D(VectorField2D parent){
        super();
        this.parent=parent;
    }
    
    // DECORATION METHODS

    public void setParent(VectorField2D parent) {this.parent=parent;}
    public VectorField2D getParent() {return parent;}
    
    /** Initializes solution curve models. */
    void initSolutionCurves(){
        if(path==null){
            path=new PointSet2D(parent.getColor());
        }
        if(reversePath==null){
            reversePath=new PointSet2D(parent.getColor());
            reversePath.style.setValue(PointSet2D.DOTTED);
        }
        path.getPath().clear();
        reversePath.getPath().clear();
    }
    
    /** Re-calculates the solution curves, using Newton's Method.
     * @param steps     The number of iterations.
     * @param stepSize  The size of path added at each step.
     */
    public static Vector<R2> calcNewton(Function<R2,R2> field,R2 start,int steps,double stepSize) throws FunctionValueException{
        Vector<R2> result=new Vector<R2>();
        result.add(start);
        R2 last;
        for(int i=0;i<steps;i++){
            last=result.lastElement();
            result.add(last.plus(getScaledVector(field,last,stepSize)));
        }
        return result;
    }
    
    /** Re-calculates solution curves using Runge-Kutta 4th order.
     * @param steps the number of iteration
     * @param stepSize the change in t for each iteration
     */
    public static Vector<R2> calcRungeKutta4(Function<R2,R2> field,R2 start,int steps,double stepSize) throws FunctionValueException{
        Vector<R2> result=new Vector<R2>();
        result.add(start);
        R2 k1,k2,k3,k4;
        R2 last;
        for(int i=0;i<steps;i++){
            last=result.lastElement();
            k1=getScaledVector(field,last,stepSize);
            k2=getScaledVector(field,last.plus(k1.multipliedBy(0.5)),stepSize);
            k3=getScaledVector(field,last.plus(k2.multipliedBy(0.5)),stepSize);
            k4=getScaledVector(field,last.plus(k3),stepSize);
            result.add(new R2(last.x+(k1.x+2*k2.x+2*k3.x+k4.x)/6,last.y+(k1.y+2*k2.y+2*k3.y+k4.y)/6));
            
        }
        return result;
    }

    public static R2 getScaledVector(Function<R2,R2> field,R2 point,double size) throws FunctionValueException{
        return field.getValue(point).scaledToLength(size);
    }
    public static R2 getMultipliedVector(Function<R2,R2> field,R2 point,double size) throws FunctionValueException{
        return field.getValue(point).multipliedBy(size/(field.maxValue().x+field.maxValue().y));
    }
    
    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        if(path!=null){path.paintComponent(g,v);}
        if(showReverse&&reversePath!=null){reversePath.paintComponent(g,v);}
    }

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v,IntegerRangeTimer t){
        if(path!=null){path.paintComponent(g,v,t);}
        if(showReverse&&reversePath!=null){reversePath.paintComponent(g,v,t);}
    }

    @Override
    public void recompute() {
        try {
            initSolutionCurves();
            switch(algorithm){
                case ALGORITHM_RUNGE_KUTTA:
                    path.setPath(calcRungeKutta4(parent.getFunction(),getPoint(),500,.04));
                    reversePath.setPath(calcRungeKutta4(parent.getFunction(),getPoint(),500,-.04));
                    break;
                case ALGORITHM_NEWTON:
                default:
                    path.setPath(calcNewton(parent.getFunction(),getPoint(),500,.04));
                    reversePath.setPath(calcNewton(parent.getFunction(),getPoint(),500,-.04));
                    break;
            }
        } catch (FunctionValueException ex) {}
    }

    // STYLE PARAMETERS

    int algorithm=ALGORITHM_NEWTON;
    public static final int ALGORITHM_NEWTON=0;
    public static final int ALGORITHM_RUNGE_KUTTA=1;
    
    @Override
    public String toString(){return "DE Solution Curve";}
}
