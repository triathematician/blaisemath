/*
 * LineSampleSet.java
 * Created on Nov 4, 2009
 */


package org.bm.utils;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import scio.coordinate.utils.SampleSetGenerator;

/**
 * <p>
 *     This class generates a line of evenly-spaced sample points in space.
 * </p>
 * @author Elisha Peterson
 */
public class LineSampleSet implements SampleSetGenerator<Double> {

    /** Min of sampled region. */
    double min;
    /** Max of sampled region. */
    double max;
    /** Determines whether the grid is sampled at "nice" points, e.g. integers */
    boolean sampleNice = false;
    /** Sample numbers. */
    int nSamples = 10;

    public LineSampleSet(double min, double max) {
        this.min = min;
        this.max = max;
    }

    transient double diff;

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public int getNumSamples() {
        return nSamples;
    }

    public void setNumSamples(int nSamples) {
        this.nSamples = nSamples;
    }

    /**
     * Computes samples using the current boundaries.
     * @param n number of samples
     * @return an <code>AbstractList</code> containing the sampled points
     */
    private List<Double> computeSamples(int n) {
        nSamples = n;
        diff = (max - min) / (n - 1);
        return new AbstractList<Double>() {
            @Override
            public Double get(int index) {
                return min + diff * index;
            }
            @Override
            public int size() {
                return nSamples;
            }
        };
    }

    /**
     * Computes samples using the current boundaries; and puts the sample points
     * at "nice" coordinates. Tries to ensure the spacing is about what is suggested
     * by the parameters to the command, although this is not guaranteed, as placement
     * of the poitns takes priority.
     *
     * @param approx approximate spacing
     * @return an <code>AbstractList</code> containing the sampled points
     */
    private List<Double> computeNiceSamples(double approx) {
        final double[] xRange = NiceRangeGenerator.STANDARD.niceRange(min, max, approx);
        nSamples = xRange.length;
        diff = xRange[1] - xRange[0];
        List<Double> result = new ArrayList<Double>();
        for (int i = 0; i < xRange.length; i++) {
            result.add(xRange[i]);
        }
        return result;
    }

    public List<Double> getSamples() {
        if (sampleNice) {
            return computeNiceSamples((max-min)/nSamples);
        } else {
            return computeSamples(nSamples);
        }
    }

    public Double getSampleDiff() {
        return diff;
    }
}
