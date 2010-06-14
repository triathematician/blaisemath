/*
 * LineAxis.java
 * Created on Sep 19, 2009
 */

package visometry.line;

import coordinate.DomainHint;
import coordinate.RealIntervalNiceSampler;
import java.util.ArrayList;
import java.util.List;
import primitive.GraphicRuledLine;
import primitive.style.ArrowStyle;
import primitive.style.RuledLineStyle;
import scio.coordinate.sample.SampleSet;
import util.ChangeBroadcaster;
import visometry.VPrimitiveEntry;
import visometry.plottable.Plottable;
import visometry.plottable.PlottableConstants;

/**
 * Displays an axis along a line.
 * 
 * @author Elisha Peterson
 */
public class LineAxis extends Plottable<Double> {

    /** AxisType used to drawArray the axes. */
    private RuledLineStyle style = new RuledLineStyle();

    /** Horizontal axis entry. */
    private VPrimitiveEntry entry;
    /** Horizontal line primitive. */
    private GraphicRuledLine<Double> line;

    /** Determines the "ideal" spacing between tick marks, in terms of pixels. */
    private int PIXEL_SPACING = 60;
    /** Whether to use multiples of pi for the tick marks. */
    private boolean usePi = false;


    //
    // CONSTRUCTORS
    //

    /** Construct using defaults. */
    public LineAxis() {
        this("t");
    }

    /** Construct with specified label. */
    public LineAxis(String label) {
        addPrimitive( entry = new VPrimitiveEntry( line = new GraphicRuledLine<Double>(-10.0, 10.0, label, null, null), style ) );
        style.getLineStyle().setAnchorShape(ArrowStyle.ArrowShape.REGULAR);
    }

    @Override
    public LineAxis clone() {
        return new LineAxis(getLabel());
    }

    @Override
    public String toString() {
        return "Axis ["+getLabel() + "]";
    }
    
    //
    // BEAN PROPERTY PATTERNS
    //

    /** Returns horizontal label. */
    public String getLabel() { return line.label; }
    /** Sets horizontal label. */
    public void setLabel(String label) { if (!line.label.equals(label)) { line.label = label; firePlottableStyleChanged(); } }

    /** @return true if first axis uses multiples of pi. */
    public boolean isUsePi() { return usePi; }
    /** Sets pi status */
    public void setUsePi(boolean value) { if (usePi != value) { usePi = value; firePlottableStyleChanged(); } }

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
        line.start = sampler.getMinimum();
        line.end = sampler.getMaximum();
        double length = line.end - line.start;
        double vStart = line.start + .02*length;
        double vEnd = line.start + .98*length;

        List<Double> xx = sample.getSamples();
        ArrayList<Double> xx2 = new ArrayList<Double>();
        for(Double x : xx)
            if (x >= vStart && x <= vEnd)
                xx2.add(x);
        int size = xx2.size();
        line.ticks = new double[size];
        line.tickLabels = new String[size];
        for(int i = 0; i < size; i++) {
            line.ticks[i] = (xx2.get(i) - line.start) / length;
            line.tickLabels[i] = PlottableConstants.FLOAT_FORMAT.format(xx2.get(i));
        }

        needsComputation = false;
    }
}
