package org.bm.utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.specto.plane.function.PlaneParametricFunction;
import scio.coordinate.utils.ParameterRange;
import scio.coordinate.utils.SampleSetGenerator;

public class Curve2DSampleSet implements SampleSetGenerator<Point2D.Double> {

    /** Function */
    UnivariateVectorialFunction func;
    /** Range of values for display purposes */
    ParameterRange<Double> range;
    /** Number of samples */
    int nSamples = 20;

    public Curve2DSampleSet(UnivariateVectorialFunction func, ParameterRange<Double> range) {
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

    public ParameterRange<Double> getRange() {
        return range;
    }

    public void setRange(ParameterRange<Double> range) {
        this.range = range;
    }

    transient Point2D.Double diff;

    public List<Point2D.Double> getSamples() {
        List<Point2D.Double> result = new ArrayList<Point2D.Double>();
        double[] val = null;
        for (int i = 0; i < nSamples; i++) {
            try {
                val = func.value(range.getMin() + i * (range.getMax() - range.getMin()) / nSamples);
            } catch (FunctionEvaluationException ex) {
                val = new double[]{0, 0};
                Logger.getLogger(PlaneParametricFunction.class.getName()).log(Level.SEVERE, null, ex);
            }
            result.add(new Point2D.Double(val[0], val[1]));
        }
        diff = new Point2D.Double(Math.abs(result.get(1).x - result.get(0).x), Math.abs(result.get(1).y - result.get(0).y));
        return result;
    }

    public Point2D.Double getSampleDiff() {
        return diff;
    }

    @Override
    public String toString() {
        return "Curve Sample";
    }
}
