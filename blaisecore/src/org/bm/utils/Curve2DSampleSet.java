package org.bm.utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateVectorialFunction;
import org.bm.blaise.specto.plane.function.PlaneParametricCurve;
import scio.coordinate.sample.RealIntervalSampler;
import scio.coordinate.sample.SampleCoordinateSetGenerator;

public class Curve2DSampleSet implements SampleCoordinateSetGenerator<Point2D.Double> {

    /** Function */
    UnivariateVectorialFunction func;
    /** Range of values for display purposes */
    RealIntervalSampler range;

    public Curve2DSampleSet(UnivariateVectorialFunction func, RealIntervalSampler range) {
        this.func = func;
        this.range = range;
    }

    public UnivariateVectorialFunction getFunc() {
        return func;
    }

    public void setFunc(UnivariateVectorialFunction func) {
        this.func = func;
    }

    public void setNumSamples(int numSamples) {
        range.setNumSamples(numSamples);
    }

    public int getNumSamples() {
        return range.getNumSamples();
    }

    public RealIntervalSampler getRange() {
        return range;
    }

    public void setRange(RealIntervalSampler range) {
        this.range = range;
    }

    transient Point2D.Double diff;

    public List<Point2D.Double> getSamples() {
        List<Point2D.Double> result = new ArrayList<Point2D.Double>();
        double[] val = null;
        List<Double> domain = range.getSamples();
        for (Double d : domain) {
            try {
                val = func.value(d);
            } catch (FunctionEvaluationException ex) {
                val = new double[]{0, 0};
                Logger.getLogger(PlaneParametricCurve.class.getName()).log(Level.SEVERE, null, ex);
            }
            result.add(new Point2D.Double(val[0], val[1]));
        }
        double dist = result.get(0).distance(result.get(1));
        // diff = new Point2D.Double(Math.abs(result.get(1).x - result.get(0).x), Math.abs(result.get(1).y - result.get(0).y));
        // TODO - currently distance is approximated by the distance between the first two points... should do a better job here!!
        diff = new Point2D.Double(dist, dist);
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
