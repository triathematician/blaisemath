/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.specto.space.function;

import org.bm.blaise.specto.space.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.math.FunctionEvaluationException;
import org.bm.blaise.specto.visometry.AbstractPlottable;
import org.bm.blaise.specto.visometry.VisometryGraphics;
import scio.coordinate.P3D;
import scio.coordinate.utils.ParameterRange;
import scio.coordinate.utils.ParameterRangeSupport;
import scio.function.MultivariateVectorialFunction;
import scio.function.utils.SampleSurface3D;

/**
 *
 * @author ae3263
 */
public class SpaceParametricSurface extends AbstractPlottable<P3D> {

    // SAMPLES

    SampleSurface3D sample = SampleSurface3D.SPHERE;

    public SampleSurface3D getSample() {
        return sample;
    }

    public void setSample(SampleSurface3D sample) {
        this.sample = sample;
        func = sample;
        domain1 = new ParameterRangeSupport<Double>(sample.u0, true, sample.u1, true);
        domain2 = new ParameterRangeSupport<Double>(sample.v0, true, sample.v1, true);
    }

    /** Function */
    MultivariateVectorialFunction func;
    /** Range of values for display purposes */
    ParameterRange<Double> domain1, domain2;
    /** Sample quantities */
    int sample1 = 20;
    int sample2 = 20;

    public SpaceParametricSurface(MultivariateVectorialFunction func, double minU, double maxU, int sampleU, double minV, double maxV, int sampleV) {
        setFunc(func);
        setDomain1(new ParameterRangeSupport<Double>(minU, true, maxU, true));
        setDomain2(new ParameterRangeSupport<Double>(minV, true, maxV, true));
        setSample1(sampleU);
        setSample2(sampleV);
    }

    public MultivariateVectorialFunction getFunc() {
        return func;
    }

    public void setFunc(MultivariateVectorialFunction func) {
        this.func = func;
    }

    public ParameterRange<Double> getDomain1() {
        return domain1;
    }

    public void setDomain1(ParameterRange<Double> domain1) {
        this.domain1 = domain1;
    }

    public ParameterRange<Double> getDomain2() {
        return domain2;
    }

    public void setDomain2(ParameterRange<Double> domain2) {
        this.domain2 = domain2;
    }

    public int getSample1() {
        return sample1;
    }

    public void setSample1(int sample1) {
        this.sample1 = sample1;
    }

    public int getSample2() {
        return sample2;
    }

    public void setSample2(int sample2) {
        this.sample2 = sample2;
    }


    //
    //
    // DRAW METHODS
    //
    //

    @Override
    public void paintComponent(VisometryGraphics<P3D> vg) {
        ((SpaceGraphics) vg).addToScene(getPolys(sample1, sample2));
    }

    List<P3D[]> getPolys(int stepX, int stepY) {
        if (stepX < 1 || stepY < 1) {
            return Collections.EMPTY_LIST;
        }
        List<P3D[]> result = new ArrayList<P3D[]>(stepX * stepY);
        double[] input = new double[2];
        double dx = (domain1.getMax() - domain1.getMin()) / stepX;
        double dy = (domain2.getMax() - domain2.getMin()) / stepY;
        P3D[][] arr = new P3D[stepX+1][stepY+1];
        try {
            for (int i = 0; i <= stepX; i++) {
                for (int j = 0; j <= stepY; j++) {
                    input[0] = domain1.getMin() + dx * i;
                    input[1] = domain2.getMin() + dy * j;
                    arr[i][j] = new P3D(func.value(input));
                }
            }
        } catch (FunctionEvaluationException e) {
            System.out.println("getPolys exception in SpaceFunction!");
        }
        for (int i = 0; i < stepX; i++) {
            for (int j = 0; j < stepY; j++) {
                result.add(new P3D[]{ arr[i][j], arr[i+1][j], arr[i+1][j+1], arr[i][j+1] });
            }
        }
        return result;
    }
}
