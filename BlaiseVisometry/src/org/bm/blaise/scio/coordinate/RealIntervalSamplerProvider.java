/*
 * RealIntervalSamplerProvider.java
 * Created Apr 14, 2010
 */

package org.bm.blaise.scio.coordinate;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bm.blaise.scio.coordinate.sample.SampleSet;

/**
 * Utility class for easily generating sampling domains.
 *
 * @author Elisha Peterson
 */
public abstract class RealIntervalSamplerProvider extends RealIntervalBroadcaster
            implements ScreenSampleDomainProvider<Double>, ChangeListener {

    /** Construct to watch for changes from a specified change broadcaster. */
    public RealIntervalSamplerProvider() {
        super(0.0, 1.0);
        setBounds(getNewMinimum(), getNewMaximum());
    }

    /** @return minimum value for when the change broadcaster changes. */
    public abstract double getNewMinimum();
    /** @return maximum value for when the change broadcaster changes. */
    public abstract double getNewMaximum();
    /** @return scale of transformation for the given pixel spacing. */
    public abstract double getScale(float pixSpacing);

    public SampleSet<Double> samplerWithPixelSpacing(final float pixSpacing, DomainHint hint) {
        final RealIntervalStepSampler result = DomainUtils.stepSamplingDomain(this, getScale(pixSpacing), hint);
        addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) { 
                result.setStep( getScale(pixSpacing) ); 
            }
        });
        return result;
    }
    
    public void stateChanged(ChangeEvent e) { 
        setBounds(getNewMinimum(), getNewMaximum()); 
    }
}
