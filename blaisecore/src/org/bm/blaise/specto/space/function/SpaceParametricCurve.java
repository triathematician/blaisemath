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
import org.apache.commons.math.FunctionEvaluationException;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.P3D;
import scio.coordinate.utils.ParameterRange;
import scio.coordinate.utils.ParameterRangeSupport;
import scio.function.UnivariateVectorialFunction;

/**
 *
 * @author ae3263
 */
public class SpaceParametricCurve extends AbstractPlottable<P3D> {

    /** Function */
    UnivariateVectorialFunction func;
    /** Range of values for display purposes */
    ParameterRange<Double> domain;
    /** Samples per curve */
    int samples = 100;

    public SpaceParametricCurve(UnivariateVectorialFunction func, double minT, double maxT, int samples) {
        setFunc(func);
        setDomain(new ParameterRangeSupport<Double>(minT, true, maxT, true));
        setSamples(samples);
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

    public ParameterRange<Double> getDomain() {
        return domain;
    }

    public void setDomain(ParameterRange<Double> range) {
        this.domain = range;
        needsComputation = true;
    }

    public int getSamples() {
        return samples;
    }

    public void setSamples(int samples) {
        if (samples <= 1) {
            throw new IllegalArgumentException("samples should be greater than 1");
        }
        this.samples = samples;
        needsComputation = true;
    }

    //
    //
    // DRAW METHODS
    //
    //

    transient boolean needsComputation = true;
    transient List<P3D[]> segments;

    @Override
    public void paintComponent(VisometryGraphics<P3D> vg) {
        if (needsComputation) {
            segments = getSegments(samples);
            needsComputation = false;
        }
        ((SpaceGraphics) vg).addToScene(segments);
    }

    List<P3D[]> getSegments(int steps) {
        List<P3D[]> result = new ArrayList<P3D[]>(steps);
        if (steps < 1) {
            return result;
        }
        double dt = (domain.getMax() - domain.getMin()) / steps;
        P3D last;
        double[] temp = null;
        double[] nextArr = null;
        try {
            temp = func.value(domain.getMin());
        } catch (FunctionEvaluationException ex) {
            Logger.getLogger(SpaceParametricCurve.class.getName()).log(Level.SEVERE, null, ex);
        }
        P3D next = new P3D(temp[0], temp[1], temp[2]);
        try {
            for (int i = 0; i < steps; i++) {
                last = next;
                nextArr = func.value(domain.getMin() + (i + 1) * dt);
                next = new P3D(nextArr[0], nextArr[1], nextArr[2]);
                result.add(new P3D[]{last, next});
            }
        } catch (FunctionEvaluationException e) {
            System.out.println("getPolys exception in SpaceFunction!");
        }
        return result;
    }

    @Override
    public String toString() {
        return "Parametric Curve";
    }

}
