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
import scio.function.utils.DemoCurve2D;

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
     */
    public PlaneParametricCurve() {
        this(new UnivariateVectorialFunction(){ public double[] value(double x) { return new double[] { Math.cos(x), Math.sin(x) }; } },
                0, 2*Math.PI, 100);
    }

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
    // GETTERS & SETTERS
    //

    public RealIntervalSampler getDomain() {
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

    //
    // EVENT HANDLING
    //

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
    // FUNCTION HANDLING & DEMOS
    //

    DemoCurve2D demo = DemoCurve2D.ARCHIMEDEAN_SPIRAL;

    public UnivariateVectorialFunction getFunction() {
        return demo == demo.NONE ? func : demo;
    }

    public void setFunction(UnivariateVectorialFunction func) {
        if (func != null && this.func != func) {
            this.func = func;
            demo = demo.NONE;
            needsComputation = true;
            fireStateChanged();
        }
    }
    public DemoCurve2D getDemoCurve() {
        return demo;
    }

    public void setDemoCurve(DemoCurve2D demo) {
        this.demo = demo;
        if (demo != DemoCurve2D.NONE) {
            domain = new RealIntervalSampler(demo.t0, demo.t1, 200);
            domainPlottable.setPoint1(demo.t0);
            domainPlottable.setPoint2(demo.t1);
        }
        needsComputation = true;
        fireStateChanged();
    }

    //
    // DRAW METHODS
    //

    /** Recomputes the visual path for the function. */
    protected void recompute(VisometryGraphics<Point2D.Double> vg) {
        UnivariateVectorialFunction useFunc = demo == DemoCurve2D.NONE ? func : demo;
        try {
            if (path == null) {
                path = new GeneralPath();
            } else {
                path.reset();
            }
            List<Double> samples = domain.getSamples();
            double[] coords = useFunc.value(samples.get(0));
            path.moveTo((float) coords[0], (float) coords[1]);
            for (double x : samples) {
                coords = useFunc.value(x);
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
