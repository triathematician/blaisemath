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
    /** Domain of function */
    RealIntervalSampler domain;
    /** Stores visual interval used to adjust the range. */
    VRectangle<Double> domainPlottable;


    /**
     * Initializes with an underlying function and min/max domain
     * @param func the function
     * @param min the min value in domain
     * @param max the max value in domain
     */
    public PlaneParametricCurve(UnivariateVectorialFunction func, double min, double max) {
        this(func, min, max, 100);
    }
    /**
     * Initializes with an underlying function and a step rate for going through parameter values.
     * @param func the function
     * @param min the min value in domain
     * @param max the max value in domain
     * @param numSamples the number of samples
     */
    public PlaneParametricCurve(UnivariateVectorialFunction func, double min, double max, int numSamples) {
        setFunction(func);
        setDomain(new RealIntervalSampler(min, max, numSamples));
        domainPlottable = new VRectangle<Double>(min, max);
        domainPlottable.getStyle().setThickness(3.0f);
        domainPlottable.addChangeListener(this);
    }

    //
    //
    // BEAN PATTERNS
    //
    //

    public UnivariateVectorialFunction getFunction() {
        return func;
    }

    public void setFunction(UnivariateVectorialFunction func) {
        if (func != null && this.func != func) {
            this.func = func;
            needsComputation = true;
            fireStateChanged();
        }
    }

    public MaxMinDomain<Double> getDomain() {
        return domain;
    }

    public void setDomain(MaxMinDomain<Double> range) {
        if (range != null) {
            if (range instanceof RealIntervalSampler) {
                this.domain = (RealIntervalSampler) range;
            } else {
                this.domain.setMin(range.getMin());
                this.domain.setMin(range.getMax());
                this.domain.setMinInclusive(range.isMinInclusive());
                this.domain.setMaxInclusive(range.isMaxInclusive());
            }
            needsComputation = true;
        }
    }

    public VRectangle<Double> getDomainPlottable() {
        return domainPlottable;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == domainPlottable) {
            domain.setMin(domainPlottable.getPoint1());
            domain.setMax(domainPlottable.getPoint2());
            needsComputation = true;
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
        System.out.println("recomputing curve...");
        try {
            if (path == null) {
                path = new GeneralPath();
            } else {
                path.reset();
            }
            List<Double> samples = domain.getSamples();
            double[] coords = func.value(samples.get(0));
            path.moveTo((float) coords[0], (float) coords[1]);
            for (double x : samples) {
                coords = func.value(x);
                path.lineTo((float) coords[0], (float) coords[1]);
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(PlaneParametricCurve.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        return "Parametric Curve 2D";
    }
}
