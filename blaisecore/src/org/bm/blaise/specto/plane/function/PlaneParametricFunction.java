/**
 * PlaneParametricFunction.java
 * Created on Aug 9, 2009
 */
package org.bm.blaise.specto.plane.function;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import scio.coordinate.utils.ParameterRange;
import scio.coordinate.utils.ParameterRangeSupport;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.VisometryMouseEvent;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.plottable.VComputedPath;

/**
 * <p>
 *   <code>PlaneParametricFunction</code> plots a curve depending on a single parameter.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneParametricFunction extends VComputedPath<Point2D.Double> {

    /** The underlying function */
    UnivariateVectorialFunction func;
    /** Range of values for display purposes */
    ParameterRange<Double> range;
    /** Step rate for going through parameter values */
    double parameterStep = 0.05;

    /** Initializes with an underlying function and a step rate for going through parameter values.
     * @param func the function
     * @param parameterStep the step rate for parameters
     */
    public PlaneParametricFunction(UnivariateVectorialFunction func, double min, double max, double parameterStep) {
        setFunc(func);
        setRange(new ParameterRangeSupport<Double>(min, true, max, true));
        setParameterStep(parameterStep);
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    public UnivariateVectorialFunction getFunc() {
        return func;
    }

    public void setFunc(UnivariateVectorialFunction func) {
        if (func != null && this.func != func) {
            this.func = func;
            needsComputation = true;
        }
    }

    public ParameterRange<Double> getRange() {
        return range;
    }

    public void setRange(ParameterRange<Double> range) {
        this.range = range;
        needsComputation = true;
    }

    public double getParameterStep() {
        return parameterStep;
    }

    public void setParameterStep(double parameterStep) {
        if (parameterStep <= 0) {
            throw new IllegalArgumentException("ParameterStep should be positive! (value = " + parameterStep);
        }
        this.parameterStep = parameterStep;
        needsComputation = true;
    }

    //
    //
    // DRAW METHODS
    //
    //

    public void visometryChanged(Visometry vis, VisometryGraphics canvas) {
        // MAY EVENTUALLY REQUIRE RECOMPUTATION, BUT NOT RIGHT NOW
    }

    /** Recomputes the visual path for the function. */
    protected void recompute(VisometryGraphics<Point2D.Double> vg) {
        try {
            if (path == null) {
                path = new GeneralPath();
            } else {
                path.reset();
            }
            double[] coords = func.value(range.getMin());
            path.moveTo((float) coords[0], (float) coords[1]);
            for (double x = range.getMin(); x < range.getMax() + parameterStep / 2; x += parameterStep) {
                coords = func.value(x);
                path.lineTo((float) coords[0], (float) coords[1]);
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneParametricFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isClickablyCloseTo(VisometryMouseEvent<Point2D.Double> e) {
        // TODO (later) - add click functionality for parametric plane curves
        return false;
    }

    @Override
    public String toString() {
        return "Parametric Curve";
    }
}
