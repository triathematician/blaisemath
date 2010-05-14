/*
 * SquareDomainStepSampler.java
 * Created Apr 7, 2010
 */

package coordinate;

import java.awt.geom.Point2D;
import java.util.AbstractList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import util.DefaultChangeBroadcaster;

/**
 * <p>
 *    Uses two step-samplers to generate an evenly-spaced sampled grid in two dimensions.
 * </p>
 * @author Elisha Peterson
 */
public class SquareDomainStepSampler extends AbstractList<Point2D.Double>
        implements SampleSet<Point2D.Double>, ChangeBroadcaster, ChangeListener {

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


    //
    // ChangeEvent HANDLING
    //

    DefaultChangeBroadcaster changer = new DefaultChangeBroadcaster();
    public void addChangeListener(ChangeListener l) { changer.addChangeListener(l); }
    public void removeChangeListener(ChangeListener l) { changer.removeChangeListener(l); }
    public void stateChanged(ChangeEvent e) { changer.fireStateChanged(); }

}
