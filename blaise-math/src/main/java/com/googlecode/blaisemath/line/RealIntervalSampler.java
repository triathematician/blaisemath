/*
 * RealIntervalSampler.java
 * Created on Nov 18, 2009
 */

package com.googlecode.blaisemath.line;

/*
 * #%L
 * BlaiseMath
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.AbstractList;
import java.util.List;
import com.googlecode.blaisemath.coordinate.SampleSet;

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
