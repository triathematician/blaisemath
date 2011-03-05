/*
 * PlaneGrid.java
 * Created on Aug 3, 2009
 */

package minimal.visometry.plane;

import coordinate.DomainHint;
import coordinate.RealIntervalNiceSampler;
import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.util.List;
import primitive.style.temp.PathStyleShape;
import org.bm.blaise.scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;

/**
 *   <code>PlaneGrid</code> is a square grid on a plot.
 * @author Elisha Peterson
 */
public class PlaneGrid extends PlanePathPlottable {

    /** Approximates pixel spacing between grid elements. */
    private static final int PIXEL_SPACING = 80;

    /** Sampling elements */
    SampleSet<Double> sampleX, sampleY;

    /** Constructs a default grid. */
    public PlaneGrid() {
        ((PathStyleShape) entry.renderer).setStrokeColor(new Color(224, 224, 255));
    }

    @Override
    public String toString() { return "PlaneGrid"; }

    @Override
    public void recompute() {
        if (sampleX == null || sampleY == null) {
            sampleX = parent.requestScreenSampleDomain("x", Double.class, PIXEL_SPACING, DomainHint.REGULAR);
            sampleY = parent.requestScreenSampleDomain("y", Double.class, PIXEL_SPACING, DomainHint.REGULAR);
            if (sampleX == null || sampleY == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sampleX).addChangeListener(this);
            ((ChangeBroadcaster)sampleY).addChangeListener(this);
        }

        List<Double> xx = sampleX.getSamples();
        List<Double> yy = sampleY.getSamples();
        
        RealIntervalNiceSampler rins1 = (RealIntervalNiceSampler) sampleX;
        RealIntervalNiceSampler rins2 = (RealIntervalNiceSampler) sampleY;
        float minX = (float)(double) rins1.getMinimum();
        float maxX = (float)(double) rins1.getMaximum();
        float minY = (float)(double) rins2.getMinimum();
        float maxY = (float)(double) rins2.getMaximum();
        
        entry.local = path = new GeneralPath();
        for (Double x : xx) {
            path.moveTo((float)(double) x, minY);
            path.lineTo((float)(double) x, maxY);
        }
        for (Double y : yy) {
            path.moveTo(minX, (float)(double) y);
            path.lineTo(maxX, (float)(double) y);
        }
        needsComputation = false;
    }
}
