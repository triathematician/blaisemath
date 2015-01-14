/*
 * SquareDomainBroadcaster.java
 */

package com.googlecode.blaisemath.plane;

/*
 * #%L
 * BlaiseMath
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import java.awt.geom.Point2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import com.googlecode.blaisemath.coordinate.Domain;
import com.googlecode.blaisemath.coordinate.RandomCoordinateGenerator;
import com.googlecode.blaisemath.line.RealIntervalBroadcaster;
import com.googlecode.blaisemath.util.MinMaxBean;

/**
 *
 * @author ae3263
 */
public class SquareDomainBroadcaster
        implements Domain<Point2D.Double>, RandomCoordinateGenerator<Point2D.Double>, MinMaxBean<Point2D.Double>, ChangeListener {

    RealIntervalBroadcaster x, y;

    /**
     * Constructs the domain with specified bounds.
     * @param minX minimum in first direction
     * @param minY minimum in second direction
     * @param maxX maximum in first direction
     * @param maxY maximum in second direction
     */
    public SquareDomainBroadcaster(double minX, double minY, double maxX, double maxY) {
        this( new RealIntervalBroadcaster(minX, maxX), new RealIntervalBroadcaster(minY, maxY) );
    }

    /**
     * Constructs square domain based on two existing interval domains
     * @param xDomain first domain
     * @param yDomain second domain
     */
    public SquareDomainBroadcaster(RealIntervalBroadcaster xDomain, RealIntervalBroadcaster yDomain) {
        x = xDomain;
        y = yDomain;
        x.addChangeListener(this);
        y.addChangeListener(this);
    }

    public boolean contains(Point2D.Double coord) {
        return x.contains(coord.x) && y.contains(coord.y);
    }

    public Point2D.Double randomValue() {
        return new Point2D.Double(x.randomValue(), y.randomValue());
    }

    public Point2D.Double getMinimum() {
        return new Point2D.Double(x.getMinimum(), y.getMinimum());
    }

    public Point2D.Double getMaximum() {
        return new Point2D.Double(x.getMaximum(), y.getMaximum());
    }

    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

    public void stateChanged(ChangeEvent e) {
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
