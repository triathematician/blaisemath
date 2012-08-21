/*
 * MeshRoot2DTaubin.java
 * Created on Feb 11, 2009 by Elisha Peterson
 */

package scio.function;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import scio.coordinate.R2;

/**
 * <p>
 * This class is designed to find roots of a given function to within a specified tolerance. Returns a set of points.
 * </p>
 * @author elisha
 */
public class MeshRoot2DTaubin {

    public static void main(String[] args) {
        Function<R2,Double> test = new Function<R2,Double>(){
            public Double getValue(R2 x) throws FunctionValueException {
                return Math.cos(x.magnitudeSq());
            }
            public Vector<Double> getValue(Vector<R2> xx) throws FunctionValueException {
                Vector<Double> result = new Vector<Double>();
                for(R2 x:xx){result.add(getValue(x));}
                return result;
            }
        };
        try {
            Vector<R2> result = findRoot1(test, 1, -4, -4, 4, 4, .1);
            for(R2 p : result) {
                System.out.println(p.x+"\t"+p.y);
            }
            System.out.println(result.size());
        } catch (FunctionValueException ex) {
            Logger.getLogger(MeshRoot2DTaubin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Returns set of points at roots along the curve, using  a first-order recursive Taubin algorithm. */
    public static Vector<R2> findRoot1(Function<R2,Double> function, double c, double x1, double y1, double x2, double y2, double tolerance) throws FunctionValueException {
        Vector<R2> result = new Vector<R2>();
        R2 midpt = new R2((x1+x2)/2,(y1+y2)/2);
        double winSize = Math.max(Math.abs(x1-x2), Math.abs(y1-y2));
        double zeroDist = zeroDistance1(function, c, midpt, tolerance/10);
        if (zeroDist < winSize) {
            if (winSize < tolerance) {
                result.add(midpt);
            } else {
                result.addAll(findRoot1(function, c, x1, y1, midpt.x, midpt.y, tolerance));
                result.addAll(findRoot1(function, c, midpt.x, y1, x2, midpt.y, tolerance));
                result.addAll(findRoot1(function, c, x1, midpt.y, midpt.x, y2, tolerance));
                result.addAll(findRoot1(function, c, midpt.x, midpt.y, x2, y2, tolerance));
            }
        }
        return result;
    }

    /** Returns set of points at roots along the curve, using a second-order recursive Taubin algorithm. */

    /** Returns first-order approximate distance to zero. */
    public static double zeroDistance1(Function<R2,Double> function, double c, R2 point, double tolerance) throws FunctionValueException {
        double value=function.getValue(point);

        R2 step1x = new R2(1.0,0);
        double resultx=Double.MAX_VALUE;
        double leftResultx=(function.getValue(point.plus(step1x))-value)/step1x.x;
        while(Math.abs(leftResultx-resultx)>tolerance){
            resultx=leftResultx;
            step1x.x /= 10;
            leftResultx=(function.getValue(point.plus(step1x))-value)/step1x.x;
        }

        R2 step1y = new R2(0,1.0);
        double resulty=Double.MAX_VALUE;
        double leftResulty=(function.getValue(point.plus(step1y))-value)/step1y.y;
        while(Math.abs(leftResulty-resulty)>tolerance){
            resulty=leftResulty;
            step1y.y /= 10;
            leftResulty=(function.getValue(point.plus(step1y))-value)/step1y.y;
        }

        return Math.abs(value-c) / Math.sqrt(leftResultx*leftResultx + leftResulty*leftResulty);
    }
}
