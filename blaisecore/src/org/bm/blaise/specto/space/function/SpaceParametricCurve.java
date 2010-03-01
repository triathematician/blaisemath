/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.space.function;

import org.bm.blaise.specto.space.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.specto.plottable.VRectangle;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.Point3D;
import scio.coordinate.MaxMinDomain;
import scio.coordinate.sample.RealIntervalSampler;
import util.ChangeEventHandler;

/**
 *
 * @author ae3263
 */
public class SpaceParametricCurve extends AbstractPlottable<Point3D> {

    /** Function */
    UnivariateVectorialFunction func;
    /** Domain of function */
    RealIntervalSampler domain;
    /** Stores visual interval used to adjust the range. */
    VRectangle<Double> domainPlottable;

    /**
     * Initializes with an underlying function and min/max domain
     * @param func the function
     * @param minT the min value in domain
     * @param maxT the max value in domain
     */
    public SpaceParametricCurve(UnivariateVectorialFunction func, double minT, double maxT) {
        this(func, minT, maxT, 100);
    }

    /**
     * Initializes with an underlying function and min/max domain
     * @param func the function
     * @param minT the min value in domain
     * @param maxT the max value in domain
     * @param numSamples the number of samples
     */
    public SpaceParametricCurve(UnivariateVectorialFunction func, double minT, double maxT, int numSamples) {
        setFunction(func);
        setDomain(new RealIntervalSampler(minT, maxT, numSamples));
        domainPlottable = new VRectangle<Double>(minT, maxT);
        domainPlottable.getStyle().setThickness(3.0f);
        domainPlottable.addChangeListener(this);
    }

    //
    // GETTERS & SETTERS
    //

    public UnivariateVectorialFunction getFunction() {
        return func;
    }

    public void setFunction(UnivariateVectorialFunction func) {
        if (func != null && this.func != func) {
            if (this.func instanceof ChangeEventHandler) {
                ((ChangeEventHandler)this.func).removeChangeListener(this);
            }
            this.func = func;
            if (func instanceof ChangeEventHandler) {
                ((ChangeEventHandler)func).addChangeListener(this);
            }
            needsComputation = true;
            fireStateChanged();
        }
    }

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
        }
        needsComputation = true;
        super.stateChanged(e);
    }

    //
    // DRAW METHODS
    //

    transient boolean needsComputation = true;
    transient List<Point3D[]> segments;

    @Override
    public void paintComponent(VisometryGraphics<Point3D> vg) {
        if (needsComputation) {
            segments = getSegments(func);
            needsComputation = false;
        }
        ((SpaceGraphics) vg).addToScene(segments);
    }

    List<Point3D[]> getSegments(UnivariateVectorialFunction func) {
        List<Point3D[]> result = new ArrayList<Point3D[]>();
        List<Double> samples = domain.getSamples();
        try {
            Point3D last;
            Point3D next = new Point3D(func.value(samples.get(0)));
            for (double x : samples) {
                last = next;
                next = new Point3D(func.value(x));
                result.add(new Point3D[]{last, next});
            }
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(SpaceParametricCurve.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public String toString() {
        return "Parametric Curve 3D";
    }

}
