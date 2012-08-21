/*
 * SquareDomainStepSampler.java
 * Created Apr 7, 2010
 */

package org.blaise.math.plane;

import java.awt.geom.Point2D;
import java.util.AbstractList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.blaise.math.line.RealIntervalStepSampler;
import org.blaise.math.coordinate.SampleSet;

/**
 * <p>
 *    Uses two step-samplers to generate an evenly-spaced sampled grid in two dimensions.
 * </p>
 * @author Elisha Peterson
 */
public class SquareDomainStepSampler extends AbstractList<Point2D.Double>
        implements SampleSet<Point2D.Double>, ChangeListener {

    RealIntervalStepSampler sampler1;
    RealIntervalStepSampler sampler2;

    public SquareDomainStepSampler(RealIntervalStepSampler sampler1, RealIntervalStepSampler sampler2) {
        this.sampler1 = sampler1;
        this.sampler2 = sampler2;
        sampler1.addChangeListener(this);
        sampler2.addChangeListener(this);
    }

    public void setStep1(double step1) {
        sampler1.setStep(step1);
    }

    public void setStep2(double step2) {
        sampler2.setStep(step2);
    }

    @Override
    public Point2D.Double get(int index) {
        return new Point2D.Double( sampler1.get(index / sampler2.size()), sampler2.get(index % sampler2.size()) );
    }

    @Override
    public int size() {
        return sampler1.size() * sampler2.size();
    }

    public List<Point2D.Double> getSamples() {
        sampler1.update();
        sampler2.update();
        return (List<Point2D.Double>) this;
    }

    public Point2D.Double getSampleDiff() {
        return new Point2D.Double(sampler1.getSampleDiff(), sampler2.getSampleDiff());
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
