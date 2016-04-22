/*
 * RealInterval.java
 * Created Nov 18, 2009
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

import com.googlecode.blaisemath.coordinate.Domain;
import com.googlecode.blaisemath.coordinate.RandomCoordinateGenerator;
import com.googlecode.blaisemath.util.MinMaxBean;

/**
 * <p>
 *   This class represents an interval of the real axis. It has domain functionality
 *   and uniform random point generation functionality.
 * </p>
 * @author Elisha Peterson
 */
public class RealInterval
        implements Domain<Double>, MinMaxBean<Double>, RandomCoordinateGenerator<Double> {

    /** Min and max values. */
    protected double min, max;
    /** Inclusive properties. */
    protected boolean includeMin = true, includeMax = true;

    /** 
     * Constructs real interval with specified min and max (included by default in the interval)
     * @param min minimum of interval
     * @param max maximum of interval
     */
    public RealInterval(double min, double max) {
        if (min <= max) {
            this.min = min;
            this.max = max;
        } else {
            this.min = max;
            this.max = min;
        }
    }

    /**
     * Constructs real interval with specified min and max, and booleans describing
     * whether they are included in the domain.
     * @param min minimum of interval
     * @param minInclusive whether minimum is included in domain
     * @param max maximum of interval
     * @param maxInclusive whether maax is included in domain
     */
    public RealInterval(double min, boolean minInclusive, double max, boolean maxInclusive) {
        this.min = min;
        this.includeMin = minInclusive;
        this.max = max;
        this.includeMax = maxInclusive;
    }

    @Override
    public String toString() {
        return (includeMin ? "[" : "(") + min + "," + max + (includeMax ? "]" : ")");
    }
    
    //
    // BEAN PROPERTIES
    //
    
    /** 
     * @return minimum value of interval
     */
    @Override
    public Double getMinimum() {
        return min;
    }

    /** 
     * Sets minimum value of interval; if greater than max value, reverses order of min and max.
     * @param min 
     */
    public void setMinimum(Double min) {
        if (max < min) {
            this.min = this.max;
            this.max = min;
        } else
            this.min = min;
    }

    /** 
     * @return maximum value of interval 
     */
    @Override
    public Double getMaximum() {
        return max;
    }

    /** 
     * Sets maximum value of interval; if less than min value, reverses order of min and max.
     * @param max
     */
    public void setMaximum(Double max) {
        if (max < min) {
            this.max = this.min;
            this.min = max;
        } else
            this.max = max;
    }

    public boolean isIncludeMaximum() {
        return includeMax;
    }

    public void setIncludeMaximum(boolean includeMax) {
        this.includeMax = includeMax;
    }

    public boolean isIncludeMinimum() {
        return includeMin;
    }

    public void setIncludeMinimum(boolean includeMin) {
        this.includeMin = includeMin;
    }



    //
    // INTERFACE METHODS
    //

    public boolean contains(Double coord) {
        return (coord > min && coord < max)
                || (coord == min && includeMin)
                || (coord == max && includeMax);
    }

    /**
     * Computes and returns a random value chosen uniformly on the range [min,max],
     * using the default math random function.
     * Does not work well if one of the limits of the interval is infinite.
     *
     * @return a randomly chosen value
     */
    public Double randomValue() {
        return min + Math.random() * (
                Double.isInfinite(min) || Double.isInfinite(max)
                    ? pseudomax() - pseudomin()
                    : max - min);
    }

    /** @return a double value "close" to the max if the range is infinite, otherwise the actual max. */
    private double pseudomax() {
        return Double.isInfinite(max) ? Double.MAX_VALUE / 10 : max;
    }

    /** @return a double value "close" to the min if the range is infinite, otherwise the actual min. */
    private double pseudomin() {
        return Double.isInfinite(min) ? -Double.MAX_VALUE / 10 : min;
    }
}
