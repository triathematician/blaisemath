/*
 * RealIntervalSampler.java
 * Created on Nov 18, 2009
 */

package scio.coordinate.sample;

import scio.coordinate.*;
import java.util.AbstractList;
import java.util.List;

/**
 * <p>
 *    This class contains features to sample an interval at regular values between
 *    the maximum and the minimum. Requires the user to provide the number of samples
 *    desired. If the interval includes the max (or the min) these will be part of the
 *    sample. Otherwise, they will not. If they do not, then the distance between successive
 *    points is (max-min)/n, and if they do the distance is (max-min)/(n-1).
 * </p>
 * @author Elisha Peterson
 */
public class RealIntervalSampler extends RealInterval implements SampleCoordinateSetGenerator<Double> {

    /** Number of samples in the interval. */
    int numSamples;

    public RealIntervalSampler(MaxMinDomain<Double> domain, int numSamples) {
        super(domain);
        this.numSamples = numSamples;
    }

    public RealIntervalSampler(Double min, boolean minInclusive, Double max, boolean maxInclusive, int numSamples) {
        super(min, minInclusive, max, maxInclusive);
        this.numSamples = numSamples;
    }

    public RealIntervalSampler(Double min, Double max, int numSamples) {
        super(min, max);
        this.numSamples = numSamples;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }

    public List<Double> getSamples() {
        // start at min if that's included, min + diff if not min but max is, min + diff/2 if neither is
        double n = (maxInclusive && minInclusive) ? numSamples - 1 : numSamples;
        final double diff = (max-min)/n;
        final double mx = minInclusive ? min : ( maxInclusive ? min + diff : min + diff/2 );
        return new AbstractList<Double>() {
            @Override
            public Double get(int index) {
                return mx + diff * index;
            }
            @Override
            public int size() {
                return numSamples;
            }
        };
    }

    public Double getSampleDiff() {
        double n = (maxInclusive && minInclusive) ? numSamples - 1 : numSamples;
        return (max-min)/n;
    }

}
