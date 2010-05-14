/*
 * RealIntervalSamplerProvider.java
 * Created Apr 14, 2010
 */

package coordinate;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;

/**
 * Utility class for easily generating sampling domains.
 *
 * @author Elisha Peterson
 */
public abstract class RealIntervalSamplerProvider extends RealIntervalBroadcaster
            implements ScreenSampleDomainProvider<Double> {

    /** Construct to watch for changes from a specified change broadcaster. */
    public RealIntervalSamplerProvider(ChangeBroadcaster cb) {
        super(0.0, 1.0);
        setBounds(getNewMinimum(), getNewMaximum());
        if (cb != null) {
            cb.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e) { setBounds(getNewMinimum(), getNewMaximum()); }
            });
        }
    }

    /** @return minimum value for when the change broadcaster changes. */
    public abstract double getNewMinimum();
    /** @return maximum value for when the change broadcaster changes. */
    public abstract double getNewMaximum();
    /** @return scale of transformation for the given pixel spacing. */
    public abstract double getScale(float pixSpacing);

    public SampleSet<Double> samplerWithPixelSpacing(final float pixSpacing, int hint) {
        final RealIntervalStepSampler result = DomainUtils.stepSamplingDomain(this, getScale(pixSpacing), hint);
        addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) { result.setStep( getScale(pixSpacing) ); }
        });
        return result;
    }
}
