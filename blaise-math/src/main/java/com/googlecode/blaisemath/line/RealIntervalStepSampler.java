/*
 * RealIntervalStepSampler.java
 * Created on Apr 7, 2010
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

import javax.swing.event.ChangeEvent;
import java.util.AbstractList;
import java.util.List;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import com.googlecode.blaisemath.coordinate.SampleSet;

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
            fireStateChanged();
        }
    }

    transient int n;
    transient double mx;

    /** Updates info about number of samples, difference between samples, and minimum. */
    public void update() {
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
