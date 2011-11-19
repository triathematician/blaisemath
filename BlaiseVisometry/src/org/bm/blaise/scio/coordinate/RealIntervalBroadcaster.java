/*
 * RealIntervalBroadcaster.java
 * Created Apr 7, 2010
 */

package org.bm.blaise.scio.coordinate;

import javax.swing.event.ChangeListener;
import org.bm.util.ChangeSupport;

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
     * Constructs real interval with specified min and max & booleans describing whether they are included in the domain
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
            changer.fireStateChanged();
        }
    }

    @Override
    public void setMaximum(Double max) {
        if (this.max != max) {
            super.setMaximum(max);
            changer.fireStateChanged();
        }
    }

    /** Sets both boundaries of the interval */
    public void setBounds(Double min, Double max) {
        Double aMin = Math.min(min, max);
        Double aMax = Math.max(min, max);
        if (aMin != this.min || aMax != this.max) {
            super.setMinimum(aMin);
            super.setMaximum(aMax);
        }
        changer.fireStateChanged();
    }
    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

    protected ChangeSupport changer = new ChangeSupport();
    public void addChangeListener(ChangeListener l) { changer.addChangeListener(l); }
    public void removeChangeListener(ChangeListener l) { changer.removeChangeListener(l); }
    //</editor-fold>
    
}
