/*
 * DESolution2D.java
 * Created on Oct 17, 2007, 3:01:18 PM
 */

package specto.euclidean3;

import specto.euclidean2.*;
import java.awt.Color;
import scio.function.FunctionValueException;
import java.awt.Graphics2D;
import java.util.Vector;
import scio.function.Function;
import scio.coordinate.R2;
import scio.coordinate.R3;
import scio.function.BoundedFunction;
import sequor.component.RangeTimer;
import specto.Decoration;

/**
 * Represents a solution curve to a differential equation. Visually consists of an initial point,
 * a forwards solution curve, and a backwards solution curve.
 * <br><br>
 * @author ae3263
 */
public class DESolution3D extends InitialPointSet3D implements Decoration<Euclidean3,VectorField3D> {
        
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
        this.parent=parent;
        this.point = new R3(1.0,1.0,1.0);
        setColor(new Color(.5f,0,.5f));
    }
    public DESolution3D(VectorField3D parent){
        super();
        point = new R3(1.0,1.0,1.0);
        this.parent=parent;
        setColor(new Color(.5f,0,.5f));
    }
    
    // DECORATION METHODS

    public void setParent(VectorField3D parent) {this.parent=parent;}
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
    
    /** Re-calculates the solution curves, using Newton's Method.
     * @param steps     The number of iterations.
     * @param stepSize  The size of path added at each step.
     */
    public static Vector<R3> calcNewton(Function<R3,R3> field,R3 start,int steps,double stepSize) throws FunctionValueException{
        Vector<R3> result=new Vector<R3>();
        result.add(start);
        R3 last;
        for(int i=0;i<steps;i++){
            last=result.lastElement();
            result.add(last.plus(getScaledVector(field,last,stepSize)));
        }
        return result;
    }    
    
    /** Re-calculates the solution curves, using Newton's Method. Instead of using a starting
     * point, uses a starting vector; removes "steps" number of points from the beginning, and
     * adds the same number onto the end of the vector.
     * @param steps     The number of iterations.
     * @param stepSize  The size of path added at each step.
     */
    public static Vector<R3> calcNewton(Function<R3,R3> field,Vector<R3> flow,int steps,double stepSize) throws FunctionValueException{
        R3 last;
        for(int i=0;i<steps;i++){
            last=flow.lastElement();
            flow.add(last.plus(getScaledVector(field,last,stepSize)));
            flow.remove(0);
        }
        return flow;
    }
    
    /** Re-calculates solution curves using Runge-Kutta 4th order.
     * @param steps the number of iteration
     * @param stepSize the change in t for each iteration
     */
    public static Vector<R3> calcRungeKutta4(Function<R3,R3> field,R3 start,int steps,double stepSize) throws FunctionValueException{
        Vector<R3> result=new Vector<R3>();
        result.add(start);
        R3 k1,k2,k3,k4;
        R3 last;
        for(int i=0;i<steps;i++){
            last=result.lastElement();
            k1=getScaledVector(field,last,stepSize);
            k2=getScaledVector(field,last.plus(k1.multipliedBy(0.5)),stepSize);
            k3=getScaledVector(field,last.plus(k2.multipliedBy(0.5)),stepSize);
            k4=getScaledVector(field,last.plus(k3),stepSize);
            result.add(new R3(
                    last.getX()+(k1.getX()+2*k2.getX()+2*k3.getX()+k4.getX())/6,
                    last.getY()+(k1.getY()+2*k2.getY()+2*k3.getY()+k4.getY())/6,
                    last.getZ()+(k1.getZ()+2*k2.getZ()+2*k3.getZ()+k4.getZ())/6));
            
        }
        return result;
    }

    public static R3 getScaledVector(Function<R3,R3> field,R3 point,double size) throws FunctionValueException{
        return field.getValue(point).scaledToLength(size);
    }
    public static R3 getMultipliedVector(BoundedFunction<R3,R3> field,R3 point,double size) throws FunctionValueException{
        return field.getValue(point).multipliedBy(size/(field.maxValue().getX()+field.maxValue().getY()+field.maxValue().getZ()));
    }
    
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
            switch(algorithm){
                case ALGORITHM_RUNGE_KUTTA:
                    path.setPath(calcRungeKutta4(parent.getFunction(),point,500,.04));
//                    reversePath.setPath(calcRungeKutta4(parent.getFunction(),getPoint(),500,-.04));
                    break;
                case ALGORITHM_NEWTON:
                default:
                    path.setPath(calcNewton(parent.getFunction(),point,500,.04));
//                    reversePath.setPath(calcNewton(parent.getFunction(),getPoint(),500,-.04));
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
