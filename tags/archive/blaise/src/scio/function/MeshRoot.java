/**
 * MeshRoot.java
 * Created on May 22, 2008
 */

package scio.function;

import java.util.Vector;

/**
 * Contains algorithm for finding multiple roots of a function within a given range.
 * @author Elisha Peterson
 */
public class MeshRoot {

    public static Vector<Double> findRoots(Function<Double, Double> function, double x1, double x2, double refinement, double tolerance, int maxIterations) throws FunctionValueException {
        Vector<Double> xValues = new Vector<Double> ();
        double x = x1;
        while(x <= x2){
            xValues.add(x);
            x+=refinement;
        }
        Vector<Double> yValues = function.getValue(xValues);
        
        // compile list of sign changes
        Vector<Double> xRoots = new Vector<Double> ();
        for (int i=0; i<yValues.size()-1; i++){
            if(yValues.get(i)*yValues.get(i+1) < 0) {
                xRoots.add(xValues.get(i));
            }
        }
        
        // apply secant method near the roots to find root within tolerance
        for(int i = 0; i<xRoots.size(); i++) {
            xRoots.set(i, falsePositionMethod(function, xRoots.get(i), xRoots.get(i)+refinement, tolerance, maxIterations));
        }
        return xRoots;
    }
    
    public static Double secantMethod(Function<Double,Double> function, double xn1, double xn, double tolerance, int maxIterations) throws FunctionValueException {
        double d=Double.MAX_VALUE;
        double fxn1,fxn;
        int n=0;
        while(n < maxIterations && Math.abs(d)>tolerance) {
            fxn1 = function.getValue(xn1);
            fxn = function.getValue(xn);
            d = (xn - xn1) / (fxn - fxn1) * fxn;
            xn1 = xn;
            xn = xn - d;
        }
        return xn;
    }
    
    public static Double falsePositionMethod(Function<Double,Double> function, double s, double t, double tolerance, int maxIterations) throws FunctionValueException {
        int side = 0;
        double r = 0, fr, fs = function.getValue(s), ft = function.getValue(t);
        for (int n = 1; n <= maxIterations; n++)
        {
            r = (fs*t - ft*s) / (fs - ft);
            if (Math.abs(t-s) < tolerance*Math.abs(t+s)) break;
            fr = function.getValue(r);
            if (fr * ft > 0) {
              t = r; ft = fr;
              if (side==-1) fs /= 2;
              side = -1;
            } else if (fs * fr > 0) {
              s = r;  fs = fr;
              if (side==+1) ft /= 2;
              side = +1;
            } else break;
        }
        return r;
    }
}
