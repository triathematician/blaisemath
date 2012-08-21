/*
 * RealIntervalStepSampler.java
 * Created on Apr 7, 2010
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
 *    This class contains features to sample an interval at values separated by a known "step".
 *    A <code>RealInterval</code> is required to create the method, together with a <code>double</code>
 *    representing the step value.
 * </p>
 * <p>
 *    If the minimum value is included in the interval, the first entry in the list is the minimum.
 *    Otherwise, the first entry is the minimum + 1/2 of the step.
 * </p>
 * <p>
 *    The class is based upon a <code>RealInterval</code> class describing the domain.
 *    If that interval fires events, then this class will listen for changes to the domain
 *    and pass them on to interested listeners.
 * </p>
 * @author Elisha Peterson
 */
public class RealIntervalStepSampler extends AbstractList<Double>
        implements SampleSet<Double>, ChangeListener {

    /** Domain of sampler. */
    RealInterval domain;
    /** Step between samples. */
    double step;

    public RealIntervalStepSampler(RealInterval domain, double step) {
        this.domain = domain;
        this.step = step;
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

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        if (this.step != step) {
            this.step = step;
            update();
            changer.fireStateChanged();
        }
    }

    transient int n;
    transient double mx;

    /** Updates info about number of samples, difference between samples, and minimum. */
    void update() {
        mx = domain.isIncludeMinimum() ? domain.getMinimum() : domain.getMinimum() + step/2;
        double n1 = (domain.getMaximum() - mx) / step;
        n = domain.isIncludeMaximum() || n1 % 0 != 0
                ? (int) n1 + 1
                : (int) n1;
    }

    @Override
    public Double get(int index) {
        return mx + step*index;
    }

    @Override
    public int size() {
        return n;
    }

    public List<Double> getSamples() {
        update();
        return (List<Double>) this;
    }

    public Double getSampleDiff() {
        return step;
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
