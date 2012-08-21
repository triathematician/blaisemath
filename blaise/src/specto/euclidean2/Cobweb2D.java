/**
 * Cobweb2D.java
 * Created on Apr 1, 2009
 */

package specto.euclidean2;

import java.awt.Color;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import scio.coordinate.R2;
import scio.function.Function;
import scio.function.FunctionValueException;
import sequor.model.IntegerRangeModel;
import sequor.style.LineStyle;

/**
 * This class implements a 1D, represents the solution as a cobweb.
 * @author Elisha Peterson
 */
public class Cobweb2D extends InitialPointSet2D {
    
    /** Default function. */
    final static Function<Double,Double> DEFAULT_FUNCTION = new Function<Double,Double>(){
        public Double getValue(Double x) throws FunctionValueException {
            return 4*x-x*x;
        }

        public Vector<Double> getValue(Vector<Double> xx) throws FunctionValueException {
            Vector<Double> result = new Vector<Double>();
            for (Double x:xx){ result.add(getValue(x)); }
            return result;
        }
    };

    /** Function used for 1D iteration. */
    public Function<Double,Double> function1 = DEFAULT_FUNCTION;
    
    /** Default constructor. */
    public Cobweb2D() { this(new Point2D(0,0)); }

    /** Sets with a given initial point. */
    public Cobweb2D(Point2D pt) {
        super(pt);
        length.setValue(50);
        path.style.setValue(LineStyle.THIN);
        path.setColor(Color.GRAY);
    }

    @Override
    public void recompute(Euclidean2 v) {
        R2 pt = getPoint();
        Vector<R2> solution = new Vector<R2>();
        double oldX = pt.x;
        double newX;
        try {
            switch(display.getValue()) {
                case COBWEB:
                    solution.add(pt);
                    for(int i=0;i<length.getValue();i++){
                        newX = function1.getValue(oldX);
                        if(Math.abs(newX) > 99999999) { break; }
                        solution.add(new R2(oldX, newX));
                        solution.add(new R2(newX, newX));
                        oldX = newX;
                    }
                    break;
                case VALUES:
                    solution.add(new R2(0,oldX));
                    for(int i=0;i<length.getValue();i++){
                        newX = function1.getValue(oldX);
                        if(Math.abs(newX) > 99999999) { break; }
                        solution.add(new R2(i+1, newX));
                        oldX = newX;
                    }
                    break;
            }
        } catch (FunctionValueException ex) {
            Logger.getLogger(Cobweb2D.class.getName()).log(Level.SEVERE, null, ex);
        }
        path.setPath(solution);
    }

    private IntegerRangeModel display = new IntegerRangeModel(0,0,1);
    public static final int COBWEB = 0;
    public static final int VALUES = 1;

    public String toString() { return "Iterated Set"; }
}
