/**
 * RootFindingUtils.java
 * Created on May 22, 2008
 */
package scio.function;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.SecantSolver;
import org.apache.commons.math.analysis.UnivariateRealSolver;
import org.apache.commons.math.analysis.UnivariateRealSolverFactory;

/**
 * Contains algorithm for finding multiple roots of a function within a given range.
 * @author Elisha Peterson
 */
public class RootFindingUtils {

    /** Enforce no instantiation */
    private RootFindingUtils() {
    }

    /**
     * Attempts to find all roots of a function using a specified solver. Divides up the interval
     * into chunks of specified size (<code>refinement</code> parameter), and attempts to find a root
     * within each interval.
     * 
     * @param function the function to find roots
     * @param solver the underlying solver
     *
     * @param min the minimum value on the interval
     * @param max the maximum value on the interval
     * @param refinement the initial chunk size for finding roots
     *
     * @return list of roots
     */
    public static List<Double> findAllRoots(
            RealFunction function,
            UnivariateRealSolver solver,
            double min, double max, double refinement) {

        // set up array of xvalues for testing
        double[] xx = new double[(int) Math.floor((max-min)/refinement)];
        for (int i = 0; i < xx.length; i++) {
            xx[i] = min + i * refinement;
        }

        ArrayList<Double> result = new ArrayList<Double>();
        for (int i = 0; i < xx.length-1; i++) {
            try {
                if (function.value(xx[i]) * function.value(xx[i+1]) > 0) {
                    continue; // same sign, so no root finding
                }
                result.add(solver.solve(xx[i], xx[i+1]));
            } catch (MathException e) {
                // doesn't converge, indicating no root
            }
        }
        return result;
    }

    /** Should use SecantSolver directly. */
    @Deprecated
    public static double secantMethod(RealFunction function, double min, double max, double tolerance, int maxIterations) throws FunctionEvaluationException, MaxIterationsExceededException {
        UnivariateRealSolverFactory ursf = UnivariateRealSolverFactory.newInstance();
        SecantSolver urs = (SecantSolver) ursf.newSecantSolver(function);
        urs.setAbsoluteAccuracy(tolerance);
        urs.setMaximalIterationCount(maxIterations);

        return urs.solve(min, max);
    }

    /** Should use solver factory directly! */
    @Deprecated
    public static double falsePositionMethod(
            RealFunction function,
            double s, double t,
            double tolerance, int maxIterations)
            throws FunctionEvaluationException {
        int side = 0;
        double r = 0, fr, fs = function.getValue(s), ft = function.getValue(t);
        for (int n = 1; n <= maxIterations; n++) {
            r = (fs * t - ft * s) / (fs - ft);
            if (Math.abs(t - s) < tolerance * Math.abs(t + s)) {
                break;
            }
            fr = function.getValue(r);
            if (fr * ft > 0) {
                t = r;
                ft = fr;
                if (side == -1) {
                    fs /= 2;
                }
                side = -1;
            } else if (fs * fr > 0) {
                s = r;
                fs = fr;
                if (side == +1) {
                    ft /= 2;
                }
                side = +1;
            } else {
                break;
            }
        }
        return r;
    }
}
