/**
 * PlaneParametricFunction.java
 * Created on Aug 9, 2009
 */
package org.bm.blaise.specto.plane.function;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import scio.coordinate.MaxMinDomain;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import org.bm.blaise.specto.visometry.Visometry;
import org.bm.blaise.specto.plottable.VComputedPath;
import org.bm.blaise.specto.plottable.VRectangle;
import scio.coordinate.sample.RealIntervalSampler;

/**
 * <p>
 *   <code>PlaneParametricFunction</code> plots a curve depending on a single parameter.
 * </p>
 *
 * @author Elisha Peterson
 */
public class PlaneParametricCurve extends VComputedPath<Point2D.Double> {

    /** The underlying function */
    UnivariateVectorialFunction func;
    /** Range of values for display purposes */
    RealIntervalSampler range;
    /** Stores visual interval used to adjust the range. */
    VRectangle<Double> domain;


    /**
     * Initializes with an underlying function and a step rate for going through parameter values.
     * @param func the function
     * @param parameterStep the step rate for parameters
     */
    public PlaneParametricCurve(UnivariateVectorialFunction func, double min, double max) {
        this(func, min, max, 100);
    }
    /**
     * Initializes with an underlying function and a step rate for going through parameter values.
     * @param func the function
     * @param parameterStep the step rate for parameters
     * @param numSamples the number of samples
     */
    public PlaneParametricCurve(UnivariateVectorialFunction func, double min, double max, int numSamples) {
        setFunc(func);
        setRange(new RealIntervalSampler(min, max, numSamples));
        domain = new VRectangle<Double>(min, max);
        domain.addChangeListener(this);
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
            fireStateChanged();
        }
    }

    public MaxMinDomain<Double> getRange() {
        return range;
    }

    public void setRange(MaxMinDomain<Double> range) {
        if (range != null) {
            if (range instanceof RealIntervalSampler) {
                this.range = (RealIntervalSampler) range;
            } else {
                this.range.setMin(range.getMin());
                this.range.setMin(range.getMax());
                this.range.setMinInclusive(range.isMinInclusive());
                this.range.setMaxInclusive(range.isMaxInclusive());
            }
            needsComputation = true;
        }
    }

    public VRectangle<Double> getDomain() {
        return domain;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == domain) {
            range.setMin(domain.getPoint1());
            range.setMax(domain.getPoint2());
        }
        super.stateChanged(e);
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
            List<Double> domain = range.getSamples();
            double[] coords = func.value(domain.get(0));
            path.moveTo((float) coords[0], (float) coords[1]);
            for (double x : domain) {
                coords = func.value(x);
                path.lineTo((float) coords[0], (float) coords[1]);
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneParametricCurve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return "Parametric Curve";
    }
}
