package org.bm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.specto.plane.function.PlaneParametricCurve;
import scio.coordinate.P3D;
import scio.coordinate.MaxMinDomain;
import scio.coordinate.sample.SampleCoordinateSetGenerator;

public class Curve3DSampleSet implements SampleCoordinateSetGenerator<P3D> {

    /** Function */
    UnivariateVectorialFunction func;
    /** Range of values for display purposes */
    MaxMinDomain<Double> range;
    /** Number of samples */
    int nSamples = 20;

    public Curve3DSampleSet(UnivariateVectorialFunction func, MaxMinDomain<Double> range) {
        this.func = func;
        this.range = range;
    }

    public UnivariateVectorialFunction getFunc() {
        return func;
    }

    public void setFunc(UnivariateVectorialFunction func) {
        this.func = func;
    }

    public int getnSamples() {
        return nSamples;
    }

    public void setnSamples(int nSamples) {
        this.nSamples = nSamples;
    }

    public MaxMinDomain<Double> getRange() {
        return range;
    }

    public void setRange(MaxMinDomain<Double> range) {
        this.range = range;
    }

    transient P3D diff;

    public List<P3D> getSamples() {
        List<P3D> result = new ArrayList<P3D>();
        double[] val = null;
        for (int i = 0; i < nSamples; i++) {
            try {
                val = func.value(range.getMin() + i * (range.getMax() - range.getMin()) / nSamples);
            } catch (FunctionEvaluationException ex) {
                val = new double[]{0, 0, 0};
            }
            result.add(new P3D(val[0], val[1], val[2]));
        }
        diff = new P3D(Math.abs(result.get(1).x - result.get(0).x), Math.abs(result.get(1).y - result.get(0).y), Math.abs(result.get(1).z - result.get(0).z));
        return result;
    }

    public P3D getSampleDiff() {
        double max = Math.max(Math.max(diff.x, diff.y), diff.z);
        return new P3D(max, max, max);
    }
}
