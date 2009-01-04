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
import java.util.Vector;
import scio.coordinate.R2;
import scio.diffeq.DESolve;
import scio.function.BoundedFunction;
import sequor.component.RangeTimer;
import sequor.model.BooleanModel;
import sequor.model.DoubleRangeModel2;
import sequor.model.IntegerRangeModel;
import sequor.model.PointRangeModel;
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
    DoubleRangeModel2 stepSize = new DoubleRangeModel2(0.1,0.00001,100.0,0.1);    
    
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
    DoubleRangeModel2 boxSep = new DoubleRangeModel2(.2,.001,5);
    /** Solution curves for the box visualization of the solution. */
    Vector<Vector<R2>> boxSolutions;
    
    
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

    @Override
    public void recompute(Euclidean2 v) {
        R2 pt = getPoint();
        BoundedFunction<R2,R2> fn = parent.getFunction();
        try {
            initSolutionCurves();
            switch(algorithm){
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
                switch(algorithm){
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
        if(path!=null){path.paintComponent(g,v);}
        if(showReverse.isTrue()&&reversePath!=null){
            g.setComposite(VisualStyle.COMPOSITE2);
            reversePath.paintComponent(g,v);
            g.setComposite(AlphaComposite.SrcOver);
        }
    }

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v,RangeTimer t){
        if(path!=null){path.paintComponent(g,v,t);}
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

    int algorithm=ALGORITHM_RUNGE_KUTTA;
    public static final int ALGORITHM_NEWTON=0;
    public static final int ALGORITHM_RUNGE_KUTTA=1;
    
    @Override
    public String toString(){
        return "DE Solution Curve "+(algorithm==1?"(RK)":"(Newton)");
    }
}
