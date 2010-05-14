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
public class RealIntervalSampler extends RealInterval
        implements SampleSet<Double> {

    /** Number of samples in the interval. */
    int numSamples;

    public RealIntervalSampler(double min, double max, int numSamples) {
        super(min, max);
        this.numSamples = numSamples;
    }

    //
    // BEAN PROPERTIES
    //

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
    }

    //
    // SAMPLING METHODS
    //

    public List<Double> getSamples() {
        // start at min if that's included, min + diff if not min but max is, min + diff/2 if neither is
        double n = (includeMax && includeMin) ? numSamples - 1 : numSamples;
        final double diff = (max-min)/n;
        final double mx = includeMin ? min : ( includeMax ? min + diff : min + diff/2 );
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
        double n = (includeMin && includeMax) ? numSamples - 1 : numSamples;
        return (max-min)/n;
    }
}
