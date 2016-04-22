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

import com.googlecode.blaisemath.util.MinMaxBean;
import javax.swing.event.ChangeEvent;
import java.util.AbstractList;
import java.util.List;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import com.googlecode.blaisemath.coordinate.SampleSet;

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
            fireStateChanged();
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
        fireStateChanged(); 
    }
    
    protected ChangeEvent changeEvent = new ChangeEvent(this);
    protected EventListenerList listenerList = new EventListenerList();

    public synchronized void addChangeListener(ChangeListener l) { 
        listenerList.add(ChangeListener.class, l);
    }
    
    public synchronized void removeChangeListener(ChangeListener l) { 
        listenerList.remove(ChangeListener.class, l); 
    }

    /** Notify interested listeners of an (unspecified) change in the plottable. */
    public synchronized void fireStateChanged() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == ChangeListener.class) {
                if (changeEvent == null)
                    return;
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
    }
    
    //</editor-fold>

}
