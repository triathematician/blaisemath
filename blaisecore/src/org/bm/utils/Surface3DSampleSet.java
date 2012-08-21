package org.bm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateVectorialFunction;
import org.bm.blaise.specto.plane.function.PlaneParametricCurve;
import scio.coordinate.Point3D;
import scio.coordinate.MaxMinDomain;
import scio.coordinate.sample.SampleGenerator;

public class Surface3DSampleSet implements SampleGenerator<Point3D> {

    /** Function */
    MultivariateVectorialFunction func;
    /** Range of values for display purposes */
    MaxMinDomain<Double> range1;
    /** Range of values for display purposes */
    MaxMinDomain<Double> range2;
    /** Number of samples, first dir */
    int nSample1 = 20;
    /** Number of samples, second dir */
    int nSample2 = 20;

    public Surface3DSampleSet(MultivariateVectorialFunction func, MaxMinDomain<Double> range1, MaxMinDomain<Double> range2) {
        this.func = func;
        this.range1 = range1;
        this.range2 = range2;
    }

    public MultivariateVectorialFunction getFunc() {
        return func;
    }

    public void setFunc(MultivariateVectorialFunction func) {
        this.func = func;
    }

    public MaxMinDomain<Double> getRange1() {
        return range1;
    }

    public void setRange1(MaxMinDomain<Double> range1) {
        this.range1 = range1;
    }

    public MaxMinDomain<Double> getRange2() {
        return range2;
    }

    public void setRange2(MaxMinDomain<Double> range2) {
        this.range2 = range2;
    }

    public int getNSample1() {
        return nSample1;
    }

    public void setNSample1(int nSample1) {
        this.nSample1 = nSample1;
    }

    public int getNSample2() {
        return nSample2;
    }

    public void setNSample2(int nSample2) {
        this.nSample2 = nSample2;
    }

    transient Point3D diff;
    
    public List<Point3D> getSamples() {
        List<Point3D> result = new ArrayList<Point3D>();
        double[] val = null;
        for (int i = 0; i < nSample1; i++) {
            for (int j = 0; j < nSample1; j++) {
                try {
                    val = func.value( new double[]{
                            range1.getMin() + i * (range1.getMax() - range1.getMin()) / nSample1,
                            range2.getMin() + j * (range2.getMax() - range2.getMin()) / nSample2 } );
                } catch (FunctionEvaluationException ex) {
                    val = new double[]{0, 0, 0};
                }
                result.add(new Point3D(val[0], val[1], val[2]));
            }
        }
        diff = new Point3D(Math.abs(result.get(1).x - result.get(0).x), Math.abs(result.get(1).y - result.get(0).y), Math.abs(result.get(1).z - result.get(0).z));
        return result;
    }

    public Point3D getSampleDiff() {
        double max = Math.max(Math.max(diff.x, diff.y), diff.z);
        max = Math.max(.1, max);
        return new Point3D(max, max, max);
    }
}
