/*
 * TimelineAxis.java
 * Created Aug 14, 2010
 */

package later.visometry.line;

import coordinate.DomainHint;
import coordinate.RealIntervalNiceSampler;
import java.util.ArrayList;
import java.util.List;
import graphics.GraphicRuledLine;
import org.bm.blaise.graphics.renderer.Anchor;
import primitive.style.temp.LineStyle;
import primitive.style.temp.PathStylePoints;
import primitive.style.temp.RuledLineStyle;
import org.bm.blaise.scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import visometry.graphics.VGraphicEntry;
import visometry.plottable.AbstractPlottable;
import other.PlottableConstants;

/**
 * Displays a view of the normal 1D axis as a "timeline"... more of a ruler kind of display.
 * There are optionally large buttons on either side to move the timeline forward and back.
 *
 * @author Elisha Peterson
 */
public class TimelineAxis extends AbstractPlottable<Double> {

    /** Style for top and bottom lines */
    private PathStylePoints styleLine = null;
    /** Style for large tick marks */
    private RuledLineStyle styleLarge = null;
    /** Style for small tick marks */
    private RuledLineStyle styleSmall = null, styleSmall2 = null;

    /** Entry for first horizontal line */
    private Double[] line = null;
    /** Entry displaying large labeled lines */
    private GraphicRuledLine<Double> large = null;
    /** Entry displaying small unlabeled ticks */
    private GraphicRuledLine<Double> small = null;

    /** Determines the spacing between large elements */
    private int SPACING_LARGE = 100;
    /** Determines the spacing between small ticks */
    private int SPACING_SMALL = 20;

    //
    // CONSTRUCTORS
    //

    /** Default constructor */
    public TimelineAxis() {
        addPrimitive(new VGraphicEntry(
                small = new GraphicRuledLine<Double>(-10.0, 10.0, null, null),
                styleSmall = new RuledLineStyle() ) );
          styleSmall.setRuleLeft(-30); styleSmall.setRuleRight(-22);
          styleSmall.setLabelsVisible(false);
        addPrimitive(new VGraphicEntry(small, styleSmall2 = new RuledLineStyle() ) );
          styleSmall2.setRuleLeft(22); styleSmall2.setRuleRight(30);
          styleSmall2.setLabelsVisible(false);
          styleSmall2.setRuleStyle(styleSmall.getRuleStyle());

        addPrimitive(new VGraphicEntry(
                large = new GraphicRuledLine<Double>(-10.0, 10.0, null, null),
                styleLarge = new RuledLineStyle() ) );
          styleLarge.setLabelPosition(1);
          styleLarge.setRuleLeft(-30); styleLarge.setRuleRight(30);
          styleLarge.getLabelStyle().setAnchor(Anchor.Southwest);

        addPrimitive(new VGraphicEntry(
                line = new Double[]{-10.0, 10.0},
                styleLine = new LineStyle() ));
    }

    @Override
    public TimelineAxis clone() {
        return new TimelineAxis();
    }

    @Override
    public String toString() {
        return "TimelineAxis";
    }

    //
    // BEANS
    //

    /** @return style of large rules */
    public RuledLineStyle getStyleLarge() {
        return styleLarge;
    }

    /** Sets style of large rules */
    public void setStyleLarge(RuledLineStyle styleLarge) {
        this.styleLarge = styleLarge;
        firePlottableStyleChanged();
    }

    /** Gets style of small rules */
    public RuledLineStyle getStyleSmall() {
        return styleSmall;
    }

    /** Sets style of small rules */
    public void setStyleSmall(RuledLineStyle styleSmall) {
        this.styleSmall = styleSmall;
        firePlottableStyleChanged();
    }



    //
    // COMPUTATION
    //

    /** Sampling elements */
    transient SampleSet<Double> sampleLarge, sampleSmall;

    @Override
    protected void recompute() {
        if (sampleLarge == null ) {
            sampleLarge = parent.requestScreenSampleDomain("t", Double.class, SPACING_LARGE, DomainHint.REGULAR );
            if (sampleLarge == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sampleLarge).addChangeListener(this);
        }
        if (sampleSmall == null ) {
            sampleSmall = parent.requestScreenSampleDomain("t", Double.class, SPACING_SMALL, DomainHint.REGULAR );
            if (sampleSmall == null)
                throw new IllegalStateException("Unable to retrieve appropriate domain from parent class!");
            ((ChangeBroadcaster)sampleSmall).addChangeListener(this);
        }
        updateLine(large, (RealIntervalNiceSampler) sampleLarge, true);
        updateLine(small, (RealIntervalNiceSampler) sampleSmall, false);
        needsComputation = false;
    } // recompute

    private static void updateLine(GraphicRuledLine<Double> line, RealIntervalNiceSampler sampler, boolean showLabels) {
        line.start = sampler.getMinimum();
        line.end = sampler.getMaximum();
        double length = line.end - line.start;
        double vStart = line.start + .02*length;
        double vEnd = line.start + .98*length;

        List<Double> xx = sampler.getSamples();
        ArrayList<Double> xx2 = new ArrayList<Double>();
        for(Double x : xx)
            if (x >= vStart && x <= vEnd)
                xx2.add(x);
        int size = xx2.size();
        line.ticks = new double[size];
        line.tickLabels = showLabels ? new String[size] : null;
        for(int i = 0; i < size; i++) {
            line.ticks[i] = (xx2.get(i) - line.start) / length;
            if (showLabels)
                line.tickLabels[i] = PlottableConstants.FLOAT_FORMAT.format(xx2.get(i));
        }
    } // updateLine

}
