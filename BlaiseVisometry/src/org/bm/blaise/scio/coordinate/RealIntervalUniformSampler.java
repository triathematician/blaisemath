/*
 * RealIntervalSampler.java
 * Created on Nov 18, 2009
 */

package org.bm.blaise.scio.coordinate;

import javax.swing.event.ChangeEvent;
import java.util.AbstractList;
import java.util.List;
import javax.swing.event.ChangeListener;
import org.bm.blaise.scio.coordinate.sample.SampleSet;
import org.bm.util.ChangeSupport;

/**
 * <p>
 *    This class contains features to sample an interval at regular values between
 *    the maximum and the minimum. Requires the user to provide the number of samples
 *    desired. If the interval includes the max (or the min) these will be part of the
 *    sample. Otherwise, they will not. If they do not, then the distance between successive
 *    points is (max-min)/n, and if they do the distance is (max-min)/(n-1).
 * </p>
 * <p>
 *    The class is based upon a <code>RealInterval</code> class describing the domain.
 *    If that interval fires events, then this class will listen for changes to the domain
 *    and pass them on to interested listeners.
 * </p>
 * @author Elisha Peterson
 */
public class RealIntervalUniformSampler extends AbstractList<Double>
        implements SampleSet<Double>, MinMaxBean<Double>, ChangeListener {

    /** Domain of sampler. */
    RealInterval domain;
    /** Number of samples in the interval. */
    int numSamples;

    public RealIntervalUniformSampler(double min, double max, int numSamples) {
        this(new RealIntervalBroadcaster(min, max), numSamples);
    }

    public RealIntervalUniformSampler(RealInterval domain, int numSamples) {
        this.domain = domain;
        if (domain instanceof RealIntervalBroadcaster)
            ((RealIntervalBroadcaster)domain).addChangeListener(this);
        this.numSamples = numSamples;
        update();
    }

    public Double getMinimum() {
        return domain.getMinimum();
    }

    public void setMinimum(Double min) {
        domain.setMinimum(min);
    }

    public Double getMaximum() {
        return domain.getMaximum();
    }

    public void setMaximum(Double max) {
        domain.setMaximum(max);
    }

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int n) {
        if (this.numSamples != n) {
            this.numSamples = n;
            update();
            changer.fireStateChanged();
        }
    }

    transient int n;
    transient double diff;
    transient double mx;

    /** Updates info about number of samples, difference between samples, and minimum. */
    void update() {
        // start at min if that's included, min + diff if not min but max is, min + diff/2 if neither is
        n = (domain.isIncludeMaximum() && domain.isIncludeMinimum()) ? numSamples - 1 : numSamples;
        diff = (domain.getMaximum() - domain.getMinimum()) / n;
        mx = domain.isIncludeMinimum()
                ? domain.getMinimum()
                : ( domain.isIncludeMaximum() ? domain.getMinimum() + diff : domain.getMinimum() + diff/2 );
    }

    public List<Double> getSamples() {
        update();
        return (List<Double>) this;
    }

    public Double getSampleDiff() {
        update();
        return diff;
    }

    @Override
    public Double get(int index) {
        return mx + diff * index;
    }

    @Override
    public int size() {
        return numSamples;
    }

    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //
    public void stateChanged(ChangeEvent e) { 
        update(); 
        changer.fireStateChanged(); 
    }

    protected ChangeSupport changer = new ChangeSupport();
    public void addChangeListener(ChangeListener l) { changer.addChangeListener(l); }
    public void removeChangeListener(ChangeListener l) { changer.removeChangeListener(l); }
    
    //</editor-fold>

}
