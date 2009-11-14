/**
 * Newton1DSpace.java
 * Created on Sep 25, 2009
 */

package scio.function;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolver;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactory;

/**
 * <p>
 *   <code>Newton1DSpace</code> applies Newton's algorithm along a line in the domain
 *   of a multivariate function.
 * </p>
 *
 * @author Elisha Peterson
 */
public class Newton1DSpace {

    MultivariateRealFunction func;    
    double[] point;
    double[] dir;

    public Newton1DSpace(MultivariateRealFunction func, double[] point, double[] dir) {
        setParameters(func, point, dir);
    }

    /** 
     * Sets up function and starting values. Checks to ensure that the dimensions are working properly,
     * and makes sure that dir is not a zero vector, by changing one of its entries if it is.
     *
     * @param func the underlying function
     * @param point the starting point
     * @param dir the direction
     */
    private void setParameters(MultivariateRealFunction func, double[] point, double[] dir) {
        // ensure dimensions work
        try {
            func.value(point);
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(Newton1DSpace.class.getName()).log(Level.SEVERE, null, ex);
            return;
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Newton1DSpace.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        this.func = func;
        this.point = point;
        
        // ensure dir has enough dimension
        if (dir.length >= point.length) {
            this.dir = dir;
        } else {
            double[] dir2 = new double[point.length];
            for (int i = 0; i < dir.length; i++) {
                dir2[i] = dir[i];
            }
            this.dir = dir2;
        }

        // if all of dir is zero, change first value to 1
        boolean allZero = true;
        for (int i = 0; i < dir.length; i++) {
            if (dir[i] != 0) {
                allZero = false;
                break;
            }
        }
        if (allZero) {
            dir[0] = 1;
        }

        traceFunction = getTraceFunction(func, point, dir);
    }

    /** Represents trace of function along given line. */
    transient UnivariateRealFunction traceFunction;


    // STATIC METHODS

    /** Constructs a function that represents the trace in the specified direction. */
    public static UnivariateRealFunction getTraceFunction(final MultivariateRealFunction func, final double[] point, final double[] dir) {
        return new UnivariateRealFunction() {
            public double value(double x) throws FunctionEvaluationException {
                double[] input = new double[point.length];
                for (int i = 0; i < point.length; i++) {
                    input[i] = point[i] + x * dir[i];
                }
                return func.value(input);
            }
        };
    }


    /** Finds zero of a function in specified direction from point. */
    public static double[] getRoot(final MultivariateRealFunction func, final double[] point, final double[] dir) {
        UnivariateRealFunction func2 = getTraceFunction(func, point, dir);
        UnivariateRealSolver solver = UnivariateRealSolverFactory.newInstance().newDefaultSolver();
        double t = 0;
        try {
            t = solver.solve(func2, -5.0, 5.0, 0.0);
        } catch (ConvergenceException ex) {
            Logger.getLogger(Newton1DSpace.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(Newton1DSpace.class.getName()).log(Level.SEVERE, null, ex);
        }
        double[] input = new double[point.length];
        for (int i = 0; i < point.length; i++) {
            input[i] = point[i] + t * dir[i];
        }
        try {
            if (Math.abs(func.value(input)) > 1e-6) {
                return point;
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(Newton1DSpace.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Newton1DSpace.class.getName()).log(Level.SEVERE, null, ex);
        }
        return input;
    }

    static double[] getRandPoint(double[] min, double[] max) {
        double[] result = new double[min.length];
        for (int i = 0; i < max.length; i++) {
            result[i] = min[i] + Math.random() * (max[i] - min[i]);
        }
        return result;
    }

    static double[] getRandDir(double[] min, double[] max, double scale) {
        double[] result = new double[min.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (2 * Math.random() - 1) * scale * (max[i] - min[i]);
        }
        return result;
    }

    /** 
     * Finds many roots of the specified function. If the algorithm fails to find a root,
     * the result for that attempt will be stored as a null.
     */
    public static double[][] getNRoots(int nRoots, final MultivariateRealFunction func, final double[] pointMin, final double[] pointMax) {
        double[][] result = new double[nRoots][pointMin.length];
        double value;
        for (int i = 0; i < result.length; i++) {
            result[i] = getRoot(func, getRandPoint(pointMin, pointMax), getRandDir(pointMin, pointMax, 0.5));
            try {
                value = func.value(result[i]);
            } catch (FunctionEvaluationException ex) {
                value = 1;
            } catch (IllegalArgumentException ex) {
                value = 1;
            }
            if (Math.abs(value) > 1e-6) {
                result[i] = null;
            }
        }
        return result;
    }


    public static void main(String[] args) {
        MultivariateRealFunction func = new MultivariateRealFunction() {
            public double value(double[] point) throws FunctionEvaluationException, IllegalArgumentException {
                return point[0]*point[0] + point[1]*point[1] + point[2]*point[2] - 25;
            }
        };
        double[] result = getRoot(func, new double[]{1, 3, 2}, new double[]{1, 1, 1});
        System.out.println(Arrays.toString(result));
    }
}
