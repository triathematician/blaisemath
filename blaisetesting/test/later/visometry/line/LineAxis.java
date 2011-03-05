/*
 * LineAxis.java
 * Created on Sep 19, 2009
 */

package later.visometry.line;

import coordinate.DomainHint;
import coordinate.RealIntervalNiceSampler;
import org.bm.blaise.graphics.renderer.Anchor;
import org.bm.blaise.scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import minimal.visometry.plottable.VAxis;

/**
 * Displays an axis along a line.
 * 
 * @author Elisha Peterson
 */
public class LineAxis extends VAxis<Double> {

    /** Determines the "ideal" spacing between tick marks, in terms of pixels. */
    private int PIXEL_SPACING = 60;

    /** Construct using defaults. */
    public LineAxis() { this("t"); }

    /** Construct with specified label. */
    public LineAxis(String label) {
        super(label, new Double[]{-10.0, 10.0});
        getRuleStyle().setLabelPosition(1.2);
        getRuleStyle().getLabelStyle().setAnchor(Anchor.South);
        getLabelStyle().setAnchor(Anchor.Northeast);
    }
    
    @Override
    public LineAxis clone() {
        LineAxis result = new LineAxis(getLabel());
        result.usePi = usePi;
        return result;
    }

    @Override
    public String toString() {
        return "Axis ["+getLabel() + "]";
    }
    
    //
    // COMPUTATION
    //

    /** Sampling elements */
    transient SampleSet<Double> sample;

    @Override
    protected void recompute() {
        if (sample == null ) {
            sample = parent.requestScreenSampleDomain("t", Double.class, PIXEL_SPACING, usePi ? DomainHint.PREFER_PI : DomainHint.REGULAR );
            if (sample == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sample).addChangeListener(this);
        }

        RealIntervalNiceSampler sampler = (RealIntervalNiceSampler) sample;
        double min = sampler.getMinimum(), max = sampler.getMaximum(), length = max-min;
        updateAxis(min, max, max-.1*length, sampler.getSamples(), min, max, min+.02*length, max-.02*length, true);

        needsComputation = false;
    } // recompute
}
