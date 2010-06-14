/*
 * PlanePolarGrid.java
 * Created on Aug 3, 2009
 */

package visometry.plane;

import coordinate.DomainHint;
import coordinate.NiceRangeGenerator;
import coordinate.RealIntervalNiceSampler;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.List;
import primitive.style.PathStyle;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;

/**
 *   <code>PlanePolarGrid</code> is a polar grid on a plot.
 * @author Elisha Peterson
 */
public class PlanePolarGrid extends PlanePathPlottable {

    /** Approximates pixel spacing between grid elements. */
    private static final int PIXEL_SPACING = 80;
    /** Number of theta-steps. */
    private static final int THETA_STEPS = 24;

    /** Sampling elements */
    SampleSet<Double> sampleX, sampleY;

    /** Constructs a default grid. */
    public PlanePolarGrid() {
        ((PathStyle) entry.style).setStrokeColor(new Color(224, 224, 255));
    }

    @Override
    public String toString() { return "PolarGrid"; }

    //
    // COMPUTATION METHODS
    //

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
        
        RealIntervalNiceSampler rins1 = (RealIntervalNiceSampler) sampleX;
        RealIntervalNiceSampler rins2 = (RealIntervalNiceSampler) sampleY;
        float minX = (float)(double) rins1.getMinimum();
        float maxX = (float)(double) rins1.getMaximum();
        float minY = (float)(double) rins2.getMinimum();
        float maxY = (float)(double) rins2.getMaximum();

        double minR = minRadius(minX, minY, maxX, maxY);
        double maxR = maxRadius(minX, minY, maxX, maxY);
        List<Double> rr = NiceRangeGenerator.STANDARD.niceRange(minR, maxR, Math.min(rins1.getSampleDiff(), rins2.getSampleDiff()));

        entry.local = path = new GeneralPath();
        for (Double r : rr)
            path.append(new Ellipse2D.Double(-r, -r, 2*r, 2*r), false);
        for (double theta = 0; theta < 2*Math.PI; theta += 2*Math.PI/THETA_STEPS)
            path.append(new Line2D.Double(minR*Math.cos(theta), minR*Math.sin(theta), maxR*Math.cos(theta), maxR*Math.sin(theta)), false);

        needsComputation = false;
    }

    //
    // UTILITY METHODS
    //

    /** Compute minimum distance from origin in window specified by given points. */
    private static double minRadius(double minX, double minY, double maxX, double maxY) {
        if ( minX*maxX <= 0 && minY*maxY <= 0 ) { // origin is inside box: min is 0
            return 0;
        } else if ( minX*maxX <= 0 ) { // origin is within x bounds
            return Math.min(Math.abs(minY), Math.abs(maxY));
        } else if ( minY*maxY <= 0 ) { // origin is within y bounds
            return Math.min(Math.abs(minX), Math.abs(maxX));
        }
        // default to minimum radius at a boundary point
        double a1 = minX*minX + minY*minY;
        double a2 = minX*minX + maxY*maxY;
        double a3 = maxX*maxX + minY*minY;
        double a4 = maxX*maxX + maxY*maxY;
        return Math.sqrt( Math.min(Math.min(Math.min(a1, a2), a3), a4) );
    }

    /** Compute maximum distance from origin in window specified by given points. */
    private static double maxRadius(double minX, double minY, double maxX, double maxY) {
        double a1 = minX*minX + minY*minY;
        double a2 = minX*minX + maxY*maxY;
        double a3 = maxX*maxX + minY*minY;
        double a4 = maxX*maxX + maxY*maxY;
        return Math.sqrt( Math.max(Math.max(Math.max(a1, a2), a3), a4) );
    }

}
