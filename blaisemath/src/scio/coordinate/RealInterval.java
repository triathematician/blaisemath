/*
 * RealInterval.java
 * Created Nov 18, 2009
 */

package scio.coordinate;

import scio.random.RandomCoordinateGenerator;

/**
 * <p>
 *   This class represents an interval of the real axis. It has domain functionality,
 *   uniformly random point generation functionality, and sampling functionality.
 * </p>
 * @author Elisha Peterson
 */
public class RealInterval extends MaxMinDomainSupport<Double> implements RandomCoordinateGenerator<Double> {

    public RealInterval(MaxMinDomain<Double> domain) {
        super(domain);
    }

    /** 
     * Constructs real interval with specified min and max (included by default in the interval)
     * @param min minimum of interval
     * @param max maximum of interval
     */
    public RealInterval(Double min, Double max) {
        super(min, max);
    }

    public RealInterval(Double min, boolean minInclusive, Double max, boolean maxInclusive) {
        super(min, minInclusive, max, maxInclusive);
    }

    @Override
    public void setMax(Double max) {
        // TODO check for infinites
        if (min != null && max < min) {
            this.max = this.min;
            this.min = max;
        } else {
            this.max = max;
        }
    }

    @Override
    public void setMin(Double min) {
        // TODO check for infinites
        if (max != null && max < min) {
            this.min = this.max;
            this.max = min;
        } else {
            this.min = min;
        }
    }

    /**
     * Computes and returns a random value chosen uniformly on the range [min,max],
     * using the default math random function.
     * Does not work well if one of the limits of the interval is infinite.
     *
     * @return a randomly chosen value
     */
    public Double randomValue() {
        if (min.isInfinite() && max.isInfinite()) {
            return (pseudomax() - pseudomin()) * Math.random() + min;
        } else {
            return (max - min) * Math.random() + min;
        }
    }

    private Double pseudomax() {
        if (max.isInfinite()) {
            return Double.MAX_VALUE/10;
        } else {
            return max;
        }
    }

    private Double pseudomin() {
        if (min.isInfinite()) {
            return -Double.MAX_VALUE/10;
        } else {
            return max;
        }
    }

    @Override
    public String toString() {
        return (minInclusive ? "[" : "(") + min + "," + max + (maxInclusive ? "]" : ")");
    }
}
