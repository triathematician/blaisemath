/*
 * RealIntervalBroadcaster.java
 * Created Apr 7, 2010
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
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

/**
 * <p>
 *   This class adds event handling to a basic real interval.
 * </p>
 * @author Elisha Peterson
 */
public class RealIntervalBroadcaster extends RealInterval {

    /** 
     * Constructs real interval with specified min and max (included by default in the interval)
     * @param min minimum of interval
     * @param max maximum of interval
     */
    public RealIntervalBroadcaster(double min, double max) {
        super(min, max);
    }

    /**
     * Constructs real interval with specified min and max and booleans describing
     * whether they are included in the domain
     * @param min minimum of interval
     * @param minInclusive whether minimum is included in domain
     * @param max maximum of interval
     * @param maxInclusive whether maax is included in domain
     */
    public RealIntervalBroadcaster(double min, boolean minInclusive, double max, boolean maxInclusive) {
        super(min, minInclusive, max, maxInclusive);
    }

    @Override
    public void setMinimum(Double min) {
        if (this.min != min) {
            super.setMinimum(min);
            fireStateChanged();
        }
    }

    @Override
    public void setMaximum(Double max) {
        if (this.max != max) {
            super.setMaximum(max);
            fireStateChanged();
        }
    }

    /** 
     * Sets both boundaries of the interval
     * @param min 
     * @param max 
     */
    public void setBounds(Double min, Double max) {
        Double aMin = Math.min(min, max);
        Double aMax = Math.max(min, max);
        if (aMin != this.min || aMax != this.max) {
            super.setMinimum(aMin);
            super.setMaximum(aMax);
        }
        fireStateChanged();
    }
    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //
    
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
