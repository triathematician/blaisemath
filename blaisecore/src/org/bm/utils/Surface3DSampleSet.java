package org.bm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.FunctionEvaluationException;
import org.bm.blaise.specto.plane.function.PlaneParametricFunction;
import scio.coordinate.P3D;
import scio.coordinate.utils.ParameterRange;
import scio.coordinate.utils.SampleSetGenerator;
import scio.function.MultivariateVectorialFunction;

public class Surface3DSampleSet implements SampleSetGenerator<P3D> {

    /** Function */
    MultivariateVectorialFunction func;
    /** Range of values for display purposes */
    ParameterRange<Double> range1;
    /** Range of values for display purposes */
    ParameterRange<Double> range2;
    /** Number of samples, first dir */
    int nSample1 = 20;
    /** Number of samples, second dir */
    int nSample2 = 20;

    public Surface3DSampleSet(MultivariateVectorialFunction func, ParameterRange<Double> range1, ParameterRange<Double> range2) {
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

    public ParameterRange<Double> getRange1() {
        return range1;
    }

    public void setRange1(ParameterRange<Double> range1) {
        this.range1 = range1;
    }

    public ParameterRange<Double> getRange2() {
        return range2;
    }

    public void setRange2(ParameterRange<Double> range2) {
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

    transient P3D diff;
    
    public List<P3D> getSamples() {
        List<P3D> result = new ArrayList<P3D>();
        double[] val = null;
        for (int i = 0; i < nSample1; i++) {
            for (int j = 0; j < nSample1; j++) {
                try {
                    val = func.value( new double[]{
                            range1.getMin() + i * (range1.getMax() - range1.getMin()) / nSample1,
                            range2.getMin() + j * (range2.getMax() - range2.getMin()) / nSample2 } );
                } catch (FunctionEvaluationException ex) {
                    val = new double[]{0, 0, 0};
                    Logger.getLogger(PlaneParametricFunction.class.getName()).log(Level.SEVERE, null, ex);
                }
                result.add(new P3D(val[0], val[1], val[2]));
            }
        }
        diff = new P3D(Math.abs(result.get(1).x - result.get(0).x), Math.abs(result.get(1).y - result.get(0).y), Math.abs(result.get(1).z - result.get(0).z));
        return result;
    }

    public P3D getSampleDiff() {
        double max = Math.max(Math.max(diff.x, diff.y), diff.z);
        max = Math.max(.1, max);
        return new P3D(max, max, max);
    }
}
